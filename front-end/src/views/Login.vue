<template>
  <contextHolder />
  <div class="min-h-screen flex items-center justify-center bg-cover bg-center relative" style="background-image: url('https://p26-flow-imagex-sign.byteimg.com/tos-cn-i-a9rns2rl98/rc/pc/super_tool/33ed8bf4564549cea7236751c1ee5596~tplv-a9rns2rl98-image.image?lk3s=8e244e95&rcl=20260207155018B420D0EB0CC79C14C28A&rrcfp=f06b921b&x-expires=1773042716&x-signature=hM8fBG1gStC5bQSCektFyf9hdyQ%3D');">
    <!-- 背景遮罩 -->
    <div class="absolute inset-0 bg-black bg-opacity-50 z-0"></div>

    <!-- 登录卡片 -->
    <div class="relative z-10 w-full max-w-md p-8 space-y-8 bg-white bg-opacity-90 dark:bg-gray-900 dark:bg-opacity-80 backdrop-blur-md rounded-2xl shadow-2xl border border-gray-200 dark:border-gray-800">
      <!-- 标题 -->
      <div class="text-center">
        <h1 class="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-purple-500">
          系统登录
        </h1>
        <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">请输入您的账号和密码</p>
      </div>

      <!-- 登录表单 -->
      <form @submit.prevent="handleLogin" class="space-y-6">
        <!-- 账号输入框 -->
        <div>
          <label for="account" class="block mb-2 text-sm font-medium text-gray-700 dark:text-gray-300">账号</label>
          <div class="relative">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500 dark:text-gray-400">
              <i class="fa fa-user"></i>
            </span>
            <input
              type="text"
              id="account"
              v-model="form.account"
              required
              class="w-full pl-10 pr-4 py-3 text-gray-900 dark:text-gray-200 bg-white dark:bg-gray-800 bg-opacity-50 border border-gray-300 dark:border-gray-700 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-300 transform focus:scale-[1.02]"
              placeholder="请输入账号"
            />
          </div>
        </div>

        <!-- 密码输入框 -->
        <div>
          <label for="password" class="block mb-2 text-sm font-medium text-gray-700 dark:text-gray-300">密码</label>
          <div class="relative">
            <span class="absolute inset-y-0 left-0 flex items-center pl-3 text-gray-500 dark:text-gray-400">
              <i class="fa fa-lock"></i>
            </span>
            <input
              :type="showPassword ? 'text' : 'password'"
              id="password"
              v-model="form.password"
              required
              class="w-full pl-10 pr-10 py-3 text-gray-900 dark:text-gray-200 bg-white dark:bg-gray-800 bg-opacity-50 border border-gray-300 dark:border-gray-700 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-300 transform focus:scale-[1.02]"
              placeholder="请输入密码"
            />
            <button
              type="button"
              @click="togglePasswordVisibility"
              class="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300 transition-colors"
            >
              <i :class="showPassword ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
            </button>
          </div>
        </div>

        <!-- 协议同意 -->
        <div class="flex items-start">
          <div class="flex items-center h-5">
            <input
              id="agreement"
              type="checkbox"
              v-model="form.agreement"
              required
              class="w-4 h-4 text-blue-600 bg-white dark:bg-gray-800 border-gray-300 dark:border-gray-600 rounded focus:ring-blue-500 focus:ring-offset-2 dark:focus:ring-offset-gray-800"
            />
          </div>
          <label for="agreement" class="ml-2 text-sm text-gray-600 dark:text-gray-400">
            我已阅读并同意 <a href="#" class="text-blue-400 hover:text-blue-300 transition-colors">用户协议</a> 和 <a href="#" class="text-blue-400 hover:text-blue-300 transition-colors">隐私政策</a>
          </label>
        </div>

        <!-- 错误信息 -->
        <div v-if="errorMessage" class="p-3 text-sm text-red-400 bg-red-100 dark:bg-red-900 dark:bg-opacity-20 border border-red-200 dark:border-red-800 rounded-lg">
          {{ errorMessage }}
        </div>

        <!-- 登录按钮 -->
        <button
          type="submit"
          :disabled="isLoading"
          class="w-full py-3 font-medium text-white bg-gradient-to-r from-blue-600 to-purple-600 rounded-lg hover:from-blue-700 hover:to-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-300 transform hover:scale-[1.02] active:scale-[0.98] disabled:opacity-70 disabled:cursor-not-allowed"
        >
          <span v-if="isLoading" class="flex items-center justify-center">
            <i class="fa fa-spinner fa-spin mr-2"></i> 登录中...
          </span>
          <span v-else>登录</span>
        </button>
      </form>

      <!-- 其他选项 -->
      <div class="flex items-center justify-between pt-4 border-t border-gray-200 dark:border-gray-800">
        <a href="#" class="text-sm text-gray-600 dark:text-gray-400 hover:text-blue-400 transition-colors">忘记密码?</a>
        <a href="#" class="text-sm text-gray-600 dark:text-gray-400 hover:text-blue-400 transition-colors">注册账号</a>
      </div>
    </div>

    <!-- 装饰元素 -->
    <div class="absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none">
      <div class="absolute top-1/4 left-1/4 w-64 h-64 bg-blue-500 rounded-full filter blur-[100px] opacity-20 animate-pulse"></div>
      <div class="absolute bottom-1/4 right-1/4 w-80 h-80 bg-purple-500 rounded-full filter blur-[120px] opacity-15 animate-pulse delay-700"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import {post} from "@/net/index.js";
