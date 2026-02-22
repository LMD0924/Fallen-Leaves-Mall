<script setup>
import {ref, reactive, computed, watch, onMounted, onUnmounted} from 'vue'

import {get, post} from '@/net/index.js'
import { ElMessage } from 'element-plus'

// ============ 状态变量 ============
const totalUsers = ref(0)
const todayNewUsers = ref(0)
const userGrowth = ref(0)
const todayCompare = ref(0)
const activeUsers = ref(0)
const activeRate = ref(0)
const vipUsers = ref(0)
const vipRate = ref(0)
const pendingReview = ref(0)

// 筛选状态
const showUserTypeDropdown = ref(false)
const showStatusDropdown = ref(false)
const showDateDropdown = ref(false)
const userTypeFilter = ref('all')
const statusFilter = ref('all')
const dateRangeText = ref('最近30天')
const searchKeyword = ref('')

// 表格选中状态
const selectedUsers = ref([])
const currentPage = ref(1)
const pageSize = ref(10)

// 模态框状态
const showUserModal = ref(false)
const modalMode = ref('create') // 'create' or 'edit'
const userForm = reactive({
  id: null,
  locked: 0,
  status: 0
})

// 详情抽屉
const showDetailDrawer = ref(false)
const currentUserDetail = ref(null)

// ============ 后端数据 ============
const users = ref([])
const loading = ref(false)

// 获取用户数据
const fetchUsers = async () => {
  loading.value = true
  try {
    await get('/api/user/selectAllUser', null, (message, data) => {
      users.value = data
      console.log(users.value)
      totalUsers.value = data.length
      // 计算统计数据
      activeUsers.value = data.filter(user => user.locked === 0 && user.status === 1).length
      vipUsers.value = data.filter(user => user.role === '管理员').length
      pendingReview.value = data.filter(user => user.locked === 0 && user.status === 0).length
      activeRate.value = totalUsers.value > 0 ? Math.round((activeUsers.value / totalUsers.value) * 100 * 10) / 10 : 0
      vipRate.value = totalUsers.value > 0 ? Math.round((vipUsers.value / totalUsers.value) * 100) : 0
    }, (message) => {
      console.error('获取用户数据失败:', message)
    })
  } catch (error) {
    console.error('获取用户数据出错:', error)
  } finally {
    loading.value = false
  }
}

//管理员审核用户信息
const reviewUser = async () =>{
  loading.value= true
  post("api/user/updateUser",{
  },(message,data)=>{
    messageApi.success(message)
  })
}

// 页面加载时获取用户数据
onMounted(() => {
  fetchUsers()
})

// ============ 计算属性 ============
const filteredUsers = computed(() => {
  return users.value.filter(user => {
    // 用户类型筛选
    if (userTypeFilter.value === 'admin' && user.role !== '管理员') return false
    if (userTypeFilter.value === 'normal' && user.role === '管理员') return false

    // 状态筛选
    if (statusFilter.value !== 'all') {
      if (statusFilter.value === 'locked' && user.locked !== 1) return false
      if (statusFilter.value === 'active' && (user.locked !== 0 || user.status !== 1)) return false
      if (statusFilter.value === 'pending' && (user.locked !== 0 || user.status !== 0)) return false
      if (statusFilter.value === 'rejected' && (user.locked !== 0 || user.status !== 2)) return false
    }

    // 搜索关键词
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      return user.username.toLowerCase().includes(keyword) ||
        user.email.toLowerCase().includes(keyword) ||
        user.phone.includes(keyword) ||
        user.account.toLowerCase().includes(keyword)
    }

    return true
  })
})

const totalPages = computed(() => {
  return Math.ceil(filteredUsers.value.length / pageSize.value)
})

const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredUsers.value.slice(start, end)
})

const selectAll = computed({
  get: () => selectedUsers.value.length === paginatedUsers.value.length && paginatedUsers.value.length > 0,
  set: (value) => {
    if (value) {
      selectedUsers.value = paginatedUsers.value.map(u => u.id)
    } else {
      selectedUsers.value = []
    }
  }
})

