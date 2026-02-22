<!-- components/settings/MerchantApplication.vue - 商家申请组件 -->
<script setup>
import { ref, reactive, computed } from 'vue'
import { message } from 'ant-design-vue'
import MerchantForm from './merchant/MerchantForm.vue'
import MerchantPending from './merchant/MerchantPending.vue'
import MerchantApproved from './merchant/MerchantApproved.vue'
import MerchantBenefits from './merchant/MerchantBenefits.vue'
import {post} from "@/net/index.js";

const [messageApi, contextHolder] = message.useMessage();

const props = defineProps({
  userInfo: {
    type: Object,
    required: true
  },
  merchantStatus: {
    type: String,
    default: 'none'
  },
  merchantApplication: {
    type: Object,
    default: () => ({ submitTime: new Date() })
  }
})

const emit = defineEmits(['update:merchantStatus', 'update:application'])

const isSubmitting = ref(false)
const validationErrors = ref([])

// 商家申请步骤
const merchantSteps = computed(() => {
  const steps = [
    { id: 'apply', name: '提交申请', description: '填写基本信息' },
    { id: 'review', name: '审核中', description: '平台审核资料' },
    { id: 'approve', name: '审核通过', description: '成为认证商家' }
  ]

  steps.forEach((step, index) => {
    if (props.merchantStatus === 'none') {
      step.completed = false
      step.active = index === 0
    } else if (props.merchantStatus === 'pending') {
      step.completed = index < 1
      step.active = index === 1
    } else if (props.merchantStatus === 'approved') {
      step.completed = true
      step.active = false
    }
  })

  return steps
})

// 获取进度条宽度
const getProgressWidth = () => {
  const completedSteps = merchantSteps.value.filter(step => step.completed).length
  return `${(completedSteps / merchantSteps.value.length) * 100}%`
}

// 获取状态文本
const getMerchantStatusText = () => {
  const statusMap = {
    none: '未申请',
    pending: '审核中',
    approved: '已认证'
  }
  return statusMap[props.merchantStatus] || '未知状态'
}

// 提交申请
const submitMerchantApplication = async (data) => {
  isSubmitting.value = true
  validationErrors.value = []

  try {
    // 检查是否有验证错误
    if (data.errors) {
      validationErrors.value = data.errors
      return
    }

    // 提取表单数据
    const formData = data.formData

    // 确保表单数据包含用户ID
    if (!formData.userId && props.userInfo?.id) {
      formData.userId = props.userInfo.id
    }

    // 实现商家申请逻辑
    await post("http://localhost:8081/api/merchant/applyMerchant",{
      ...formData,
      status:0 // 申请状态：0-待审核，1-审核通过，2-审核不通过，3-已禁用
    },(message,data)=>{
      messageApi.success(message)
    })

    messageApi.success('商家申请已提交，请等待审核')
  } catch (error) {
    validationErrors.value.push(error.message || '提交失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

// 查看商家后台
const viewMerchantDashboard = () => {
  messageApi.info('正在跳转到商家后台...')
}

// 查看商家信息
const viewMerchantInfo = () => {
  messageApi.info('查看商家信息功能')
}
</script>

<template>
  <div class="space-y-6">
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp">
      <div class="flex items-center justify-between mb-6">
        <div>
          <h3 class="text-lg font-light text-gray-900 dark:text-gray-100 mb-1">商家申请</h3>
          <p class="text-sm text-gray-500 dark:text-gray-400">申请成为落叶商城认证商家</p>
        </div>
        <div :class="[
          'px-3 py-1 rounded-full text-sm font-medium transition-all duration-300',
          merchantStatus === 'approved'
            ? 'bg-emerald-100 dark:bg-emerald-900 text-emerald-800 dark:text-emerald-200'
            : merchantStatus === 'pending'
            ? 'bg-amber-100 dark:bg-amber-900 text-amber-800 dark:text-amber-200'
            : 'bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-gray-200'
        ]">
          {{ getMerchantStatusText() }}
        </div>
      </div>

      <!-- 申请进度 -->
      <div v-if="merchantStatus !== 'none'" class="mb-8">
        <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">申请进度</h4>
        <div class="relative">
          <!-- 进度条 -->
          <div class="absolute top-5 left-0 right-0 h-1 bg-gray-200 dark:bg-gray-700"></div>
          <div class="absolute top-5 left-0 h-1 bg-gradient-to-r from-purple-500 to-fuchsia-500 transition-all duration-500"
               :style="{ width: getProgressWidth() }"></div>

          <!-- 进度节点 -->
          <div class="relative flex justify-between">
            <div v-for="(step, index) in merchantSteps" :key="step.id" class="flex flex-col items-center">
              <div :class="[
                'w-10 h-10 rounded-full flex items-center justify-center text-sm font-medium border-2 transition-all duration-300',
                step.completed
                  ? 'bg-purple-500 border-purple-500 text-white'
                  : step.active
                  ? 'border-purple-500 bg-white dark:bg-black text-purple-500'
                  : 'border-gray-300 dark:border-gray-600 bg-white dark:bg-black text-gray-400 dark:text-gray-500'
              ]">
                <i v-if="step.completed" class="fa fa-check"></i>
                <span v-else>{{ index + 1 }}</span>
              </div>
              <div class="mt-2 text-xs text-center">
                <div class="font-medium text-gray-700 dark:text-gray-300">{{ step.name }}</div>
                <div class="text-gray-500 dark:text-gray-400 mt-1">{{ step.description }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 申请表单 -->
      <MerchantForm
        v-if="merchantStatus === 'none'"
        :user-id="userInfo.id"
        :is-submitting="isSubmitting"
        :validation-errors="validationErrors"
        @submit="submitMerchantApplication"
      />

      <!-- 审核中状态 -->
      <MerchantPending
        v-else-if="merchantStatus === 'pending'"
        :submit-time="merchantApplication.submitTime"
        :progress-width="getProgressWidth()"
        :steps="merchantSteps"
      />

      <!-- 已通过状态 -->
      <MerchantApproved
        v-else-if="merchantStatus === 'approved'"
        @view-dashboard="viewMerchantDashboard"
        @view-info="viewMerchantInfo"
      />
    </div>

    <!-- 商家特权 -->
    <MerchantBenefits />
  </div>
</template>
