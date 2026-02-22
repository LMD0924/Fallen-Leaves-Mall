<!-- components/settings/merchant/MerchantStatusQuery.vue - 商家申请状态查询组件 -->
<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { get } from '@/net/index.js'
const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  },
  userId: {
    type: [Number, String],
    default: null
  },
  initialStatus: {
    type: Number,
    default: 0 // 0-待审核 1-已通过 2-已拒绝 3-已禁用
  }
})

const emit = defineEmits(['statusChange', 'refresh'])

const [messageApi] = message.useMessage()

// 状态配置
const statusConfig = {
  0: {
    text: '待审核',
    icon: 'fa-clock',
    bgClass: 'bg-amber-100 dark:bg-amber-900/30',
    textClass: 'text-amber-700 dark:text-amber-400'
  },
  1: {
    text: '已通过',
    icon: 'fa-check-circle',
    bgClass: 'bg-emerald-100 dark:bg-emerald-900/30',
    textClass: 'text-emerald-700 dark:text-emerald-400'
  },
  2: {
    text: '已拒绝',
    icon: 'fa-times-circle',
    bgClass: 'bg-red-100 dark:bg-red-900/30',
    textClass: 'text-red-700 dark:text-red-400'
  },
  3: {
    text: '已禁用',
    icon: 'fa-ban',
    bgClass: 'bg-gray-100 dark:bg-gray-800',
    textClass: 'text-gray-700 dark:text-gray-400'
  }
}

// 状态数据
const status = ref(props.initialStatus)
const loading = ref(true)
const error = ref('')
const refreshing = ref(false)
const lastUpdate = ref(new Date())

// 申请信息
const applicationInfo = ref([])

// 步骤配置（根据状态动态更新）
const steps = computed(() => {
  const baseSteps = [
    {
      id: 'submit',
      name: '提交申请',
      description: '填写商家信息',
      eta: '即时',
      status: 'completed'
    },
    {
      id: 'review',
      name: '平台审核',
      description: '审核资料真实性',
      eta: '1-3个工作日',
      status: 'pending'
    },
    {
      id: 'approve',
      name: '审核通过',
      description: '开通商家权限',
      eta: '即时',
      status: 'pending'
    }
  ]

  // 根据当前状态更新步骤状态
  if (status.value === 0) { // 待审核
    baseSteps[0].status = 'completed'
    baseSteps[1].status = 'active'
    baseSteps[2].status = 'pending'
  } else if (status.value === 1) { // 已通过
    baseSteps[0].status = 'completed'
    baseSteps[1].status = 'completed'
    baseSteps[2].status = 'active'
  } else if (status.value === 2) { // 已拒绝
    baseSteps[0].status = 'completed'
    baseSteps[1].status = 'rejected'
    baseSteps[1].reason = applicationInfo.rejectReason
    baseSteps[2].status = 'pending'
  } else if (status.value === 3) { // 已禁用
    baseSteps[0].status = 'completed'
    baseSteps[1].status = 'completed'
    baseSteps[2].status = 'rejected'
    baseSteps[2].reason = applicationInfo.disableReason
  }

  return baseSteps
})

// 已开通权限
const permissions = ref([
  '商品发布权限',
  '订单管理权限',
  '营销活动权限',
  '数据查看权限',
  '客服接待权限',
  '店铺装修权限'
])

