<!-- components/settings/NotificationSettings.vue - 通知设置组件 -->
<template>
  <div class="space-y-6">
    <!-- 通知设置卡片 -->
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp">
      <div class="flex items-center justify-between mb-6">
        <div>
          <h3 class="text-lg font-light text-gray-900 dark:text-gray-100 mb-1">通知设置</h3>
          <p class="text-sm text-gray-500 dark:text-gray-400">管理您的消息通知偏好</p>
        </div>
        <div class="flex items-center gap-2">
          <span class="text-xs text-gray-500 dark:text-gray-400">通知总开关</span>
          <button
            @click="toggleAllNotifications"
            :class="[
              'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
              notificationSettings.master ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
            ]"
          >
            <span
              :class="[
                'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                notificationSettings.master ? 'translate-x-5' : 'translate-x-1'
              ]"
            />
          </button>
        </div>
      </div>

      <!-- 通知类别列表 -->
      <div class="space-y-4">
        <!-- 订单通知 -->
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 transition-colors">
          <div class="flex items-start justify-between">
            <div class="flex items-start gap-3">
              <div class="w-10 h-10 rounded-lg bg-purple-100 dark:bg-purple-950/30 flex items-center justify-center">
                <i class="fa fa-shopping-bag text-purple-600 dark:text-purple-400"></i>
              </div>
              <div>
                <h4 class="font-medium text-gray-900 dark:text-gray-100 mb-1">订单通知</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">订单状态变更、支付结果、发货提醒等</p>
              </div>
            </div>
            <div class="flex items-center gap-4">
              <select
                v-model="notificationSettings.order.method"
                :disabled="!notificationSettings.master"
                class="text-sm bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg px-2 py-1 text-gray-700 dark:text-gray-300 focus:ring-1 focus:ring-purple-500"
              >
                <option value="all">全部接收</option>
                <option value="important">仅重要</option>
                <option value="none">不接收</option>
              </select>
              <button
                @click="toggleNotification('order')"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  notificationSettings.order.enabled && notificationSettings.master ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    notificationSettings.order.enabled && notificationSettings.master ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </div>

          <!-- 订单通知细分选项 -->
          <div v-if="notificationSettings.order.enabled && notificationSettings.master" class="mt-4 ml-13 pl-13 grid grid-cols-2 md:grid-cols-4 gap-3">
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.order.options.created" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>新订单</span>
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.order.options.paid" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>支付成功</span>
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.order.options.shipped" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>发货提醒</span>
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.order.options.delivered" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>确认收货</span>
            </label>
          </div>
        </div>

        <!-- 营销通知 -->
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 transition-colors">
          <div class="flex items-start justify-between">
            <div class="flex items-start gap-3">
              <div class="w-10 h-10 rounded-lg bg-emerald-100 dark:bg-emerald-950/30 flex items-center justify-center">
                <i class="fa fa-tag text-emerald-600 dark:text-emerald-400"></i>
              </div>
              <div>
                <h4 class="font-medium text-gray-900 dark:text-gray-100 mb-1">营销通知</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">促销活动、优惠券、新品推荐等</p>
              </div>
            </div>
            <div class="flex items-center gap-4">
              <select
                v-model="notificationSettings.marketing.method"
                :disabled="!notificationSettings.master"
                class="text-sm bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg px-2 py-1 text-gray-700 dark:text-gray-300 focus:ring-1 focus:ring-purple-500"
              >
                <option value="all">全部接收</option>
                <option value="important">仅重要</option>
                <option value="none">不接收</option>
              </select>
              <button
                @click="toggleNotification('marketing')"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  notificationSettings.marketing.enabled && notificationSettings.master ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    notificationSettings.marketing.enabled && notificationSettings.master ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </div>

          <!-- 营销通知细分选项 -->
          <div v-if="notificationSettings.marketing.enabled && notificationSettings.master" class="mt-4 ml-13 pl-13 grid grid-cols-2 md:grid-cols-3 gap-3">
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.marketing.options.promotion" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>促销活动</span>
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.marketing.options.coupon" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>优惠券</span>
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.marketing.options.newProduct" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>新品推荐</span>
            </label>
          </div>
        </div>

        <!-- 系统通知 -->
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 transition-colors">
          <div class="flex items-start justify-between">
            <div class="flex items-start gap-3">
              <div class="w-10 h-10 rounded-lg bg-amber-100 dark:bg-amber-950/30 flex items-center justify-center">
                <i class="fa fa-bell text-amber-600 dark:text-amber-400"></i>
              </div>
              <div>
                <h4 class="font-medium text-gray-900 dark:text-gray-100 mb-1">系统通知</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">系统更新、安全提醒、账号动态等</p>
              </div>
            </div>
            <div class="flex items-center gap-4">
              <select
                v-model="notificationSettings.system.method"
                :disabled="!notificationSettings.master"
                class="text-sm bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg px-2 py-1 text-gray-700 dark:text-gray-300 focus:ring-1 focus:ring-purple-500"
              >
                <option value="all">全部接收</option>
                <option value="important">仅重要</option>
                <option value="none">不接收</option>
              </select>
              <button
                @click="toggleNotification('system')"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  notificationSettings.system.enabled && notificationSettings.master ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    notificationSettings.system.enabled && notificationSettings.master ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </div>

          <!-- 系统通知细分选项 -->
          <div v-if="notificationSettings.system.enabled && notificationSettings.master" class="mt-4 ml-13 pl-13 grid grid-cols-2 md:grid-cols-3 gap-3">
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.system.options.update" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>系统更新</span>
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.system.options.security" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>安全提醒</span>
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.system.options.account" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>账号动态</span>
            </label>
          </div>
        </div>

        <!-- 互动通知 -->
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 transition-colors">
          <div class="flex items-start justify-between">
            <div class="flex items-start gap-3">
              <div class="w-10 h-10 rounded-lg bg-rose-100 dark:bg-rose-950/30 flex items-center justify-center">
                <i class="fa fa-heart text-rose-600 dark:text-rose-400"></i>
              </div>
              <div>
                <h4 class="font-medium text-gray-900 dark:text-gray-100 mb-1">互动通知</h4>
                <p class="text-xs text-gray-500 dark:text-gray-400">评论回复、点赞收藏、关注动态等</p>
              </div>
            </div>
            <div class="flex items-center gap-4">
              <select
                v-model="notificationSettings.interaction.method"
                :disabled="!notificationSettings.master"
                class="text-sm bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg px-2 py-1 text-gray-700 dark:text-gray-300 focus:ring-1 focus:ring-purple-500"
              >
                <option value="all">全部接收</option>
                <option value="important">仅重要</option>
                <option value="none">不接收</option>
              </select>
              <button
                @click="toggleNotification('interaction')"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  notificationSettings.interaction.enabled && notificationSettings.master ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    notificationSettings.interaction.enabled && notificationSettings.master ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </div>

          <!-- 互动通知细分选项 -->
          <div v-if="notificationSettings.interaction.enabled && notificationSettings.master" class="mt-4 ml-13 pl-13 grid grid-cols-2 md:grid-cols-3 gap-3">
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.interaction.options.comment" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>评论回复</span>
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.interaction.options.like" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>点赞收藏</span>
            </label>
            <label class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
              <input type="checkbox" v-model="notificationSettings.interaction.options.follow" class="rounded border-gray-300 text-purple-500 focus:ring-purple-500">
              <span>关注动态</span>
            </label>
          </div>
        </div>
      </div>
    </div>

    <!-- 通知方式设置 -->
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp" style="animation-delay: 0.1s">
      <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">通知方式</h4>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800">
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-2">
              <i class="fa fa-bell text-purple-500"></i>
              <span class="font-medium text-gray-700 dark:text-gray-300">站内通知</span>
            </div>
            <button
              @click="toggleChannel('site')"
              :class="[
                'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                notificationChannels.site ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
              ]"
            >
              <span
                :class="[
                  'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                  notificationChannels.site ? 'translate-x-5' : 'translate-x-1'
                ]"
              />
            </button>
          </div>
          <p class="text-xs text-gray-500 dark:text-gray-400">在网站内部接收通知</p>
        </div>

        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800">
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-2">
              <i class="fa fa-envelope text-purple-500"></i>
              <span class="font-medium text-gray-700 dark:text-gray-300">邮件通知</span>
            </div>
            <button
              @click="toggleChannel('email')"
              :class="[
                'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                notificationChannels.email ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
              ]"
            >
              <span
                :class="[
                  'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                  notificationChannels.email ? 'translate-x-5' : 'translate-x-1'
                ]"
              />
            </button>
          </div>
          <p class="text-xs text-gray-500 dark:text-gray-400">发送到绑定邮箱</p>
          <div v-if="notificationChannels.email" class="mt-2 text-xs text-gray-400">
            {{ userInfo.email }}
          </div>
        </div>

        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-800">
          <div class="flex items-center justify-between mb-3">
            <div class="flex items-center gap-2">
              <i class="fa fa-mobile-alt text-purple-500"></i>
              <span class="font-medium text-gray-700 dark:text-gray-300">短信通知</span>
            </div>
            <button
              @click="toggleChannel('sms')"
              :class="[
                'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                notificationChannels.sms ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
              ]"
            >
              <span
                :class="[
                  'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                  notificationChannels.sms ? 'translate-x-5' : 'translate-x-1'
                ]"
              />
            </button>
          </div>
          <p class="text-xs text-gray-500 dark:text-gray-400">手机短信提醒</p>
        </div>
      </div>
    </div>

    <!-- 安静时段设置 -->
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp" style="animation-delay: 0.2s">
      <div class="flex items-center justify-between mb-4">
        <h4 class="font-medium text-gray-700 dark:text-gray-300">安静时段</h4>
        <button
          @click="toggleQuietHours"
          :class="[
            'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
            quietHours.enabled ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
          ]"
        >
          <span
            :class="[
              'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
              quietHours.enabled ? 'translate-x-5' : 'translate-x-1'
            ]"
          />
        </button>
      </div>

      <div v-if="quietHours.enabled" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-400 mb-2">开始时间</label>
          <input
            type="time"
            v-model="quietHours.start"
            class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-black text-gray-900 dark:text-gray-100 focus:ring-1 focus:ring-purple-500"
          />
        </div>
        <div>
          <label class="block text-sm text-gray-600 dark:text-gray-400 mb-2">结束时间</label>
          <input
            type="time"
            v-model="quietHours.end"
            class="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-black text-gray-900 dark:text-gray-100 focus:ring-1 focus:ring-purple-500"
          />
        </div>
      </div>

      <p v-if="quietHours.enabled" class="mt-3 text-xs text-gray-500 dark:text-gray-400">
        在安静时段内，您将不会收到任何通知提醒
      </p>
    </div>

    <!-- 保存按钮 -->
    <div class="flex justify-end space-x-4 animate-fadeInUp" style="animation-delay: 0.3s">
      <button @click="resetNotificationSettings" class="px-6 py-3 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-colors">
        恢复默认
      </button>
      <button @click="saveNotificationSettings" class="px-6 py-3 bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white rounded-lg hover:from-purple-600 hover:to-fuchsia-600 shadow-lg hover:shadow-xl transition-all transform hover:-translate-y-0.5">
        <i class="fa fa-save mr-2"></i>保存设置
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

