<template>
  <contextHolder />
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900 relative overflow-hidden">
    <!-- 动态背景网格 + 光晕 -->
    <div class="absolute inset-0"></div>

  <!-- 抽象漂浮粒子 (伪元素用div模拟) -->
  <div class="absolute top-20 left-[10%] w-72 h-72 bg-blue-500/30 rounded-full blur-[100px] animate-pulse"></div>
  <div class="absolute bottom-20 right-[10%] w-80 h-80 bg-purple-500/30 rounded-full blur-[120px] animate-pulse delay-1000"></div>
  <div class="absolute top-1/3 right-1/4 w-64 h-64 bg-cyan-500/20 rounded-full blur-[90px] animate-pulse delay-500"></div>

  <!-- 登录卡片 — 现代玻璃质感 + 微边框 -->
  <div class="relative z-10 w-full max-w-md p-8 space-y-8 backdrop-blur-xl bg-white/10 dark:bg-black/20 rounded-3xl shadow-[0_20px_50px_rgba(0,0,0,0.3)] border border-white/20 dark:border-white/10">

    <!-- 标题区域：极简 + 发光字 -->
    <div class="text-center">
      <h1 class="text-4xl font-light tracking-wider text-white drop-shadow-lg">
        <span class="font-semibold bg-gradient-to-r from-blue-200 to-purple-200 bg-clip-text text-transparent">WELCOME</span>
      </h1>
      <p class="mt-3 text-sm font-extralight text-white/70">FallenLeavesMall</p>
    </div>

    <!-- 表单区域 -->
    <form @submit.prevent="handleLogin" class="space-y-5">
      <!-- 账号输入 —— 极简下划线风格，悬浮动效 -->
      <div class="group">
        <label for="account" class="block mb-1 text-xs font-medium tracking-wide text-white/60 group-focus-within:text-white/90 transition-colors">账号</label>
        <div class="relative">
          <input
            type="text"
            id="account"
            v-model="form.account"
            required
            class="w-full py-3 bg-transparent border-b-2 border-white/20 text-white placeholder-white/30 text-lg outline-none focus:border-blue-400 transition-all duration-300 peer"
            placeholder=" "
          />
          <span class="absolute left-0 -bottom-[1px] w-0 h-[2px] bg-gradient-to-r from-blue-400 to-purple-400 transition-all duration-500 peer-focus:w-full"></span>
        </div>
      </div>

      <!-- 密码输入 —— 同样下划线 + 显示切换 -->
      <div class="group">
        <label for="password" class="block mb-1 text-xs font-medium tracking-wide text-white/60 group-focus-within:text-white/90 transition-colors">密码</label>
        <div class="relative">
          <input
            :type="showPassword ? 'text' : 'password'"
            id="password"
            v-model="form.password"
            required
            class="w-full py-3 bg-transparent border-b-2 border-white/20 text-white placeholder-white/30 text-lg outline-none focus:border-blue-400 transition-all duration-300 peer pr-10"
            placeholder=" "
          />
          <span class="absolute left-0 -bottom-[1px] w-0 h-[2px] bg-gradient-to-r from-blue-400 to-purple-400 transition-all duration-500 peer-focus:w-full"></span>

          <!-- 眼睛图标优化 -->
          <button
            type="button"
            @click="togglePasswordVisibility"
            class="absolute right-0 bottom-3 text-white/50 hover:text-white/90 transition-colors"
          >
            <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
          </button>
        </div>
      </div>

      <!-- 协议同意 + 忘记密码 在一行 -->
      <div class="flex items-center justify-between text-sm">
        <label class="flex items-center gap-2 text-white/70 cursor-pointer group">
          <input
            type="checkbox"
            v-model="form.agreement"
            required
            class="w-4 h-4 rounded border-white/30 bg-white/10 checked:bg-gradient-to-r from-blue-500 to-purple-500 focus:ring-0 focus:ring-offset-0 text-transparent"
          />
          <span class="group-hover:text-white/90 transition">同意条款</span>
        </label>
        <a href="#" class="text-white/50 hover:text-white/90 transition-all border-b border-transparent hover:border-white/30 text-xs">忘记密码?</a>
      </div>

      <!-- 错误信息 现代警示框 -->
      <div v-if="errorMessage" class="px-4 py-3 text-sm text-amber-200 bg-amber-500/20 border border-amber-500/30 rounded-xl backdrop-blur-sm">
        <i class="fas fa-exclamation-circle mr-2"></i> {{ errorMessage }}
      </div>

      <!-- 登录按钮 — 玻璃态 + 悬浮光效 -->
      <button
        type="submit"
        :disabled="isLoading"
        class="relative w-full py-3.5 mt-6 font-medium text-white rounded-xl bg-gradient-to-r from-blue-500/80 to-purple-500/80 backdrop-blur-sm border border-white/30 shadow-[0_8px_20px_rgba(0,0,0,0.3)] hover:from-blue-500 hover:to-purple-500 hover:shadow-[0_8px_25px_rgba(168,85,247,0.4)] disabled:opacity-50 disabled:pointer-events-none transition-all duration-300 group overflow-hidden"
      >
          <span class="relative z-10 flex items-center justify-center gap-2">
            <i v-if="isLoading" class="fas fa-spinner fa-spin"></i>
            <span>{{ isLoading ? '登录中...' : '进入' }}</span>
            <i v-if="!isLoading" class="fas fa-arrow-right group-hover:translate-x-1 transition-transform"></i>
          </span>
        <!-- 按钮内部光晕 -->
        <span class="absolute inset-0 bg-white/20 opacity-0 group-hover:opacity-100 transition-opacity"></span>
      </button>
    </form>

    <!-- 注册引导 -->
    <div class="text-center text-white/50 text-sm">
      还没有账号？
      <a href="#" class="text-white/90 font-medium border-b border-white/30 hover:text-white transition">立即注册</a>
    </div>

    <!-- 装饰分隔线 现代方式 -->
    <div class="flex items-center gap-3 text-white/30 text-xs">
      <span class="h-px flex-1 bg-white/20"></span>
      <span>MODERN SPACE</span>
      <span class="h-px flex-1 bg-white/20"></span>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { message } from "ant-design-vue";
