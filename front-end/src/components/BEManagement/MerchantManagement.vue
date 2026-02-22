<template>
  <div class="space-y-6 page-enter-active">
    <!-- 页面标题与操作区 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white flex items-center gap-3">
          <span class="w-1.5 h-6 bg-gradient-to-b from-green-500 to-blue-500 rounded-full"></span>
          商家管理
        </h1>
        <p class="text-sm text-gray-500 dark:text-gray-400 mt-1.5 ml-3">
          共 {{ totalMerchants }} 家商家，今日新增 {{ todayNewMerchants }} 家
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
          @click="openMerchantModal"
          class="px-5 py-2.5 bg-gradient-to-r from-green-500 to-blue-600 hover:from-green-600 hover:to-blue-700 text-white rounded-xl text-sm font-medium transition-all flex items-center gap-2 shadow-lg shadow-green-500/25 hover:shadow-xl hover:scale-105 active:scale-95"
        >
          <i class="fas fa-plus-circle"></i>
          新增商家
        </button>
      </div>
    </div>

    <!-- 数据概览卡片 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
      <div class="bg-white dark:bg-gray-900 rounded-xl p-5 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-lg transition-all group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">总商家数</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ formatNumber(totalMerchants) }}</p>
          </div>
          <div class="w-11 h-11 rounded-xl bg-green-100 dark:bg-green-900/30 flex items-center justify-center group-hover:scale-110 transition-transform">
            <i class="fas fa-store text-green-600 dark:text-green-400 text-xl"></i>
          </div>
        </div>
        <div class="mt-2 text-xs text-green-600 dark:text-green-400">
          <i class="fas fa-arrow-up mr-1"></i> {{ merchantGrowth }}% 较上月
        </div>
      </div>

      <div class="bg-white dark:bg-gray-900 rounded-xl p-5 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-lg transition-all group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">今日新增</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ todayNewMerchants }}</p>
          </div>
          <div class="w-11 h-11 rounded-xl bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center group-hover:scale-110 transition-transform">
            <i class="fas fa-store-plus text-blue-600 dark:text-blue-400 text-xl"></i>
          </div>
        </div>
        <div class="mt-2 text-xs text-gray-500 dark:text-gray-400">
          环比 {{ todayCompare }}%
        </div>
      </div>

      <div class="bg-white dark:bg-gray-900 rounded-xl p-5 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-lg transition-all group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">已通过</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ approvedMerchants }}</p>
          </div>
          <div class="w-11 h-11 rounded-xl bg-green-100 dark:bg-green-900/30 flex items-center justify-center group-hover:scale-110 transition-transform">
            <i class="fas fa-check-circle text-green-600 dark:text-green-400 text-xl"></i>
          </div>
        </div>
        <div class="mt-2 text-xs text-gray-500 dark:text-gray-400">
          占比 {{ approvedRate }}%
        </div>
      </div>

      <div class="bg-white dark:bg-gray-900 rounded-xl p-5 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-lg transition-all group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">待审核</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ pendingMerchants }}</p>
          </div>
          <div class="w-11 h-11 rounded-xl bg-yellow-100 dark:bg-yellow-900/30 flex items-center justify-center group-hover:scale-110 transition-transform">
            <i class="fas fa-clock text-yellow-600 dark:text-yellow-400 text-xl"></i>
          </div>
        </div>
        <div class="mt-2 text-xs text-red-600 dark:text-red-400">
          <i class="fas fa-exclamation-circle mr-1"></i> 需尽快处理
        </div>
      </div>

      <div class="bg-white dark:bg-gray-900 rounded-xl p-5 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-lg transition-all group">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">未通过</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ rejectedMerchants }}</p>
          </div>
          <div class="w-11 h-11 rounded-xl bg-red-100 dark:bg-red-900/30 flex items-center justify-center group-hover:scale-110 transition-transform">
            <i class="fas fa-times-circle text-red-600 dark:text-red-400 text-xl"></i>
          </div>
        </div>
        <div class="mt-2 text-xs text-gray-500 dark:text-gray-400">
          占比 {{ rejectedRate }}%
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
              @click="toggleMerchantTypeDropdown"
              class="px-4 py-2.5 bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl text-sm flex items-center gap-2 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            >
              <i class="fas fa-filter text-gray-500"></i>
              <span class="text-gray-700 dark:text-gray-300">{{ merchantTypeFilter === 'all' ? '全部类型' : merchantTypeFilter === 'retail' ? '零售商家' : '批发商家' }}</span>
              <i class="fas fa-chevron-down text-gray-400 text-xs ml-1"></i>
            </button>

            <!-- 商家类型下拉菜单 -->
            <div v-if="showMerchantTypeDropdown" class="absolute top-full left-0 mt-1 w-48 bg-white dark:bg-gray-900 rounded-xl shadow-2xl border border-gray-200 dark:border-gray-700 py-2 z-20">
              <button @click="setMerchantTypeFilter('all')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center gap-2">
                <i class="fas fa-store w-4 text-gray-500"></i>
                <span :class="merchantTypeFilter === 'all' ? 'text-green-600 dark:text-green-400 font-medium' : 'text-gray-700 dark:text-gray-300'">全部类型</span>
              </button>
              <button @click="setMerchantTypeFilter('retail')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center gap-2">
                <i class="fas fa-shopping-bag w-4 text-gray-500"></i>
                <span :class="merchantTypeFilter === 'retail' ? 'text-green-600 dark:text-green-400 font-medium' : 'text-gray-700 dark:text-gray-300'">零售商家</span>
              </button>
              <button @click="setMerchantTypeFilter('wholesale')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center gap-2">
                <i class="fas fa-boxes w-4 text-gray-500"></i>
                <span :class="merchantTypeFilter === 'wholesale' ? 'text-green-600 dark:text-green-400 font-medium' : 'text-gray-700 dark:text-gray-300'">批发商家</span>
              </button>
            </div>
          </div>

          <div class="relative">
            <button
              @click="toggleStatusDropdown"
              class="px-4 py-2.5 bg-gray-50 dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl text-sm flex items-center gap-2 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
            >
              <i class="fas fa-circle text-gray-500"></i>
              <span class="text-gray-700 dark:text-gray-300">{{ statusFilter === 'all' ? '全部状态' : statusFilter === 'approved' ? '已通过' : statusFilter === 'pending' ? '待审核' : '未通过' }}</span>
              <i class="fas fa-chevron-down text-gray-400 text-xs ml-1"></i>
            </button>

            <div v-if="showStatusDropdown" class="absolute top-full left-0 mt-1 w-40 bg-white dark:bg-gray-900 rounded-xl shadow-2xl border border-gray-200 dark:border-gray-700 py-2 z-20">
              <button @click="setStatusFilter('all')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">全部状态</button>
              <button @click="setStatusFilter('approved')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">已通过</button>
              <button @click="setStatusFilter('pending')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">待审核</button>
              <button @click="setStatusFilter('rejected')" class="w-full px-4 py-2 text-left text-sm hover:bg-gray-100 dark:hover:bg-gray-800">未通过</button>
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

          <button @click="resetFilters" class="px-4 py-2.5 text-sm text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800 rounded-xl transition-colors flex items-center gap-2">
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
            placeholder="搜索商家名称、联系人或电话..."
            class="w-full pl-10 pr-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-green-500 dark:focus:border-green-400 focus:ring-2 focus:ring-green-500/20 outline-none transition-all text-sm"
          />
          <div v-if="searchKeyword" class="absolute inset-y-0 right-0 pr-3 flex items-center">
            <button @click="searchKeyword = ''" class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
              <i class="fas fa-times-circle"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- 快速筛选 -->
      <div class="flex items-center gap-2 mt-4 pt-4 border-t border-gray-200/50 dark:border-gray-700/50">
        <span class="text-xs text-gray-500 dark:text-gray-400 mr-1">快速筛选:</span>
        <button @click="setStatusFilter('pending')" class="px-3 py-1.5 text-xs bg-yellow-50 dark:bg-yellow-900/20 text-yellow-600 dark:text-yellow-400 rounded-lg hover:bg-yellow-100 dark:hover:bg-yellow-900/40 transition-colors font-medium">
          待审核商家
        </button>
        <button @click="setStatusFilter('approved')" class="px-3 py-1.5 text-xs bg-green-50 dark:bg-green-900/20 text-green-600 dark:text-green-400 rounded-lg hover:bg-green-100 dark:hover:bg-green-900/40 transition-colors">
          已通过商家
        </button>
        <button @click="setStatusFilter('rejected')" class="px-3 py-1.5 text-xs bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/40 transition-colors">
          未通过商家
        </button>
      </div>
    </div>

    <!-- 商家列表表格 -->
    <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 overflow-hidden shadow-sm">
      <!-- 表格头部 - 选中操作栏 -->
      <div v-if="selectedMerchants.length > 0" class="px-6 py-4 bg-green-50/50 dark:bg-green-900/10 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="w-6 h-6 rounded-full bg-green-500 text-white flex items-center justify-center text-xs font-bold">{{ selectedMerchants.length }}</span>
          <span class="text-sm text-gray-700 dark:text-gray-300">个商家已选中</span>
        </div>
        <div class="flex items-center gap-2">
          <button class="px-3 py-1.5 text-xs bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors flex items-center gap-1">
            <i class="fas fa-envelope text-gray-500"></i>
            批量通知
          </button>
          <button class="px-3 py-1.5 text-xs bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/40 transition-colors flex items-center gap-1">
            <i class="fas fa-trash-alt"></i>
            批量删除
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
                  class="w-4 h-4 rounded border-gray-300 dark:border-gray-600 text-green-500 focus:ring-green-500/20 focus:ring-offset-0"
                />
              </label>
            </th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">商家信息</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">联系方式</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">商家类型</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">状态</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">创建时间</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">更新时间</th>
            <th class="px-6 py-4 text-right text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">操作</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
          <tr v-for="merchant in paginatedMerchants" :key="merchant.id" class="hover:bg-gray-50/50 dark:hover:bg-gray-800/50 transition-colors group">
            <td class="px-6 py-4">
              <label class="flex items-center">
                <input
                  type="checkbox"
                  v-model="selectedMerchants"
                  :value="merchant.id"
                  class="w-4 h-4 rounded border-gray-300 dark:border-gray-600 text-green-500 focus:ring-green-500/20 focus:ring-offset-0"
                />
              </label>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="relative">
                  <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-green-500 to-blue-600 flex items-center justify-center text-white font-semibold shadow-lg shadow-green-500/20">
                    {{ merchant.merchantName.charAt(0) }}
                  </div>
                  <span class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-green-500 border-2 border-white dark:border-gray-900 rounded-full" v-if="merchant.status === 1"></span>
                  <span class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-yellow-500 border-2 border-white dark:border-gray-900 rounded-full" v-if="merchant.status === 0"></span>
                  <span class="absolute -bottom-0.5 -right-0.5 w-3.5 h-3.5 bg-red-500 border-2 border-white dark:border-gray-900 rounded-full" v-if="merchant.status === 2"></span>
                </div>
                <div>
                  <div class="flex items-center gap-2">
                    <span class="font-medium text-gray-900 dark:text-white">{{ merchant.merchantName }}</span>
                    <span class="text-xs text-gray-500 dark:text-gray-400">ID: {{ merchant.id }}</span>
                  </div>
                  <span class="text-xs text-gray-500 dark:text-gray-400">{{ merchant.businessLicense }}</span>
                </div>
              </div>
            </td>
            <td class="px-6 py-4">
              <div class="text-sm text-gray-700 dark:text-gray-300">{{ merchant.contactName }}</div>
              <div class="text-xs text-gray-500 dark:text-gray-400 mt-0.5">{{ merchant.contactPhone }}</div>
              <div class="text-xs text-gray-500 dark:text-gray-400">{{ merchant.contactEmail }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-1.5">
                <i class="fas fa-shopping-bag text-green-500 text-sm" v-if="merchant.merchantType === 1"></i>
                <i class="fas fa-boxes text-blue-500 text-sm" v-else-if="merchant.merchantType === 2"></i>
                <i class="fas fa-store text-gray-500 text-sm" v-else></i>
                <span class="text-sm font-medium text-gray-700 dark:text-gray-300">{{ merchant.merchantType === 1 ? '个人' : merchant.merchantType === 2 ? '企业' : '其他' }}</span>
              </div>
            </td>
            <td class="px-6 py-4">
              <span v-if="merchant.status === 0" class="px-2 py-1 text-xs font-medium bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600 dark:text-yellow-400 rounded-full">
                    待审核
                  </span>
              <span v-else-if="merchant.status === 1" class="px-2 py-1 text-xs font-medium bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400 rounded-full">
                    已通过
                  </span>
              <span v-else-if="merchant.status === 2" class="px-2 py-1 text-xs font-medium bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400 rounded-full">
                    未通过
                  </span>
            </td>
            <td class="px-6 py-4">
              <div class="text-sm text-gray-700 dark:text-gray-300">{{ formatDate(merchant.createTime) }}</div>
            </td>
            <td class="px-6 py-4">
              <div class="text-sm text-gray-700 dark:text-gray-300">{{ formatDate(merchant.updateTime) }}</div>
            </td>
            <td class="px-6 py-4 text-right">
              <div class="flex items-center justify-end gap-3">
                <button @click="editMerchant(merchant)" class="p-2.5 bg-blue-500 text-white hover:bg-blue-600 rounded-lg transition-all hover:scale-110 shadow-md" title="编辑">
                  <i class="fas fa-edit"></i>
                </button>
                <button @click="viewMerchantDetail(merchant)" class="p-2.5 bg-purple-500 text-white hover:bg-purple-600 rounded-lg transition-all hover:scale-110 shadow-md" title="详情">
                  <i class="fas fa-eye"></i>
                </button>
                <button @click="approveMerchant(merchant)" class="p-2.5 bg-green-500 text-white hover:bg-green-600 rounded-lg transition-all hover:scale-110 shadow-md" v-if="merchant.status === 0" title="通过">
                  <i class="fas fa-check-circle"></i>
                </button>
                <button @click="rejectMerchant(merchant)" class="p-2.5 bg-red-500 text-white hover:bg-red-600 rounded-lg transition-all hover:scale-110 shadow-md" v-if="merchant.status === 0" title="拒绝">
                  <i class="fas fa-times-circle"></i>
                </button>
                <button @click="deleteMerchant(merchant)" class="p-2.5 bg-red-500 text-white hover:bg-red-600 rounded-lg transition-all hover:scale-110 shadow-md" title="删除">
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </td>
          </tr>

          <!-- 空状态 -->
          <tr v-if="paginatedMerchants.length === 0">
            <td colspan="8" class="px-6 py-16 text-center">
              <div class="flex flex-col items-center">
                <div class="w-20 h-20 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mb-4">
                  <i class="fas fa-store text-4xl text-gray-400"></i>
                </div>
                <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-1">暂无商家数据</h3>
                <p class="text-sm text-gray-500 dark:text-gray-400 mb-4">没有找到符合条件的商家</p>
                <button @click="resetFilters" class="px-5 py-2 bg-green-500 hover:bg-green-600 text-white rounded-xl text-sm font-medium transition-all">
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
          显示第 {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, filteredMerchants.length) }} 条，共 {{ filteredMerchants.length }} 条记录
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
                ? 'bg-gradient-to-r from-green-500 to-blue-500 text-white shadow-md'
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

  <!-- 商家编辑/新增模态框 -->
  <Transition name="modal">
    <div v-if="showMerchantModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 transition-opacity" @click="closeMerchantModal">
          <div class="absolute inset-0 bg-gray-900/60 backdrop-blur-sm"></div>
        </div>

        <span class="hidden sm:inline-block sm:align-middle sm:h-screen">&#8203;</span>

        <div class="inline-block align-bottom bg-white dark:bg-gray-900 rounded-2xl text-left overflow-hidden shadow-2xl transform transition-all sm:my-8 sm:align-middle sm:max-w-2xl sm:w-full">
          <!-- 模态框头部 -->
          <div class="px-6 py-5 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
              <span class="w-1 h-5 bg-gradient-to-b from-green-500 to-blue-500 rounded-full"></span>
              {{ modalMode === 'create' ? '新增商家' : '编辑商家信息' }}
            </h3>
            <button @click="closeMerchantModal" class="w-8 h-8 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 flex items-center justify-center transition-colors">
              <i class="fas fa-times text-gray-500"></i>
            </button>
          </div>

          <!-- 模态框内容 -->
          <div class="px-6 py-6">
            <form @submit.prevent="saveMerchant" class="space-y-5">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">商家名称</label>
                  <input
                    v-model="merchantForm.merchantName"
                    type="text"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all text-sm"
                    placeholder="请输入商家名称"
                    required
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">商家类型</label>
                  <select
                    v-model="merchantForm.merchantType"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all text-sm"
                  >
                    <option value="1">零售</option>
                    <option value="2">批发</option>
                    <option value="3">其他</option>
                  </select>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">联系人姓名</label>
                  <input
                    v-model="merchantForm.contactName"
                    type="text"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all text-sm"
                    placeholder="请输入联系人姓名"
                    required
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">联系电话</label>
                  <input
                    v-model="merchantForm.contactPhone"
                    type="tel"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all text-sm"
                    placeholder="请输入联系电话"
                    required
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">电子邮箱</label>
                  <input
                    v-model="merchantForm.contactEmail"
                    type="email"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all text-sm"
                    placeholder="请输入电子邮箱"
                    required
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">营业执照号</label>
                  <input
                    v-model="merchantForm.businessLicense"
                    type="text"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all text-sm"
                    placeholder="请输入营业执照号"
                    required
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">身份证号</label>
                  <input
                    v-model="merchantForm.idCard"
                    type="text"
                    class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all text-sm"
                    placeholder="请输入身份证号"
                    required
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">状态</label>
                  <div class="flex items-center h-10 gap-4">
                    <label class="flex items-center gap-2">
                      <input type="radio" v-model="merchantForm.status" value="0" class="w-4 h-4 text-green-500 focus:ring-green-500/20 border-gray-300" />
                      <span class="text-sm text-gray-700 dark:text-gray-300">待审核</span>
                    </label>
                    <label class="flex items-center gap-2">
                      <input type="radio" v-model="merchantForm.status" value="1" class="w-4 h-4 text-green-500 focus:ring-green-500/20 border-gray-300" />
                      <span class="text-sm text-gray-700 dark:text-gray-300">已通过</span>
                    </label>
                    <label class="flex items-center gap-2">
                      <input type="radio" v-model="merchantForm.status" value="2" class="w-4 h-4 text-gray-500 focus:ring-gray-500/20 border-gray-300" />
                      <span class="text-sm text-gray-700 dark:text-gray-300">未通过</span>
                    </label>
                  </div>
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">拒绝原因</label>
                <textarea
                  v-model="merchantForm.rejectReason"
                  rows="3"
                  class="w-full px-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 focus:bg-white dark:focus:bg-gray-800 focus:border-green-500 focus:ring-2 focus:ring-green-500/20 outline-none transition-all text-sm"
                  placeholder="请输入拒绝原因（仅当状态为未通过时填写）"
                ></textarea>
              </div>

              <div class="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  @click="closeMerchantModal"
                  class="px-5 py-2.5 border border-gray-200 dark:border-gray-700 rounded-xl text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
                >
                  取消
                </button>
                <button
                  type="submit"
                  class="px-6 py-2.5 bg-gradient-to-r from-green-500 to-blue-500 hover:from-green-600 hover:to-blue-600 text-white rounded-xl text-sm font-medium shadow-lg shadow-green-500/25 hover:shadow-xl transition-all hover:scale-105 active:scale-95"
                >
                  {{ modalMode === 'create' ? '创建商家' : '保存修改' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </Transition>

  <!-- 商家详情抽屉 -->
  <Transition name="slide">
    <div v-if="showDetailDrawer" class="fixed inset-0 z-50 overflow-hidden">
      <div class="absolute inset-0 overflow-hidden">
        <div class="absolute inset-0 bg-gray-900/60 backdrop-blur-sm transition-opacity" @click="closeDetailDrawer"></div>

        <div class="fixed inset-y-0 right-0 flex max-w-full pl-10">
          <div class="relative w-screen max-w-2xl">
            <div class="flex h-full flex-col overflow-y-auto bg-white dark:bg-gray-900 shadow-2xl">
              <!-- 抽屉头部 -->
              <div class="px-6 py-6 border-b border-gray-200 dark:border-gray-700 flex items-center justify-between bg-gradient-to-r from-green-50 to-blue-50 dark:from-gray-800 dark:to-gray-800">
                <div class="flex items-center gap-4">
                  <div class="w-14 h-14 rounded-2xl bg-gradient-to-br from-green-500 to-blue-600 flex items-center justify-center text-white font-bold text-xl shadow-lg">
                    {{ currentMerchantDetail?.merchantName?.charAt(0) || 'M' }}
                  </div>
                  <div>
                    <h3 class="text-xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
                      {{ currentMerchantDetail?.merchantName }}
                      <span v-if="currentMerchantDetail?.status === 1" class="px-2 py-0.5 text-xs bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400 rounded-full flex items-center gap-1">
                        <i class="fas fa-check-circle"></i> 已通过
                      </span>
                      <span v-else-if="currentMerchantDetail?.status === 0" class="px-2 py-0.5 text-xs bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600 dark:text-yellow-400 rounded-full flex items-center gap-1">
                        <i class="fas fa-clock"></i> 待审核
                      </span>
                      <span v-else-if="currentMerchantDetail?.status === 2" class="px-2 py-0.5 text-xs bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400 rounded-full flex items-center gap-1">
                        <i class="fas fa-times-circle"></i> 未通过
                      </span>
                    </h3>
                    <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">商家ID: {{ currentMerchantDetail?.id }}</p>
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
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">商家类型</p>
                    <div class="flex items-center gap-2">
                      <i class="fas fa-shopping-bag text-green-500" v-if="currentMerchantDetail?.merchantType === 1"></i>
                      <i class="fas fa-boxes text-blue-500" v-else-if="currentMerchantDetail?.merchantType === 2"></i>
                      <i class="fas fa-store text-gray-500" v-else></i>
                      <span class="text-lg font-bold text-gray-900 dark:text-white">{{ currentMerchantDetail?.merchantType === 1 ? '零售' : currentMerchantDetail?.merchantType === 2 ? '批发' : '其他' }}</span>
                    </div>
                  </div>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">营业执照号</p>
                    <div class="flex items-center gap-2">
                      <i class="fas fa-file-certificate text-blue-500"></i>
                      <span class="text-lg font-bold text-gray-900 dark:text-white">{{ currentMerchantDetail?.businessLicense }}</span>
                    </div>
                  </div>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">创建时间</p>
                    <div class="flex items-center gap-2">
                      <i class="fas fa-calendar text-green-500"></i>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ formatDate(currentMerchantDetail?.createTime) }}</span>
                    </div>
                  </div>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">更新时间</p>
                    <div class="flex items-center gap-2">
                      <i class="fas fa-clock text-blue-500"></i>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ formatDate(currentMerchantDetail?.updateTime) }}</span>
                    </div>
                  </div>
                </div>

                <!-- 详细信息 -->
                <div class="space-y-4">
                  <h4 class="font-medium text-gray-900 dark:text-white flex items-center gap-2">
                    <i class="fas fa-address-card text-gray-500"></i>
                    联系信息
                  </h4>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-5 space-y-4">
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">联系人姓名</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentMerchantDetail?.contactName }}</span>
                    </div>
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">联系电话</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentMerchantDetail?.contactPhone }}</span>
                    </div>
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">电子邮箱</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentMerchantDetail?.contactEmail }}</span>
                    </div>
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">身份证号</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentMerchantDetail?.idCard }}</span>
                    </div>
                  </div>
                </div>

                <!-- 审核信息 -->
                <div class="space-y-4">
                  <h4 class="font-medium text-gray-900 dark:text-white flex items-center gap-2">
                    <i class="fas fa-clipboard-check text-gray-500"></i>
                    审核信息
                  </h4>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-5 space-y-4">
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">审核状态</span>
                      <span
                        class="px-2.5 py-1 text-xs font-medium rounded-full"
                        :class="{
                          'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400': currentMerchantDetail?.status === 1,
                          'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400': currentMerchantDetail?.status === 0,
                          'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400': currentMerchantDetail?.status === 2
                        }"
                      >
                        {{ currentMerchantDetail?.status === 1 ? '已通过' : currentMerchantDetail?.status === 0 ? '待审核' : '未通过' }}
                      </span>
                    </div>
                    <div class="flex items-center justify-between" v-if="currentMerchantDetail?.status === 2">
                      <span class="text-sm text-gray-500 dark:text-gray-400">拒绝原因</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentMerchantDetail?.rejectReason }}</span>
                    </div>
                    <div class="flex items-center justify-between" v-if="currentMerchantDetail?.auditTime">
                      <span class="text-sm text-gray-500 dark:text-gray-400">审核时间</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentMerchantDetail?.auditTime }}</span>
                    </div>
                    <div class="flex items-center justify-between" v-if="currentMerchantDetail?.auditor">
                      <span class="text-sm text-gray-500 dark:text-gray-400">审核人</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentMerchantDetail?.auditor }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 抽屉底部 -->
              <div class="px-6 py-4 border-t border-gray-200 dark:border-gray-700 flex items-center justify-end gap-3">
                <button @click="editMerchant(currentMerchantDetail)" class="px-5 py-2.5 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-xl text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors flex items-center gap-2">
                  <i class="fas fa-edit"></i>
                  编辑资料
                </button>
                <button class="px-5 py-2.5 bg-gradient-to-r from-green-500 to-blue-500 text-white rounded-xl text-sm font-medium shadow-lg hover:shadow-xl transition-all hover:scale-105 active:scale-95 flex items-center gap-2">
                  <i class="fas fa-envelope"></i>
                  发送通知
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

