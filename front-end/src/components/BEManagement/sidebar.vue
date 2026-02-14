<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import MenuItem from './MenuItem.vue'
import tokenManager from '@/utils/tokenManager'

const router = useRouter()
const route = useRoute()

// 状态管理
const isCollapsed = ref(false)
const isDark = ref(false)
const userMenuOpen = ref(false)
const activeMenu = ref('/BEManagement')

// 监听路由变化，更新激活菜单
watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})

// 用户信息
const userInfo = reactive({
  username: '刘梦达',
  avatar: '',
  role: '超级管理员'
})

// 统计数据
const statistics = ref([
  { label: '总用户数', value: '12,345', trend: '+12%', icon: 'fa-users', bgColor: 'bg-blue-100 dark:bg-blue-900/30', iconColor: 'text-blue-600 dark:text-blue-400' },
  { label: '商家总数', value: '856', trend: '+5%', icon: 'fa-store', bgColor: 'bg-purple-100 dark:bg-purple-900/30', iconColor: 'text-purple-600 dark:text-purple-400' },
  { label: '店铺数量', value: '2,345', trend: '+8%', icon: 'fa-shop', bgColor: 'bg-green-100 dark:bg-green-900/30', iconColor: 'text-green-600 dark:text-green-400' },
  { label: '商品总数', value: '45,678', trend: '+15%', icon: 'fa-box', bgColor: 'bg-yellow-100 dark:bg-yellow-900/30', iconColor: 'text-yellow-600 dark:text-yellow-400' }
])

// 菜单配置 - 按组分类
const coreMenus = [
  { path: '/BEManagement', name: '仪表盘', icon: 'fa-chart-pie', badge: null },
  { path: '/analytics', name: '数据分析', icon: 'fa-chart-line', badge: 'new' }
]

const userMenus = [
  { path: '/UserManagement', name: '用户管理', icon: 'fa-user-group', badge: '12' },
  { path: '/user-roles', name: '角色权限', icon: 'fa-shield', badge: null },
  { path: '/user-logs', name: '操作日志', icon: 'fa-history', badge: null }
]

const merchantMenus = [
  { path: '/merchants', name: '商家管理', icon: 'fa-store', badge: '3' },
  { path: '/merchant-verify', name: '商家审核', icon: 'fa-check-circle', badge: '8' },
  { path: '/merchant-settle', name: '入驻申请', icon: 'fa-file-signature', badge: '5' },
  { path: '/merchant-level', name: '等级管理', icon: 'fa-ranking-star', badge: null }
]

const productMenus = [
  { path: '/shops', name: '店铺管理', icon: 'fa-shop', badge: null },
  { path: '/shop-verify', name: '店铺审核', icon: 'fa-clipboard-check', badge: '6' },
  { path: '/shop-category', name: '店铺分类', icon: 'fa-tags', badge: null },
  { path: '/products', name: '商品管理', icon: 'fa-box', badge: '24' },
  { path: '/product-category', name: '商品分类', icon: 'fa-sitemap', badge: null },
  { path: '/product-reviews', name: '商品评价', icon: 'fa-star', badge: '18' },
  { path: '/product-brands', name: '品牌管理', icon: 'fa-copyright', badge: null }
]

const systemMenus = [
  { path: '/settings', name: '系统设置', icon: 'fa-cog', badge: null },
  { path: '/logs', name: '系统日志', icon: 'fa-file-lines', badge: null },
  { path: '/backup', name: '备份恢复', icon: 'fa-database', badge: null }
]

// 当前路由名称
const currentRouteName = computed(() => {
  const allMenus = [...coreMenus, ...userMenus, ...merchantMenus, ...productMenus, ...systemMenus]
  const current = allMenus.find(m => m.path === route.path)
  return current?.name || '仪表盘'
})

