<!-- components/settings/merchant/MerchantPending.vue - 审核中状态组件 -->
<template>
  <div class="py-8">
    <div class="text-center mb-8">
      <div class="relative inline-block">
        <div class="w-24 h-24 mx-auto mb-6 rounded-full bg-gradient-to-r from-amber-100 to-amber-200 dark:from-amber-900/30 dark:to-amber-800/30 flex items-center justify-center">
          <i class="fa fa-clock text-4xl text-amber-500 animate-pulse"></i>
        </div>
        <div class="absolute -bottom-2 -right-2 w-8 h-8 rounded-full bg-amber-500 flex items-center justify-center text-white text-sm border-4 border-white dark:border-black">
          <i class="fa fa-hourglass-half"></i>
        </div>
      </div>
      <h4 class="text-xl font-light text-gray-900 dark:text-gray-100 mb-2">申请审核中</h4>
      <p class="text-gray-500 dark:text-gray-400 mb-4 max-w-md mx-auto">您的商家申请正在审核中，通常需要1-3个工作日，请耐心等待</p>
      <div class="inline-flex items-center gap-2 px-4 py-2 bg-gray-100 dark:bg-gray-900 rounded-full text-sm text-gray-600 dark:text-gray-400">
        <i class="fa fa-calendar-alt"></i>
        <span>申请时间：{{ formatDate(submitTime) }}</span>
      </div>
    </div>

    <!-- 审核流程图 -->
    <div class="max-w-2xl mx-auto mt-10">
      <!-- 进度条背景 -->
      <div class="relative mb-8">
        <div class="h-1.5 bg-gray-200 dark:bg-gray-800 rounded-full">
          <div
            class="h-1.5 bg-gradient-to-r from-purple-500 to-fuchsia-500 rounded-full transition-all duration-1000 ease-out"
            :style="{ width: progressWidth }"
          ></div>
        </div>
      </div>

      <!-- 步骤节点 -->
      <div class="relative flex justify-between">
        <div v-for="(step, index) in steps" :key="step.id" class="flex flex-col items-center flex-1">
          <!-- 节点图标 -->
          <div class="relative mb-3">
            <div
              :class="[
                'w-12 h-12 rounded-full flex items-center justify-center text-base font-medium transition-all duration-500',
                step.completed
                  ? 'bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white shadow-lg shadow-purple-500/30'
                  : step.active
                  ? 'bg-white dark:bg-black border-2 border-purple-500 text-purple-500'
                  : 'bg-gray-100 dark:bg-gray-800 text-gray-400 dark:text-gray-600'
              ]"
            >
              <i v-if="step.completed" class="fa fa-check"></i>
              <span v-else>{{ index + 1 }}</span>
            </div>
            <!-- 活跃状态光晕 -->
            <div v-if="step.active" class="absolute inset-0 rounded-full animate-ping bg-purple-400/30"></div>
          </div>

          <!-- 步骤名称 -->
          <div class="text-sm font-medium text-center mb-1" :class="step.completed || step.active ? 'text-gray-900 dark:text-gray-100' : 'text-gray-500 dark:text-gray-500'">
            {{ step.name }}
          </div>

          <!-- 步骤描述 -->
          <div class="text-xs text-center px-2" :class="step.completed || step.active ? 'text-gray-600 dark:text-gray-400' : 'text-gray-400 dark:text-gray-600'">
            {{ step.description }}
          </div>

          <!-- 预计时间 -->
          <div v-if="step.active" class="mt-2 text-xs text-purple-500 bg-purple-50 dark:bg-purple-950/30 px-2 py-1 rounded-full">
            预计1-2个工作日
          </div>
        </div>
      </div>
    </div>

    <!-- 温馨提示 -->
    <div class="mt-10 p-4 bg-blue-50 dark:bg-blue-950/30 rounded-lg border border-blue-200 dark:border-blue-800">
      <div class="flex items-start gap-3">
        <i class="fa fa-info-circle text-blue-500 mt-0.5"></i>
        <div>
          <p class="text-sm font-medium text-blue-800 dark:text-blue-300 mb-1">审核温馨提示</p>
          <ul class="text-xs text-blue-700 dark:text-blue-400 space-y-1 list-disc list-inside">
            <li>审核期间请保持电话畅通，审核人员可能会与您联系</li>
            <li>您可以在“我的申请”中查看实时审核进度</li>
            <li>审核通过后将有短信通知，请注意查收</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="flex justify-center gap-4 mt-8">
      <button @click="cancelApplication" class="px-6 py-2.5 border border-gray-300 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-colors text-sm">
        撤回申请
      </button>
      <button @click="viewApplicationDetail" class="px-6 py-2.5 bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white rounded-lg hover:from-purple-600 hover:to-fuchsia-600 transition-all text-sm">
        查看申请详情
      </button>
    </div>
  </div>
</template>

<script setup>
import { message } from 'ant-design-vue'

const props = defineProps({
  submitTime: {
    type: [Date, String],
    default: () => new Date()
  },
  progressWidth: {
    type: String,
    default: '33.33%'
  },
  steps: {
    type: Array,
    default: () => [
      { id: 'apply', name: '提交申请', description: '填写基本信息', completed: true, active: false },
      { id: 'review', name: '审核中', description: '平台审核资料', completed: false, active: true },
      { id: 'approve', name: '审核通过', description: '成为认证商家', completed: false, active: false }
    ]
  }
})

const [messageApi] = message.useMessage()

const formatDate = (date) => {
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const cancelApplication = () => {
  messageApi.info('撤回申请功能开发中')
}

const viewApplicationDetail = () => {
  messageApi.info('查看申请详情功能开发中')
}
</script>
