import { defineStore } from 'pinia'

// 主题类型
export const useThemeStore = defineStore('theme', {
  state: () => ({
    // 默认使用系统主题，然后检查localStorage中是否有存储的主题偏好
    isDark: localStorage.getItem('theme') ? localStorage.getItem('theme') === 'dark' : window.matchMedia('(prefers-color-scheme: dark)').matches,
    // 主题设置
    theme: 'blue',
    borderRadius: 8,
    font: 'inter'
  }),
  actions: {
    // 切换主题
    toggleTheme() {
      this.isDark = !this.isDark
      this.saveTheme()
      this.applyTheme()
    },
    // 保存主题到localStorage
    saveTheme() {
      localStorage.setItem('theme', this.isDark ? 'dark' : 'light')
      
      // 保存其他设置
      const settings = {
        theme: this.theme,
        darkMode: this.isDark,
        borderRadius: this.borderRadius,
        font: this.font
      }
      localStorage.setItem('luoye_mall_settings', JSON.stringify(settings))
    },
    // 应用主题
    applyTheme() {
      if (this.isDark) {
        document.documentElement.classList.add('dark')
      } else {
        document.documentElement.classList.remove('dark')
      }
      
      // 应用其他设置
      this.applyThemeSettings()
    },
    // 应用主题设置
    applyThemeSettings() {
      // 主题颜色
      const themeColors = {
        blue: {
          primary: '#3B82F6',
          secondary: '#8B5CF6',
          accent: '#06B6D4'
        },
        green: {
          primary: '#10B981',
          secondary: '#059669',
          accent: '#34D399'
        },
        purple: {
          primary: '#8B5CF6',
          secondary: '#A78BFA',
          accent: '#D946EF'
        },
        red: {
          primary: '#EF4444',
          secondary: '#F87171',
          accent: '#FB7185'
        }
      }
      
      const color = themeColors[this.theme] || themeColors.blue
      document.documentElement.style.setProperty('--primary-color', color.primary)
      document.documentElement.style.setProperty('--secondary-color', color.secondary)
      document.documentElement.style.setProperty('--accent-color', color.accent)
      
      // 圆角
      document.documentElement.style.setProperty('--border-radius', `${this.borderRadius}px`)
      
      // 字体
      const fonts = {
        inter: 'Inter, sans-serif',
        system: 'system-ui, sans-serif',
        georgia: 'Georgia, serif',
        mono: 'monospace'
      }
      
      const fontFamily = fonts[this.font] || fonts.inter
      document.documentElement.style.setProperty('--font-family', fontFamily)
      // 直接设置body元素的字体，确保全局生效
      document.body.style.fontFamily = fontFamily
    },
    // 初始化主题
    initTheme() {
      // 加载保存的设置
      const savedSettings = localStorage.getItem('luoye_mall_settings')
      if (savedSettings) {
        try {
          const settings = JSON.parse(savedSettings)
          this.theme = settings.theme || 'blue'
          this.isDark = settings.darkMode !== undefined ? settings.darkMode : this.isDark
          this.borderRadius = settings.borderRadius || 8
          this.font = settings.font || 'inter'
        } catch (e) {
          console.error('加载设置失败:', e)
        }
      }
      
      this.applyTheme()
    },
    // 更新主题设置
    updateSettings(settings) {
      if (settings.theme) this.theme = settings.theme
      if (settings.darkMode !== undefined) this.isDark = settings.darkMode
      if (settings.borderRadius) this.borderRadius = settings.borderRadius
      if (settings.font) this.font = settings.font
      
      this.saveTheme()
      this.applyTheme()
    }
  }
})