// 方法
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const toggleDarkMode = () => {
  isDark.value = !isDark.value
  if (isDark.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}

const toggleUserMenu = () => {
  userMenuOpen.value = !userMenuOpen.value
}

const setActiveMenu = (path) => {
  activeMenu.value = path
  router.push(path)
}

const handleLogout = () => {
  // 退出登录逻辑
  console.log('退出登录')
  tokenManager.clearAccessToken()
  router.push('/login')
}

// 点击外部关闭用户菜单
const handleClickOutside = (e) => {
  if (!e.target.closest('.relative')) {
    userMenuOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  // 组件挂载时根据当前路由设置激活菜单
  activeMenu.value = route.path
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-950 flex">
    <!-- 侧边栏 - 玻璃态设计 -->
    <aside
      class="fixed left-0 top-0 h-screen w-72 bg-white/80 dark:bg-gray-900/80 backdrop-blur-xl border-r border-gray-200/50 dark:border-gray-700/50 flex flex-col transition-all duration-300 z-50 shadow-xl"
      :class="{ 'w-20': isCollapsed }"
    >
      <!-- Logo区域 -->
      <div class="flex items-center justify-between h-20 px-6 border-b border-gray-200/50 dark:border-gray-700/50">
        <div class="flex items-center space-x-3">
          <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center shadow-lg shadow-blue-500/20">
            <span class="text-white font-bold text-xl">L</span>
          </div>
          <transition name="fade">
            <span v-if="!isCollapsed" class="text-xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
              落叶·Admin
            </span>
          </transition>
        </div>
        <!-- 折叠按钮 -->
        <button
          @click="toggleSidebar"
          class="w-8 h-8 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center justify-center transition-all hover:scale-110"
        >
          <i :class="['fas', isCollapsed ? 'fa-chevron-right' : 'fa-chevron-left', 'text-gray-500 dark:text-gray-400']"></i>
        </button>
      </div>

      <!-- 用户信息卡片 -->
      <div v-if="!isCollapsed" class="mx-4 mt-6 p-4 rounded-2xl bg-gradient-to-br from-blue-50 to-purple-50 dark:from-gray-800 dark:to-gray-800 border border-blue-100/50 dark:border-gray-700/50">
        <div class="flex items-center space-x-3">
          <div class="w-12 h-12 rounded-xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-semibold text-lg shadow-lg">
            {{ userInfo.username?.charAt(0) || 'A' }}
          </div>
          <div>
            <p class="font-semibold text-gray-900 dark:text-white">{{ userInfo.username || '管理员' }}</p>
            <p class="text-xs text-gray-500 dark:text-gray-400 flex items-center mt-1">
              <span class="w-2 h-2 bg-green-500 rounded-full mr-2 animate-pulse"></span>
              在线
            </p>
          </div>
        </div>
      </div>

      <!-- 导航菜单 - 高级分组设计 -->
      <nav class="flex-1 overflow-y-auto py-6 px-4">
        <!-- 核心管理 -->
        <div v-if="!isCollapsed" class="mb-6">
          <p class="text-xs font-semibold text-gray-400 dark:text-gray-500 uppercase tracking-wider px-3 mb-3">
            核心管理
          </p>
          <div class="space-y-1">
            <MenuItem
              v-for="item in coreMenus"
              :key="item.path"
              :item="item"
              :collapsed="isCollapsed"
              :active="activeMenu === item.path"
              @click="setActiveMenu"
            />
          </div>
        </div>

        <!-- 用户管理组 -->
        <div class="mb-6">
          <p v-if="!isCollapsed" class="text-xs font-semibold text-gray-400 dark:text-gray-500 uppercase tracking-wider px-3 mb-3">
            用户中心
          </p>
          <div class="space-y-1">
            <MenuItem
              v-for="item in userMenus"
              :key="item.path"
              :item="item"
              :collapsed="isCollapsed"
              :active="activeMenu === item.path"
              @click="setActiveMenu"
            />
          </div>
        </div>

        <!-- 商家管理组 -->
        <div class="mb-6">
          <p v-if="!isCollapsed" class="text-xs font-semibold text-gray-400 dark:text-gray-500 uppercase tracking-wider px-3 mb-3">
            商家运营
          </p>
          <div class="space-y-1">
            <MenuItem
              v-for="item in merchantMenus"
              :key="item.path"
              :item="item"
              :collapsed="isCollapsed"
              :active="activeMenu === item.path"
              @click="setActiveMenu"
            />
          </div>
        </div>

        <!-- 商品管理组 -->
        <div class="mb-6">
          <p v-if="!isCollapsed" class="text-xs font-semibold text-gray-400 dark:text-gray-500 uppercase tracking-wider px-3 mb-3">
            商品管理
          </p>
          <div class="space-y-1">
            <MenuItem
              v-for="item in productMenus"
              :key="item.path"
              :item="item"
              :collapsed="isCollapsed"
              :active="activeMenu === item.path"
              @click="setActiveMenu"
            />
          </div>
        </div>

        <!-- 系统管理组 -->
        <div class="mb-6">
          <p v-if="!isCollapsed" class="text-xs font-semibold text-gray-400 dark:text-gray-500 uppercase tracking-wider px-3 mb-3">
            系统设置
          </p>
          <div class="space-y-1">
            <MenuItem
              v-for="item in systemMenus"
              :key="item.path"
              :item="item"
              :collapsed="isCollapsed"
              :active="activeMenu === item.path"
              @click="setActiveMenu"
            />
          </div>
        </div>
      </nav>

      <!-- 底部升级卡片 -->
      <div v-if="!isCollapsed" class="m-4 p-4 rounded-2xl bg-gradient-to-br from-gray-900 to-gray-800 dark:from-gray-950 dark:to-gray-900 text-white">
        <div class="flex items-center justify-between mb-2">
          <span class="text-sm font-semibold">高级版</span>
          <span class="text-xs px-2 py-1 bg-white/20 rounded-full">PRO</span>
        </div>
        <p class="text-xs text-gray-300 mb-3">解锁更多高级功能</p>
        <button class="w-full py-2 text-xs bg-white/10 hover:bg-white/20 rounded-lg transition-all duration-300 flex items-center justify-center space-x-2">
          <i class="fas fa-crown text-yellow-400"></i>
          <span>立即升级</span>
        </button>
      </div>

      <!-- 折叠状态下的简化底部 -->
      <div v-else class="m-2 p-3 flex justify-center">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-yellow-500 to-orange-600 flex items-center justify-center">
          <i class="fas fa-crown text-white"></i>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="flex-1 transition-all duration-300" :class="{ 'ml-72': !isCollapsed, 'ml-20': isCollapsed }">
      <!-- 现代化导航栏 -->
      <nav class="sticky top-0 z-40 bg-white/80 dark:bg-gray-900/80 backdrop-blur-xl border-b border-gray-200/50 dark:border-gray-700/50 h-16 flex items-center justify-between px-8 shadow-sm">
        <!-- 左侧：面包屑和搜索 -->
        <div class="flex items-center space-x-6">
          <div class="flex items-center space-x-2 text-sm">
            <span class="text-gray-400">首页</span>
            <i class="fas fa-chevron-right text-gray-400 text-xs"></i>
            <span class="text-gray-900 dark:text-white font-medium">{{ currentRouteName }}</span>
          </div>

          <!-- 搜索框 - 现代化设计 -->
          <div class="relative group">
            <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <i class="fas fa-search text-gray-400 group-focus-within:text-blue-500 transition-colors"></i>
            </div>
            <input
              type="text"
              placeholder="搜索菜单、功能或设置..."
              class="w-80 pl-10 pr-4 py-2 rounded-xl border border-gray-200/50 dark:border-gray-700/50 bg-gray-50/50 dark:bg-gray-800/50 focus:bg-white dark:focus:bg-gray-800 focus:border-blue-500 dark:focus:border-blue-400 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all text-sm"
            />
            <div class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
              <span class="text-xs text-gray-400 border border-gray-200 dark:border-gray-700 px-1.5 py-0.5 rounded-md">⌘K</span>
            </div>
          </div>
        </div>

        <!-- 右侧：通知、用户等 -->
        <div class="flex items-center space-x-4">
          <!-- 快捷操作按钮组 -->
          <div class="hidden md:flex items-center space-x-1">
            <button class="w-9 h-9 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center justify-center transition-all hover:scale-110">
              <i class="fas fa-sync-alt text-gray-600 dark:text-gray-300"></i>
            </button>
            <button class="w-9 h-9 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center justify-center transition-all hover:scale-110">
              <i class="fas fa-calendar text-gray-600 dark:text-gray-300"></i>
            </button>
          </div>

          <!-- 消息通知 -->
          <button class="relative p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition-all group">
            <i class="fas fa-bell text-gray-600 dark:text-gray-300 group-hover:text-blue-500 transition-colors"></i>
            <span class="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full ring-2 ring-white dark:ring-gray-900 animate-pulse"></span>
          </button>

          <!-- 语言切换 -->
          <button class="px-3 py-1.5 rounded-lg border border-gray-200 dark:border-gray-700 hover:bg-gray-100 dark:hover:bg-gray-800 transition-all flex items-center space-x-2 text-sm">
            <i class="fas fa-language text-gray-600 dark:text-gray-300"></i>
            <span class="text-gray-700 dark:text-gray-200 hidden sm:inline">简体中文</span>
          </button>

          <!-- 主题切换 -->
          <button
            @click="toggleDarkMode"
            class="w-9 h-9 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center justify-center transition-all hover:scale-110"
          >
            <i :class="['fas', isDark ? 'fa-sun text-yellow-500' : 'fa-moon text-gray-600', 'text-lg']"></i>
          </button>

          <!-- 用户菜单 -->
          <div class="relative">
            <button
              @click="toggleUserMenu"
              class="flex items-center space-x-3 focus:outline-none group"
            >
              <div class="relative">
                <div class="w-9 h-9 rounded-lg bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-semibold group-hover:scale-110 transition-all shadow-lg shadow-blue-500/20">
                  {{ userInfo.username?.charAt(0) || 'A' }}
                </div>
                <span class="absolute bottom-0 right-0 w-2.5 h-2.5 bg-green-500 border-2 border-white dark:border-gray-900 rounded-full"></span>
              </div>
              <div class="hidden lg:block text-left">
                <p class="text-sm font-semibold text-gray-900 dark:text-white">{{ userInfo.username || '管理员' }}</p>
                <p class="text-xs text-gray-500 dark:text-gray-400">超级管理员</p>
              </div>
              <i class="fas fa-chevron-down text-xs text-gray-400"></i>
            </button>

            <!-- 下拉菜单 - 现代化设计 -->
            <div v-if="userMenuOpen" class="absolute right-0 mt-2 w-64 bg-white dark:bg-gray-900 rounded-2xl shadow-2xl border border-gray-200/50 dark:border-gray-700/50 py-2 z-50 backdrop-blur-xl">
              <div class="px-4 py-3 border-b border-gray-100 dark:border-gray-800">
                <p class="text-sm font-semibold text-gray-900 dark:text-white">{{ userInfo.username || '管理员' }}</p>
                <p class="text-xs text-gray-500 dark:text-gray-400 mt-0.5">admin@example.com</p>
              </div>
              <div class="py-1">
                <a href="#" class="flex items-center px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
                  <i class="fas fa-user w-5 text-gray-500"></i>
                  <span class="ml-2">个人资料</span>
                </a>
                <a href="#" class="flex items-center px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
                  <i class="fas fa-cog w-5 text-gray-500"></i>
                  <span class="ml-2">账户设置</span>
                </a>
                <a href="#" class="flex items-center px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
                  <i class="fas fa-shield-alt w-5 text-gray-500"></i>
                  <span class="ml-2">安全中心</span>
                  <span class="ml-auto text-xs px-2 py-0.5 bg-green-100 dark:bg-green-900 text-green-600 dark:text-green-400 rounded-full">已保护</span>
                </a>
              </div>
              <div class="border-t border-gray-100 dark:border-gray-800 py-1">
                <button @click="handleLogout" class="w-full flex items-center px-4 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
                  <i class="fas fa-sign-out-alt w-5"></i>
                  <span class="ml-2">退出登录</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </nav>
      <main class="p-2">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
/* 菜单项动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 自定义滚动条样式 */
.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 4px;
}

.dark .overflow-y-auto::-webkit-scrollbar-thumb {
  background: #374151;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

.dark .overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #4b5563;
}
</style>
