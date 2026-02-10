<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

// 响应式数据
const activeTab = ref('theme')
const isDarkMode = ref(themeStore.isDark)
const borderRadius = ref(8)
const selectedFont = ref({ id: 'inter', name: 'Inter', family: 'Inter, sans-serif', description: '现代简洁字体' })
const isSubmitting = ref(false)
const validationErrors = ref([])

// 用户信息
const userInfo = reactive({
  username: '落叶用户',
  email: 'user@luoye.com',
  vipLevel: 3,
  balance: 1588.50
})

// 商家状态
const merchantStatus = ref('none') // none, pending, approved, rejected
const merchantApplication = reactive({
  submitTime: new Date('2024-02-05')
})

// 商家表单
const merchantForm = reactive({
  userId: userInfo.id, // 从用户信息获取
  merchantType: '', // 商家类型
  merchantName: '', // 商家名称
  contactName: '', // 联系人姓名
  contactPhone: '', // 联系人电话
  contactEmail: '', // 联系人邮箱
  businessLicense: '', // 营业执照号
  licenseImage: '', // 营业执照图片（注意字段名改为小写）
  idCard: '', // 身份证号
  idCardFront: '', // 身份证正面图片
  idCardBack: '', // 身份证背面图片
  agreed: false // 是否同意协议
})

// 导航标签
const tabs = [
  { id: 'theme', name: '主题设置', icon: 'fa fa-palette' },
  { id: 'merchant', name: '商家申请', icon: 'fa fa-store', badge: merchantStatus.value === 'none' ? '新' : null },
  { id: 'notifications', name: '通知设置', icon: 'fa fa-bell' },
  { id: 'security', name: '账号安全', icon: 'fa fa-shield-alt' },
  { id: 'privacy', name: '隐私设置', icon: 'fa fa-lock' },
  { id: 'about', name: '关于系统', icon: 'fa fa-info-circle' }
]

// 主题颜色
const themeColors = ref([
  {
    id: 'blue',
    name: '蓝色主题',
    description: '官方默认主题',
    primary: '#3B82F6',
    secondary: '#8B5CF6',
    accent: '#06B6D4'
  },
  {
    id: 'green',
    name: '绿色主题',
    description: '清新自然',
    primary: '#10B981',
    secondary: '#059669',
    accent: '#34D399'
  },
  {
    id: 'purple',
    name: '紫色主题',
    description: '优雅神秘',
    primary: '#8B5CF6',
    secondary: '#A78BFA',
    accent: '#D946EF'
  },
  {
    id: 'red',
    name: '红色主题',
    description: '热情活力',
    primary: '#EF4444',
    secondary: '#F87171',
    accent: '#FB7185'
  }
])

const selectedTheme = ref(themeColors.value[0])

// 字体选项
const fonts = ref([
  { id: 'inter', name: 'Inter', family: 'Inter, sans-serif', description: '现代简洁字体' },
  { id: 'system', name: '系统字体', family: 'system-ui, sans-serif', description: '使用系统默认字体' },
  { id: 'georgia', name: 'Georgia', family: 'Georgia, serif', description: '优雅衬线字体' },
  { id: 'mono', name: '等宽字体', family: 'monospace', description: '代码风格字体' }
])

// 商家申请步骤
const merchantSteps = computed(() => {
  const steps = [
    { id: 'apply', name: '提交申请', description: '填写基本信息' },
    { id: 'review', name: '审核中', description: '平台审核资料' },
    { id: 'approve', name: '审核通过', description: '成为认证商家' }
  ]

  // 根据状态更新步骤状态
  steps.forEach((step, index) => {
    if (merchantStatus.value === 'none') {
      step.completed = false
      step.active = index === 0
    } else if (merchantStatus.value === 'pending') {
      step.completed = index < 2
      step.active = index === 1
    } else if (merchantStatus.value === 'approved') {
      step.completed = true
      step.active = false
    }
  })

  return steps
})