import { get, post, del } from '@/net/index.js'
import { ElMessage } from 'element-plus'
import tokenManager from '@/utils/tokenManager'

// 商家后端接口 base（端口 8081）
const MERCHANT_API = 'http://localhost:8081/api/merchant'

// ============ 状态变量 ============
const totalMerchants = ref(0)
const todayNewMerchants = ref(0)
const merchantGrowth = ref(0)
const todayCompare = ref(0)
const approvedMerchants = ref(0)
const approvedRate = ref(0)
const pendingMerchants = ref(0)
const rejectedMerchants = ref(0)
const rejectedRate = ref(0)

// 筛选状态
const showMerchantTypeDropdown = ref(false)
const showStatusDropdown = ref(false)
const showDateDropdown = ref(false)
const merchantTypeFilter = ref('all')
const statusFilter = ref('all')
const dateRangeText = ref('最近30天')
const searchKeyword = ref('')

// 表格选中状态
const selectedMerchants = ref([])
const currentPage = ref(1)
const pageSize = ref(10)

// 模态框状态
const showMerchantModal = ref(false)
const modalMode = ref('create') // 'create' or 'edit'
const merchantForm = reactive({
  id: null,
  userId: null,
  merchantType: 1,
  merchantName: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  businessLicense: '',
  licenseImage: '',
  idCard: '',
  idCardFront: '',
  idCardBack: '',
  status: 0,
  rejectReason: ''
})

