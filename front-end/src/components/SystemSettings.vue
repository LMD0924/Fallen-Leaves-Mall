<!-- SystemSettings.vue - 主页面 -->
<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { useRouter } from 'vue-router'
import { message } from "ant-design-vue"
import tokenManager from '@/utils/tokenManager'
import { get } from "@/net/index.js"

// 导入子组件
import ThemeSettings from '@/components/settings/ThemeSettings.vue'
import MerchantApplication from '@/components/settings/MerchantApplication.vue'
import NotificationSettings from '@/components/settings/NotificationSettings.vue'
import SecuritySettings from '@/components/settings/SecuritySettings.vue'
import PrivacySettings from '@/components/settings/PrivacySettings.vue'
import AboutSystem from '@/components/settings/AboutSystem.vue'
import MerchantStatusQuery from "@/components/settings/merchant/MerchantStatusQuery.vue";

const router = useRouter()
const themeStore = useThemeStore()
const [messageApi, contextHolder] = message.useMessage();

// 当前激活的标签
const activeTab = ref('theme')

// 导航标签
const tabs = [
  { id: 'theme', name: '主题设置', icon: 'fa fa-palette' },
  { id: 'merchant', name: '商家申请', icon: 'fa fa-store', badge: null },
  { id: 'status', name: '申请状态', icon: 'fa fa-store'},
  { id: 'notifications', name: '通知设置', icon: 'fa fa-bell' },
  { id: 'security', name: '账号安全', icon: 'fa fa-shield-alt' },
  { id: 'privacy', name: '隐私设置', icon: 'fa fa-lock' },
  { id: 'about', name: '关于系统', icon: 'fa fa-info-circle' }
]

// 用户信息
const userInfo = reactive({
  id: null,
  username: '加载中...',
  email: '加载中...',
  vipLevel: 1,
  balance: 0
})

// 商家状态
const merchantStatus = ref('none')
const merchantApplication = reactive({
  submitTime: new Date()
})

// 当前组件映射
const currentComponent = computed(() => {
  const map = {
    theme: ThemeSettings,
    merchant: MerchantApplication,
    notifications: NotificationSettings,
    security: SecuritySettings,
    privacy: PrivacySettings,
    about: AboutSystem,
    status: MerchantStatusQuery
  }
  return map[activeTab.value]
})

// 格式化金额
const formatMoney = (amount) => {
  return amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const token = tokenManager.getAccessToken()
    if (!token) {
      await router.push('/')
      return
    }

    await get('api/user/selectUserById', {},
      (message, data) => {
        Object.assign(userInfo, data)
        checkMerchantStatus(data.id)
      },
      (message) => {
        messageApi.error(message || '获取用户信息失败')
        router.push('/')
      },
      () => {
        messageApi.error('网络错误，请稍后重试')
      }
    )
  } catch (error) {
    console.error('获取用户信息异常:', error)
    messageApi.error('获取用户信息失败')
    await router.push('/')
  }
}

// 检查商家状态
const checkMerchantStatus = async (userId) => {
  try {
    // 模拟API调用
    const mockStatus = localStorage.getItem(`merchant_status_${userId}`) || 'none'
    merchantStatus.value = mockStatus

    if (mockStatus === 'pending' || mockStatus === 'approved') {
      const savedApplication = localStorage.getItem(`merchant_application_${userId}`)
      if (savedApplication) {
        const appData = JSON.parse(savedApplication)
        merchantApplication.submitTime = new Date(appData.submitTime)
      }
    }

    // 更新导航徽章
    const merchantTab = tabs.find(tab => tab.id === 'merchant')
    if (merchantTab) {
      merchantTab.badge = mockStatus === 'none' ? '新' : null
    }
  } catch (error) {
    console.error('检查商家状态失败:', error)
  }
}

// 初始化
onMounted(() => {
  fetchUserInfo()
})
</script>