// 商家特权
const merchantBenefits = ref([
  {
    id: 1,
    title: '免费店铺',
    description: '免费创建专属店铺页面，展示商品信息',
    icon: 'fa fa-store',
    bgColor: 'bg-blue-100 dark:bg-blue-900/30',
    textColor: 'text-blue-600 dark:text-blue-400'
  },
  {
    id: 2,
    title: '低佣金率',
    description: '享受远低于市场平均的佣金费率',
    icon: 'fa fa-percentage',
    bgColor: 'bg-green-100 dark:bg-green-900/30',
    textColor: 'text-green-600 dark:text-green-400'
  },
  {
    id: 3,
    title: '营销工具',
    description: '使用丰富的营销工具推广商品',
    icon: 'fa fa-bullhorn',
    bgColor: 'bg-purple-100 dark:bg-purple-900/30',
    textColor: 'text-purple-600 dark:text-purple-400'
  },
  {
    id: 4,
    title: '数据分析',
    description: '查看详细的店铺数据和分析报告',
    icon: 'fa fa-chart-line',
    bgColor: 'bg-yellow-100 dark:bg-yellow-900/30',
    textColor: 'text-yellow-600 dark:text-yellow-400'
  },
  {
    id: 5,
    title: '优先客服',
    description: '享受24小时优先客服支持',
    icon: 'fa fa-headset',
    bgColor: 'bg-red-100 dark:bg-red-900/30',
    textColor: 'text-red-600 dark:text-red-400'
  },
  {
    id: 6,
    title: '促销活动',
    description: '参与平台大型促销活动',
    icon: 'fa fa-gift',
    bgColor: 'bg-pink-100 dark:bg-pink-900/30',
    textColor: 'text-pink-600 dark:text-pink-400'
  }
])

// 计算属性
const canSubmitApplication = computed(() => {
  // 验证必需字段
  return (
    merchantForm.merchantName &&
    merchantForm.merchantType &&
    merchantForm.contactName &&
    merchantForm.contactPhone &&
    merchantForm.businessLicense &&
    merchantForm.idCard &&
    merchantForm.agreed
  )
})

// 表单验证方法
const validateForm = () => {
  validationErrors.value = []

  if (!merchantForm.merchantName) {
    validationErrors.value.push('商家名称不能为空')
  }

  if (!merchantForm.merchantType) {
    validationErrors.value.push('请选择商家类型')
  }

  if (!merchantForm.contactName) {
    validationErrors.value.push('联系人姓名不能为空')
  }

  if (!merchantForm.contactPhone) {
    validationErrors.value.push('联系人电话不能为空')
  } else if (!/^1[3-9]\d{9}$/.test(merchantForm.contactPhone)) {
    validationErrors.value.push('请输入正确的手机号码')
  }

  if (merchantForm.contactEmail && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(merchantForm.contactEmail)) {
    validationErrors.value.push('请输入正确的邮箱格式')
  }

  if (!merchantForm.businessLicense) {
    validationErrors.value.push('营业执照号不能为空')
  }

  if (!merchantForm.idCard) {
    validationErrors.value.push('身份证号不能为空')
  } else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(merchantForm.idCard)) {
    validationErrors.value.push('请输入正确的身份证号')
  }

  if (!merchantForm.agreed) {
    validationErrors.value.push('请同意商家入驻协议和服务条款')
  }

  return validationErrors.value.length === 0
}

// 提交商家申请
const submitMerchantApplication = async () => {
  if (!validateForm()) {
    return
  }

  isSubmitting.value = true
  validationErrors.value = []

  try {
    // 构建请求数据
    const requestData = {
      userId: merchantForm.userId,
      merchantType: merchantForm.merchantType,
      merchantName: merchantForm.merchantName,
      contactName: merchantForm.contactName,
      contactPhone: merchantForm.contactPhone,
      contactEmail: merchantForm.contactEmail || null,
      businessLicense: merchantForm.businessLicense,
      licenseImage: merchantForm.licenseImage || null,
      idCard: merchantForm.idCard,
      idCardFront: merchantForm.idCardFront || null,
      idCardBack: merchantForm.idCardBack || null
    }

    // 调用API（示例）
    // const response = await applyMerchant(requestData)

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))

    // 模拟成功响应
    merchantStatus.value = 'pending'
    merchantApplication.submitTime = new Date()

    // 更新导航标签
    const merchantTab = tabs.find(tab => tab.id === 'merchant')
    if (merchantTab) merchantTab.badge = null

    // 重置表单
    Object.keys(merchantForm).forEach(key => {
      if (key !== 'userId' && key !== 'agreed') merchantForm[key] = ''
    })
    merchantForm.agreed = false

    // 显示成功消息
    alert('商家申请已提交，请等待审核')

  } catch (error) {
    console.error('提交失败:', error)
    validationErrors.value.push(error.message || '提交失败，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

// 处理图片上传
const handleLicenseImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (file.size > 5 * 1024 * 1024) {
      alert('文件大小不能超过5MB')
      return
    }
    // 这里应该上传到服务器并获取URL
    merchantForm.licenseImage = '上传成功：' + file.name
    console.log('上传营业执照图片:', file.name)
  }
}

