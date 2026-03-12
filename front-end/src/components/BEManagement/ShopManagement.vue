<!-- ShopManagement.vue -->
<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const router = useRouter()

// 店铺统计数据（按数据库字段调整）
const shopStats = ref([
  { label: '总店铺数', value: '2,345', trend: '+8.2%', icon: 'fa-store', bgColor: 'bg-blue-100 dark:bg-blue-900/30', iconColor: 'text-blue-600 dark:text-blue-400' },
  { label: '营业中', value: '1,890', trend: '+5.3%', icon: 'fa-store-alt', bgColor: 'bg-green-100 dark:bg-green-900/30', iconColor: 'text-green-600 dark:text-green-400' },
  { label: '待审核', value: '28', trend: '-2', icon: 'fa-clock', bgColor: 'bg-yellow-100 dark:bg-yellow-900/30', iconColor: 'text-yellow-600 dark:text-yellow-400' },
  { label: '已关闭', value: '427', trend: '-12', icon: 'fa-store-slash', bgColor: 'bg-red-100 dark:bg-red-900/30', iconColor: 'text-red-600 dark:text-red-400' }
])

// 搜索表单（字段名匹配数据库）
const searchForm = reactive({
  keyword: '',
  status: '', // 店铺运营状态：1=正常/2=休息中/3=已关闭
  shopLevel: '', // 店铺等级：1=普通/2=银牌/3=金牌/4=钻石
  shopStatus: '', // 审核状态：1=待审核/2=审核通过/3=审核不通过
  dateRange: [],
  sortField: 'create_time', // 数据库字段名
  sortOrder: 'descending'
})

// 店铺列表数据
const shopList = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 选中的店铺
const selectedShops = ref([])

// 店铺分类选项（数据库shop_category表对应）
const categoryOptions = [
  { label: '全部', value: '' },
  { label: '旗舰店', value: 'flagship', count: 156 },
  { label: '专营店', value: 'specialty', count: 432 },
  { label: '专卖店', value: 'exclusive', count: 278 },
  { label: '普通店', value: 'normal', count: 1479 }
]

// 店铺等级选项（匹配数据库：1=普通/2=银牌/3=金牌/4=钻石）
const levelOptions = [
  { label: '全部', value: '' },
  { label: '普通店铺', value: 1, color: 'text-blue-600', bg: 'bg-blue-100' },
  { label: '银牌店铺', value: 2, color: 'text-gray-600', bg: 'bg-gray-100' },
  { label: '金牌店铺', value: 3, color: 'text-yellow-600', bg: 'bg-yellow-100' },
  { label: '钻石店铺', value: 4, color: 'text-purple-600', bg: 'bg-purple-100' }
]

// 店铺运营状态选项（匹配数据库：1=正常/2=休息中/3=已关闭）
const statusOptions = [
  { label: '全部', value: '' },
  { label: '营业中', value: 1, type: 'success' },
  { label: '休息中', value: 2, type: 'warning' },
  { label: '已关闭', value: 3, type: 'danger' }
]

// 审核状态选项（匹配数据库：1=待审核/2=审核通过/3=审核不通过）
const auditStatusOptions = [
  { label: '全部', value: '' },
  { label: '待审核', value: 1, type: 'info' },
  { label: '审核通过', value: 2, type: 'success' },
  { label: '审核不通过', value: 3, type: 'danger' }
]

// 状态标签样式映射（匹配数据库数字值）
const statusMap = {
  1: { label: '营业中', type: 'success', icon: 'fa-circle-check' },
  2: { label: '休息中', type: 'warning', icon: 'fa-clock' },
  3: { label: '已关闭', type: 'danger', icon: 'fa-circle-xmark' }
}

// 审核状态映射（匹配数据库数字值）
const auditStatusMap = {
  1: { label: '待审核', type: 'info', icon: 'fa-hourglass-half' },
  2: { label: '审核通过', type: 'success', icon: 'fa-check-circle' },
  3: { label: '审核不通过', type: 'danger', icon: 'fa-times-circle' }
}

// 等级映射（匹配数据库：1=普通/2=银牌/3=金牌/4=钻石）
const levelMap = {
  1: { label: '普通店铺', color: 'text-blue-600', bg: 'bg-blue-100' },
  2: { label: '银牌店铺', color: 'text-gray-600', bg: 'bg-gray-100' },
  3: { label: '金牌店铺', color: 'text-yellow-600', bg: 'bg-yellow-100' },
  4: { label: '钻石店铺', color: 'text-purple-600', bg: 'bg-purple-100' }
}

