<!-- components/settings/ThemeSettings.vue - 主题设置组件 -->
<template>
  <div class="space-y-6">
    <!-- 主题设置卡片 -->
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp">
      <div class="flex items-center justify-between mb-6">
        <div>
          <h3 class="text-lg font-light text-gray-900 dark:text-gray-100 mb-1">主题设置</h3>
          <p class="text-sm text-gray-500 dark:text-gray-400">自定义您的界面外观</p>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-500 dark:text-gray-400">{{ isDarkMode ? '深色模式' : '浅色模式' }}</span>
          <button
            @click="toggleDarkMode"
            :class="[
              'relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-300',
              isDarkMode ? 'bg-purple-600' : 'bg-gray-300 dark:bg-gray-600'
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
              'relative p-4 rounded-xl cursor-pointer transition-all duration-300 hover:scale-105 border-2',
              selectedTheme.id === color.id
                ? 'border-purple-500 ring-2 ring-purple-200 dark:ring-purple-800'
                : 'border-gray-200 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800'
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
                ? 'border-purple-500 bg-purple-50 dark:bg-purple-950/30'
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
              class="w-full h-2 bg-gray-200 dark:bg-gray-700 rounded-lg appearance-none cursor-pointer accent-purple-500"
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
            <div class="text-2xl font-light text-purple-600 dark:text-purple-400">{{ borderRadius }}px</div>
            <div class="text-xs text-gray-500 dark:text-gray-400">圆角</div>
          </div>
        </div>

        <!-- 预览卡片 -->
        <div class="mt-6 p-4 bg-gray-50 dark:bg-gray-900 rounded-lg">
          <div class="text-sm text-gray-600 dark:text-gray-400 mb-2">预览效果：</div>
          <div
            class="w-full h-20 bg-gradient-to-r from-purple-500 to-fuchsia-500 rounded-lg flex items-center justify-center text-white font-medium"
            :style="{ borderRadius: borderRadius + 'px' }"
          >
            卡片圆角预览
          </div>
        </div>
      </div>
    </div>

    <!-- 应用主题预览 -->
    <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800 animate-fadeInUp" style="animation-delay: 0.1s">
      <h4 class="font-medium text-gray-700 dark:text-gray-300 mb-4">主题预览</h4>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-700">
          <div class="text-sm font-medium text-gray-600 dark:text-gray-400 mb-2">按钮样式</div>
          <div class="flex flex-wrap gap-3">
            <button class="px-4 py-2 rounded-lg bg-purple-500 text-white hover:bg-purple-600 transition-colors">主要按钮</button>
            <button class="px-4 py-2 rounded-lg bg-emerald-500 text-white hover:bg-emerald-600 transition-colors">成功按钮</button>
            <button class="px-4 py-2 rounded-lg bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors">次要按钮</button>
          </div>
        </div>
        <div class="p-4 rounded-lg border border-gray-200 dark:border-gray-700">
          <div class="text-sm font-medium text-gray-600 dark:text-gray-400 mb-2">卡片样式</div>
          <div class="space-y-3">
            <div class="p-3 rounded-lg bg-gradient-to-r from-purple-50 to-purple-100 dark:from-purple-950/30 dark:to-purple-900/30">
              <div class="font-medium text-purple-800 dark:text-purple-300">信息卡片</div>
              <div class="text-sm text-purple-600 dark:text-purple-400">这是主题预览效果</div>
            </div>
            <div class="p-3 rounded-lg bg-gradient-to-r from-emerald-50 to-emerald-100 dark:from-emerald-950/30 dark:to-emerald-900/30">
              <div class="font-medium text-emerald-800 dark:text-emerald-300">成功卡片</div>
              <div class="text-sm text-emerald-600 dark:text-emerald-400">操作成功提示</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="flex justify-end space-x-4 animate-fadeInUp" style="animation-delay: 0.2s">
      <button @click="resetSettings" class="px-6 py-3 border border-gray-300 dark:border-gray-600 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-colors">
        恢复默认
      </button>
      <button @click="saveSettings" class="px-6 py-3 bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white rounded-lg hover:from-purple-600 hover:to-fuchsia-600 shadow-lg hover:shadow-xl transition-all transform hover:-translate-y-0.5">
        <i class="fa fa-save mr-2"></i>保存设置
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { message } from 'ant-design-vue'

const themeStore = useThemeStore()
const [messageApi] = message.useMessage()

// 状态
const isDarkMode = ref(themeStore.isDark)
const borderRadius = ref(8)
const selectedFont = ref({ id: 'inter', name: 'Inter', family: 'Inter, sans-serif', description: '现代简洁字体' })

// 主题颜色选项
const themeColors = ref([
  {
    id: 'purple',
    name: '紫色主题',
    description: '优雅神秘',
    primary: '#8B5CF6',
    secondary: '#A78BFA',
    accent: '#D946EF'
  },
  {
    id: 'blue',
    name: '蓝色主题',
    description: '沉稳专业',
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

const saveSettings = () => {
  const settings = {
    theme: selectedTheme.value.id,
    darkMode: isDarkMode.value,
    borderRadius: borderRadius.value,
    font: selectedFont.value.id
  }

  themeStore.updateSettings(settings)
  messageApi.success({
    content: '设置已保存',
    icon: () => '✓'
  })
}

const resetSettings = () => {
  selectedTheme.value = themeColors.value[0]
  isDarkMode.value = false
  borderRadius.value = 8
  selectedFont.value = fonts.value[0]
  updateThemeInDOM()
  applyThemeColor(selectedTheme.value)
  messageApi.success('已恢复默认设置')
}

// 监听变化
watch(isDarkMode, updateThemeInDOM)
watch(borderRadius, (value) => {
  document.documentElement.style.setProperty('--border-radius', `${value}px`)
})
watch(selectedFont, (font) => {
  document.documentElement.style.setProperty('--font-family', font.family)
})

// 初始化
onMounted(() => {
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
    } catch (e) {
      console.error('加载设置失败:', e)
    }
  }
})
</script>