import { useThemeStore } from '../stores/theme';
import router from "@/router/index.js";
import tokenManager from '../utils/tokenManager';
import { post } from "@/net/index.js";

const themeStore = useThemeStore();
const [messageApi, contextHolder] = message.useMessage();

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
  errorMessage.value = '';

  if (!form.account.trim()) {
    errorMessage.value = '账号不能为空';
    return;
  }
  if (!form.password) {
    errorMessage.value = '密码不能为空';
    return;
  }
  if (!form.agreement) {
    errorMessage.value = '请同意用户协议';
    return;
  }

  try {
    isLoading.value = true;

    await post(
      'api/auth/login',
      {
        account: form.account,
        password: form.password
      },
      // success
      (message, data) => {
        tokenManager.setAccessToken(data.accessToken);
        if (data && (data.id != null || data.username != null)) {
          tokenManager.setUserInfo(data);
        }
        messageApi.success({
          content: message,
          icon: () => '🎉',
        });
        setTimeout(() => {
          router.push('/User');
        }, 500);
      },
      // failure
      (msg) => {
        console.error('登录业务失败:', msg);
        errorMessage.value = msg || '登录失败';
        messageApi.error(msg || '登录失败');
      },
      // error
      () => {
        console.error('登录系统错误');
        errorMessage.value = '网络错误，请稍后重试';
        messageApi.error('网络错误，请稍后重试');
      },
      false
    );

  } catch (error) {
    console.error('登录异常:', error);
    errorMessage.value = '登录失败，请检查账号密码是否正确';
  } finally {
    // 延迟清空密码和loading状态
    setTimeout(() => {
      form.password = '';
      isLoading.value = false;
    }, 1500);
  }
};
</script>

<style>
/* 导入Font Awesome 6 (免费版) */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css');

/* 基础重置 & 字体 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', 'Segoe UI', system-ui, -apple-system, sans-serif;
  background: #0f0f1a;
}

/* 自定义复选框样式 (现代简洁) */
input[type="checkbox"] {
  appearance: none;
  background-color: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 4px;
  width: 1.2rem;
  height: 1.2rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  cursor: pointer;
}

input[type="checkbox"]:checked {
  background: linear-gradient(135deg, #3b82f6, #a855f7);
  border-color: transparent;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='white'%3E%3Cpath d='M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z'/%3E%3C/svg%3E");
  background-size: 70%;
  background-position: center;
  background-repeat: no-repeat;
}

/* 输入框 autofill 背景修正 */
input:-webkit-autofill,
input:-webkit-autofill:hover,
input:-webkit-autofill:focus {
  -webkit-text-fill-color: white;
  -webkit-box-shadow: 0 0 0px 1000px transparent inset;
  transition: background-color 5000s ease-in-out 0s;
  caret-color: white;
}

/* 滚动条 */
::-webkit-scrollbar {
  width: 5px;
}
::-webkit-scrollbar-track {
  background: rgba(255,255,255,0.05);
}
::-webkit-scrollbar-thumb {
  background: rgba(255,255,255,0.2);
  border-radius: 20px;
}
::-webkit-scrollbar-thumb:hover {
  background: rgba(255,255,255,0.4);
}
</style>