const displayedPages = computed(() => {
  const pages = []
  const total = totalPages.value
  let start = Math.max(1, currentPage.value - 2)
  let end = Math.min(total, start + 4)

  if (end - start < 4) {
    start = Math.max(1, end - 4)
  }

  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// ============ 方法 ============
const toggleUserTypeDropdown = () => {
  showUserTypeDropdown.value = !showUserTypeDropdown.value
  showStatusDropdown.value = false
  showDateDropdown.value = false
}

const toggleStatusDropdown = () => {
  showStatusDropdown.value = !showStatusDropdown.value
  showUserTypeDropdown.value = false
  showDateDropdown.value = false
}

const toggleDateDropdown = () => {
  showDateDropdown.value = !showDateDropdown.value
  showUserTypeDropdown.value = false
  showStatusDropdown.value = false
}

const setUserTypeFilter = (type) => {
  userTypeFilter.value = type
  showUserTypeDropdown.value = false
  currentPage.value = 1
}

const setStatusFilter = (status) => {
  statusFilter.value = status
  showStatusDropdown.value = false
  currentPage.value = 1
}

const resetFilters = () => {
  userTypeFilter.value = 'all'
  statusFilter.value = 'all'
  searchKeyword.value = ''
  currentPage.value = 1
}

const formatNumber = (num) => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

// 用户操作
const openUserModal = () => {
  modalMode.value = 'create'
  Object.assign(userForm, {
    id: null,
    username: '',
    email: '',
    phone: '',
    vipLevel: 1,
    points: 0,
    status: 'active',
    remark: ''
  })
  showUserModal.value = true
}

const editUser = (user) => {
  modalMode.value = 'edit'
  Object.assign(userForm, {
    id: user.id,
    locked: user.locked,
    status: user.status
  })
  showUserModal.value = true
}

const closeUserModal = () => {
  showUserModal.value = false
}

const saveUser = async () => {
  try {
    if (modalMode.value === 'edit') {
      // 编辑用户，调用updateUser接口
      await post('/api/user/updateUser', userForm, (message, data) => {
        ElMessage.success('用户信息更新成功')
        closeUserModal()
        fetchUsers() // 重新获取用户列表
      }, (message) => {
        ElMessage.error('用户信息更新失败: ' + message)
      })
    }
  } catch (error) {
    console.error('保存用户数据出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const viewUserDetail = (user) => {
  currentUserDetail.value = user
  showDetailDrawer.value = true
}

const closeDetailDrawer = () => {
  showDetailDrawer.value = false
  currentUserDetail.value = null
}

const toggleUserStatus = async (user) => {
  try {
    const updatedUser = {
      id: user.id,
      locked: user.locked === 1 ? 0 : 1,
      status: user.status
    }
    await post('/api/user/updateUser', updatedUser, (message, data) => {
      ElMessage.success(user.locked === 1 ? '用户已启用' : '用户已禁用')
      fetchUsers() // 重新获取用户列表
    }, (message) => {
      ElMessage.error('操作失败: ' + message)
    })
  } catch (error) {
    console.error('切换用户状态出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const deleteUser = (user) => {
  if (confirm(`确定要删除用户 ${user.name} 吗？`)) {
    const index = users.value.findIndex(u => u.id === user.id)
    if (index !== -1) {
      users.value.splice(index, 1)
    }
  }
}

const handleExport = () => {
  alert('开始导出用户数据...')
}

// 监听页码变化
watch([userTypeFilter, statusFilter, searchKeyword], () => {
  currentPage.value = 1
})

// 点击外部关闭下拉菜单
const handleClickOutside = (e) => {
  if (!e.target.closest('.relative')) {
    showUserTypeDropdown.value = false
    showStatusDropdown.value = false
    showDateDropdown.value = false
  }
}

// 生命周期
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div class="space-y-6 page-enter-active">
    <!-- 页面标题与操作区 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white flex items-center gap-3">
          <span class="w-1.5 h-6 bg-gradient-to-b from-blue-500 to-purple-500 rounded-full"></span>
          用户管理
        </h1>
        <p class="text-sm text-gray-500 dark:text-gray-400 mt-1.5 ml-3">
          共 {{ totalUsers }} 位注册用户，今日新增 {{ todayNewUsers }} 人
        </p>
      </div>

      <!-- 操作按钮组 -->
      <div class="flex items-center gap-3">
        <button
          @click="handleExport"
          class="px-4 py-2.5 bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-xl text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-800 transition-all flex items-center gap-2 shadow-sm hover:shadow"
        >
          <i class="fas fa-download text-gray-500"></i>
          导出数据
        </button>
        <button
          @click="openUserModal"
          class="px-5 py-2.5 bg-gradient-to-r from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700 text-white rounded-xl text-sm font-medium transition-all flex items-center gap-2 shadow-lg shadow-blue-500/25 hover:shadow-xl hover:scale-105 active:scale-95"
        >
          <i class="fas fa-plus-circle"></i>
          新增用户
        </button>
      </div>
    </div>

    <!-- 数据概览卡片 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
      <div class="bg-white dark:bg-gray-900 rounded-xl p-5 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-lg transition-all group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">总用户数</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ formatNumber(totalUsers) }}</p>
          </div>
          <div class="w-11 h-11 rounded-xl bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center group-hover:scale-110 transition-transform">
            <i class="fas fa-users text-blue-600 dark:text-blue-400 text-xl"></i>
          </div>
        </div>
        <div class="mt-2 text-xs text-green-600 dark:text-green-400">
          <i class="fas fa-arrow-up mr-1"></i> {{ userGrowth }}% 较上月
        </div>
      </div>

      <div class="bg-white dark:bg-gray-900 rounded-xl p-5 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-lg transition-all group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">今日新增</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ todayNewUsers }}</p>
          </div>
          <div class="w-11 h-11 rounded-xl bg-green-100 dark:bg-green-900/30 flex items-center justify-center group-hover:scale-110 transition-transform">
            <i class="fas fa-user-plus text-green-600 dark:text-green-400 text-xl"></i>
          </div>
        </div>
        <div class="mt-2 text-xs text-gray-500 dark:text-gray-400">
          环比 {{ todayCompare }}%
        </div>
      </div>

      <div class="bg-white dark:bg-gray-900 rounded-xl p-5 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-lg transition-all group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">活跃用户</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ formatNumber(activeUsers) }}</p>
          </div>
          <div class="w-11 h-11 rounded-xl bg-yellow-100 dark:bg-yellow-900/30 flex items-center justify-center group-hover:scale-110 transition-transform">
            <i class="fas fa-user-check text-yellow-600 dark:text-yellow-400 text-xl"></i>
          </div>
        </div>
        <div class="mt-2 text-xs text-gray-500 dark:text-gray-400">
          活跃率 {{ activeRate }}%
        </div>
      </div>

      <div class="bg-white dark:bg-gray-900 rounded-xl p-5 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-lg transition-all group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">VIP用户</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ formatNumber(vipUsers) }}</p>
          </div>
          <div class="w-11 h-11 rounded-xl bg-purple-100 dark:bg-purple-900/30 flex items-center justify-center group-hover:scale-110 transition-transform">
            <i class="fas fa-crown text-purple-600 dark:text-purple-400 text-xl"></i>
          </div>
        </div>
        <div class="mt-2 text-xs text-gray-500 dark:text-gray-400">
          占比 {{ vipRate }}%
        </div>
      </div>

      <div class="bg-white dark:bg-gray-900 rounded-xl p-5 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-lg transition-all group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">待审核</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ pendingReview }}</p>
          </div>
          <div class="w-11 h-11 rounded-xl bg-orange-100 dark:bg-orange-900/30 flex items-center justify-center group-hover:scale-110 transition-transform">
            <i class="fas fa-clock text-orange-600 dark:text-orange-400 text-xl"></i>
          </div>
        </div>
        <div class="mt-2 text-xs text-red-600 dark:text-red-400">
          <i class="fas fa-exclamation-circle mr-1"></i> 需尽快处理
        </div>
      </div>
    </div>

    <!-- 筛选与搜索栏 -->
    <div class="bg-white dark:bg-gray-900 rounded-2xl p-5 border border-gray-200/50 dark:border-gray-700/50 shadow-sm">
      <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
        <!-- 左侧筛选组 -->
        <div class="flex flex-wrap items-center gap-3">
          <div class="relative">
            <button
              @click="toggleUserTypeDropdown"
              class="px-4 py-2.5 bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl text-sm flex items-center gap-2 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            >
              <i class="fas fa-filter text-gray-500"></i>
              <span class="text-gray-700 dark:text-gray-300">{{ userTypeFilter === 'all' ? '全部用户' : userTypeFilter === 'normal' ? '普通用户' : 'VIP用户' }}</span>
              <i class="fas fa-chevron-down text-gray-400 text-xs ml-1"></i>
            </button>

            <!-- 用户类型下拉菜单 -->
            <div v-if="showUserTypeDropdown" class="absolute top-full left-0 mt-1 w-48 bg-white dark:bg-gray-900 rounded-xl shadow-2xl border border-gray-200 dark:border-gray-700 py-2 z-20">
              <button @click="setUserTypeFilter('all')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center gap-2">
                <i class="fas fa-users w-4 text-gray-500"></i>
                <span :class="userTypeFilter === 'all' ? 'text-blue-600 dark:text-blue-400 font-medium' : 'text-gray-700 dark:text-gray-300'">全部用户</span>
              </button>
              <button @click="setUserTypeFilter('normal')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center gap-2">
                <i class="fas fa-user w-4 text-gray-500"></i>
                <span :class="userTypeFilter === 'normal' ? 'text-blue-600 dark:text-blue-400 font-medium' : 'text-gray-700 dark:text-gray-300'">普通用户</span>
              </button>
              <button @click="setUserTypeFilter('vip')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center gap-2">
                <i class="fas fa-crown w-4 text-gray-500"></i>
                <span :class="userTypeFilter === 'vip' ? 'text-blue-600 dark:text-blue-400 font-medium' : 'text-gray-700 dark:text-gray-300'">VIP用户</span>
              </button>
            </div>
          </div>

          <div class="relative">
            <button
              @click="toggleStatusDropdown"
              class="px-4 py-2.5 bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl text-sm flex items-center gap-2 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            >
              <i class="fas fa-circle text-gray-500"></i>
              <span class="text-gray-700 dark:text-gray-300">{{ statusFilter === 'all' ? '全部状态' : statusFilter === 'active' ? '已通过' : statusFilter === 'pending' ? '待审核' : statusFilter === 'rejected' ? '未通过' : '已禁用' }}</span>
              <i class="fas fa-chevron-down text-gray-400 text-xs ml-1"></i>
            </button>

            <div v-if="showStatusDropdown" class="absolute top-full left-0 mt-1 w-40 bg-white dark:bg-gray-900 rounded-xl shadow-2xl border border-gray-200 dark:border-gray-700 py-2 z-20">
              <button @click="setStatusFilter('all')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">全部状态</button>
              <button @click="setStatusFilter('active')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">已通过</button>
              <button @click="setStatusFilter('pending')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">待审核</button>
              <button @click="setStatusFilter('rejected')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">未通过</button>
              <button @click="setStatusFilter('locked')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">已禁用</button>
            </div>
          </div>

          <div class="relative">
            <button
              @click="toggleDateDropdown"
              class="px-4 py-2.5 bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl text-sm flex items-center gap-2 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            >
              <i class="fas fa-calendar text-gray-500"></i>
              <span class="text-gray-700 dark:text-gray-300">{{ dateRangeText }}</span>
              <i class="fas fa-chevron-down text-gray-400 text-xs ml-1"></i>
            </button>
          </div>

          <button class="px-4 py-2.5 text-sm text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800 rounded-xl transition-colors flex items-center gap-2">
            <i class="fas fa-redo-alt"></i>
            重置
          </button>
        </div>

        <!-- 右侧搜索框 -->
        <div class="relative flex-1 max-w-md">
          <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            <i class="fas fa-search text-gray-400"></i>
          </div>
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索用户名、手机号或邮箱..."
            class="w-full pl-10 pr-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-blue-500 dark:focus:border-blue-400 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all text-sm"
          />
          <div v-if="searchKeyword" class="absolute inset-y-0 right-0 pr-3 flex items-center">
            <button @click="searchKeyword = ''" class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
              <i class="fas fa-times-circle"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- 活跃标签快速筛选 -->
      <div class="flex items-center gap-2 mt-4 pt-4 border-t border-gray-200/50 dark:border-gray-700/50">
        <span class="text-xs text-gray-500 dark:text-gray-400 mr-1">快速筛选:</span>
        <button class="px-3 py-1.5 text-xs bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400 rounded-lg hover:bg-blue-100 dark:hover:bg-blue-900/40 transition-colors font-medium">
          今日活跃
        </button>
        <button class="px-3 py-1.5 text-xs bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors">
          近7日活跃
        </button>
        <button class="px-3 py-1.5 text-xs bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors">
          未登录 >30天
        </button>
        <button class="px-3 py-1.5 text-xs bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors">
          未实名认证
        </button>
      </div>
    </div>

    <!-- 用户列表表格 - 现代化卡片式表格 -->
    <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 overflow-hidden shadow-sm">
      <!-- 表格头部 - 选中操作栏 -->
      <div v-if="selectedUsers.length > 0" class="px-6 py-4 bg-blue-50/50 dark:bg-blue-900/10 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="w-6 h-6 rounded-full bg-blue-500 text-white flex items-center justify-center text-xs font-bold">{{ selectedUsers.length }}</span>
          <span class="text-sm text-gray-700 dark:text-gray-300">个用户已选中</span>
        </div>
        <div class="flex items-center gap-2">
          <button class="px-3 py-1.5 text-xs bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors flex items-center gap-1">
            <i class="fas fa-envelope text-gray-500"></i>
            发送消息
          </button>
          <button class="px-3 py-1.5 text-xs bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors flex items-center gap-1">
            <i class="fas fa-tag text-gray-500"></i>
            批量标记
          </button>
          <button class="px-3 py-1.5 text-xs bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/40 transition-colors flex items-center gap-1">
            <i class="fas fa-ban"></i>
            批量禁用
          </button>
        </div>
      </div>

      <!-- 表格主体 -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
          <tr class="border-b border-gray-200 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50">
            <th class="w-12 px-6 py-4">
              <label class="flex items-center">
                <input
                  type="checkbox"
                  v-model="selectAll"
                  class="w-4 h-4 rounded border-gray-300 dark:border-gray-600 text-blue-500 focus:ring-blue-500/20 focus:ring-offset-0"
                />
              </label>
            </th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">用户信息</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">联系方式</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">用户类型</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">状态</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">注册时间</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">更新时间</th>
            <th class="px-6 py-4 text-right text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">操作</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
          <tr v-for="user in paginatedUsers" :key="user.id" class="hover:bg-gray-50/50 dark:hover:bg-gray-800/50 transition-colors group">
            <td class="px-6 py-4">
              <label class="flex items-center">
                <input
                  type="checkbox"
                  v-model="selectedUsers"
                  :value="user.id"
                  class="w-4 h-4 rounded border-gray-300 dark:border-gray-600 text-blue-500 focus:ring-blue-500/20 focus:ring-offset-0"
                />
              </label>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="relative">
                  <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-semibold shadow-lg shadow-blue-500/20">
                    {{ user.username.charAt(0) }}
                  </div>
                  <span class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-green-500 border-2 border-white dark:border-gray-900 rounded-full" v-if="user.locked === 0 && user.status === 1"></span>
                  <span class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-red-500 border-2 border-white dark:border-gray-900 rounded-full" v-if="user.locked === 1"></span>
                  <span class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-yellow-500 border-2 border-white dark:border-gray-900 rounded-full" v-if="user.locked === 0 && user.status === 0"></span>
                  <span class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-gray-400 border-2 border-white dark:border-gray-900 rounded-full" v-if="user.locked === 0 && user.status === 2"></span>
                </div>
                <div>
                  <div class="flex items-center gap-2">
                    <span class="font-medium text-gray-900 dark:text-white">{{ user.username }}</span>
                    <span class="text-xs text-gray-500 dark:text-gray-400">{{ user.account }}</span>
                  </div>
                  <span class="text-xs text-gray-500 dark:text-gray-400">ID: {{ user.id }}</span>
                </div>
              </div>
            </td>
            <td class="px-6 py-4">
              <div class="text-sm text-gray-700 dark:text-gray-300">{{ user.email }}</div>
              <div class="text-xs text-gray-500 dark:text-gray-400 mt-0.5">{{ user.phone }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-1.5">
                <i class="fas fa-user-shield text-purple-500 text-sm" v-if="user.role === '管理员'"></i>
                <i class="fas fa-user text-gray-500 text-sm" v-else></i>
                <span class="text-sm font-medium text-gray-700 dark:text-gray-300">{{ user.role }}</span>
              </div>
            </td>
            <td class="px-6 py-4">
              <span v-if="user.locked === 1" class="px-2 py-1 text-xs font-medium bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400 rounded-full">
                    已禁用
                  </span>
              <span v-else-if="user.status === 0" class="px-2 py-1 text-xs font-medium bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600 dark:text-yellow-400 rounded-full">
                    待审核
                  </span>
              <span v-else-if="user.status === 1" class="px-2 py-1 text-xs font-medium bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400 rounded-full">
                    已通过
                  </span>
              <span v-else-if="user.status === 2" class="px-2 py-1 text-xs font-medium bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 rounded-full">
                    未通过
                  </span>
            </td>
            <td class="px-6 py-4">
              <div class="text-sm text-gray-700 dark:text-gray-300">{{ formatDate(user.createTime) }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="text-sm text-gray-700 dark:text-gray-300">{{ formatDate(user.updateTime) }}</div>
            </td>
            <td class="px-6 py-4 text-right">
              <div class="flex items-center justify-end gap-3">
                <button @click="editUser(user)" class="p-2.5 bg-blue-500 text-white hover:bg-blue-600 rounded-lg transition-all hover:scale-110 shadow-md" title="编辑">
                  <i class="fas fa-edit"></i>
                </button>
                <button @click="viewUserDetail(user)" class="p-2.5 bg-purple-500 text-white hover:bg-purple-600 rounded-lg transition-all hover:scale-110 shadow-md" title="详情">
                  <i class="fas fa-eye"></i>
                </button>
                <button @click="toggleUserStatus(user)" class="p-2.5 bg-yellow-500 text-white hover:bg-yellow-600 rounded-lg transition-all hover:scale-110 shadow-md" :title="user.locked === 1 ? '启用' : '禁用'">
                  <i :class="['fas', user.locked === 1 ? 'fa-check-circle' : 'fa-ban']"></i>
                </button>
                <button @click="deleteUser(user)" class="p-2.5 bg-red-500 text-white hover:bg-red-600 rounded-lg transition-all hover:scale-110 shadow-md" title="删除">
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </td>
          </tr>

          <!-- 空状态 -->
          <tr v-if="paginatedUsers.length === 0">
            <td colspan="8" class="px-6 py-16 text-center">
              <div class="flex flex-col items-center">
                <div class="w-20 h-20 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mb-4">
                  <i class="fas fa-users text-4xl text-gray-400"></i>
                </div>
                <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-1">暂无用户数据</h3>
                <p class="text-sm text-gray-500 dark:text-gray-400 mb-4">没有找到符合条件的用户</p>
                <button @click="resetFilters" class="px-5 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-xl text-sm font-medium transition-all">
                  清除筛选条件
                </button>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 表格底部 - 分页 -->
      <div class="px-6 py-4 border-t border-gray-200 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50 flex items-center justify-between">
        <div class="text-sm text-gray-500 dark:text-gray-400">
          显示第 {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, filteredUsers.length) }} 条，共 {{ filteredUsers.length }} 条记录
        </div>
        <div class="flex items-center gap-2">
          <button
            @click="currentPage--"
            :disabled="currentPage === 1"
            class="w-8 h-8 rounded-lg border border-gray-200 dark:border-gray-700 flex items-center justify-center hover:bg-gray-100 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <i class="fas fa-chevron-left text-sm"></i>
          </button>

          <button
            v-for="page in displayedPages"
            :key="page"
            @click="currentPage = page"
            :class="[
              'min-w-[32px] h-8 px-2 rounded-lg text-sm font-medium transition-colors',
              currentPage === page
                ? 'bg-gradient-to-r from-blue-500 to-purple-500 text-white shadow-md'
                : 'hover:bg-gray-100 dark:hover:bg-gray-700 text-gray-700 dark:text-gray-300'
            ]"
          >
            {{ page }}
          </button>

          <button
            @click="currentPage++"
            :disabled="currentPage === totalPages"
            class="w-8 h-8 rounded-lg border border-gray-200 dark:border-gray-700 flex items-center justify-center hover:bg-gray-100 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <i class="fas fa-chevron-right text-sm"></i>
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- 用户编辑/新增模态框 -->
  <Transition name="modal">
    <div v-if="showUserModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 transition-opacity" @click="closeUserModal">
          <div class="absolute inset-0 bg-gray-900/60 backdrop-blur-sm"></div>
        </div>

        <span class="hidden sm:inline-block sm:align-middle sm:h-screen">&#8203;</span>

        <div class="inline-block align-bottom bg-white dark:bg-gray-900 rounded-2xl text-left overflow-hidden shadow-2xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl sm:w-full">
          <!-- 模态框头部 -->
          <div class="px-6 py-5 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
              <span class="w-1 h-5 bg-gradient-to-b from-blue-500 to-purple-500 rounded-full"></span>
              {{ modalMode === 'create' ? '新增用户' : '编辑用户信息' }}
            </h3>
            <button @click="closeUserModal" class="w-8 h-8 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center justify-center transition-colors">
              <i class="fas fa-times text-gray-500"></i>
            </button>
          </div>

          <!-- 模态框内容 -->
          <div class="px-6 py-6">
            <form @submit.prevent="saveUser" class="space-y-5">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">账户状态</label>
                  <select
                    v-model="userForm.locked"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all text-sm"
                  >
                    <option value="0">启用</option>
                    <option value="1">禁用</option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">审核状态</label>
                  <select
                    v-model="userForm.status"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all text-sm"
                  >
                    <option value="0">待审核</option>
                    <option value="1">已通过</option>
                    <option value="2">已拒绝</option>
                    <option value="3">已禁用</option>
                  </select>
                </div>
              </div>

              <div class="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  @click="closeUserModal"
                  class="px-5 py-2.5 border border-gray-200 dark:border-gray-700 rounded-xl text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
                >
                  取消
                </button>
                <button
                  type="submit"
                  class="px-6 py-2.5 bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 text-white rounded-xl text-sm font-medium shadow-lg shadow-blue-500/25 hover:shadow-xl transition-all hover:scale-105 active:scale-95"
                >
                  保存修改
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </Transition>

  <!-- 用户详情抽屉 -->
  <Transition name="slide">
    <div v-if="showDetailDrawer" class="fixed inset-0 z-50 overflow-hidden">
      <div class="absolute inset-0 overflow-hidden">
        <div class="absolute inset-0 bg-gray-900/60 backdrop-blur-sm transition-opacity" @click="closeDetailDrawer"></div>

        <div class="fixed inset-y-0 right-0 flex max-w-full pl-10">
          <div class="relative w-screen max-w-2xl">
            <div class="flex h-full flex-col overflow-y-auto bg-white dark:bg-gray-900 shadow-2xl">
              <!-- 抽屉头部 -->
              <div class="px-6 py-6 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between bg-gradient-to-r from-blue-50 to-purple-50 dark:from-gray-800 dark:to-gray-800">
                <div class="flex items-center gap-4">
                  <div class="w-14 h-14 rounded-2xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-bold text-xl shadow-lg">
                    {{ currentUserDetail?.name?.charAt(0) || 'U' }}
                  </div>
                  <div>
                    <h3 class="text-xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
                      {{ currentUserDetail?.name }}
                      <span v-if="currentUserDetail?.verify" class="px-2 py-0.5 text-xs bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 rounded-full flex items-center gap-1">
                        <i class="fas fa-check-circle"></i> 已认证
                      </span>
                    </h3>
                    <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">用户ID: {{ currentUserDetail?.id }}</p>
                  </div>
                </div>
                <button @click="closeDetailDrawer" class="w-9 h-9 rounded-lg hover:bg-white/50 dark:hover:bg-gray-700 flex items-center justify-center transition-colors">
                  <i class="fas fa-times text-gray-600 dark:text-gray-400 text-lg"></i>
                </button>
              </div>

              <!-- 抽屉内容 -->
              <div class="flex-1 px-6 py-6 space-y-6">
                <!-- 信息卡片网格 -->
                <div class="grid grid-cols-2 gap-4">
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">会员等级</p>
                    <div class="flex items-center gap-2">
                      <i class="fas fa-crown text-yellow-500"></i>
                      <span class="text-lg font-bold text-gray-900 dark:text-white">VIP{{ currentUserDetail?.vipLevel }}</span>
                    </div>
                  </div>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">账户积分</p>
                    <div class="flex items-center gap-2">
                      <i class="fas fa-star text-purple-500"></i>
                      <span class="text-lg font-bold text-gray-900 dark:text-white">{{ currentUserDetail?.points || 0 }}</span>
                    </div>
                  </div>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">注册时间</p>
                    <div class="flex items-center gap-2">
                      <i class="fas fa-calendar text-blue-500"></i>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ formatDate(currentUserDetail?.createTime) }}</span>
                    </div>
                  </div>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">最后登录</p>
                    <div class="flex items-center gap-2">
                      <i class="fas fa-clock text-green-500"></i>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ formatDate(currentUserDetail?.updateTime) }}</span>
                    </div>
                  </div>
                </div>

                <!-- 详细信息 -->
                <div class="space-y-4">
                  <h4 class="font-medium text-gray-900 dark:text-white flex items-center gap-2">
                    <i class="fas fa-address-card text-gray-500"></i>
                    基本信息
                  </h4>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-5 space-y-4">
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">电子邮箱</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentUserDetail?.email }}</span>
                    </div>
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">手机号码</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentUserDetail?.phone }}</span>
                    </div>
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">账户状态</span>
                      <span
                        class="px-2.5 py-1 text-xs font-medium rounded-full"
                        :class="{
                          'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400': currentUserDetail?.locked === 0,
                          'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400': currentUserDetail?.locked === 1
                        }"
                      >
                        {{ currentUserDetail?.locked === 0 ? '启用' : '禁用' }}
                      </span>
                    </div>
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">审核状态</span>
                      <span
                        class="px-2.5 py-1 text-xs font-medium rounded-full"
                        :class="{
                          'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400': currentUserDetail?.status === 1,
                          'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400': currentUserDetail?.status === 0,
                          'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400': currentUserDetail?.status === 2,
                          'bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-400': currentUserDetail?.status === 3
                        }"
                      >
                        {{ currentUserDetail?.status === 0 ? '待审核' : currentUserDetail?.status === 1 ? '已通过' : currentUserDetail?.status === 2 ? '已拒绝' : '已禁用' }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- 统计概览 -->
                <div class="space-y-4">
                  <h4 class="font-medium text-gray-900 dark:text-white flex items-center gap-2">
                    <i class="fas fa-chart-line text-gray-500"></i>
                    数据概览
                  </h4>
                  <div class="grid grid-cols-3 gap-3">
                    <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-3 text-center">
                      <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">订单总数</p>
                      <p class="text-lg font-bold text-gray-900 dark:text-white">23</p>
                    </div>
                    <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-3 text-center">
                      <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">消费金额</p>
                      <p class="text-lg font-bold text-gray-900 dark:text-white">¥3,456</p>
                    </div>
                    <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-3 text-center">
                      <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">优惠券</p>
                      <p class="text-lg font-bold text-gray-900 dark:text-white">5</p>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 抽屉底部 -->
              <div class="px-6 py-4 border-t border-gray-200 dark:border-gray-700 flex items-center justify-end gap-3">
                <button @click="editUser(currentUserDetail)" class="px-5 py-2.5 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors flex items-center gap-2">
                  <i class="fas fa-edit"></i>
                  编辑资料
                </button>
                <button class="px-5 py-2.5 bg-gradient-to-r from-blue-500 to-purple-500 text-white rounded-xl text-sm font-medium shadow-lg hover:shadow-xl transition-all hover:scale-105 active:scale-95 flex items-center gap-2">
                  <i class="fas fa-envelope"></i>
                  发送消息
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
/* 模态框动画 */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

/* 抽屉动画 */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}

/* 表格行悬停效果 */
tbody tr {
  transition: all 0.2s ease;
}

/* 数字动画 */
@keyframes count-up {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 页面进入动画 */
@keyframes fade-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-enter-active {
  animation: fade-up 0.6s ease-out;
}

.stat-number {
  animation: count-up 0.5s ease-out;
}

/* 自定义复选框样式 */
input[type="checkbox"] {
  cursor: pointer;
  transition: all 0.2s ease;
}

input[type="checkbox"]:checked {
  background-image: url("data:image/svg+xml,%3csvg viewBox='0 0 16 16' fill='white' xmlns='http://www.w3.org/2000/svg'%3e%3cpath d='M12.207 4.793a1 1 0 010 1.414l-5 5a1 1 0 01-1.414 0l-2-2a1 1 0 011.414-1.414L6.5 9.086l4.293-4.293a1 1 0 011.414 0z'/%3e%3c/svg%3e");
  background-size: 100% 100%;
  background-position: 50%;
  background-repeat: no-repeat;
  background-color: #3b82f6;
  border-color: #3b82f6;
}
</style>
