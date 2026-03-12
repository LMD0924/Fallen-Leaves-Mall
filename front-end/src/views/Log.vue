<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <!-- 页面标题 -->
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-gray-800">操作日志管理</h1>
      <p class="text-sm text-gray-500 mt-1">查看和管理系统所有操作记录</p>
    </div>

    <!-- 搜索条件卡片 -->
    <div class="bg-white rounded-lg shadow-sm p-6 mb-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <!-- 日志类型 -->
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-2">日志类型</label>
          <select v-model="filters.logType" class="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option value="">全部</option>
            <option v-for="type in logTypes" :key="type.value" :value="type.value">
              {{ type.label }}
            </option>
          </select>
        </div>

        <!-- 业务模块 -->
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-2">业务模块</label>
          <select v-model="filters.businessModule" class="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option value="">全部</option>
            <option v-for="module in businessModules" :key="module.value" :value="module.value">
              {{ module.label }}
            </option>
          </select>
        </div>

        <!-- 操作者 -->
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-2">操作者</label>
          <input type="text" v-model="filters.operator" placeholder="姓名/ID"
                 class="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <!-- 业务ID/单号 -->
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-2">业务ID/单号</label>
          <input type="text" v-model="filters.businessNo" placeholder="订单号/用户ID"
                 class="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <!-- 时间范围 -->
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-2">开始时间</label>
          <input type="datetime-local" v-model="filters.startTime"
                 class="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-600 mb-2">结束时间</label>
          <input type="datetime-local" v-model="filters.endTime"
                 class="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
        </div>

        <!-- 执行结果 -->
        <div>
          <label class="block text-sm font-medium text-gray-600 mb-2">执行结果</label>
          <select v-model="filters.resultStatus" class="w-full px-3 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
            <option value="">全部</option>
            <option value="1">成功</option>
            <option value="2">失败</option>
            <option value="3">部分成功</option>
          </select>
        </div>

        <!-- 搜索按钮 -->
        <div class="flex items-end">
          <button @click="handleSearch" class="w-full bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition duration-200 flex items-center justify-center">
            <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            查询
          </button>
        </div>
      </div>

      <!-- 快捷筛选 -->
      <div class="flex flex-wrap gap-2 mt-4 pt-4 border-t border-gray-100">
        <span class="text-sm text-gray-500 py-2">快捷筛选：</span>
        <button v-for="quick in quickFilters" :key="quick.value" @click="applyQuickFilter(quick.value)"
                class="px-3 py-1 text-sm bg-gray-100 text-gray-600 rounded-full hover:bg-gray-200 transition duration-200">
          {{ quick.label }}
        </button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div class="bg-white rounded-lg shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">今日日志</p>
            <p class="text-2xl font-bold text-gray-800">{{ statistics.todayCount }}</p>
          </div>
          <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
            <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">用户操作</p>
            <p class="text-2xl font-bold text-gray-800">{{ statistics.userOperCount }}</p>
          </div>
          <div class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
            <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">订单操作</p>
            <p class="text-2xl font-bold text-gray-800">{{ statistics.orderOperCount }}</p>
          </div>
          <div class="w-10 h-10 bg-yellow-100 rounded-full flex items-center justify-center">
            <svg class="w-5 h-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow-sm p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">失败操作</p>
            <p class="text-2xl font-bold text-red-600">{{ statistics.failCount }}</p>
          </div>
          <div class="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
            <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 日志列表 -->
    <div class="bg-white rounded-lg shadow-sm overflow-hidden">
      <!-- 列表头部 -->
      <div class="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
        <h2 class="text-lg font-semibold text-gray-800">日志记录</h2>
        <div class="flex items-center space-x-2">
          <button @click="exportLogs" class="px-3 py-1 text-sm text-gray-600 hover:text-blue-600 transition duration-200">
            <svg class="w-4 h-4 inline mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            导出
          </button>
          <button @click="refreshLogs" class="px-3 py-1 text-sm text-gray-600 hover:text-blue-600 transition duration-200">
            <svg class="w-4 h-4 inline mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            刷新
          </button>
        </div>
      </div>

      <!-- 表格 -->
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">时间</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作者</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">业务模块</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作描述</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">业务ID/单号</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">结果</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">IP地址</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">耗时</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
          </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
          <tr v-for="log in logs" :key="log.id" class="hover:bg-gray-50 transition duration-200">
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
              {{ formatDate(log.createTime) }}
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium text-gray-800">{{ log.operatorName }}</div>
              <div class="text-xs text-gray-500">{{ getOperatorTypeLabel(log.operatorType) }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 text-xs rounded-full" :class="getModuleBadgeClass(log.businessModule)">
                  {{ getBusinessModuleLabel(log.businessModule) }}
                </span>
            </td>
            <td class="px-6 py-4">
              <div class="text-sm text-gray-800">{{ log.operationDesc }}</div>
              <div class="text-xs text-gray-500">{{ getLogTypeLabel(log.logType) }} · {{ log.operationAction }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm text-gray-600">{{ log.businessNo || '-' }}</div>
              <div class="text-xs text-gray-500">ID: {{ log.businessId || '-' }}</div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 text-xs rounded-full" :class="getStatusBadgeClass(log.resultStatus)">
                  {{ getResultStatusLabel(log.resultStatus) }}
                </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ log.operatorIp || '-' }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ log.executionDuration ? log.executionDuration + 'ms' : '-' }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              <button @click="viewLogDetail(log)" class="text-blue-600 hover:text-blue-800 mr-3">详情</button>
              <button @click="viewTrace(log.traceId)" class="text-green-600 hover:text-green-800 mr-3">追踪</button>
              <button @click="deleteLog(log.logUuid)" class="text-red-600 hover:text-red-800">删除</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 空状态 -->
      <div v-if="logs.length === 0" class="text-center py-12">
        <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
        <h3 class="mt-2 text-sm font-medium text-gray-900">暂无日志记录</h3>
        <p class="mt-1 text-sm text-gray-500">尝试调整筛选条件或等待新的操作产生</p>
      </div>

      <!-- 分页 -->
      <div class="px-6 py-4 border-t border-gray-100 flex items-center justify-between">
        <div class="text-sm text-gray-500">
          显示第 {{ pagination.start }} 到第 {{ pagination.end }} 条记录，共 {{ pagination.total }} 条
        </div>
        <div class="flex items-center space-x-2">
          <button @click="prevPage" :disabled="pagination.current === 1"
                  class="px-3 py-1 border border-gray-200 rounded-lg text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50">
            上一页
          </button>
          <span class="px-3 py-1 text-sm text-gray-600">第 {{ pagination.current }} / {{ pagination.totalPages }} 页</span>
          <button @click="nextPage" :disabled="pagination.current === pagination.totalPages"
                  class="px-3 py-1 border border-gray-200 rounded-lg text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50">
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 日志详情弹窗 -->
    <div v-if="showDetailModal" class="fixed inset-0 z-50 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <div class="flex items-end justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" @click="showDetailModal = false"></div>
        <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-4xl sm:w-full">
          <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-medium text-gray-900">日志详情 - {{ currentLog.logUuid }}</h3>
              <button @click="showDetailModal = false" class="text-gray-400 hover:text-gray-500">
                <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <!-- 详情内容 -->
            <div class="grid grid-cols-2 gap-4">
              <!-- 基本信息 -->
              <div class="col-span-2 bg-gray-50 p-4 rounded-lg">
                <h4 class="text-sm font-medium text-gray-500 mb-3">基本信息</h4>
                <div class="grid grid-cols-3 gap-4">
                  <div>
                    <p class="text-xs text-gray-500">日志类型</p>
                    <p class="text-sm font-medium">{{ getLogTypeLabel(currentLog.logType) }}</p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">业务模块</p>
                    <p class="text-sm font-medium">{{ getBusinessModuleLabel(currentLog.businessModule) }}</p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">操作动作</p>
                    <p class="text-sm font-medium">{{ currentLog.operationAction }}</p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">操作时间</p>
                    <p class="text-sm font-medium">{{ formatDateTime(currentLog.createTime) }}</p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">执行结果</p>
                    <p class="text-sm font-medium" :class="getResultStatusColor(currentLog.resultStatus)">
                      {{ getResultStatusLabel(currentLog.resultStatus) }}
                    </p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">执行耗时</p>
                    <p class="text-sm font-medium">{{ currentLog.executionDuration }}ms</p>
                  </div>
                </div>
              </div>

              <!-- 操作者信息 -->
              <div class="bg-gray-50 p-4 rounded-lg">
                <h4 class="text-sm font-medium text-gray-500 mb-3">操作者信息</h4>
                <div class="space-y-2">
                  <div class="flex justify-between">
                    <span class="text-xs text-gray-500">操作者类型：</span>
                    <span class="text-sm">{{ getOperatorTypeLabel(currentLog.operatorType) }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-xs text-gray-500">操作者ID：</span>
                    <span class="text-sm">{{ currentLog.operatorId }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-xs text-gray-500">操作者姓名：</span>
                    <span class="text-sm">{{ currentLog.operatorName }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-xs text-gray-500">IP地址：</span>
                    <span class="text-sm">{{ currentLog.operatorIp }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-xs text-gray-500">设备类型：</span>
                    <span class="text-sm">{{ currentLog.deviceType || '-' }}</span>
                  </div>
                </div>
              </div>

              <!-- 业务信息 -->
              <div class="bg-gray-50 p-4 rounded-lg">
                <h4 class="text-sm font-medium text-gray-500 mb-3">业务信息</h4>
                <div class="space-y-2">
                  <div class="flex justify-between">
                    <span class="text-xs text-gray-500">业务ID：</span>
                    <span class="text-sm">{{ currentLog.businessId || '-' }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-xs text-gray-500">业务单号：</span>
                    <span class="text-sm">{{ currentLog.businessNo || '-' }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-xs text-gray-500">业务子类型：</span>
                    <span class="text-sm">{{ currentLog.businessType || '-' }}</span>
                  </div>
                  <div class="flex justify-between">
                    <span class="text-xs text-gray-500">操作描述：</span>
                    <span class="text-sm">{{ currentLog.operationDesc }}</span>
                  </div>
                </div>
              </div>

              <!-- 请求信息 -->
              <div class="col-span-2 bg-gray-50 p-4 rounded-lg">
                <h4 class="text-sm font-medium text-gray-500 mb-3">请求信息</h4>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <p class="text-xs text-gray-500">请求URL</p>
                    <p class="text-sm break-all">{{ currentLog.requestUrl || '-' }}</p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">请求方法</p>
                    <p class="text-sm">{{ currentLog.requestMethod || '-' }}</p>
                  </div>
                  <div class="col-span-2">
                    <p class="text-xs text-gray-500">User Agent</p>
                    <p class="text-sm break-all">{{ currentLog.userAgent || '-' }}</p>
                  </div>
                </div>
              </div>

              <!-- 数据变更 -->
              <div v-if="currentLog.oldData || currentLog.newData" class="col-span-2 bg-gray-50 p-4 rounded-lg">
                <h4 class="text-sm font-medium text-gray-500 mb-3">数据变更</h4>
                <div class="grid grid-cols-2 gap-4">
                  <div v-if="currentLog.oldData">
                    <p class="text-xs text-gray-500 mb-2">变更前数据</p>
                    <pre class="text-xs bg-gray-800 text-gray-100 p-3 rounded-lg overflow-auto max-h-60">{{ formatJsonData(currentLog.oldData) }}</pre>
                  </div>
                  <div v-if="currentLog.newData">
                    <p class="text-xs text-gray-500 mb-2">变更后数据</p>
                    <pre class="text-xs bg-gray-800 text-gray-100 p-3 rounded-lg overflow-auto max-h-60">{{ formatJsonData(currentLog.newData) }}</pre>
                  </div>
                </div>
                <div v-if="currentLog.changeData" class="mt-3">
                  <p class="text-xs text-gray-500 mb-2">变更字段</p>
                  <div class="flex flex-wrap gap-2">
                    <span v-for="field in parseChangeData(currentLog.changeData)" :key="field" class="px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded-full">
                      {{ field }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 错误信息 -->
              <div v-if="currentLog.resultStatus !== 1" class="col-span-2 bg-red-50 p-4 rounded-lg">
                <h4 class="text-sm font-medium text-red-800 mb-2">错误信息</h4>
                <p class="text-sm text-red-600">错误码：{{ currentLog.errorCode || '-' }}</p>
                <p class="text-sm text-red-600 mt-1">错误信息：{{ currentLog.errorMsg || '-' }}</p>
              </div>

              <!-- 追踪信息 -->
              <div class="col-span-2 bg-gray-50 p-4 rounded-lg">
                <h4 class="text-sm font-medium text-gray-500 mb-3">追踪信息</h4>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <p class="text-xs text-gray-500">Trace ID</p>
                    <p class="text-sm font-mono">{{ currentLog.traceId || '-' }}</p>
                  </div>
                  <div>
                    <p class="text-xs text-gray-500">父日志ID</p>
                    <p class="text-sm">{{ currentLog.parentLogId || '-' }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            <button type="button" @click="showDetailModal = false" class="w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 sm:mt-0 sm:w-auto sm:text-sm">
              关闭
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import axios from 'axios'
import tokenManager from "@/utils/tokenManager";

// 日志类型常量
const logTypes = [
  { value: 1, label: '用户行为' },
  { value: 2, label: '订单操作' },
  { value: 3, label: '商品操作' },
  { value: 4, label: '系统操作' },
  { value: 5, label: '支付日志' },
  { value: 6, label: '营销活动' }
]

// 业务模块常量
const businessModules = [
  { value: 'order', label: '订单模块' },
  { value: 'product', label: '商品模块' },
  { value: 'user', label: '用户模块' },
  { value: 'marketing', label: '营销模块' },
  { value: 'payment', label: '支付模块' }
]

// 操作者类型
const operatorTypes = {
  1: '管理员',
  2: '商家',
  3: '普通用户',
  4: '系统自动'
}

// 快捷筛选
const quickFilters = [
  { label: '今天', value: 'today' },
  { label: '昨天', value: 'yesterday' },
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' },
  { label: '失败记录', value: 'failed' }
]

// 筛选条件
const filters = reactive({
  logType: '',
  businessModule: '',
  operator: '',
  businessNo: '',
  startTime: '',
  endTime: '',
  resultStatus: ''
})

// 统计数据
const statistics = ref({
  todayCount: 0,
  userOperCount: 0,
  orderOperCount: 0,
  failCount: 0
})

// 日志列表数据
const logs = ref([])
const loading = ref(false)
const error = ref('')

// 分页信息
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  start: computed(() => (pagination.current - 1) * pagination.pageSize + 1),
  end: computed(() => Math.min(pagination.current * pagination.pageSize, pagination.total)),
  totalPages: computed(() => Math.ceil(pagination.total / pagination.pageSize))
})

// 弹窗控制
const showDetailModal = ref(false)
const currentLog = ref({})

// 格式化函数
const formatDate = (datetime) => {
  if (!datetime) return ''
  const date = new Date(datetime)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const formatDateTime = (datetime) => {
  if (!datetime) return ''
  const date = new Date(datetime)
  return date.toLocaleString('zh-CN')
}

// 格式化JSON数据，处理解析错误
const formatJsonData = (data) => {
  if (!data) return ''
  try {
    return JSON.stringify(JSON.parse(data), null, 2)
  } catch (error) {
    // 解析失败时返回原始数据
    return data
  }
}

// 解析变更字段，处理解析错误
const parseChangeData = (data) => {
  if (!data) return []
  try {
    const parsed = JSON.parse(data)
    return Array.isArray(parsed) ? parsed : [parsed]
  } catch (error) {
    // 解析失败时返回空数组
    return []
  }
}

// 获取标签函数
const getLogTypeLabel = (type) => {
  const found = logTypes.find(t => t.value === type)
  return found ? found.label : '未知'
}

const getBusinessModuleLabel = (module) => {
  const found = businessModules.find(m => m.value === module)
  return found ? found.label : module || '未知'
}

const getOperatorTypeLabel = (type) => {
  return operatorTypes[type] || '未知'
}

const getResultStatusLabel = (status) => {
  const map = { 1: '成功', 2: '失败', 3: '部分成功' }
  return map[status] || '未知'
}

// 样式类
const getModuleBadgeClass = (module) => {
  const classes = {
    order: 'bg-blue-100 text-blue-700',
    product: 'bg-green-100 text-green-700',
    user: 'bg-purple-100 text-purple-700',
    marketing: 'bg-yellow-100 text-yellow-700',
    payment: 'bg-red-100 text-red-700'
  }
  return classes[module] || 'bg-gray-100 text-gray-700'
}

const getStatusBadgeClass = (status) => {
  const classes = {
    1: 'bg-green-100 text-green-700',
    2: 'bg-red-100 text-red-700',
    3: 'bg-yellow-100 text-yellow-700'
  }
  return classes[status] || 'bg-gray-100 text-gray-700'
}

const getResultStatusColor = (status) => {
  const classes = {
    1: 'text-green-600',
    2: 'text-red-600',
    3: 'text-yellow-600'
  }
  return classes[status] || 'text-gray-600'
}

// API调用
const fetchLogs = async () => {
  loading.value = true
  error.value = ''
  try {
    // 使用axios直接调用，绕过自定义的post方法，因为后端返回的数据格式不同
    const response = await axios.post('http://localhost:8082/api/log/list', {
      logType: filters.logType || undefined,
      businessModule: filters.businessModule || undefined,
      operator: filters.operator || undefined,
      businessNo: filters.businessNo || undefined,
      startTime: filters.startTime ? new Date(filters.startTime).toISOString() : undefined,
      endTime: filters.endTime ? new Date(filters.endTime).toISOString() : undefined,
      resultStatus: filters.resultStatus || undefined,
      page: pagination.current,
      pageSize: pagination.pageSize
    }, {
      headers: {
        "Content-Type": "application/json",
        "Authorization": tokenManager.getAccessToken() ? `Bearer ${tokenManager.getAccessToken()}` : ''
      },
      withCredentials: true
    })

    const data = response.data || {}
    logs.value = data.list || []
    pagination.total = data.total || 0
  } catch (err) {
    error.value = '获取日志列表失败'
    console.error('获取日志列表失败', err)
    logs.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  try {
    // 使用axios直接调用，绕过自定义的get方法
    const response = await axios.get('http://localhost:8082/api/log/statistics', {
      headers: {
        "Authorization": tokenManager.getAccessToken() ? `Bearer ${tokenManager.getAccessToken()}` : ''
      },
      withCredentials: true
    })
    const data = response.data || {}
    statistics.value = {
      todayCount: data.todayCount || 0,
      userOperCount: data.userOperCount || 0,
      orderOperCount: data.orderOperCount || 0,
      failCount: data.failCount || 0
    }
  } catch (err) {
    console.error('获取统计数据失败', err)
    // 保持statistics对象的结构，避免渲染错误
    statistics.value = {
      todayCount: 0,
      userOperCount: 0,
      orderOperCount: 0,
      failCount: 0
    }
  }
}

const fetchLogDetail = async (logUuid) => {
  try {
    // 使用axios直接调用，绕过自定义的get方法
    const response = await axios.get(`http://localhost:8082/api/log/detail/${logUuid}`, {
      headers: {
        "Authorization": tokenManager.getAccessToken() ? `Bearer ${tokenManager.getAccessToken()}` : ''
      },
      withCredentials: true
    })
    return response.data || null
  } catch (err) {
    console.error('获取日志详情失败', err)
    return null
  }
}

const exportLogs = async () => {
  try {
    // 使用axios直接调用，绕过自定义的post方法
    const response = await axios.post('http://localhost:8082/api/log/export', {
      logType: filters.logType || undefined,
      businessModule: filters.businessModule || undefined,
      operator: filters.operator || undefined,
      businessNo: filters.businessNo || undefined,
      startTime: filters.startTime ? new Date(filters.startTime).toISOString() : undefined,
      endTime: filters.endTime ? new Date(filters.endTime).toISOString() : undefined,
      resultStatus: filters.resultStatus || undefined,
      page: 1,
      pageSize: 1000
    }, {
      headers: {
        "Content-Type": "application/json",
        "Authorization": tokenManager.getAccessToken() ? `Bearer ${tokenManager.getAccessToken()}` : ''
      },
      withCredentials: true,
      responseType: 'blob'
    })

    if (response && response.data) {
      // 处理导出文件
      const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `日志导出_${new Date().toISOString().slice(0, 10)}.xlsx`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    }
  } catch (err) {
    console.error('导出日志失败', err)
  }
}

const deleteLog = async (logUuid) => {
  if (!confirm('确定要删除这条日志吗？')) return

  try {
    // 使用axios直接调用，绕过自定义的del方法
    const response = await axios.delete(`http://localhost:8082/api/log/delete/${logUuid}`, {
      headers: {
        "Content-Type": "application/json",
        "Authorization": tokenManager.getAccessToken() ? `Bearer ${tokenManager.getAccessToken()}` : ''
      },
      withCredentials: true
    })
    const data = response.data || {}
    if (data.success) {
      await fetchLogs()
      await fetchStatistics()
    }
  } catch (err) {
    console.error('删除日志失败', err)
  }
}

// 操作方法
const handleSearch = () => {
  pagination.current = 1
  fetchLogs()
}

const applyQuickFilter = (type) => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')

  switch (type) {
    case 'today':
      filters.startTime = `${year}-${month}-${day}T00:00`
      filters.endTime = `${year}-${month}-${day}T23:59`
      break
    case 'yesterday':
      const yesterday = new Date(today)
      yesterday.setDate(yesterday.getDate() - 1)
      const yesterdayYear = yesterday.getFullYear()
      const yesterdayMonth = String(yesterday.getMonth() + 1).padStart(2, '0')
      const yesterdayDay = String(yesterday.getDate()).padStart(2, '0')
      filters.startTime = `${yesterdayYear}-${yesterdayMonth}-${yesterdayDay}T00:00`
      filters.endTime = `${yesterdayYear}-${yesterdayMonth}-${yesterdayDay}T23:59`
      break
    case 'week':
      const weekStart = new Date(today)
      weekStart.setDate(weekStart.getDate() - weekStart.getDay())
      const weekStartYear = weekStart.getFullYear()
      const weekStartMonth = String(weekStart.getMonth() + 1).padStart(2, '0')
      const weekStartDay = String(weekStart.getDate()).padStart(2, '0')
      filters.startTime = `${weekStartYear}-${weekStartMonth}-${weekStartDay}T00:00`
      filters.endTime = `${year}-${month}-${day}T23:59`
      break
    case 'month':
      filters.startTime = `${year}-${month}-01T00:00`
      filters.endTime = `${year}-${month}-${day}T23:59`
      break
    case 'failed':
      filters.resultStatus = '2'
      break
  }
  handleSearch()
}

const refreshLogs = () => {
  handleSearch()
  fetchStatistics()
}

const prevPage = () => {
  if (pagination.current > 1) {
    pagination.current--
    fetchLogs()
  }
}

const nextPage = () => {
  if (pagination.current < pagination.totalPages) {
    pagination.current++
    fetchLogs()
  }
}

const viewLogDetail = async (log) => {
  const detail = await fetchLogDetail(log.logUuid)
  if (detail) {
    currentLog.value = detail
    showDetailModal.value = true
  }
}

const viewTrace = (traceId) => {
  console.log('查看追踪：', traceId)
  // 跳转到链路追踪页面
}

// 初始化
onMounted(() => {
  // 设置默认时间范围（今天）
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  filters.startTime = `${year}-${month}-${day}T00:00`
  filters.endTime = `${year}-${month}-${day}T23:59`

  handleSearch()
  fetchStatistics()
})
</script>