// 检查商家状态
const checkMerchantStatus = async () => {
  try {
    // 调用API检查商家状态
    // const response = await getMerchantStatus(userInfo.id)
    // merchantStatus.value = response.status || 'none'

    // 模拟API调用
    const mockStatus = localStorage.getItem('merchant_status') || 'none'
    merchantStatus.value = mockStatus

    if (merchantStatus.value === 'pending' || merchantStatus.value === 'approved') {
      // 加载申请信息
      const savedApplication = localStorage.getItem('merchant_application')
      if (savedApplication) {
        const appData = JSON.parse(savedApplication)
        merchantApplication.submitTime = new Date(appData.submitTime)
      }
    }
  } catch (error) {
    console.error('检查商家状态失败:', error)
  }
}

const handleIdCardFrontUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (file.size > 5 * 1024 * 1024) {
      alert('文件大小不能超过5MB')
      return
    }
    merchantForm.idCardFront = '上传成功：' + file.name
    console.log('上传身份证正面:', file.name)
  }
}

const handleIdCardBackUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (file.size > 5 * 1024 * 1024) {
      alert('文件大小不能超过5MB')
      return
    }
    merchantForm.idCardBack = '上传成功：' + file.name
    console.log('上传身份证背面:', file.name)
  }
}

// 方法
const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value
  themeStore.toggleTheme()
  updateThemeInDOM()
}

const selectThemeColor = (color) => {
  selectedTheme.value = color
  applyThemeColor(color)
}

const applyThemeColor = (color) => {
  const root = document.documentElement
  root.style.setProperty('--primary-color', color.primary)
  root.style.setProperty('--secondary-color', color.secondary)
  root.style.setProperty('--accent-color', color.accent)
}

const updateThemeInDOM = () => {
  if (isDarkMode.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}


const handleLicenseUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    if (file.size > 5 * 1024 * 1024) {
      alert('文件大小不能超过5MB')
      return
    }
    console.log('上传营业执照:', file.name)
  }
}

const getMerchantStatusText = () => {
  const statusMap = {
    none: '未申请',
    pending: '审核中',
    approved: '已认证',
    rejected: '已拒绝'
  }
  return statusMap[merchantStatus.value] || '未知状态'
}

const getProgressWidth = () => {
  const completedSteps = merchantSteps.value.filter(step => step.completed).length
  const totalSteps = merchantSteps.value.length
  return `${(completedSteps / totalSteps) * 100}%`
}

