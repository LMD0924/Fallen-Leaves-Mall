import './assets/css/main.css'

import { createApp, watch } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import App from './App.vue'
import router from './router'
import axios from "axios";

axios.defaults.baseURL = 'http://localhost:8080'//设置全局的基础路径

axios.defaults.withCredentials=true;
//后端基础url 之后在请求时只用填写路径 Axios会自动以该url为基础添加路径

const app = createApp(App)
const pinia = createPinia()

app.config.globalProperties.$axios = axios
app.use(pinia)
app.use(router).use(ElementPlus).use(Antd);

// 初始化主题
import { useThemeStore } from './stores/theme';
const themeStore = useThemeStore()
themeStore.initTheme()

// 监听主题变化，同步更新Element Plus和Ant Design Vue的主题
watch(() => themeStore.isDark, (isDark) => {
  if (isDark) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
})

app.mount('#app')
