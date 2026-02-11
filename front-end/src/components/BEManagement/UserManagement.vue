<template>
  <div class="space-y-6">
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
              <span class="text-gray-700 dark:text-gray-300">{{ statusFilter === 'all' ? '全部状态' : statusFilter === 'active' ? '已激活' : '已禁用' }}</span>
              <i class="fas fa-chevron-down text-gray-400 text-xs ml-1"></i>
            </button>

            <div v-if="showStatusDropdown" class="absolute top-full left-0 mt-1 w-40 bg-white dark:bg-gray-900 rounded-xl shadow-2xl border border-gray-200 dark:border-gray-700 py-2 z-20">
              <button @click="setStatusFilter('all')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">全部状态</button>
              <button @click="setStatusFilter('active')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">已激活</button>
              <button @click="setStatusFilter('inactive')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">已禁用</button>
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
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">会员等级</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">状态</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">注册时间</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">最后登录</th>
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
                    {{ user.name.charAt(0) }}
                  </div>
                  <span class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-green-500 border-2 border-white dark:border-gray-900 rounded-full" v-if="user.status === 'active'"></span>
                  <span class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-gray-400 border-2 border-white dark:border-gray-900 rounded-full" v-else></span>
                </div>
                <div>
                  <div class="flex items-center gap-2">
                    <span class="font-medium text-gray-900 dark:text-white">{{ user.name }}</span>
                    <span v-if="user.verify" class="px-1.5 py-0.5 text-xs bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 rounded flex items-center gap-0.5">
                        <i class="fas fa-check-circle text-xs"></i> 已认证
                      </span>
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
                <i class="fas fa-crown text-yellow-500 text-sm"></i>
                <span class="text-sm font-medium text-gray-900 dark:text-white">VIP{{ user.vipLevel }}</span>
              </div>
              <div class="text-xs text-gray-500 dark:text-gray-400 mt-0.5">{{ user.points }} 积分</div>
            </td>
            <td class="px-6 py-4">
                <span
                  class="px-2.5 py-1 text-xs font-medium rounded-full"
                  :class="{
                    'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400': user.status === 'active',
                    'bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-400': user.status === 'inactive',
                    'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400': user.status === 'pending'
                  }"
                >
                  {{ user.status === 'active' ? '已激活' : user.status === 'inactive' ? '已禁用' : '待审核' }}
                </span>
            </td>
            <td class="px-6 py-4 text-sm text-gray-700 dark:text-gray-300">
              {{ formatDate(user.registerDate) }}
            </td>
            <td class="px-6 py-4 text-sm text-gray-700 dark:text-gray-300">
              {{ formatDate(user.lastLogin) }}
            </td>
            <td class="px-6 py-4 text-right">
              <div class="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                <button @click="editUser(user)" class="p-1.5 text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg transition-colors" title="编辑">
                  <i class="fas fa-edit"></i>
                </button>
                <button @click="viewUserDetail(user)" class="p-1.5 text-gray-600 dark:text-gray-400 hover:text-purple-600 dark:hover:text-purple-400 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg transition-colors" title="详情">
                  <i class="fas fa-eye"></i>
                </button>
                <button @click="toggleUserStatus(user)" class="p-1.5 text-gray-600 dark:text-gray-400 hover:text-yellow-600 dark:hover:text-yellow-400 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg transition-colors" :title="user.status === 'active' ? '禁用' : '启用'">
                  <i :class="['fas', user.status === 'active' ? 'fa-ban' : 'fa-check-circle']"></i>
                </button>
                <button @click="deleteUser(user)" class="p-1.5 text-gray-600 dark:text-gray-400 hover:text-red-600 dark:hover:text-red-400 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg transition-colors" title="删除">
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
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">用户名</label>
                  <input
                    v-model="userForm.username"
                    type="text"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all text-sm"
                    placeholder="请输入用户名"
                    required
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">邮箱</label>
                  <input
                    v-model="userForm.email"
                    type="email"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all text-sm"
                    placeholder="请输入邮箱"
                    required
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">手机号</label>
                  <input
                    v-model="userForm.phone"
                    type="tel"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all text-sm"
                    placeholder="请输入手机号"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">会员等级</label>
                  <select
                    v-model="userForm.vipLevel"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all text-sm"
                  >
                    <option value="1">VIP 1</option>
                    <option value="2">VIP 2</option>
                    <option value="3">VIP 3</option>
                    <option value="4">VIP 4</option>
                    <option value="5">VIP 5</option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">初始积分</label>
                  <input
                    v-model.number="userForm.points"
                    type="number"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all text-sm"
                    placeholder="0"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">状态</label>
                  <div class="flex items-center h-10 gap-4">
                    <label class="flex items-center gap-2">
                      <input type="radio" v-model="userForm.status" value="active" class="w-4 h-4 text-blue-500 focus:ring-blue-500/20 border-gray-300" />
                      <span class="text-sm text-gray-700 dark:text-gray-300">启用</span>
                    </label>
                    <label class="flex items-center gap-2">
                      <input type="radio" v-model="userForm.status" value="inactive" class="w-4 h-4 text-gray-500 focus:ring-gray-500/20 border-gray-300" />
                      <span class="text-sm text-gray-700 dark:text-gray-300">禁用</span>
                    </label>
                  </div>
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">备注信息</label>
                <textarea
                  v-model="userForm.remark"
                  rows="3"
                  class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all text-sm"
                  placeholder="请输入备注信息..."
                ></textarea>
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
                  {{ modalMode === 'create' ? '创建用户' : '保存修改' }}
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
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ formatDate(currentUserDetail?.registerDate) }}</span>
                    </div>
                  </div>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">最后登录</p>
                    <div class="flex items-center gap-2">
                      <i class="fas fa-clock text-green-500"></i>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ formatDate(currentUserDetail?.lastLogin) }}</span>
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
                          'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400': currentUserDetail?.status === 'active',
                          'bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-400': currentUserDetail?.status === 'inactive'
                        }"
                      >
                        {{ currentUserDetail?.status === 'active' ? '已激活' : '已禁用' }}
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

