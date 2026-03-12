<template>
  <div class="space-y-6 page-enter-active">
    <!-- 页面标题与操作区 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white flex items-center gap-3">
          <span class="w-1.5 h-6 bg-gradient-to-b from-green-500 to-blue-500 rounded-full"></span>
          商家审核
        </h1>
        <p class="text-sm text-gray-500 dark:text-gray-400 mt-1.5 ml-3">
          共 {{ totalMerchants }} 家商家，待审核 {{ pendingMerchants }} 家
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
          @click="fetchMerchants"
          class="px-4 py-2.5 bg-blue-500 hover:bg-blue-600 text-white rounded-xl text-sm font-medium transition-all flex items-center gap-2 shadow-sm"
        >
          <i class="fas fa-sync-alt"></i>
          刷新
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
            @keyup.enter="handleSearch"
          />
          <div v-if="searchKeyword" class="absolute inset-y-0 right-0 pr-3 flex items-center">
            <button @click="clearSearch" class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
              <i class="fas fa-times-circle"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- 快速筛选 -->
      <div class="flex items-center gap-2 mt-4 pt-4 border-t border-gray-200/50 dark:border-gray-700/50">
        <span class="text-xs text-gray-500 dark:text-gray-400 mr-1">快速筛选:</span>
        <button @click="filterByStatus(0)" class="px-3 py-1.5 text-xs bg-yellow-50 dark:bg-yellow-900/20 text-yellow-600 dark:text-yellow-400 rounded-lg hover:bg-yellow-100 dark:hover:bg-yellow-900/40 transition-colors font-medium">
          待审核商家
        </button>
        <button @click="filterByStatus(1)" class="px-3 py-1.5 text-xs bg-green-50 dark:bg-green-900/20 text-green-600 dark:text-green-400 rounded-lg hover:bg-green-100 dark:hover:bg-green-900/40 transition-colors">
          已通过商家
        </button>
        <button @click="filterByStatus(2)" class="px-3 py-1.5 text-xs bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-100 dark:hover:bg-red-900/40 transition-colors">
          未通过商家
        </button>
      </div>
    </div>

    <!-- 商家列表表格 -->
    <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 overflow-hidden shadow-sm">
      <!-- 表格主体 -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
          <tr class="border-b border-gray-200 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50">
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">商家信息</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">联系方式</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">商家类型</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">状态</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">创建时间</th>
            <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">审核操作</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
          <tr v-for="merchant in paginatedMerchants" :key="merchant.id" class="hover:bg-gray-50/50 dark:hover:bg-gray-800/50 transition-colors group">
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div class="relative">
                  <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-green-500 to-blue-600 flex items-center justify-center text-white font-semibold shadow-lg shadow-green-500/20">
                    {{ merchant.merchantName?.charAt(0) || 'M' }}
                  </div>
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
              <div class="flex items-center gap-2">
                <button @click="viewMerchantDetail(merchant)" class="px-3 py-1.5 bg-purple-500 text-white hover:bg-purple-600 rounded-lg text-xs font-medium transition-all hover:scale-105 shadow-sm flex items-center gap-1">
                  <i class="fas fa-eye text-xs"></i> 详情
                </button>
                <button @click="approveMerchant(merchant)" class="px-3 py-1.5 bg-green-500 text-white hover:bg-green-600 rounded-lg text-xs font-medium transition-all hover:scale-105 shadow-sm flex items-center gap-1">
                  <i class="fas fa-check-circle text-xs"></i> 通过
                </button>
                <button @click="rejectMerchant(merchant)" class="px-3 py-1.5 bg-red-500 text-white hover:bg-red-600 rounded-lg text-xs font-medium transition-all hover:scale-105 shadow-sm flex items-center gap-1">
                  <i class="fas fa-times-circle text-xs"></i> 拒绝
                </button>
              </div>
            </td>
          </tr>

          <!-- 空状态 -->
          <tr v-if="paginatedMerchants.length === 0">
            <td colspan="6" class="px-6 py-16 text-center">
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
                      <span v-if="currentMerchantDetail?.status === 1" class="px-2 py-0.5 text-xs bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400 rounded-full">
                        已通过
                      </span>
                      <span v-else-if="currentMerchantDetail?.status === 0" class="px-2 py-0.5 text-xs bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600 dark:text-yellow-400 rounded-full">
                        待审核
                      </span>
                      <span v-else-if="currentMerchantDetail?.status === 2" class="px-2 py-0.5 text-xs bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400 rounded-full">
                        未通过
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
                      <span class="text-lg font-bold text-gray-900 dark:text-white">{{ currentMerchantDetail?.merchantType === 1 ? '个人' : currentMerchantDetail?.merchantType === 2 ? '企业' : '其他' }}</span>
                    </div>
                  </div>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">营业执照号</p>
                    <div class="flex items-center gap-2">
                      <span class="text-lg font-bold text-gray-900 dark:text-white">{{ currentMerchantDetail?.businessLicense }}</span>
                    </div>
                  </div>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50">
                    <p class="text-xs text-gray-500 dark:text-gray-400 mb-1">创建时间</p>
                    <div class="flex items-center gap-2">
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ formatDate(currentMerchantDetail?.createTime) }}</span>
                    </div>
                  </div>
                </div>

                <!-- 联系信息 -->
                <div class="space-y-4">
                  <h4 class="font-medium text-gray-900 dark:text-white">联系信息</h4>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-5 space-y-4">
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">联系人</span>
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
                  </div>
                </div>

                <!-- 审核信息 -->
                <div class="space-y-4">
                  <h4 class="font-medium text-gray-900 dark:text-white">审核信息</h4>
                  <div class="bg-gray-50 dark:bg-gray-800/50 rounded-xl p-5 space-y-4">
                    <div class="flex items-center justify-between">
                      <span class="text-sm text-gray-500 dark:text-gray-400">拒绝原因</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentMerchantDetail?.rejectReason || '无' }}</span>
                    </div>
                    <div class="flex items-center justify-between" v-if="currentMerchantDetail?.auditTime">
                      <span class="text-sm text-gray-500 dark:text-gray-400">审核时间</span>
                      <span class="text-sm font-medium text-gray-900 dark:text-white">{{ currentMerchantDetail?.auditTime }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 抽屉底部 - 始终显示审核按钮 -->
              <div class="px-6 py-4 border-t border-gray-200 dark:border-gray-700 flex items-center justify-end gap-3">
                <button @click="approveMerchant(currentMerchantDetail)" class="px-5 py-2.5 bg-green-500 hover:bg-green-600 text-white rounded-xl text-sm font-medium shadow-lg hover:shadow-xl transition-all hover:scale-105 active:scale-95 flex items-center gap-2">
                  <i class="fas fa-check-circle"></i>
                  通过审核
                </button>
                <button @click="rejectMerchant(currentMerchantDetail)" class="px-5 py-2.5 bg-red-500 hover:bg-red-600 text-white rounded-xl text-sm font-medium shadow-lg hover:shadow-xl transition-all hover:scale-105 active:scale-95 flex items-center gap-2">
                  <i class="fas fa-times-circle"></i>
                  拒绝申请
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
import {ref, reactive, computed, watch, onMounted} from 'vue'
import { get, post } from '@/net/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import tokenManager from '@/utils/tokenManager'

const MERCHANT_API = 'http://localhost:8081/api/merchant'

// ============ 状态变量 ============
const merchants = ref([])
const loading = ref(false)
const totalMerchants = ref(0)
const todayNewMerchants = ref(0)
const merchantGrowth = ref(15)
const todayCompare = ref(20)
const approvedMerchants = ref(0)
const approvedRate = ref(0)
const pendingMerchants = ref(0)
const rejectedMerchants = ref(0)
const rejectedRate = ref(0)
const loginId = ref('')

// 筛选状态
const showMerchantTypeDropdown = ref(false)
const showStatusDropdown = ref(false)
const merchantTypeFilter = ref('all')
const statusFilter = ref('all')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// 审核表单
const auditForm = reactive({
  id: null,
  userId: null,
  adminId: null,
  status: 0,
  rejectReason: ''
})

//获取当前登录用户的id
const current = () =>{
  get('api/user/selectUserById',{},(message,data)=>{
    loginId.value=data.id
  })
}

// 详情抽屉
const showDetailDrawer = ref(false)
const currentMerchantDetail = ref(null)

// ============ 获取数据 ============
const fetchMerchants = async () => {
  loading.value = true
  const userId = 1 // 固定使用管理员ID

  try {
    const params = {
      merchantType: merchantTypeFilter.value === 'all' ? null : (merchantTypeFilter.value === 'retail' ? 1 : 2),
      status: statusFilter.value === 'all' ? null : (statusFilter.value === 'approved' ? 1 : statusFilter.value === 'pending' ? 0 : 2),
      merchantName: searchKeyword.value || null
    }

    await post(`${MERCHANT_API}/selectMerchant?userId=${userId}`, params, (message, data) => {
      merchants.value = data.map(item => ({
        ...item,
        id: String(item.id),
        userId: String(item.userId)
      }))

      // 计算统计数据
      totalMerchants.value = merchants.value.length
      approvedMerchants.value = merchants.value.filter(m => m.status === 1).length
      pendingMerchants.value = merchants.value.filter(m => m.status === 0).length
      rejectedMerchants.value = merchants.value.filter(m => m.status === 2).length
      approvedRate.value = totalMerchants.value > 0 ? Math.round((approvedMerchants.value / totalMerchants.value) * 100) : 0
      rejectedRate.value = totalMerchants.value > 0 ? Math.round((rejectedMerchants.value / totalMerchants.value) * 100) : 0

      // 获取今日新增
      fetchTodayNewMerchants()
    })
  } catch (error) {
    console.error('获取商家数据出错:', error)
  } finally {
    loading.value = false
  }
}

// 获取今日新增商家
const fetchTodayNewMerchants = async () => {
  try {
    await get(`${MERCHANT_API}/getTodayMerchantCount`, null, (message, data) => {
      todayNewMerchants.value = data
    })
  } catch (error) {
    console.error('获取今日新增失败:', error)
  }
}

// 搜索商家
const handleSearch = async () => {
  if (!searchKeyword.value) {
    fetchMerchants()
    return
  }

  try {
    await get(`${MERCHANT_API}/searchMerchant?keyword=${encodeURIComponent(searchKeyword.value)}&userId=1`, null, (message, data) => {
      merchants.value = data.map(item => ({
        ...item,
        id: String(item.id),
        userId: String(item.userId)
      }))
      currentPage.value = 1
    })
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

// 清空搜索
const clearSearch = () => {
  searchKeyword.value = ''
  fetchMerchants()
}

// 按状态筛选
const filterByStatus = async (status) => {
  try {
    await get(`${MERCHANT_API}/selectMerchantByStatus?status=${status}&userId=1`, null, (message, data) => {
      merchants.value = data.map(item => ({
        ...item,
        id: String(item.id),
        userId: String(item.userId)
      }))
      statusFilter.value = status === 0 ? 'pending' : status === 1 ? 'approved' : 'rejected'
      currentPage.value = 1
    })
  } catch (error) {
    console.error('筛选失败:', error)
  }
}

// ============ 审核操作 ============
const auditMerchant = async (merchant, status) => {
  try {
    // 直接使用原始ID字符串，避免精度丢失
    auditForm.id = merchant.id
    auditForm.status = status
    auditForm.userId = merchant.userId
    auditForm.adminId = loginId.value

    if (status === 2) {
      try {
        const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝商家', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: /\S/,
          inputErrorMessage: '拒绝原因不能为空'
        })
        auditForm.rejectReason = reason
      } catch {
        return
      }
    } else {
      auditForm.rejectReason = ''
    }

    console.log('审核请求数据:', auditForm)
    
    await post(`${MERCHANT_API}/adminAuditMerchant`, auditForm, (message) => {
      ElMessage.success(`商家${status === 1 ? '通过' : '拒绝'}成功`)
      fetchMerchants()
      if (showDetailDrawer.value) {
        closeDetailDrawer()
      }
    })
  } catch (error) {
    console.error('审核商家出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const approveMerchant = (merchant) => {
  auditMerchant(merchant, 1)
}

const rejectMerchant = (merchant) => {
  auditMerchant(merchant, 2)
}

// ============ 详情操作 ============
const viewMerchantDetail = async (merchant) => {
  try {
    await get(`${MERCHANT_API}/selectMerchantById/${merchant.id}?userId=1`, null, (message, data) => {
      currentMerchantDetail.value = {
        ...data,
        id: String(data.id),
        userId: String(data.userId)
      }
      showDetailDrawer.value = true
    })
  } catch (error) {
    console.error('获取商家详情出错:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const closeDetailDrawer = () => {
  showDetailDrawer.value = false
  currentMerchantDetail.value = null
}

// ============ 筛选方法 ============
const toggleMerchantTypeDropdown = () => {
  showMerchantTypeDropdown.value = !showMerchantTypeDropdown.value
  showStatusDropdown.value = false
}

const toggleStatusDropdown = () => {
  showStatusDropdown.value = !showStatusDropdown.value
  showMerchantTypeDropdown.value = false
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

// ============ 工具方法 ============
const formatNumber = (num) => {
  return num?.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',') || '0'
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return '-'
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  } catch {
    return '-'
  }
}

// 导出数据
const handleExport = () => {
  if (filteredMerchants.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }

  try {
    const headers = ['ID', '商家名称', '商家类型', '联系人', '电话', '营业执照号', '状态', '创建时间']
    const statusText = (s) => {
      if (s === 0) return '待审核'
      if (s === 1) return '已通过'
      if (s === 2) return '未通过'
      return '未知'
    }
    const typeText = (t) => {
      if (t === 1) return '个人'
      if (t === 2) return '企业'
      return '其他'
    }

    const csvLines = [headers.join(',')]
    filteredMerchants.value.forEach(m => {
      csvLines.push([
        m.id,
        m.merchantName,
        typeText(m.merchantType),
        m.contactName,
        m.contactPhone,
        m.businessLicense,
        statusText(m.status),
        m.createTime || ''
      ].join(','))
    })

    const blob = new Blob(['\uFEFF' + csvLines.join('\n')], { type: 'text/csv;charset=utf-8' })
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = `商家列表_${new Date().toISOString().slice(0, 10)}.csv`
    a.click()
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// ============ 计算属性 ============
const filteredMerchants = computed(() => {
  return merchants.value
})

const totalPages = computed(() => {
  return Math.ceil(filteredMerchants.value.length / pageSize.value)
})

const paginatedMerchants = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredMerchants.value.slice(start, end)
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

// ============ 生命周期 ============
onMounted(() => {
  current()
  fetchMerchants()
  fetchTodayNewMerchants()
})
</script>

<style scoped>
/* 动画样式 */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}

.page-enter-active {
  animation: fade-up 0.6s ease-out;
}

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
</style>