// 详情抽屉
const showDetailDrawer = ref(false)
const currentMerchantDetail = ref(null)

// ============ 后端数据 ============
const merchants = ref([])
const loading = ref(false)

// 当前用户 ID（从登录信息获取，用于后端鉴权）
const currentUserId = () => tokenManager.getUserInfo()?.id

// 当前用户角色
const currentUserRole = () => tokenManager.getUserInfo()?.role

// 是否为管理员
const isAdmin = () => currentUserRole() === '管理员'

// 是否为商家自己（只能修改自己的信息）
const isMerchantOwner = (merchant) => merchant.userId === currentUserId()

// 获取商家数据
const fetchMerchants = async () => {
  loading.value = true
  const userId = currentUserId()
  try {
    const merchant = {
      merchantType: merchantTypeFilter.value === 'all' ? null : (merchantTypeFilter.value === 'retail' ? 1 : 2),
      status: statusFilter.value === 'all' ? null : (statusFilter.value === 'approved' ? 1 : statusFilter.value === 'pending' ? 0 : 2),
      merchantName: searchKeyword.value || null
    }

    await post(`${MERCHANT_API}/selectMerchant?userId=${userId}`, merchant, (message, data) => {
      // 转换数据格式，确保字段名称匹配
      merchants.value = data.map(item => ({
        id: item.id,
        userId: item.userId,
        merchantType: item.merchantType,
        merchantName: item.merchantName,
        contactName: item.contactName,
        contactPhone: item.contactPhone,
        contactEmail: item.contactEmail,
        businessLicense: item.businessLicense,
        licenseImage: item.licenseImage,
        idCard: item.idCard,
        idCardFront: item.idCardFront,
        idCardBack: item.idCardBack,
        status: item.status,
        rejectReason: item.rejectReason,
        auditTime: item.auditTime,
        auditor: item.auditor,
        isDeleted: item.isDeleted,
        createTime: item.createTime,
        updateTime: item.updateTime
      }))

      // 计算统计数据
      totalMerchants.value = merchants.value.length
      todayNewMerchants.value = merchants.value.filter(m => m.createTime && m.createTime.includes('2026-02-21')).length
      approvedMerchants.value = merchants.value.filter(m => m.status === 1).length
      pendingMerchants.value = merchants.value.filter(m => m.status === 0).length
      rejectedMerchants.value = merchants.value.filter(m => m.status === 2).length
      approvedRate.value = totalMerchants.value > 0 ? Math.round((approvedMerchants.value / totalMerchants.value) * 100) : 0
      rejectedRate.value = totalMerchants.value > 0 ? Math.round((rejectedMerchants.value / totalMerchants.value) * 100) : 0
      merchantGrowth.value = 15 // 模拟数据
      todayCompare.value = 20 // 模拟数据
    }, (message) => {
      console.error('获取商家数据失败:', message)
      merchants.value = []
    }, undefined, true, { userId })
  } catch (error) {
    console.error('获取商家数据出错:', error)
    merchants.value = []
  } finally {
    loading.value = false
  }
}

