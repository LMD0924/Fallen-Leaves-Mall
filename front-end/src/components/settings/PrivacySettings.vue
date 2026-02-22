<!-- components/settings/PrivacySettings.vue - 隐私设置组件 -->
<template>
  <div class="space-y-6">
    <!-- 隐私设置卡片 -->
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp">
      <h3 class="text-lg font-light text-gray-900 dark:text-gray-100 mb-6">隐私设置</h3>

      <!-- 个人资料可见性 -->
      <div class="mb-8">
        <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">个人资料可见性</h4>
        <div class="space-y-4">
          <div class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">头像</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">控制谁可以看到您的头像</p>
            </div>
            <select v-model="privacy.avatar" class="text-sm bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg px-3 py-1.5">
              <option value="public">所有人</option>
              <option value="friends">仅好友</option>
              <option value="private">仅自己</option>
            </select>
          </div>

          <div class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">昵称</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">控制谁可以看到您的昵称</p>
            </div>
            <select v-model="privacy.nickname" class="text-sm bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg px-3 py-1.5">
              <option value="public">所有人</option>
              <option value="friends">仅好友</option>
              <option value="private">仅自己</option>
            </select>
          </div>

          <div class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">联系方式</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">控制谁可以看到您的手机号和邮箱</p>
            </div>
            <select v-model="privacy.contact" class="text-sm bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg px-3 py-1.5">
              <option value="public">所有人</option>
              <option value="friends">仅好友</option>
              <option value="private">仅自己</option>
            </select>
          </div>

          <div class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">订单记录</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">控制谁可以看到您的购物订单</p>
            </div>
            <select v-model="privacy.orders" class="text-sm bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg px-3 py-1.5">
              <option value="public">所有人</option>
              <option value="friends">仅好友</option>
              <option value="private">仅自己</option>
            </select>
          </div>

          <div class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">收藏列表</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">控制谁可以看到您的收藏商品</p>
            </div>
            <select v-model="privacy.favorites" class="text-sm bg-transparent border border-gray-300 dark:border-gray-700 rounded-lg px-3 py-1.5">
              <option value="public">所有人</option>
              <option value="friends">仅好友</option>
              <option value="private">仅自己</option>
            </select>
          </div>
        </div>
      </div>

      <!-- 数据使用偏好 -->
      <div class="mb-8">
        <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">数据使用偏好</h4>
        <div class="space-y-3">
          <label class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800 cursor-pointer">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">个性化推荐</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">根据您的浏览和购买记录推荐商品</p>
            </div>
            <div class="flex items-center">
              <button
                @click="togglePreference('personalization')"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  preferences.personalization ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    preferences.personalization ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </label>

          <label class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800 cursor-pointer">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">行为分析</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">允许我们分析您的使用行为以改进服务</p>
            </div>
            <div class="flex items-center">
              <button
                @click="togglePreference('analytics')"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  preferences.analytics ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    preferences.analytics ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </label>

          <label class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800 cursor-pointer">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">第三方数据共享</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">允许将您的数据用于广告定向投放</p>
            </div>
            <div class="flex items-center">
              <button
                @click="togglePreference('sharing')"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  preferences.sharing ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    preferences.sharing ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </label>
        </div>
      </div>

      <!-- 隐私控制 -->
      <div class="mb-8">
        <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">隐私控制</h4>
        <div class="space-y-3">
          <label class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800 cursor-pointer">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">在线状态</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">向好友显示您的在线状态</p>
            </div>
            <div class="flex items-center">
              <button
                @click="toggleControl('onlineStatus')"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  controls.onlineStatus ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    controls.onlineStatus ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </label>

          <label class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800 cursor-pointer">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">搜索可见性</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">允许通过手机号搜索到您的账号</p>
            </div>
            <div class="flex items-center">
              <button
                @click="toggleControl('searchable')"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  controls.searchable ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    controls.searchable ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </label>

          <label class="flex items-center justify-between p-3 rounded-lg border border-gray-200 dark:border-gray-800 cursor-pointer">
            <div>
              <p class="font-medium text-gray-800 dark:text-gray-200">评论权限</p>
              <p class="text-xs text-gray-500 dark:text-gray-400">允许未关注的人评论您的动态</p>
            </div>
            <div class="flex items-center">
              <button
                @click="toggleControl('comment')"
                :class="[
                  'relative inline-flex h-5 w-9 items-center rounded-full transition-colors duration-300',
                  controls.comment ? 'bg-purple-500' : 'bg-gray-300 dark:bg-gray-600'
                ]"
              >
                <span
                  :class="[
                    'inline-block h-3 w-3 transform rounded-full bg-white transition-transform duration-300',
                    controls.comment ? 'translate-x-5' : 'translate-x-1'
                  ]"
                />
              </button>
            </div>
          </label>
        </div>
      </div>

      <!-- 数据管理 -->
      <div>
        <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">数据管理</h4>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <button @click="downloadData" class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 transition-all text-left group">
            <i class="fa fa-download text-purple-500 text-xl mb-2 group-hover:scale-110 transition-transform"></i>
            <p class="font-medium text-gray-800 dark:text-gray-200 mb-1">下载我的数据</p>
            <p class="text-xs text-gray-500 dark:text-gray-400">获取您的账号数据副本</p>
          </button>
          <button @click="clearHistory" class="p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 transition-all text-left group">
            <i class="fa fa-trash-alt text-rose-500 text-xl mb-2 group-hover:scale-110 transition-transform"></i>
            <p class="font-medium text-gray-800 dark:text-gray-200 mb-1">清除历史记录</p>
            <p class="text-xs text-gray-500 dark:text-gray-400">清空浏览和搜索历史</p>
          </button>
        </div>
      </div>
    </div>

    <!-- 保存按钮 -->
    <div class="flex justify-end space-x-4 animate-fadeInUp" style="animation-delay: 0.1s">
      <button @click="resetPrivacySettings" class="px-6 py-3 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-colors">
        恢复默认
      </button>
      <button @click="savePrivacySettings" class="px-6 py-3 bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white rounded-lg hover:from-purple-600 hover:to-fuchsia-600 shadow-lg hover:shadow-xl transition-all transform hover:-translate-y-0.5">
        <i class="fa fa-save mr-2"></i>保存设置
      </button>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { message } from 'ant-design-vue'