<script setup>
import {ref, reactive, computed, watch, onMounted, onUnmounted} from 'vue'

// ============ 状态变量 ============
const totalUsers = ref(12345)
const todayNewUsers = ref(128)
const userGrowth = ref(12.5)
const todayCompare = ref(8.3)
const activeUsers = ref(8234)
const activeRate = ref(66.7)
const vipUsers = ref(3456)
const vipRate = ref(28)
const pendingReview = ref(23)

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
  username: '',
  email: '',
  phone: '',
  vipLevel: 1,
  points: 0,
  status: 'active',
  remark: ''
})

// 详情抽屉
const showDetailDrawer = ref(false)
const currentUserDetail = ref(null)

// ============ 模拟数据 ============
const users = ref([
  {
    id: 1001,
    name: '王小明',
    email: 'wang.xm@example.com',
    phone: '138****5678',
    vipLevel: 3,
    points: 2450,
    status: 'active',
    verify: true,
    registerDate: '2024-01-15T08:30:00',
    lastLogin: '2024-03-28T14:22:00'
  },
  {
    id: 1002,
    name: '李芳芳',
    email: 'li.ff@example.com',
    phone: '159****2341',
    vipLevel: 5,
    points: 8920,
    status: 'active',
    verify: true,
    registerDate: '2023-11-03T10:15:00',
    lastLogin: '2024-03-28T09:45:00'
  },
  {
    id: 1003,
    name: '张伟',
    email: 'zhang.w@example.com',
    phone: '177****8902',
    vipLevel: 2,
    points: 1230,
    status: 'inactive',
    verify: false,
    registerDate: '2024-02-20T16:40:00',
    lastLogin: '2024-03-15T11:30:00'
  },
  {
    id: 1004,
    name: '陈婷婷',
    email: 'chen.tt@example.com',
    phone: '152****6734',
    vipLevel: 4,
    points: 5670,
    status: 'active',
    verify: true,
    registerDate: '2023-09-08T13:20:00',
    lastLogin: '2024-03-28T16:15:00'
  },
  {
    id: 1005,
    name: '刘阳',
    email: 'liu.y@example.com',
    phone: '186****4523',
    vipLevel: 1,
    points: 450,
    status: 'pending',
    verify: false,
    registerDate: '2024-03-25T09:10:00',
    lastLogin: '2024-03-25T09:10:00'
  },
  {
    id: 1006,
    name: '赵雅茹',
    email: 'zhao.yr@example.com',
    phone: '139****7890',
    vipLevel: 5,
    points: 12450,
    status: 'active',
    verify: true,
    registerDate: '2023-05-12T11:25:00',
    lastLogin: '2024-03-27T20:50:00'
  },
  {
    id: 1007,
    name: '周杰',
    email: 'zhou.j@example.com',
    phone: '188****3467',
    vipLevel: 3,
    points: 3450,
    status: 'active',
    verify: true,
    registerDate: '2023-12-01T14:30:00',
    lastLogin: '2024-03-26T10:20:00'
  },
  {
    id: 1008,
    name: '吴倩',
    email: 'wu.q@example.com',
    phone: '151****9023',
    vipLevel: 2,
    points: 1890,
    status: 'inactive',
    verify: false,
    registerDate: '2024-02-28T08:45:00',
    lastLogin: '2024-03-20T15:40:00'
  },
  {
    id: 1009,
    name: '郑浩然',
    email: 'zheng.hr@example.com',
    phone: '155****6781',
    vipLevel: 4,
    points: 6780,
    status: 'active',
    verify: true,
    registerDate: '2023-10-17T09:50:00',
    lastLogin: '2024-03-28T11:05:00'
  },
  {
    id: 1010,
    name: '林心怡',
    email: 'lin.xy@example.com',
    phone: '157****2348',
    vipLevel: 3,
    points: 4320,
    status: 'active',
    verify: true,
    registerDate: '2024-01-05T10:30:00',
    lastLogin: '2024-03-27T16:30:00'
  }
])