// 页面加载时获取商家数据
onMounted(() => {
  fetchMerchants()
})

// ============ 计算属性 ============
const filteredMerchants = computed(() => {
  return merchants.value.filter(merchant => {
    // 商家类型筛选
    if (merchantTypeFilter.value === 'retail' && merchant.merchantType !== 1) return false
    if (merchantTypeFilter.value === 'wholesale' && merchant.merchantType !== 2) return false

    // 状态筛选
    if (statusFilter.value !== 'all') {
      if (statusFilter.value === 'approved' && merchant.status !== 1) return false
      if (statusFilter.value === 'pending' && merchant.status !== 0) return false
      if (statusFilter.value === 'rejected' && merchant.status !== 2) return false
    }

    // 搜索关键词
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      return merchant.merchantName.toLowerCase().includes(keyword) ||
        merchant.contactName.toLowerCase().includes(keyword) ||
        merchant.contactPhone.includes(keyword) ||
        merchant.contactEmail.toLowerCase().includes(keyword) ||
        merchant.businessLicense.toLowerCase().includes(keyword)
    }

    return true
  })
})

const totalPages = computed(() => {
  return Math.ceil(filteredMerchants.value.length / pageSize.value)
})

const paginatedMerchants = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredMerchants.value.slice(start, end)
})

