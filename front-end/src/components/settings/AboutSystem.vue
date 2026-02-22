<!-- components/settings/AboutSystem.vue - 关于系统组件 -->
<template>
  <div class="space-y-6">
    <!-- 关于系统卡片 -->
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp">
      <div class="text-center mb-8">
        <div class="w-20 h-20 mx-auto mb-4 rounded-full bg-gradient-to-r from-purple-500 to-fuchsia-500 flex items-center justify-center">
          <span class="text-3xl font-bold text-white">叶</span>
        </div>
        <h3 class="text-2xl font-light text-gray-900 dark:text-gray-100 mb-2">落叶商城</h3>
        <p class="text-sm text-gray-500 dark:text-gray-400">版本 {{ version }}</p>
      </div>

      <div class="space-y-4 mb-8">
        <div class="p-4 rounded-lg bg-gray-50 dark:bg-gray-900">
          <div class="flex justify-between mb-2">
            <span class="text-sm text-gray-600 dark:text-gray-400">构建版本</span>
            <span class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ buildVersion }}</span>
          </div>
          <div class="flex justify-between mb-2">
            <span class="text-sm text-gray-600 dark:text-gray-400">最后更新</span>
            <span class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ lastUpdate }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-sm text-gray-600 dark:text-gray-400">环境</span>
            <span class="text-sm font-medium text-gray-900 dark:text-gray-100">{{ environment }}</span>
          </div>
        </div>
      </div>

      <!-- 功能特性 -->
      <div class="mb-8">
        <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">功能特性</h4>
        <div class="grid grid-cols-2 gap-3">
          <div v-for="feature in features" :key="feature.name" class="p-3 rounded-lg border border-gray-200 dark:border-gray-800">
            <i :class="['text-lg mb-2', feature.icon, feature.iconColor]"></i>
            <p class="text-sm font-medium text-gray-800 dark:text-gray-200">{{ feature.name }}</p>
            <p class="text-xs text-gray-500 dark:text-gray-400">{{ feature.description }}</p>
          </div>
        </div>
      </div>

      <!-- 技术栈 -->
      <div class="mb-8">
        <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">技术栈</h4>
        <div class="flex flex-wrap gap-2">
          <span v-for="tech in techStack" :key="tech" class="px-3 py-1 text-xs bg-gray-100 dark:bg-gray-900 text-gray-700 dark:text-gray-300 rounded-full">
            {{ tech }}
          </span>
        </div>
      </div>

      <!-- 团队信息 -->
      <div class="mb-8 p-4 rounded-lg bg-gradient-to-r from-purple-50 to-fuchsia-50 dark:from-purple-950/30 dark:to-fuchsia-950/30">
        <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-3">开发团队</h4>
        <p class="text-sm text-gray-600 dark:text-gray-400 mb-2">落叶科技 · 前端团队</p>
        <p class="text-xs text-gray-500 dark:text-gray-400">致力于打造极致的购物体验</p>
      </div>

      <!-- 相关链接 -->
      <div class="grid grid-cols-2 gap-4">
        <a href="#" class="text-center p-3 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 transition-colors">
          <i class="fa fa-file-alt text-purple-500 mb-1"></i>
          <p class="text-sm text-gray-700 dark:text-gray-300">用户协议</p>
        </a>
        <a href="#" class="text-center p-3 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 transition-colors">
          <i class="fa fa-shield-alt text-purple-500 mb-1"></i>
          <p class="text-sm text-gray-700 dark:text-gray-300">隐私政策</p>
        </a>
        <a href="#" class="text-center p-3 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 transition-colors">
          <i class="fa fa-clipboard-list text-purple-500 mb-1"></i>
          <p class="text-sm text-gray-700 dark:text-gray-300">更新日志</p>
        </a>
        <a href="#" class="text-center p-3 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 transition-colors">
          <i class="fa fa-headset text-purple-500 mb-1"></i>
          <p class="text-sm text-gray-700 dark:text-gray-300">联系客服</p>
        </a>
      </div>
    </div>

    <!-- 检查更新 -->
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp" style="animation-delay: 0.1s">
      <div class="flex items-center justify-between">
        <div>
          <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-1">检查更新</h4>
          <p class="text-xs text-gray-500 dark:text-gray-400">当前为最新版本</p>
        </div>
        <button @click="checkForUpdates" class="px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600 transition-colors text-sm">
          检查更新
        </button>
      </div>
    </div>

    <!-- 版权信息 -->
    <div class="text-center text-xs text-gray-500 dark:text-gray-400 animate-fadeInUp" style="animation-delay: 0.2s">
      <p>© 2024 落叶科技 版权所有</p>
      <p class="mt-1">ICP证：京ICP备xxxxxx号</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { message } from 'ant-design-vue'

const [messageApi] = message.useMessage()

// 版本信息
const version = ref('2.5.0')
const buildVersion = ref('2024.0320.1450')
const lastUpdate = ref('2024-03-20')
const environment = ref('生产环境')

// 功能特性
const features = ref([
  { name: '主题定制', description: '个性化界面设置', icon: 'fa fa-palette', iconColor: 'text-purple-500' },
  { name: '商家入驻', description: '快速成为商家', icon: 'fa fa-store', iconColor: 'text-emerald-500' },
  { name: '智能推荐', description: '个性化商品推荐', icon: 'fa fa-robot', iconColor: 'text-blue-500' },
  { name: '多端同步', description: '数据云端同步', icon: 'fa fa-sync', iconColor: 'text-amber-500' },
  { name: '安全支付', description: '多重安全保障', icon: 'fa fa-shield', iconColor: 'text-rose-500' },
  { name: '24h客服', description: '全天候在线支持', icon: 'fa fa-headset', iconColor: 'text-indigo-500' }
])

// 技术栈
const techStack = ref([
  'Vue 3', 'Tailwind CSS', 'Pinia', 'Vite', 'Ant Design Vue', 'Node.js', 'Express', 'MySQL', 'Redis'
])

// 检查更新
const checkForUpdates = () => {
  messageApi.info('正在检查更新...')
  setTimeout(() => {
    messageApi.success('当前已是最新版本')
  }, 2000)
}
</script>