const [messageApi] = message.useMessage()

// 通知设置
const notificationSettings = reactive({
  master: true,
  order: {
    enabled: true,
    method: 'all',
    options: {
      created: true,
      paid: true,
      shipped: true,
      delivered: true
    }
  },
  marketing: {
    enabled: false,
    method: 'important',
    options: {
      promotion: true,
      coupon: true,
      newProduct: false
    }
  },
  system: {
    enabled: true,
    method: 'all',
    options: {
      update: true,
      security: true,
      account: true
    }
  },
  interaction: {
    enabled: true,
    method: 'all',
    options: {
      comment: true,
      like: true,
      follow: true
    }
  }
})

// 通知渠道
const notificationChannels = reactive({
  site: true,
  email: true,
  sms: false
})

// 安静时段
const quietHours = reactive({
  enabled: false,
  start: '22:00',
  end: '08:00'
})

// 切换通知类别
const toggleNotification = (type) => {
  if (!notificationSettings.master) return
  notificationSettings[type].enabled = !notificationSettings[type].enabled
}

// 切换总开关
const toggleAllNotifications = () => {
  notificationSettings.master = !notificationSettings.master
}

// 切换通知渠道
const toggleChannel = (channel) => {
  notificationChannels[channel] = !notificationChannels[channel]
}