import {message} from "ant-design-vue";
import { useThemeStore } from '../stores/theme';
import router from "@/router/index.js";

const themeStore = useThemeStore();
const [messageApi, contextHolder] = message.useMessage()
// 表单数据
const form = reactive({
  account: '',
  password: '',
  agreement: false
});

// 状态变量
const showPassword = ref(false);
const isLoading = ref(false);
const errorMessage = ref('');

// 切换密码可见性
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

// 处理登录
const handleLogin = async () => {
  // 重置错误信息
  errorMessage.value = '';

  // 表单验证
  if (!form.account.trim()) {
    errorMessage.value = '请输入账号';
    return;
  }

  if (!form.password) {
    errorMessage.value = '请输入密码';
    return;
  }

  if (!form.agreement) {
    errorMessage.value = '请阅读并同意用户协议和隐私政策';
    return;
  }

  try {
    // 模拟登录请求
    isLoading.value = true;
    post('api/user/login',{
      account: form.account,
      password: form.password
    },(message,data)=>{
      messageApi.success(message)
      localStorage.setItem('落叶商城token',data.accessToken)
      //登录成功跳转个人页面
      router.push('/User')
    })
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 1500));

    // 重置表单
    form.account = '';
    form.password = '';
    form.agreement = false;
  } catch (error) {
    console.error('登录失败:', error);
    errorMessage.value = '登录失败，请检查账号密码是否正确';
  } finally {
    isLoading.value = false;
  }
};
</script>

<style>
/* 导入Font Awesome图标库 */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #1a1a1a;
}

::-webkit-scrollbar-thumb {
  background: #333;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #444;
}

/* 动画效果 */
@keyframes float {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
  100% {
    transform: translateY(0px);
  }
}

/* 输入框聚焦动画 */
input:focus {
  box-shadow: 0 0 15px rgba(59, 130, 246, 0.5);
}

/* 按钮点击波纹效果 */
button {
  position: relative;
  overflow: hidden;
}

button:after {
  content: "";
  display: block;
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
  background-image: radial-gradient(circle, #fff 10%, transparent 10.01%);
  background-repeat: no-repeat;
  background-position: 50%;
  transform: scale(10, 10);
  opacity: 0;
  transition: transform .5s, opacity 1s;
}

button:active:after {
  transform: scale(0, 0);
  opacity: .3;
  transition: 0s;
}
</style>