// 计算进度条宽度
const getProgressWidth = () => {
  if (status.value === 0) return '33.33%'
  if (status.value === 1) return '100%'
  if (status.value === 2) return '33.33%' // 拒绝停在第一步
  if (status.value === 3) return '66.66%' // 禁用停在第二步
  return '0%'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '暂无'
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

// 查询申请状态
const queryApplicationStatus = () => {
  loading.value = true
  error.value = ''

  try {
    // 优先从 userInfo.id 获取用户 ID，其次使用 userId
    const userId = props.userInfo?.id || props.userId

    if (!userId) {
      throw new Error('用户ID为空')
    }

    get("http://localhost:8081/api/merchant/queryApplyProgress",{
        userId: userId
    },(message,data)=>{
      applicationInfo.value=data
      console.log(applicationInfo.value)

      // 模拟数据
      setTimeout(() => {
        if (typeof data === 'number') {
          // 后端直接返回了状态码
          status.value = data
          lastUpdate.value = new Date()
          emit('statusChange', status.value)
        } else if (data && typeof data === 'object') {
          // 兼容返回对象的情况
          status.value = data.status || 0
          Object.assign(applicationInfo, data)

          lastUpdate.value = new Date()
          emit('statusChange', status.value)
        } else {
          error.value = '查询失败：返回数据格式错误'
        }
        loading.value = false
      }, 1000)
    },(message)=>{
      console.error('查询状态失败:', message)
      error.value = message || '查询失败，请稍后重试'
      loading.value = false
    },()=>{
      console.error('查询状态网络错误')
      error.value = '网络错误，请稍后重试'
      loading.value = false
    },false)

  } catch (err) {
    console.error('查询状态失败:', err)
    error.value = '查询失败，请稍后重试'
    loading.value = false
  }
}

// 刷新状态
const refreshStatus = () => {
  refreshing.value = true
  queryApplicationStatus()
  // 由于查询是异步的，我们需要在查询完成后设置 refreshing.value = false
  // 但为了用户体验，我们可以在查询开始后短暂延迟后设置，或者在查询完成的回调中设置
  setTimeout(() => {
    refreshing.value = false
    messageApi.success('状态已刷新')
  }, 1000)
}

// 重试查询
const retryQuery = () => {
  queryApplicationStatus()
}

// 查看申请详情
const viewApplicationDetail = () => {
  messageApi.info('查看申请详情功能开发中')
}

// 撤回申请
const cancelApplication = () => {
  messageApi.info('撤回申请功能开发中')
}

// 进入商家后台
const goToDashboard = () => {
  messageApi.info('正在跳转至商家后台...')
}

// 查看商家信息
const viewMerchantInfo = () => {
  messageApi.info('查看商家信息功能开发中')
}

// 重新申请
const reapply = () => {
  messageApi.info('重新申请功能开发中')
}

// 联系客服
const contactCustomerService = () => {
  messageApi.info('联系客服功能开发中')
}

// 提起申诉
const appeal = () => {
  messageApi.info('申诉功能开发中')
}

// 初始化查询
onMounted(() => {
  queryApplicationStatus()
})
</script>

<template>
  <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800">
    <!-- 头部：状态标签和标题 -->
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 rounded-lg bg-purple-100 dark:bg-purple-950/30 flex items-center justify-center">
          <i class="fa fa-store text-purple-600 dark:text-purple-400"></i>
        </div>
        <div>
          <h3 class="text-lg font-light text-gray-900 dark:text-gray-100">商家申请状态</h3>
          <p class="text-xs text-gray-500 dark:text-gray-400">实时查看您的申请进度</p>
        </div>
      </div>

      <!-- 状态标签 -->
      <div
        :class="[
          'px-4 py-2 rounded-full text-sm font-medium flex items-center gap-2',
          statusConfig[status].bgClass,
          statusConfig[status].textClass
        ]"
      >
        <i :class="['fa', statusConfig[status].icon]"></i>
        <span>{{ statusConfig[status].text }}</span>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="py-12 text-center">
      <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-gray-300 dark:border-gray-700 border-t-purple-500"></div>
      <p class="mt-3 text-sm text-gray-500 dark:text-gray-400">正在查询申请状态...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="py-12 text-center">
      <div class="w-16 h-16 mx-auto mb-4 rounded-full bg-red-100 dark:bg-red-900/30 flex items-center justify-center">
        <i class="fa fa-exclamation-triangle text-2xl text-red-500"></i>
      </div>
      <p class="text-gray-600 dark:text-gray-400 mb-3">{{ error }}</p>
      <button
        @click="retryQuery"
        class="px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600 transition-colors text-sm"
      >
        <i class="fa fa-refresh mr-2"></i>重新查询
      </button>
    </div>

    <!-- 申请状态内容 -->
    <div v-else>
      <!-- 申请信息卡片 -->
      <div v-for="(item,index) in applicationInfo" :key="index" class="mb-6 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">商家联系人</p>
            <p class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ item.contactName }}</p>
          </div>
          <div>
            <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">申请时间</p>
            <p class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ formatDate(item.createTime) }}</p>
          </div>
          <div v-if="applicationInfo.reviewTime">
            <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">审核时间</p>
            <p class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ formatDate(item.reviewTime) }}</p>
          </div>
          <div v-if="applicationInfo.reviewer">
            <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">审核人</p>
            <p class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ item.reviewer }}</p>
          </div>
        </div>
      </div>

      <!-- 进度跟踪 - 仅待审核和已通过显示 -->
      <div v-if="status === 0 || status === 1" class="mb-8">
        <h4 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-4">申请进度</h4>

        <!-- 进度条 -->
        <div class="relative mb-6">
          <div class="h-2 bg-gray-200 dark:bg-gray-800 rounded-full">
            <div
              class="h-2 bg-gradient-to-r from-purple-500 to-fuchsia-500 rounded-full transition-all duration-1000"
              :style="{ width: getProgressWidth() }"
            ></div>
          </div>
        </div>

        <!-- 步骤节点 -->
        <div class="relative flex justify-between">
          <div
            v-for="(step, index) in steps"
            :key="step.id"
            class="flex flex-col items-center flex-1"
          >
            <!-- 节点图标 -->
            <div class="relative mb-3">
              <div
                :class="[
                  'w-10 h-10 rounded-full flex items-center justify-center text-sm transition-all duration-500',
                  step.status === 'completed'
                    ? 'bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white shadow-lg shadow-purple-500/30'
                    : step.status === 'active'
                    ? 'bg-white dark:bg-black border-2 border-purple-500 text-purple-500'
                    : 'bg-gray-100 dark:bg-gray-800 text-gray-400 dark:text-gray-600'
                ]"
              >
                <i v-if="step.status === 'completed'" class="fa fa-check"></i>
                <span v-else-if="step.status === 'active' && step.icon" class="animate-pulse">●</span>
                <span v-else>{{ index + 1 }}</span>
              </div>
              <!-- 活跃状态光晕 -->
              <div v-if="step.status === 'active'" class="absolute inset-0 rounded-full animate-ping bg-purple-400/30"></div>
            </div>

            <!-- 步骤名称 -->
            <div class="text-xs font-medium text-center mb-1" :class="step.status !== 'pending' ? 'text-gray-900 dark:text-gray-100' : 'text-gray-400 dark:text-gray-600'">
              {{ step.name }}
            </div>

            <!-- 步骤描述 -->
            <div class="text-[10px] text-center text-gray-500 dark:text-gray-400">
              {{ step.description }}
            </div>

            <!-- 预计时间/状态说明 -->
            <div v-if="step.status === 'active'" class="mt-2 text-[10px] text-purple-500 bg-purple-50 dark:bg-purple-950/30 px-2 py-0.5 rounded-full">
              {{ step.eta }}
            </div>
            <div v-else-if="step.status === 'rejected'" class="mt-2 text-[10px] text-red-500 bg-red-50 dark:bg-red-950/30 px-2 py-0.5 rounded-full">
              {{ step.reason }}
            </div>
          </div>
        </div>
      </div>

      <!-- 拒绝原因（状态2） -->
      <div v-if="status === 2" class="mb-6 p-4 bg-red-50 dark:bg-red-950/30 rounded-lg border border-red-200 dark:border-red-800">
        <div class="flex items-start gap-3">
          <i class="fa fa-times-circle text-red-500 mt-0.5"></i>
          <div>
            <p class="text-sm font-medium text-red-800 dark:text-red-300 mb-1">拒绝原因</p>
            <p class="text-sm text-red-700 dark:text-red-400">{{ applicationInfo.rejectReason || '未提供具体原因' }}</p>
            <p class="text-xs text-red-600 dark:text-red-500 mt-2">您可以根据拒绝原因修改资料后重新提交申请</p>
          </div>
        </div>
      </div>

      <!-- 禁用原因（状态3） -->
      <div v-if="status === 3" class="mb-6 p-4 bg-gray-100 dark:bg-gray-800 rounded-lg border border-gray-300 dark:border-gray-700">
        <div class="flex items-start gap-3">
          <i class="fa fa-ban text-gray-500 mt-0.5"></i>
          <div>
            <p class="text-sm font-medium text-gray-800 dark:text-gray-300 mb-1">账号已被禁用</p>
            <p class="text-sm text-gray-600 dark:text-gray-400">{{ applicationInfo.disableReason || '您的商家账号已被禁用' }}</p>
            <p class="text-xs text-gray-500 dark:text-gray-500 mt-2">如有疑问，请联系客服：400-123-4567</p>
          </div>
        </div>
      </div>

      <!-- 商家信息摘要（仅已通过显示） -->
      <div v-if="status === 1" class="mb-6 p-4 bg-emerald-50 dark:bg-emerald-950/30 rounded-lg border border-emerald-200 dark:border-emerald-800">
        <div class="flex items-center gap-2 mb-3">
          <i class="fa fa-check-circle text-emerald-500"></i>
          <span class="text-sm font-medium text-emerald-800 dark:text-emerald-300">已开通的商家权限</span>
        </div>
        <div class="grid grid-cols-2 gap-3">
          <div v-for="permission in permissions" :key="permission" class="flex items-center gap-2 text-xs text-emerald-700 dark:text-emerald-400">
            <i class="fa fa-check"></i>
            <span>{{ permission }}</span>
          </div>
        </div>
      </div>

      <!-- 操作按钮组 -->
      <div class="flex flex-wrap gap-3 mt-6">
        <!-- 待审核状态的操作 -->
        <template v-if="status === 0">
          <button
            @click="viewApplicationDetail"
            class="flex-1 px-4 py-2.5 bg-purple-500 text-white rounded-lg hover:bg-purple-600 transition-colors text-sm"
          >
            <i class="fa fa-file-alt mr-2"></i>查看申请详情
          </button>
          <button
            @click="cancelApplication"
            class="flex-1 px-4 py-2.5 border border-gray-300 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-colors text-sm"
          >
            <i class="fa fa-times mr-2"></i>撤回申请
          </button>
        </template>

        <!-- 已通过状态的操作 -->
        <template v-if="status === 1">
          <button
            @click="goToDashboard"
            class="flex-1 px-4 py-2.5 bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white rounded-lg hover:from-purple-600 hover:to-fuchsia-600 transition-all text-sm"
          >
            <i class="fa fa-store-alt mr-2"></i>进入商家后台
          </button>
          <button
            @click="viewMerchantInfo"
            class="flex-1 px-4 py-2.5 border border-gray-300 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-colors text-sm"
          >
            <i class="fa fa-info-circle mr-2"></i>查看商家信息
          </button>
        </template>

        <!-- 已拒绝状态的操作 -->
        <template v-if="status === 2">
          <button
            @click="reapply"
            class="flex-1 px-4 py-2.5 bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white rounded-lg hover:from-purple-600 hover:to-fuchsia-600 transition-all text-sm"
          >
            <i class="fa fa-redo mr-2"></i>重新申请
          </button>
          <button
            @click="contactCustomerService"
            class="flex-1 px-4 py-2.5 border border-gray-300 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-colors text-sm"
          >
            <i class="fa fa-headset mr-2"></i>联系客服
          </button>
        </template>

        <!-- 已禁用状态的操作 -->
        <template v-if="status === 3">
          <button
            @click="appeal"
            class="flex-1 px-4 py-2.5 bg-gradient-to-r from-amber-500 to-orange-500 text-white rounded-lg hover:from-amber-600 hover:to-orange-600 transition-all text-sm"
          >
            <i class="fa fa-gavel mr-2"></i>提起申诉
          </button>
          <button
            @click="contactCustomerService"
            class="flex-1 px-4 py-2.5 border border-gray-300 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-colors text-sm"
          >
            <i class="fa fa-headset mr-2"></i>联系客服
          </button>
        </template>

        <!-- 通用刷新按钮 -->
        <button
          @click="refreshStatus"
          class="px-4 py-2.5 border border-gray-300 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-colors text-sm"
          title="刷新状态"
        >
          <i class="fa fa-refresh" :class="{ 'animate-spin': refreshing }"></i>
        </button>
      </div>

      <!-- 最后更新时间 -->
      <div class="mt-4 text-center">
        <p class="text-xs text-gray-400">
          最后更新：{{ formatDate(lastUpdate) }}
          <button @click="refreshStatus" class="ml-2 text-purple-500 hover:text-purple-600">
            <i class="fa fa-sync-alt"></i>
          </button>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* 自定义动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