const selectAll = computed({
  get: () => selectedMerchants.value.length === paginatedMerchants.value.length && paginatedMerchants.value.length > 0,
  set: (value) => {
    if (value) {
      selectedMerchants.value = paginatedMerchants.value.map(m => m.id)
    } else {
      selectedMerchants.value = []
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
const toggleMerchantTypeDropdown = () => {
  showMerchantTypeDropdown.value = !showMerchantTypeDropdown.value
  showStatusDropdown.value = false
  showDateDropdown.value = false
}

const toggleStatusDropdown = () => {
  showStatusDropdown.value = !showStatusDropdown.value
  showMerchantTypeDropdown.value = false
  showDateDropdown.value = false
}

const toggleDateDropdown = () => {
  showDateDropdown.value = !showDateDropdown.value
  showMerchantTypeDropdown.value = false
  showStatusDropdown.value = false
}

const setMerchantTypeFilter = async (type) => {
  merchantTypeFilter.value = type
  showMerchantTypeDropdown.value = false
  currentPage.value = 1
  await fetchMerchants()
}

const setStatusFilter = async (status) => {
  statusFilter.value = status
  showStatusDropdown.value = false
  currentPage.value = 1
  await fetchMerchants()
}

const resetFilters = async () => {
  merchantTypeFilter.value = 'all'
  statusFilter.value = 'all'
  searchKeyword.value = ''
  currentPage.value = 1
  await fetchMerchants()
}

const formatNumber = (num) => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

// 商家操作
const queryApplyProgress = async () => {
  const userId = currentUserId()
  try {
    await get(`${MERCHANT_API}/queryApplyProgress?userId=${userId}`, null, (message, data) => {
      ElMessage.success('获取申请进度成功')
      // 处理申请进度数据
      console.log('申请进度:', data)
    }, (message) => {
      ElMessage.error('获取申请进度失败: ' + message)
    }, undefined, true)
  } catch (error) {
    console.error('获取申请进度出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const selectMerchantByStatus = async (status) => {
  const userId = currentUserId()
  try {
    await get(`${MERCHANT_API}/selectMerchantByStatus?status=${status}&userId=${userId}`, null, (message, data) => {
      ElMessage.success('获取商家列表成功')
      merchants.value = data
    }, (message) => {
      ElMessage.error('获取商家列表失败: ' + message)
    }, undefined, true)
  } catch (error) {
    console.error('获取商家列表出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const searchMerchant = async (keyword) => {
  const userId = currentUserId()
  try {
    await get(`${MERCHANT_API}/searchMerchant?keyword=${encodeURIComponent(keyword)}&userId=${userId}`, null, (message, data) => {
      ElMessage.success('搜索商家成功')
      merchants.value = data
    }, (message) => {
      ElMessage.error('搜索商家失败: ' + message)
    }, undefined, true)
  } catch (error) {
    console.error('搜索商家出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const openMerchantModal = () => {
  modalMode.value = 'create'
  Object.assign(merchantForm, {
    id: null,
    userId: null,
    merchantType: 1,
    merchantName: '',
    contactName: '',
    contactPhone: '',
    contactEmail: '',
    businessLicense: '',
    licenseImage: '',
    idCard: '',
    idCardFront: '',
    idCardBack: '',
    status: 0,
    rejectReason: ''
  })
  showMerchantModal.value = true
}

const editMerchant = (merchant) => {
  modalMode.value = 'edit'
  Object.assign(merchantForm, {
    id: merchant.id,
    userId: merchant.userId,
    merchantType: merchant.merchantType,
    merchantName: merchant.merchantName,
    contactName: merchant.contactName,
    contactPhone: merchant.contactPhone,
    contactEmail: merchant.contactEmail,
    businessLicense: merchant.businessLicense,
    licenseImage: merchant.licenseImage || merchant.LicenseImage,
    idCard: merchant.idCard,
    idCardFront: merchant.idCardFront,
    idCardBack: merchant.idCardBack,
    status: merchant.status,
    rejectReason: merchant.rejectReason
  })
  showMerchantModal.value = true
}

const closeMerchantModal = () => {
  showMerchantModal.value = false
}

const saveMerchant = async () => {
  const userId = currentUserId()
  try {
    if (modalMode.value === 'create') {
      merchantForm.userId = userId
      await post(`${MERCHANT_API}/applyMerchant`, merchantForm, (message, data) => {
        ElMessage.success('商家创建成功')
        closeMerchantModal()
        fetchMerchants()
      }, (message) => {
        ElMessage.error('商家创建失败: ' + message)
      }, undefined, true, { userId })
    } else {
      const targetMerchant = merchants.value.find(m => m.id === merchantForm.id)
      if (!targetMerchant) {
        ElMessage.error('商家信息不存在')
        return
      }
      
      let updateData = { ...merchantForm }
      
      if (isAdmin()) {
        // 管理员只能修改审核状态
        updateData = {
          id: merchantForm.id,
          userId: targetMerchant.userId,
          status: merchantForm.status,
          rejectReason: merchantForm.rejectReason
        }
      } else if (isMerchantOwner(targetMerchant)) {
        // 商家只能修改自己的基本信息，不能修改审核状态
        updateData = {
          id: merchantForm.id,
          userId: targetMerchant.userId,
          merchantType: merchantForm.merchantType,
          merchantName: merchantForm.merchantName,
          contactName: merchantForm.contactName,
          contactPhone: merchantForm.contactPhone,
          contactEmail: merchantForm.contactEmail,
          businessLicense: merchantForm.businessLicense,
          licenseImage: merchantForm.licenseImage,
          idCard: merchantForm.idCard,
          idCardFront: merchantForm.idCardFront,
          idCardBack: merchantForm.idCardBack,
          // 保持原有的审核状态
          status: targetMerchant.status,
          rejectReason: targetMerchant.rejectReason
        }
      } else {
        ElMessage.error('无权限修改此商家信息')
        return
      }
      
      await post(`${MERCHANT_API}/updateMerchant?userId=${userId}`, updateData, (message, data) => {
        ElMessage.success('商家信息更新成功')
        closeMerchantModal()
        fetchMerchants()
      }, (message) => {
        ElMessage.error('商家信息更新失败: ' + message)
      }, undefined, true)
    }
  } catch (error) {
    console.error('保存商家数据出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const viewMerchantDetail = async (merchant) => {
  const userId = currentUserId()
  try {
    await get(`${MERCHANT_API}/selectMerchantById/${merchant.id}?userId=${userId}`, null, (message, data) => {
      currentMerchantDetail.value = data
      showDetailDrawer.value = true
    }, (message) => {
      ElMessage.error('获取商家详情失败: ' + message)
    }, undefined, true)
  } catch (error) {
    console.error('获取商家详情出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const closeDetailDrawer = () => {
  showDetailDrawer.value = false
  currentMerchantDetail.value = null
}

const approveMerchant = async (merchant) => {
  const userId = currentUserId()
  try {
    if (!isAdmin()) {
      ElMessage.error('只有管理员可以审核商家')
      return
    }
    
    const updatedMerchant = {
      id: merchant.id,
      userId: merchant.userId,
      status: 1,
      rejectReason: ''
    }
    await post(`${MERCHANT_API}/updateMerchant?userId=${userId}`, updatedMerchant, (message, data) => {
      ElMessage.success('商家审核通过')
      fetchMerchants()
    }, (message) => {
      ElMessage.error('审核操作失败: ' + message)
    }, undefined, true)
  } catch (error) {
    console.error('审核商家出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const rejectMerchant = async (merchant) => {
  const reason = prompt('请输入拒绝原因：')
  if (reason) {
    const userId = currentUserId()
    try {
      if (!isAdmin()) {
        ElMessage.error('只有管理员可以审核商家')
        return
      }
      
      const updatedMerchant = {
        id: merchant.id,
        userId: merchant.userId,
        status: 2,
        rejectReason: reason
      }
      await post(`${MERCHANT_API}/updateMerchant?userId=${userId}`, updatedMerchant, (message, data) => {
        ElMessage.success('商家审核拒绝')
        fetchMerchants()
      }, (message) => {
        ElMessage.error('审核操作失败: ' + message)
      }, undefined, true)
    } catch (error) {
      console.error('审核商家出错:', error)
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

const deleteMerchant = async (merchant) => {
  if (!confirm(`确定要删除商家 ${merchant.merchantName} 吗？删除后为逻辑删除，仅管理员/测试员可操作。`)) return
  const userId = currentUserId()
  try {
    if (!isAdmin()) {
      ElMessage.error('只有管理员可以删除商家')
      return
    }
    
    await del(`${MERCHANT_API}/deleteMerchant/${merchant.id}?userId=${userId}`, null, (message) => {
      ElMessage.success('商家删除成功')
      fetchMerchants()
    }, (message) => {
      ElMessage.error('删除失败: ' + message)
    })
  } catch (error) {
    console.error('删除商家出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const handleExport = () => {
  const rows = filteredMerchants.value
  if (rows.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  const headers = ['ID', '用户ID', '商家名称', '商家类型', '联系人', '电话', '邮箱', '营业执照号', '状态', '创建时间', '更新时间']
  const statusText = (s) => (s === 0 ? '待审核' : s === 1 ? '已通过' : s === 2 ? '未通过' : '')
  const typeText = (t) => (t === 1 ? '零售' : t === 2 ? '批发' : '其他')
  const escape = (v) => {
    const s = v == null ? '' : String(v)
    return /[,"\n\r]/.test(s) ? `"${s.replace(/"/g, '""')}"` : s
  }
  const csvLines = [headers.map(escape).join(',')]
  rows.forEach(m => {
    csvLines.push([
      m.id,
      m.userId,
      m.merchantName,
      typeText(m.merchantType),
      m.contactName,
      m.contactPhone,
      m.contactEmail,
      m.businessLicense,
      statusText(m.status),
      m.createTime || '',
      m.updateTime || ''
    ].map(escape).join(','))
  })
  const BOM = '\uFEFF'
  const blob = new Blob([BOM + csvLines.join('\r\n')], { type: 'text/csv;charset=utf-8' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = `商家列表_${new Date().toISOString().slice(0, 10)}.csv`
  a.click()
  URL.revokeObjectURL(a.href)
  ElMessage.success(`已导出 ${rows.length} 条记录`)
}

// 监听筛选条件变化
watch([merchantTypeFilter, statusFilter, searchKeyword], async () => {
  currentPage.value = 1
  await fetchMerchants()
})

// 点击外部关闭下拉菜单
const handleClickOutside = (e) => {
  if (!e.target.closest('.relative')) {
    showMerchantTypeDropdown.value = false
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
  background-color: #10b981;
  border-color: #10b981;
}
</style>
