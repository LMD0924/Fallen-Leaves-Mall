<!-- components/settings/SecuritySettings.vue - 安全设置组件 -->
<template>
  <div class="space-y-6">
    <!-- 账号安全卡片 -->
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp">
      <h3 class="text-lg font-light text-gray-900 dark:text-gray-100 mb-6">账号安全</h3>

      <!-- 安全评分 -->
      <div class="mb-8 p-4 bg-gradient-to-r from-purple-50 to-fuchsia-50 dark:from-purple-950/30 dark:to-fuchsia-950/30 rounded-lg">
        <div class="flex items-center justify-between mb-2">
          <span class="text-sm font-medium text-gray-700 dark:text-gray-300">账号安全评分</span>
          <span class="text-2xl font-light text-purple-600 dark:text-purple-400">{{ securityScore }}%</span>
        </div>
        <div class="w-full h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
          <div
            class="h-full bg-gradient-to-r from-purple-500 to-fuchsia-500 rounded-full transition-all duration-500"
            :style="{ width: securityScore + '%' }"
          ></div>
        </div>
        <p class="mt-2 text-xs text-gray-500 dark:text-gray-400">{{ securityAdvice }}</p>
      </div>

      <!-- 安全项目列表 -->
      <div class="space-y-4">
        <!-- 密码设置 -->
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 transition-all">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-purple-100 dark:bg-purple-950/30 flex items-center justify-center">
                <i class="fa fa-lock text-purple-600 dark:text-purple-400"></i>
              </div>
              <div>
                <h4 class="font-medium text-gray-900 dark:text-gray-100 mb-1">登录密码</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">最后修改：{{ passwordLastChanged }}</p>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <span class="text-xs px-2 py-1 bg-green-100 dark:bg-green-900 text-green-700 dark:text-green-300 rounded-full">强度：{{ passwordStrength }}</span>
              <button @click="openChangePasswordModal" class="text-purple-500 hover:text-purple-600 text-sm font-medium transition-colors">
                修改
              </button>
            </div>
          </div>
        </div>

        <!-- 双重验证 -->
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 transition-all">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-emerald-100 dark:bg-emerald-950/30 flex items-center justify-center">
                <i class="fa fa-shield-alt text-emerald-600 dark:text-emerald-400"></i>
              </div>
              <div>
                <h4 class="font-medium text-gray-900 dark:text-gray-100 mb-1">双重验证 (2FA)</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">为您的账号增加一层安全保护</p>
              </div>
            </div>
            <div class="flex items-center gap-3">
              <button
                @click="toggle2FA"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  twoFA.enabled ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    twoFA.enabled ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </div>
          <div v-if="twoFA.enabled" class="mt-3 ml-13 text-sm">
            <p class="text-gray-600 dark:text-gray-400 mb-2">已绑定设备：{{ twoFA.devices }} 台</p>
            <button @click="manage2FA" class="text-purple-500 hover:text-purple-600 text-sm">管理设备</button>
          </div>
        </div>

        <!-- 登录设备管理 -->
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 transition-all">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-amber-100 dark:bg-amber-950/30 flex items-center justify-center">
                <i class="fa fa-mobile-alt text-amber-600 dark:text-amber-400"></i>
              </div>
              <div>
                <h4 class="font-medium text-gray-900 dark:text-gray-100 mb-1">登录设备</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">当前有 {{ activeDevices }} 台设备处于活跃状态</p>
              </div>
            </div>
            <button @click="manageDevices" class="text-purple-500 hover:text-purple-600 text-sm font-medium">
              管理设备
            </button>
          </div>
        </div>

        <!-- 登录历史 -->
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 transition-all">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-rose-100 dark:bg-rose-950/30 flex items-center justify-center">
                <i class="fa fa-history text-rose-600 dark:text-rose-400"></i>
              </div>
              <div>
                <h4 class="font-medium text-gray-900 dark:text-gray-100 mb-1">登录历史</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">最近登录：{{ lastLogin }}</p>
              </div>
            </div>
            <button @click="viewLoginHistory" class="text-purple-500 hover:text-purple-600 text-sm font-medium">
              查看详情
            </button>
          </div>
        </div>

        <!-- 账号绑定 -->
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 transition-all">
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-lg bg-blue-100 dark:bg-blue-950/30 flex items-center justify-center">
                <i class="fa fa-link text-blue-600 dark:text-blue-400"></i>
              </div>
              <div>
                <h4 class="font-medium text-gray-900 dark:text-gray-100 mb-1">账号绑定</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">绑定第三方账号，方便快捷登录</p>
              </div>
            </div>
          </div>
          <div class="ml-13 space-y-3">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <i class="fab fa-weixin text-green-500"></i>
                <span class="text-sm text-gray-700 dark:text-gray-300">微信</span>
              </div>
              <button @click="bindWeChat" class="text-sm text-purple-500 hover:text-purple-600">
                {{ bindings.wechat ? '已绑定' : '立即绑定' }}
              </button>
            </div>
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <i class="fab fa-alipay text-blue-500"></i>
                <span class="text-sm text-gray-700 dark:text-gray-300">支付宝</span>
              </div>
              <button @click="bindAlipay" class="text-sm text-purple-500 hover:text-purple-600">
                {{ bindings.alipay ? '已绑定' : '立即绑定' }}
              </button>
            </div>
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <i class="fab fa-qq text-blue-400"></i>
                <span class="text-sm text-gray-700 dark:text-gray-300">QQ</span>
              </div>
              <button @click="bindQQ" class="text-sm text-purple-500 hover:text-purple-600">
                {{ bindings.qq ? '已绑定' : '立即绑定' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 近期活动 -->
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp" style="animation-delay: 0.1s">
      <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">近期安全活动</h4>
      <div class="space-y-3">
        <div v-for="(activity, index) in recentActivities" :key="index" class="flex items-start gap-3 text-sm">
          <div class="w-6 h-6 rounded-full flex items-center justify-center" :class="activity.iconBg">
            <i :class="['text-xs', activity.icon, activity.iconColor]"></i>
          </div>
          <div class="flex-1">
            <p class="text-gray-700 dark:text-gray-300">{{ activity.description }}</p>
            <p class="text-xs text-gray-500 dark:text-gray-400">{{ activity.time }}</p>
          </div>
          <span class="text-xs text-gray-500 dark:text-gray-400">{{ activity.location }}</span>
        </div>
      </div>
    </div>

    <!-- 修改密码模态框 -->
    <Modal v-model="showPasswordModal" title="修改密码" @ok="changePassword">
      <div class="space-y-4">
        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-400 mb-2">当前密码</label>
          <input type="password" v-model="passwordForm.current" class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-black">
        </div>
        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-400 mb-2">新密码</label>
          <input type="password" v-model="passwordForm.new" class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-black">
        </div>
        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-400 mb-2">确认新密码</label>
          <input type="password" v-model="passwordForm.confirm" class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-black">
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import {ref, computed, reactive} from 'vue'
import { message } from 'ant-design-vue'
import Modal from '@/components/common/Modal.vue'

const [messageApi] = message.useMessage()

// 安全评分计算
const securityScore = computed(() => {
  let score = 60
  if (passwordStrength.value === '强') score += 20
  if (twoFA.enabled) score += 15
  if (bindings.wechat || bindings.alipay || bindings.qq) score += 5
  return Math.min(score, 100)
})

const securityAdvice = computed(() => {
  if (securityScore.value >= 90) return '账号安全等级高，继续保持'
  if (securityScore.value >= 70) return '建议开启双重验证，提高安全性'
  return '建议设置强密码并开启双重验证'
})

// 密码相关
const passwordLastChanged = ref('2024-02-15')
const passwordStrength = ref('强')
const showPasswordModal = ref(false)
const passwordForm = reactive({
  current: '',
  new: '',
  confirm: ''
})

// 双重验证
const twoFA = reactive({
  enabled: false,
  devices: 0
})

// 设备信息
const activeDevices = ref(2)
const lastLogin = ref('2024-03-20 14:30')

// 账号绑定
const bindings = reactive({
  wechat: true,
  alipay: false,
  qq: false
})

// 近期活动
const recentActivities = ref([
  {
    description: '登录成功',
    time: '10分钟前',
    location: '北京',
    icon: 'fa fa-sign-in-alt',
    iconBg: 'bg-green-100 dark:bg-green-900',
    iconColor: 'text-green-600 dark:text-green-400'
  },
  {
    description: '修改密码',
    time: '2天前',
    location: '上海',
    icon: 'fa fa-lock',
    iconBg: 'bg-amber-100 dark:bg-amber-900',
    iconColor: 'text-amber-600 dark:text-amber-400'
  },
  {
    description: '新设备登录',
    time: '5天前',
    location: '广州',
    icon: 'fa fa-mobile-alt',
    iconBg: 'bg-purple-100 dark:bg-purple-900',
    iconColor: 'text-purple-600 dark:text-purple-400'
  }
])

// 方法
const openChangePasswordModal = () => {
  showPasswordModal.value = true
}

const changePassword = () => {
  if (passwordForm.new !== passwordForm.confirm) {
    messageApi.error('两次输入的密码不一致')
    return
  }
  messageApi.success('密码修改成功')
  showPasswordModal.value = false
}

const toggle2FA = () => {
  twoFA.enabled = !twoFA.enabled
  messageApi.info(twoFA.enabled ? '双重验证已开启' : '双重验证已关闭')
}

const manage2FA = () => {
  messageApi.info('设备管理功能开发中')
}

const manageDevices = () => {
  messageApi.info('设备管理功能开发中')
}

const viewLoginHistory = () => {
  messageApi.info('登录历史功能开发中')
}

const bindWeChat = () => {
  bindings.wechat = !bindings.wechat
  messageApi.info(bindings.wechat ? '微信绑定成功' : '微信已解绑')
}

const bindAlipay = () => {
  bindings.alipay = !bindings.alipay
  messageApi.info(bindings.alipay ? '支付宝绑定成功' : '支付宝已解绑')
}

const bindQQ = () => {
  bindings.qq = !bindings.qq
  messageApi.info(bindings.qq ? 'QQ绑定成功' : 'QQ已解绑')
}
</script>