const [messageApi] = message.useMessage()

// 隐私设置
const privacy = reactive({
  avatar: 'public',
  nickname: 'public',
  contact: 'private',
  orders: 'private',
  favorites: 'friends'
})

// 数据使用偏好
const preferences = reactive({
  personalization: true,
  analytics: true,
  sharing: false
})

// 隐私控制
const controls = reactive({
  onlineStatus: true,
  searchable: true,
  comment: true
})

// 切换偏好
const togglePreference = (key) => {
  preferences[key] = !preferences[key]
}

// 切换控制
const toggleControl = (key) => {
  controls[key] = !controls[key]
}

// 下载数据
const downloadData = () => {
  messageApi.info('正在准备您的数据，请稍候...')
  setTimeout(() => {
    messageApi.success('数据下载链接已发送到您的邮箱')
  }, 2000)
}

// 清除历史
const clearHistory = () => {
  messageApi.info('正在清除历史记录...')
  setTimeout(() => {
    messageApi.success('历史记录已清除')
  }, 1500)
}

// 保存设置
const savePrivacySettings = () => {
  const settings = {
    privacy,
    preferences,
    controls
  }
  localStorage.setItem('privacy_settings', JSON.stringify(settings))
  messageApi.success('隐私设置已保存')
}

// 恢复默认
const resetPrivacySettings = () => {
  privacy.avatar = 'public'
  privacy.nickname = 'public'
  privacy.contact = 'private'
  privacy.orders = 'private'
  privacy.favorites = 'friends'
  preferences.personalization = true
  preferences.analytics = true
  preferences.sharing = false
  controls.onlineStatus = true
  controls.searchable = true
  controls.comment = true
  messageApi.success('已恢复默认设置')
}
</script>