// 分类映射
const categoryMap = {
  flagship: '旗舰店',
  specialty: '专营店',
  exclusive: '专卖店',
  normal: '普通店'
}

// 获取店铺列表（修正接口地址+参数，匹配数据库字段）
const fetchShopList = () => {
  loading.value = true
  // 构造请求参数，匹配后端查询条件
  const params = {
    page: currentPage.value,
    pageSize: pageSize.value,
    keyword: searchForm.keyword,
    status: searchForm.status,
    shopLevel: searchForm.shopLevel,
    shopStatus: searchForm.shopStatus,
    startTime: searchForm.dateRange[0] ? new Date(searchForm.dateRange[0]).toISOString() : '',
    endTime: searchForm.dateRange[1] ? new Date(searchForm.dateRange[1]).toISOString() : '',
    sortField: searchForm.sortField,
    sortOrder: searchForm.sortOrder
  }

  axios.get('http://localhost:8081/api/shop/list', {
    params: params
  })
    .then(response => {
      // 检查响应数据格式
      if (response.data && response.data.records) {
        shopList.value = response.data.records || []
        total.value = response.data.total || 0
      } else if (response.data) {
        // 如果后端直接返回Page对象
        shopList.value = response.data.records || []
        total.value = response.data.total || 0
      } else {
        shopList.value = []
        total.value = 0
      }
    })
    .catch(error => {
      ElMessage.error('获取店铺列表失败')
      console.error('获取店铺列表失败:', error)
    })
    .finally(() => {
      loading.value = false
    })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchShopList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  searchForm.shopLevel = ''
  searchForm.shopStatus = ''
  searchForm.dateRange = []
  handleSearch()
}

// 新增店铺
const handleAdd = () => {
  router.push('/shop-management/add')
}

// 编辑店铺
const handleEdit = (row) => {
  router.push(`/shop-management/edit/${row.id}`)
}

// 查看详情
const handleView = (row) => {
  router.push(`/shop-management/detail/${row.id}`)
}

// 审核店铺（核心修正：传递数字参数，匹配后端枚举code）
const handleVerify = (row) => {
  ElMessageBox.confirm(`是否审核通过店铺"${row.shopName}"？`, '店铺审核', {
    confirmButtonText: '通过',
    cancelButtonText: '拒绝',
    distinguishCancelAndClose: true,
    type: 'info'
  }).then(() => {
    // 审核通过：传递数字2（匹配后端AUDIT_APPROVED的code）
    axios.put(`http://localhost:8081/api/shop/review/${row.id}`, {}, {
      params: {
        shopStatus: 2 // 审核通过=2
      }
    })
      .then(() => {
        ElMessage.success('审核通过成功')
        fetchShopList()
      })
      .catch(error => {
        ElMessage.error('审核失败')
        console.error('审核失败:', error)
      })
  }).catch((action) => {
    if (action === 'cancel') {
      // 拒绝审核：传递数字3 + 拒绝原因
      ElMessageBox.prompt('请输入拒绝原因', '拒绝审核', {
        confirmButtonText: '确认拒绝',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (!value) {
          ElMessage.warning('请输入拒绝原因')
          return
        }
        axios.put(`http://localhost:8081/api/shop/review/${row.id}`, {}, {
          params: {
            shopStatus: 3, // 审核不通过=3
            rejectReason: value
          }
        })
          .then(() => {
            ElMessage.success('已拒绝审核')
            fetchShopList()
          })
          .catch(error => {
            ElMessage.error('审核失败')
            console.error('审核失败:', error)
          })
      })
    }
  })
}

// 上下架店铺（修正状态判断：数字1=营业中/2=休息中/3=已关闭）
const handleToggleStatus = (row) => {
  const targetStatus = row.status === 1 ? 3 : 1 // 营业中→已关闭，非营业中→营业中
  const action = row.status === 1 ? '下架' : '上架'
  ElMessageBox.confirm(`确定要${action}店铺"${row.shopName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 调用上下架接口
    axios.put(`http://localhost:8081/api/shop/status/${row.id}`, {}, {
      params: { status: targetStatus }
    })
      .then(() => {
        ElMessage.success(`${action}成功`)
        fetchShopList()
      })
      .catch(error => {
        ElMessage.error(`${action}失败`)
        console.error(`${action}失败:`, error)
      })
  })
}

