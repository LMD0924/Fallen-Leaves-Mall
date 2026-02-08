import { defineStore } from 'pinia'

// 主题类型
export const useThemeStore = defineStore('theme', {
  state: () => ({
    // 默认使用系统主题，然后检查localStorage中是否有存储的主题偏好
    isDark: localStorage.getItem('theme') ? localStorage.getItem('theme') === 'dark' : window.matchMedia('(prefers-color-scheme: dark)').matches
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
    },
    // 应用主题
    applyTheme() {
      if (this.isDark) {
        document.documentElement.classList.add('dark')
      } else {
        document.documentElement.classList.remove('dark')
      }
    },
    // 初始化主题
    initTheme() {
      this.applyTheme()
    }
  }
})