const formatMoney = (amount) => {
  return amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const viewMerchantInfo = () => {
  alert('查看商家信息功能')
}

const saveSettings = () => {
  const settings = {
    theme: selectedTheme.value.id,
    darkMode: isDarkMode.value,
    borderRadius: borderRadius.value,
    font: selectedFont.value.id
  }

  // 调用theme store的updateSettings方法
  themeStore.updateSettings(settings)
  alert('设置已保存！')
}

const resetSettings = () => {
  selectedTheme.value = themeColors.value[0]
  isDarkMode.value = false
  borderRadius.value = 8
  selectedFont.value = fonts.value[0]
  updateThemeInDOM()
  alert('已恢复默认设置')
}

// 监听主题变化
watch(isDarkMode, updateThemeInDOM)
watch(borderRadius, (value) => {
  document.documentElement.style.setProperty('--border-radius', `${value}px`)
})
watch(selectedFont, (font) => {
  document.documentElement.style.setProperty('--font-family', font.family)
})

// 初始化
onMounted(() => {
  // 从本地存储加载设置
  const savedSettings = localStorage.getItem('luoye_mall_settings')
  if (savedSettings) {
    try {
      const settings = JSON.parse(savedSettings)
      selectedTheme.value = themeColors.value.find(color => color.id === settings.theme) || themeColors.value[0]
      isDarkMode.value = settings.darkMode || false
      borderRadius.value = settings.borderRadius || 8
      selectedFont.value = fonts.value.find(font => font.id === settings.font) || fonts.value[0]

      applyThemeColor(selectedTheme.value)
      updateThemeInDOM()
      document.documentElement.style.setProperty('--font-family', selectedFont.value.family)
    } catch (e) {
      console.error('加载设置失败:', e)
    }
  }

  // 检查商家状态
  checkMerchantStatus()
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-black text-gray-900 dark:text-gray-100 transition-colors duration-300">
    <!-- 顶部导航 -->
    <nav class="bg-white dark:bg-black shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <span class="text-xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-500 to-purple-600">
              系统设置
            </span>
          </div>
          <div class="flex items-center space-x-4">
            <router-link to="/User" class="text-gray-600 dark:text-gray-300 hover:text-blue-500 dark:hover:text-blue-400">
              <i class="fa fa-arrow-left mr-1"></i> 返回首页
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
          <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 sticky top-8">
            <h2 class="text-lg font-semibold mb-6 text-gray-800 dark:text-gray-200">设置</h2>
            <nav class="space-y-2">
              <button
                v-for="tab in tabs"
                :key="tab.id"
                @click="activeTab = tab.id"
                :class="[
                  'w-full flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-all duration-200',
                  activeTab === tab.id
                    ? 'bg-gradient-to-r from-blue-50 to-blue-100 dark:from-blue-900/30 dark:to-blue-800/30 text-blue-600 dark:text-blue-400 border-l-4 border-blue-500'
                    : 'hover:bg-gray-100 dark:hover:bg-gray-700 text-gray-600 dark:text-gray-400'
                ]"
              >
                <i :class="['mr-3', tab.icon]"></i>
                {{ tab.name }}
                <span v-if="tab.badge" class="ml-auto bg-red-100 dark:bg-red-900 text-red-800 dark:text-red-200 text-xs px-2 py-1 rounded-full">
                  {{ tab.badge }}
                </span>
              </button>
            </nav>

            <!-- 用户信息卡片 -->
            <div class="mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
              <div class="flex items-center">
                <div class="w-12 h-12 rounded-full bg-gradient-to-r from-blue-400 to-purple-500 flex items-center justify-center text-white font-bold text-lg">
                  {{ userInfo.username?.charAt(0) || 'U' }}
                </div>
                <div class="ml-4">
                  <h3 class="font-medium text-gray-800 dark:text-gray-200">{{ userInfo.username }}</h3>
                  <p class="text-sm text-gray-500 dark:text-gray-400">{{ userInfo.email }}</p>
                </div>
              </div>
              <div class="mt-4 grid grid-cols-2 gap-2 text-sm">
                <div class="text-center p-2 bg-gray-50 dark:bg-gray-900 rounded-lg">
                  <div class="text-gray-600 dark:text-gray-400">会员等级</div>
                  <div class="font-semibold text-blue-600 dark:text-blue-400">VIP{{ userInfo.vipLevel }}</div>
                </div>
                <div class="text-center p-2 bg-gray-50 dark:bg-gray-900 rounded-lg">
                  <div class="text-gray-600 dark:text-gray-400">账户余额</div>
                  <div class="font-semibold text-green-600 dark:text-green-400">¥{{ formatMoney(userInfo.balance) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：设置内容 -->
        <div class="lg:col-span-2">
          <!-- 主题设置 -->
          <div v-if="activeTab === 'theme'" class="space-y-6">
            <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6">
              <div class="flex items-center justify-between mb-6">
                <div>
                  <h3 class="text-lg font-semibold text-gray-800 dark:text-gray-200">主题设置</h3>
                  <p class="text-sm text-gray-500 dark:text-gray-400">自定义您的界面外观</p>
                </div>
                <div class="flex items-center space-x-2">
                  <span class="text-sm text-gray-500 dark:text-gray-400">{{ isDarkMode ? '深色模式' : '浅色模式' }}</span>
                  <button
                    @click="toggleDarkMode"
                    :class="[
                      'relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-300',
                      isDarkMode ? 'bg-blue-600' : 'bg-gray-300 dark:bg-gray-600'
                    ]"
                  >
                    <span
                      :class="[
                        'inline-block h-4 w-4 transform rounded-full bg-white transition-transform duration-300',
                        isDarkMode ? 'translate-x-6' : 'translate-x-1'
                      ]"
                    />
                  </button>
                </div>
              </div>

              <!-- 主题颜色选择 -->
              <div class="mb-8">
                <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">主题颜色</h4>
                <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
                  <div
                    v-for="color in themeColors"
                    :key="color.id"
                    @click="selectThemeColor(color)"
                    :class="[
                        'relative p-4 rounded-xl cursor-pointer transition-transform duration-200 hover:scale-105 border-2',
                        selectedTheme.id === color.id
                          ? 'border-blue-500 ring-2 ring-blue-200 dark:ring-blue-800'
                          : 'border-gray-200 dark:border-gray-800'
                      ]"
                  >
                    <div class="flex items-center mb-3">
                      <div class="w-8 h-8 rounded-full" :style="{ backgroundColor: color.primary }"></div>
                      <div class="ml-3">
                        <div class="font-medium text-gray-800 dark:text-gray-200">{{ color.name }}</div>
                        <div class="text-xs text-gray-500 dark:text-gray-400">{{ color.description }}</div>
                      </div>
                    </div>
                    <div class="flex space-x-1">
                      <div class="h-2 flex-1 rounded" :style="{ backgroundColor: color.primary }"></div>
                      <div class="h-2 flex-1 rounded" :style="{ backgroundColor: color.secondary }"></div>
                      <div class="h-2 flex-1 rounded" :style="{ backgroundColor: color.accent }"></div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 字体设置 -->
              <div class="mb-8">
                <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">字体设置</h4>
                <div class="grid grid-cols-2 gap-4">
                  <div
                    v-for="font in fonts"
                    :key="font.id"
                    @click="selectedFont = font"
                    :class="[
                        'p-4 rounded-lg border cursor-pointer transition-all duration-200',
                        selectedFont.id === font.id
                          ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20'
                          : 'border-gray-200 dark:border-gray-800 hover:bg-gray-50 dark:hover:bg-gray-900'
                      ]"
                    :style="{ fontFamily: font.family }"
                  >
                    <div class="font-medium">{{ font.name }}</div>
                    <div class="text-sm text-gray-600 dark:text-gray-400 mt-1">{{ font.description }}</div>
                  </div>
                </div>
              </div>

              <!-- 圆角设置 -->
              <div>
                <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">界面圆角</h4>
                <div class="flex items-center space-x-4">
                  <div class="flex-1">
                    <input
                      type="range"
                      min="0"
                      max="24"
                      v-model="borderRadius"
                      class="w-full h-2 bg-gray-200 dark:bg-gray-700 rounded-lg appearance-none cursor-pointer"
                    />
                    <div class="flex justify-between text-xs text-gray-500 dark:text-gray-400 mt-2">
                      <span>直角</span>
                      <span>轻微</span>
                      <span>中等</span>
                      <span>圆润</span>
                      <span>全圆</span>
                    </div>
                  </div>
                  <div class="w-16 text-center">
                    <div class="text-2xl font-bold text-blue-600 dark:text-blue-400">{{ borderRadius }}px</div>
                    <div class="text-xs text-gray-500 dark:text-gray-400">圆角</div>
                  </div>
                </div>
                <div class="mt-6 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
                  <div class="text-sm text-gray-600 dark:text-gray-400 mb-2">预览效果：</div>
                  <div
                    class="w-full h-20 bg-gradient-to-r from-blue-500 to-purple-500 rounded-lg flex items-center justify-center text-white font-medium"
                    :style="{ borderRadius: borderRadius + 'px' }"
                  >
                    卡片圆角预览
                  </div>
                </div>
              </div>
            </div>

            <!-- 应用主题预览 -->
            <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6">
              <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">主题预览</h4>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-700">
                  <div class="text-sm font-medium text-gray-600 dark:text-gray-400 mb-2">按钮样式</div>
                  <div class="flex flex-wrap gap-3">
                    <button class="px-4 py-2 rounded-lg bg-blue-500 text-white hover:bg-blue-600 transition-colors">主要按钮</button>
                    <button class="px-4 py-2 rounded-lg bg-green-500 text-white hover:bg-green-600 transition-colors">成功按钮</button>
                    <button class="px-4 py-2 rounded-lg bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors">次要按钮</button>
                  </div>
                </div>
                <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-700">
                  <div class="text-sm font-medium text-gray-600 dark:text-gray-400 mb-2">卡片样式</div>
                  <div class="space-y-3">
                    <div class="p-3 rounded-lg bg-gradient-to-r from-blue-50 to-blue-100 dark:from-blue-900/20 dark:to-blue-800/20">
                      <div class="font-medium text-blue-800 dark:text-blue-300">信息卡片</div>
                      <div class="text-sm text-blue-600 dark:text-blue-400">这是主题预览效果</div>
                    </div>
                    <div class="p-3 rounded-lg bg-gradient-to-r from-green-50 to-green-100 dark:from-green-900/20 dark:to-green-800/20">
                      <div class="font-medium text-green-800 dark:text-green-300">成功卡片</div>
                      <div class="text-sm text-green-600 dark:text-green-400">操作成功提示</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 商家申请 -->
          <div v-if="activeTab === 'merchant'" class="space-y-6">
            <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6">
              <div class="flex items-center justify-between mb-6">
                <div>
                  <h3 class="text-lg font-semibold text-gray-800 dark:text-gray-200">商家申请</h3>
                  <p class="text-sm text-gray-500 dark:text-gray-400">申请成为落叶商城认证商家</p>
                </div>
                <div :class="[
        'px-3 py-1 rounded-full text-sm font-medium',
        merchantStatus === 'approved'
          ? 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200'
          : merchantStatus === 'pending'
          ? 'bg-yellow-100 dark:bg-yellow-900 text-yellow-800 dark:text-yellow-200'
          : 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200'
      ]">
                  {{ getMerchantStatusText() }}
                </div>
              </div>

              <!-- 申请进度 -->
              <div v-if="merchantStatus !== 'none'" class="mb-8">
                <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">申请进度</h4>
                <div class="relative">
                  <!-- 进度条 -->
                  <div class="absolute top-1/2 left-0 right-0 h-1 bg-gray-200 dark:bg-gray-700 transform -translate-y-1/2"></div>
                  <div class="absolute top-1/2 left-0 h-1 bg-blue-500 transform -translate-y-1/2 transition-all duration-500"
                       :style="{ width: getProgressWidth() }"></div>

                  <!-- 进度节点 -->
                  <div class="relative flex justify-between">
                    <div v-for="(step, index) in merchantSteps" :key="step.id" class="flex flex-col items-center">
                      <div :class="[
              'w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium border-2 transition-all duration-300',
              step.completed
                ? 'bg-blue-500 border-blue-500 text-white'
                : step.active
                ? 'border-blue-500 bg-white dark:bg-gray-800 text-blue-500'
                : 'border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-400 dark:text-gray-500'
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
              <div v-if="merchantStatus === 'none'">
                <!-- 隐藏的userId字段（从用户信息获取） -->
                <input type="hidden" v-model="merchantForm.userId" />

                <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                  <!-- 商家基本信息 -->
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">商家类型 *</label>
                    <select
                      v-model="merchantForm.merchantType"
                      class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    >
                      <option value="">请选择商家类型</option>
                      <option value="个人商家">个人商家</option>
                      <option value="企业商家">企业商家</option>
                      <option value="品牌商家">品牌商家</option>
                      <option value="旗舰店">旗舰店</option>
                      <option value="专卖店">专卖店</option>
                    </select>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">商家名称 *</label>
                    <input
                      v-model="merchantForm.merchantName"
                      type="text"
                      placeholder="请输入商家名称"
                      class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    />
                  </div>

                  <!-- 联系人信息 -->
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">联系人姓名 *</label>
                    <input
                      v-model="merchantForm.contactName"
                      type="text"
                      placeholder="请输入联系人姓名"
                      class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">联系人电话 *</label>
                    <input
                      v-model="merchantForm.contactPhone"
                      type="tel"
                      placeholder="请输入联系电话"
                      class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">联系人邮箱</label>
                    <input
                      v-model="merchantForm.contactEmail"
                      type="email"
                      placeholder="请输入联系邮箱"
                      class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    />
                  </div>

                  <!-- 资质信息 -->
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">营业执照号 *</label>
                    <input
                      v-model="merchantForm.businessLicense"
                      type="text"
                      placeholder="请输入营业执照号"
                      class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">身份证号 *</label>
                    <input
                      v-model="merchantForm.idCard"
                      type="text"
                      placeholder="请输入身份证号"
                      class="w-full px-4 py-3 rounded-lg border border-gray-300 dark:border-gray-800 bg-white dark:bg-black focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                    />
                  </div>

                  <!-- 身份证图片上传 -->
                  <div class="md:col-span-2">
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">身份证正反面照片</label>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <!-- 身份证正面 -->
                      <div>
                        <div class="border-2 border-dashed border-gray-300 dark:border-gray-800 rounded-lg p-6 text-center">
                          <i class="fa fa-id-card text-3xl text-gray-400 mb-4"></i>
                          <p class="text-gray-600 dark:text-gray-400 mb-2">身份证正面</p>
                          <input
                            type="file"
                            accept="image/*"
                            class="hidden"
                            id="idCardFrontUpload"
                            @change="handleIdCardFrontUpload"
                          />
                          <label for="idCardFrontUpload" class="inline-block mt-2 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 cursor-pointer transition-colors text-sm">
                            上传正面
                          </label>
                          <div v-if="merchantForm.idCardFront" class="mt-2 text-sm text-green-600 dark:text-green-400">
                            ✓ 已上传
                          </div>
                        </div>
                      </div>
                      <!-- 身份证背面 -->
                      <div>
                        <div class="border-2 border-dashed border-gray-300 dark:border-gray-800 rounded-lg p-6 text-center">
                          <i class="fa fa-id-card-alt text-3xl text-gray-400 mb-4"></i>
                          <p class="text-gray-600 dark:text-gray-400 mb-2">身份证背面</p>
                          <input
                            type="file"
                            accept="image/*"
                            class="hidden"
                            id="idCardBackUpload"
                            @change="handleIdCardBackUpload"
                          />
                          <label for="idCardBackUpload" class="inline-block mt-2 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 cursor-pointer transition-colors text-sm">
                            上传背面
                          </label>
                          <div v-if="merchantForm.idCardBack" class="mt-2 text-sm text-green-600 dark:text-green-400">
                            ✓ 已上传
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 营业执照图片上传 -->
                  <div class="md:col-span-2">
                    <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">营业执照图片</label>
                    <div class="border-2 border-dashed border-gray-300 dark:border-gray-800 rounded-lg p-8 text-center">
                      <i class="fa fa-file-image text-4xl text-gray-400 mb-4"></i>
                      <p class="text-gray-600 dark:text-gray-400 mb-2">点击或拖拽上传营业执照图片</p>
                      <p class="text-sm text-gray-500 dark:text-gray-500 mb-4">支持 JPG、PNG 格式，大小不超过 5MB</p>
                      <input
                        type="file"
                        accept="image/*"
                        class="hidden"
                        id="licenseImageUpload"
                        @change="handleLicenseImageUpload"
                      />
                      <label for="licenseImageUpload" class="inline-block mt-2 px-6 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 cursor-pointer transition-colors">
                        选择文件
                      </label>
                      <div v-if="merchantForm.licenseImage" class="mt-4 text-sm text-green-600 dark:text-green-400">
                        ✓ 营业执照图片已上传
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 表单验证提示 -->
                <div v-if="validationErrors.length > 0" class="mb-6 p-4 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 rounded-lg">
                  <div class="flex items-start">
                    <i class="fa fa-exclamation-triangle text-red-500 mt-0.5 mr-3"></i>
                    <div>
                      <p class="font-medium text-red-800 dark:text-red-200 mb-1">请完善以下信息：</p>
                      <ul class="text-sm text-red-700 dark:text-red-300 list-disc pl-5">
                        <li v-for="error in validationErrors" :key="error">{{ error }}</li>
                      </ul>
                    </div>
                  </div>
                </div>

                <!-- 申请协议 -->
                <div class="mb-8 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
                  <div class="flex items-start">
                    <input
                      type="checkbox"
                      v-model="merchantForm.agreed"
                      id="merchantAgreement"
                      class="mt-1 mr-3"
                    />
                    <label for="merchantAgreement" class="text-sm text-gray-700 dark:text-gray-300">
                      我已阅读并同意
                      <a href="#" class="text-blue-500 hover:text-blue-600 dark:text-blue-400 dark:hover:text-blue-300">《落叶商城商家入驻协议》</a>
                      和
                      <a href="#" class="text-blue-500 hover:text-blue-600 dark:text-blue-400 dark:hover:text-blue-300">《商家服务条款》</a>
                    </label>
                  </div>
                </div>

                <!-- 提交按钮 -->
                <button
                  @click="submitMerchantApplication"
                  :disabled="!canSubmitApplication || isSubmitting"
                  :class="[
          'w-full py-3 rounded-lg font-medium transition-all duration-300',
          canSubmitApplication && !isSubmitting
            ? 'bg-gradient-to-r from-green-500 to-emerald-600 hover:from-green-600 hover:to-emerald-700 text-white shadow-lg hover:shadow-xl transform hover:-translate-y-0.5'
            : 'bg-gray-300 dark:bg-gray-700 text-gray-500 dark:text-gray-400 cursor-not-allowed'
        ]"
                >
                  <i class="fa fa-check-circle mr-2"></i>
                  {{ isSubmitting ? '提交中...' : '提交商家申请' }}
                </button>
              </div>

              <!-- 审核中状态 -->
              <div v-else-if="merchantStatus === 'pending'" class="py-12">
                <div class="text-center mb-10">
                  <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-gradient-to-r from-yellow-100 to-yellow-200 dark:from-yellow-900/30 dark:to-yellow-800/30 flex items-center justify-center">
                    <i class="fa fa-clock text-3xl text-yellow-500"></i>
                  </div>
                  <h4 class="text-xl font-semibold text-gray-800 dark:text-gray-200 mb-2">申请审核中</h4>
                  <p class="text-gray-600 dark:text-gray-400 mb-6">您的商家申请正在审核中，通常需要1-3个工作日</p>
                  <div class="inline-flex items-center space-x-2 text-sm text-gray-500 dark:text-gray-500">
                    <i class="fa fa-calendar"></i>
                    <span>申请时间：{{ formatDate(merchantApplication.submitTime) }}</span>
                  </div>
                </div>

                <!-- 审核流程图 -->
                <div class="max-w-2xl mx-auto">
                  <!-- 进度条 -->
                  <div class="relative mb-10">
                    <div class="h-2 bg-gray-200 dark:bg-gray-700 rounded-full">
                      <div 
                        class="h-2 bg-gradient-to-r from-blue-500 to-purple-500 rounded-full transition-all duration-500" 
                        :style="{ width: getProgressWidth() }"
                      ></div>
                    </div>
                  </div>

                  <!-- 步骤流程 -->
                  <div class="relative">
                    <div class="flex justify-between">
                      <div 
                        v-for="(step, index) in merchantSteps" 
                        :key="step.id"
                        class="flex flex-col items-center"
                        :style="{ flex: 1 }"
                      >
                        <!-- 步骤圆圈 -->
                        <div 
                          :class="[
                            'w-10 h-10 rounded-full flex items-center justify-center text-sm font-medium mb-3 transition-all duration-300',
                            step.completed 
                              ? 'bg-gradient-to-r from-blue-500 to-purple-500 text-white shadow-lg shadow-blue-500/30' 
                              : step.active 
                                ? 'bg-gradient-to-r from-yellow-400 to-orange-500 text-white shadow-lg shadow-yellow-500/30' 
                                : 'bg-gray-200 dark:bg-gray-700 text-gray-500 dark:text-gray-400'
                          ]"
                        >
                          <i v-if="step.completed" class="fa fa-check"></i>
                          <i v-else-if="step.active" class="fa fa-clock"></i>
                          <span v-else>{{ index + 1 }}</span>
                        </div>

                        <!-- 步骤标题 -->
                        <h5 
                          :class="[
                            'text-sm font-medium mb-1 transition-colors duration-300',
                            step.completed || step.active 
                              ? 'text-gray-800 dark:text-gray-200' 
                              : 'text-gray-500 dark:text-gray-400'
                          ]"
                        >
                          {{ step.name }}
                        </h5>

                        <!-- 步骤描述 -->
                        <p 
                          :class="[
                            'text-xs text-center transition-colors duration-300',
                            step.completed || step.active 
                              ? 'text-gray-600 dark:text-gray-400' 
                              : 'text-gray-500 dark:text-gray-500'
                          ]"
                        >
                          {{ step.description }}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 已通过状态 -->
              <div v-else-if="merchantStatus === 'approved'" class="text-center py-12">
                <div class="w-20 h-20 mx-auto mb-6 rounded-full bg-gradient-to-r from-green-100 to-green-200 dark:from-green-900/30 dark:to-green-800/30 flex items-center justify-center">
                  <i class="fa fa-check-circle text-3xl text-green-500"></i>
                </div>
                <h4 class="text-xl font-semibold text-gray-800 dark:text-gray-200 mb-2">恭喜！您已成为认证商家</h4>
                <p class="text-gray-600 dark:text-gray-400 mb-8">您现在可以享受所有商家特权并开始管理您的店铺</p>
                <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
                  <div class="p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
                    <div class="text-2xl font-bold text-blue-600 dark:text-blue-400 mb-1">30天</div>
                    <div class="text-sm text-gray-600 dark:text-gray-400">免费试用期</div>
                  </div>
                  <div class="p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
                    <div class="text-2xl font-bold text-green-600 dark:text-green-400 mb-1">0.5%</div>
                    <div class="text-sm text-gray-600 dark:text-gray-400">优惠佣金率</div>
                  </div>
                  <div class="p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
                    <div class="text-2xl font-bold text-purple-600 dark:text-purple-400 mb-1">24h</div>
                    <div class="text-sm text-gray-600 dark:text-gray-400">专属客服</div>
                  </div>
                </div>
                <div class="flex justify-center space-x-4">
                  <router-link to="/merchant/dashboard" class="px-6 py-3 bg-gradient-to-r from-blue-500 to-purple-500 text-white rounded-lg hover:from-blue-600 hover:to-purple-600 transition-all">
                    进入商家后台
                  </router-link>
                  <button @click="viewMerchantInfo" class="px-6 py-3 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
                    查看商家信息
                  </button>
                </div>
              </div>
            </div>

            <!-- 商家特权 -->
            <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6">
              <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-6">商家特权</h4>
              <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div v-for="benefit in merchantBenefits" :key="benefit.id" class="p-4 rounded-lg border border-gray-200 dark:border-gray-700 hover:border-blue-300 dark:hover:border-blue-500 transition-colors">
                  <div class="w-12 h-12 rounded-lg mb-4 flex items-center justify-center" :class="benefit.bgColor">
                    <i :class="['text-xl', benefit.icon, benefit.textColor]"></i>
                  </div>
                  <h5 class="font-medium text-gray-800 dark:text-gray-200 mb-2">{{ benefit.title }}</h5>
                  <p class="text-sm text-gray-600 dark:text-gray-400">{{ benefit.description }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 其他设置标签页 -->
          <div v-if="activeTab === 'notifications'" class="space-y-6">
            <!-- 通知设置 -->
            <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6">
              <h3 class="text-lg font-semibold text-gray-800 dark:text-gray-200 mb-6">通知设置</h3>
              <!-- 通知设置内容 -->
            </div>
          </div>

          <!-- 保存按钮 -->
          <div v-if="activeTab === 'theme'" class="flex justify-end space-x-4">
            <button @click="resetSettings" class="px-6 py-3 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors">
              恢复默认
            </button>
            <button @click="saveSettings" class="px-6 py-3 bg-gradient-to-r from-blue-500 to-purple-500 text-white rounded-lg hover:from-blue-600 hover:to-purple-600 shadow-lg hover:shadow-xl transition-all transform hover:-translate-y-0.5">
              <i class="fa fa-save mr-2"></i>保存设置
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