// 删除店铺
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除店铺"${row.shopName}"吗？此操作不可恢复！`, '警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    axios.delete(`http://localhost:8081/api/shop/${row.id}`)
      .then(() => {
        ElMessage.success('删除成功')
        fetchShopList()
      })
      .catch(error => {
        ElMessage.error('删除失败')
        console.error('删除失败:', error)
      })
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedShops.value.length === 0) {
    ElMessage.warning('请至少选择一条数据')
    return
  }
  ElMessageBox.confirm(`确定要删除选中的 ${selectedShops.value.length} 个店铺吗？`, '警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    axios.delete(`http://localhost:8081/api/shop/batch`, {
      data: selectedShops.value // 批量删除传递ID列表
    })
      .then(() => {
        ElMessage.success('批量删除成功')
        selectedShops.value = []
        fetchShopList()
      })
      .catch(error => {
        ElMessage.error('批量删除失败')
        console.error('批量删除失败:', error)
      })
  })
}

// 导出数据
const handleExport = () => {
  // 构造导出参数
  const params = {
    keyword: searchForm.keyword,
    status: searchForm.status,
    shopLevel: searchForm.shopLevel,
    shopStatus: searchForm.shopStatus
  }
  // 导出接口（GET请求，返回文件流）
  axios.get('http://localhost:8081/api/shop/export', {
    params: params,
    responseType: 'blob'
  })
    .then(response => {
      // 处理文件下载
      const blob = new Blob([response.data])
      const fileName = `店铺列表_${new Date().getTime()}.xlsx`
      const a = document.createElement('a')
      a.href = URL.createObjectURL(blob)
      a.download = fileName
      a.click()
      URL.revokeObjectURL(a.href)
      ElMessage.success('导出数据成功')
    })
    .catch(error => {
      ElMessage.error('导出数据失败')
      console.error('导出数据失败:', error)
    })
}

onMounted(() => {
  fetchShopList()
})
</script>