// 切换安静时段
const toggleQuietHours = () => {
  quietHours.enabled = !quietHours.enabled
}

// 保存设置
const saveNotificationSettings = () => {
  const settings = {
    notificationSettings,
    notificationChannels,
    quietHours
  }
  localStorage.setItem('notification_settings', JSON.stringify(settings))
  messageApi.success({
    content: '通知设置已保存',
    icon: () => '✓'
  })
}

// 恢复默认
const resetNotificationSettings = () => {
  notificationSettings.master = true
  notificationSettings.order = {
    enabled: true,
    method: 'all',
    options: { created: true, paid: true, shipped: true, delivered: true }
  }
  notificationSettings.marketing = {
    enabled: false,
    method: 'important',
    options: { promotion: true, coupon: true, newProduct: false }
  }
  notificationSettings.system = {
    enabled: true,
    method: 'all',
    options: { update: true, security: true, account: true }
  }
  notificationSettings.interaction = {
    enabled: true,
    method: 'all',
    options: { comment: true, like: true, follow: true }
  }
  notificationChannels.site = true
  notificationChannels.email = true
  notificationChannels.sms = false
  quietHours.enabled = false
  quietHours.start = '22:00'
  quietHours.end = '08:00'

  messageApi.success('已恢复默认设置')
}
</script>