<template>
  <contextHolder />
  <div class="min-h-screen bg-gray-50 dark:bg-black text-gray-900 dark:text-gray-100 transition-colors duration-300">
    <!-- 顶部导航 - 紫色点缀玻璃态 -->
    <nav class="sticky top-0 z-50 bg-white/70 dark:bg-black/70 backdrop-blur-xl border-b border-gray-200/50 dark:border-gray-800/50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center gap-2">
            <div class="w-2 h-2 rounded-full bg-purple-500 animate-pulse"></div>
            <span class="text-xl font-light tracking-tight text-gray-900 dark:text-gray-100">
              <span class="font-medium">系统</span>设置
            </span>
          </div>
          <div class="flex items-center space-x-4">
            <router-link to="/User" class="flex items-center text-gray-600 dark:text-gray-300 hover:text-purple-600 dark:hover:text-purple-400 transition-colors group">
              <i class="fa fa-arrow-left mr-2 group-hover:-translate-x-1 transition-transform"></i>
              返回首页
            </router-link>
          </div>
        </div>
      </div>
    </nav>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 设置卡片 -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- 左侧：设置导航 -->
        <div class="lg:col-span-1">
          <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 sticky top-24 border border-gray-100 dark:border-gray-800">
            <h2 class="text-lg font-light mb-6 text-gray-900 dark:text-gray-100">设置</h2>
            <nav class="space-y-2">
              <button
                v-for="tab in tabs"
                :key="tab.id"
                @click="activeTab = tab.id"
                :class="[
                  'w-full flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-all duration-300',
                  activeTab === tab.id
                    ? 'bg-purple-50 dark:bg-purple-950/30 text-purple-600 dark:text-purple-400 border-l-4 border-purple-500'
                    : 'hover:bg-gray-100 dark:hover:bg-gray-900 text-gray-600 dark:text-gray-400'
                ]"
              >
                <i :class="['mr-3', tab.icon]"></i>
                {{ tab.name }}
                <span v-if="tab.badge" class="ml-auto bg-purple-100 dark:bg-purple-900 text-purple-800 dark:text-purple-200 text-xs px-2 py-1 rounded-full animate-pulse">
                  {{ tab.badge }}
                </span>
              </button>
            </nav>

            <!-- 用户信息卡片 -->
            <div class="mt-8 pt-6 border-t border-gray-200 dark:border-gray-800">
              <div class="flex items-center">
                <div class="w-12 h-12 rounded-full bg-gradient-to-r from-purple-500 to-fuchsia-500 flex items-center justify-center text-white font-bold text-lg shadow-md">
                  {{ userInfo.username?.charAt(0) || 'U' }}
                </div>
                <div class="ml-4">
                  <h3 class="font-medium text-gray-900 dark:text-gray-100">{{ userInfo.username }}</h3>
                  <p class="text-sm text-gray-500 dark:text-gray-400">{{ userInfo.email }}</p>
                </div>
              </div>
              <div class="mt-4 grid grid-cols-2 gap-2 text-sm">
                <div class="text-center p-2 bg-gray-100 dark:bg-gray-900 rounded-lg">
                  <div class="text-gray-600 dark:text-gray-400">会员等级</div>
                  <div class="font-semibold text-purple-600 dark:text-purple-400">VIP{{ userInfo.vipLevel }}</div>
                </div>
                <div class="text-center p-2 bg-gray-100 dark:bg-gray-900 rounded-lg">
                  <div class="text-gray-600 dark:text-gray-400">账户余额</div>
                  <div class="font-semibold text-emerald-600 dark:text-emerald-400">¥{{ formatMoney(userInfo.balance) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：动态组件切换 -->
        <div class="lg:col-span-2">
          <transition
            enter-active-class="transition duration-300 ease-out"
            enter-from-class="opacity-0 translate-x-4"
            enter-to-class="opacity-100 translate-x-0"
            leave-active-class="transition duration-200 ease-in"
            leave-from-class="opacity-100 translate-x-0"
            leave-to-class="opacity-0 -translate-x-4"
            mode="out-in"
          >
            <component
              :is="currentComponent"
              :user-info="userInfo"
              :merchant-status="merchantStatus"
              :merchant-application="merchantApplication"
              @update:merchant-status="merchantStatus = $event"
              @update:application="merchantApplication = $event"
            />
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