// ============ 计算属性 ============
const filteredUsers = computed(() => {
  return users.value.filter(user => {
    // 用户类型筛选
    if (userTypeFilter.value === 'vip' && user.vipLevel < 3) return false
    if (userTypeFilter.value === 'normal' && user.vipLevel >= 3) return false

    // 状态筛选
    if (statusFilter.value !== 'all' && user.status !== statusFilter.value) return false

    // 搜索关键词
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      return user.name.toLowerCase().includes(keyword) ||
        user.email.toLowerCase().includes(keyword) ||
        user.phone.includes(keyword)
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
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
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
    username: user.name,
    email: user.email,
    phone: user.phone,
    vipLevel: user.vipLevel,
    points: user.points,
    status: user.status,
    remark: ''
  })
  showUserModal.value = true
}

const closeUserModal = () => {
  showUserModal.value = false
}

const saveUser = () => {
  if (modalMode.value === 'create') {
    const newUser = {
      id: Math.max(...users.value.map(u => u.id)) + 1,
      name: userForm.username,
      email: userForm.email,
      phone: userForm.phone,
      vipLevel: userForm.vipLevel,
      points: userForm.points,
      status: userForm.status,
      verify: false,
      registerDate: new Date().toISOString(),
      lastLogin: null
    }
    users.value.push(newUser)
  } else {
    const index = users.value.findIndex(u => u.id === userForm.id)
    if (index !== -1) {
      users.value[index] = {
        ...users.value[index],
        name: userForm.username,
        email: userForm.email,
        phone: userForm.phone,
        vipLevel: userForm.vipLevel,
        points: userForm.points,
        status: userForm.status
      }
    }
  }
  closeUserModal()
}

const viewUserDetail = (user) => {
  currentUserDetail.value = user
  showDetailDrawer.value = true
}

const closeDetailDrawer = () => {
  showDetailDrawer.value = false
  currentUserDetail.value = null
}

const toggleUserStatus = (user) => {
  user.status = user.status === 'active' ? 'inactive' : 'active'
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