<template>
  <div class="space-y-6">
    <!-- 页面标题与面包屑 -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white flex items-center">
          <i class="fas fa-store text-blue-500 mr-3"></i>
          店铺管理
        </h1>
        <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
          管理平台所有店铺信息、审核、上下架等操作
        </p>
      </div>
      <div class="flex items-center space-x-3">
        <button
          @click="handleAdd"
          class="px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-xl hover:shadow-lg hover:shadow-blue-500/30 transition-all flex items-center space-x-2"
        >
          <i class="fas fa-plus"></i>
          <span>新增店铺</span>
        </button>
        <button
          @click="handleExport"
          class="px-4 py-2 border border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-200 rounded-xl hover:bg-gray-50 dark:hover:bg-gray-800 transition-all flex items-center space-x-2"
        >
          <i class="fas fa-download"></i>
          <span>导出数据</span>
        </button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
      <div
        v-for="(stat, index) in shopStats"
        :key="index"
        class="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-xl transition-all group"
      >
        <div class="flex items-start justify-between">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">{{ stat.label }}</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ stat.value }}</p>
            <p class="text-xs mt-2" :class="stat.trend.startsWith('+') ? 'text-green-600' : 'text-red-600'">
              {{ stat.trend }} 较上月
            </p>
          </div>
          <div :class="['w-12 h-12 rounded-xl flex items-center justify-center', stat.bgColor, 'group-hover:scale-110 transition-transform']">
            <i :class="['fas', stat.icon, stat.iconColor, 'text-xl']"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索卡片 - 现代化设计 -->
    <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 p-6">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-sm font-semibold text-gray-900 dark:text-white flex items-center">
          <i class="fas fa-filter text-blue-500 mr-2"></i>
          筛选条件
        </h2>
        <button @click="resetSearch" class="text-xs text-gray-500 hover:text-blue-500 transition-colors">
          <i class="fas fa-redo-alt mr-1"></i>
          重置
        </button>
      </div>
      <el-form :model="searchForm" label-position="top" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <el-form-item label="搜索关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="店铺名称/商家ID/联系方式"
            clearable
            class="custom-input"
          >
            <template #prefix>
              <i class="fas fa-search text-gray-400"></i>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="店铺状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable class="w-full">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="店铺等级">
          <el-select v-model="searchForm.shopLevel" placeholder="全部" clearable class="w-full">
            <el-option
              v-for="item in levelOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="审核状态">
          <el-select v-model="searchForm.shopStatus" placeholder="全部" clearable class="w-full">
            <el-option
              v-for="item in auditStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="创建时间" class="lg:col-span-2">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            class="w-full"
          />
        </el-form-item>

        <div class="lg:col-span-2 flex items-end justify-end space-x-3">
          <el-button type="primary" @click="handleSearch" class="px-6">
            <i class="fas fa-search mr-2"></i>
            查询
          </el-button>
          <el-button @click="resetSearch">
            <i class="fas fa-undo mr-2"></i>
            重置
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 店铺列表卡片 -->
    <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 overflow-hidden">
      <!-- 列表工具栏 -->
      <div class="p-4 border-b border-gray-200/50 dark:border-gray-700/50 flex items-center justify-between">
        <div class="flex items-center space-x-3">
          <el-checkbox
            v-model="selectedShops"
            :indeterminate="selectedShops.length > 0 && selectedShops.length < shopList.length"
            @change="val => selectedShops = val ? shopList.map(s => s.id) : []"
          >
            <span class="text-sm text-gray-700 dark:text-gray-300">全选</span>
          </el-checkbox>
          <span class="text-sm text-gray-500">已选择 {{ selectedShops.length }} 项</span>
        </div>
        <div class="flex items-center space-x-2">
          <button
            @click="handleBatchDelete"
            class="px-3 py-1.5 text-sm text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-all"
          >
            <i class="fas fa-trash-alt mr-1"></i>
            批量删除
          </button>
          <button
            class="px-3 py-1.5 text-sm text-gray-600 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg transition-all"
            @click="fetchShopList"
          >
            <i class="fas fa-sync-alt mr-1" :class="{ 'animate-spin': loading }"></i>
            刷新
          </button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        :data="shopList"
        v-loading="loading"
        @selection-change="val => selectedShops = val.map(v => v.id)"
        class="w-full"
        :header-cell-style="{ background: 'transparent', color: '#6b7280', fontWeight: '500' }"
        row-class-name="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column label="店铺信息" min-width="200">
          <template #default="{ row }">
            <div class="flex items-center py-3">
              <img
                :src="row.shopLogo || 'https://picsum.photos/48/48'"
                class="w-12 h-12 rounded-xl object-cover mr-3 border border-gray-200 dark:border-gray-700"
                alt="店铺logo"
              >
              <div>
                <div class="flex items-center mb-1">
                  <span class="font-semibold text-gray-900 dark:text-white">{{ row.shopName }}</span>
                  <span v-if="levelMap[row.shopLevel]" :class="[levelMap[row.shopLevel].bg, levelMap[row.shopLevel].color, 'text-xs px-2 py-0.5 rounded-full ml-2']">
                    {{ levelMap[row.shopLevel].label }}
                  </span>
                </div>
                <div class="text-xs text-gray-500 dark:text-gray-400 flex items-center space-x-2">
                  <span><i class="fas fa-user mr-1"></i>商家ID: {{ row.merchantId }}</span>
                  <span v-if="row.contactWechat"><i class="fas fa-weixin mr-1"></i>{{ row.contactWechat }}</span>
                  <span v-if="row.contactQq"><i class="fas fa-qq mr-1"></i>{{ row.contactQq }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="店铺状态" width="90">
          <template #default="{ row }">
            <span v-if="statusMap[row.status]" :class="[
              'inline-flex items-center px-2 py-1 rounded-lg text-xs',
              statusMap[row.status].type === 'success' ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' : '',
              statusMap[row.status].type === 'warning' ? 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400' : '',
              statusMap[row.status].type === 'danger' ? 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400' : ''
            ]">
              <i :class="['fas', statusMap[row.status].icon, 'mr-1 text-xs']"></i>
              {{ statusMap[row.status].label }}
            </span>
            <span v-else class="text-xs text-gray-500">未知</span>
          </template>
        </el-table-column>

        <el-table-column label="审核状态" width="110">
          <template #default="{ row }">
            <span v-if="auditStatusMap[row.shopStatus]" :class="[
              'inline-flex items-center px-2 py-1 rounded-lg text-xs',
              auditStatusMap[row.shopStatus].type === 'success' ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' : '',
              auditStatusMap[row.shopStatus].type === 'info' ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' : '',
              auditStatusMap[row.shopStatus].type === 'danger' ? 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400' : ''
            ]">
              <i :class="['fas', auditStatusMap[row.shopStatus].icon, 'mr-1 text-xs']"></i>
              {{ auditStatusMap[row.shopStatus].label }}
            </span>
            <span v-else class="text-xs text-gray-500">未知</span>
          </template>
        </el-table-column>

        <el-table-column prop="productCount" label="商品数" width="80" sortable>
          <template #default="{ row }">
            <span class="text-sm font-medium text-gray-900 dark:text-white">{{ row.productCount || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="monthSales" label="月销量" width="80" sortable>
          <template #default="{ row }">
            <span class="text-sm font-medium text-gray-900 dark:text-white">{{ row.monthSales || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="totalSales" label="总销量" width="80" sortable>
          <template #default="{ row }">
            <span class="text-sm font-medium text-gray-900 dark:text-white">{{ row.totalSales || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="shopScore" label="店铺评分" width="80" sortable>
          <template #default="{ row }">
            <div class="flex items-center">
              <span class="text-sm font-medium text-gray-900 dark:text-white mr-1">{{ row.shopScore || 5.00 }}</span>
              <i class="fas fa-star text-yellow-400 text-xs"></i>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="110" sortable>
          <template #default="{ row }">
            <span class="text-sm text-gray-500">{{ row.createTime }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <div class="flex items-center space-x-2">
              <!-- 仅待审核状态显示审核按钮 -->
              <button
                v-if="row.shopStatus === 1"
                @click="handleVerify(row)"
                class="px-2 py-1 text-xs bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 rounded-lg hover:bg-blue-200 transition-all"
              >
                <i class="fas fa-check mr-1"></i>
                审核
              </button>
              <button
                @click="handleView(row)"
                class="px-2 py-1 text-xs bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300 rounded-lg hover:bg-gray-200 transition-all"
              >
                <i class="fas fa-eye mr-1"></i>
                详情
              </button>
              <button
                @click="handleEdit(row)"
                class="px-2 py-1 text-xs bg-indigo-100 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400 rounded-lg hover:bg-indigo-200 transition-all"
              >
                <i class="fas fa-edit mr-1"></i>
                编辑
              </button>
              <button
                @click="handleToggleStatus(row)"
                :class="[
                  'px-2 py-1 text-xs rounded-lg transition-all',
                  row.status === 1
                    ? 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400 hover:bg-yellow-200'
                    : 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 hover:bg-green-200'
                ]"
              >
                <i :class="['fas', row.status === 1 ? 'fa-eye-slash' : 'fa-eye', 'mr-1']"></i>
                {{ row.status === 1 ? '下架' : '上架' }}
              </button>
              <button
                @click="handleDelete(row)"
                class="px-2 py-1 text-xs bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 rounded-lg hover:bg-red-200 transition-all"
              >
                <i class="fas fa-trash-alt mr-1"></i>
                删除
              </button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="p-4 border-t border-gray-200/50 dark:border-gray-700/50 flex items-center justify-between">
        <div class="text-sm text-gray-500">
          显示第 {{ (currentPage - 1) * pageSize + 1 }} 到第 {{ Math.min(currentPage * pageSize, total) }} 条记录，共 {{ total }} 条
        </div>
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="fetchShopList"
          @current-change="fetchShopList"
          background
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 自定义Element Plus样式 */
:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-date-editor) {
  background-color: transparent;
  box-shadow: 0 0 0 1px #e5e7eb inset;
  border-radius: 0.75rem;
  transition: all 0.2s;
}

.dark :deep(.el-input__wrapper),
.dark :deep(.el-select__wrapper),
.dark :deep(.el-date-editor) {
  background-color: transparent;
  box-shadow: 0 0 0 1px #374151 inset;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover),
:deep(.el-date-editor:hover) {
  box-shadow: 0 0 0 1px #3b82f6 inset;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focus),
:deep(.el-date-editor.is-focused) {
  box-shadow: 0 0 0 2px #3b82f6 inset;
}

:deep(.el-table) {
  --el-table-border-color: transparent;
  --el-table-header-bg-color: transparent;
  background-color: transparent;
  width: 100% !important;
  overflow-x: hidden;
}

:deep(.el-table__row) {
  border-bottom: 1px solid #e5e7eb;
}

.dark :deep(.el-table__row) {
  border-bottom: 1px solid #374151;
}

:deep(.el-table__inner-wrapper::before) {
  display: none;
}

:deep(.el-pagination.is-background .btn-prev),
:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .el-pager li) {
  background-color: transparent;
  border: 1px solid #e5e7eb;
  margin: 0 4px;
  border-radius: 0.5rem;
  transition: all 0.2s;
}

.dark :deep(.el-pagination.is-background .btn-prev),
.dark :deep(.el-pagination.is-background .btn-next),
.dark :deep(.el-pagination.is-background .el-pager li) {
  border-color: #374151;
  color: #9ca3af;
}

:deep(.el-pagination.is-background .el-pager li:not(.disabled).active) {
  background: linear-gradient(to right, #3b82f6, #8b5cf6);
  border-color: transparent;
  color: white;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  :deep(.el-table__header-wrapper),
  :deep(.el-table__body-wrapper) {
    overflow-x: auto;
    width: 100%;
  }

  :deep(.el-table) {
    min-width: 1000px;
  }
}

/* 操作按钮调整 */
:deep(.el-table-column--selection) {
  width: 55px !important;
}

/* 移除表格的默认滚动条 */
:deep(.el-table__body-wrapper) {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  display: none;
}
</style>
