<template>
  <div class="space-y-6">
    <!-- 欢迎区域 - 动态问候 -->
    <div class="relative overflow-hidden bg-gradient-to-br from-blue-600 via-purple-600 to-pink-500 dark:from-blue-700 dark:via-purple-700 dark:to-pink-600 rounded-3xl p-8 shadow-2xl">
      <!-- 装饰元素 -->
      <div class="absolute top-0 right-0 w-96 h-96 bg-white/10 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2"></div>
      <div class="absolute bottom-0 left-0 w-64 h-64 bg-black/10 rounded-full blur-3xl -translate-x-1/2 translate-y-1/2"></div>
      <div class="absolute top-1/2 left-1/2 w-48 h-48 bg-white/5 rounded-full blur-2xl"></div>

      <div class="relative z-10 flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
        <div>
          <div class="flex items-center gap-3 mb-3">
            <span class="px-3 py-1 bg-white/20 backdrop-blur-sm rounded-full text-white text-xs font-medium border border-white/30">
              🎉 欢迎回来
            </span>
            <span class="px-3 py-1 bg-white/20 backdrop-blur-sm rounded-full text-white text-xs font-medium border border-white/30">
              {{ currentDate }}
            </span>
          </div>
          <h1 class="text-3xl lg:text-4xl font-bold text-white mb-3 tracking-tight">
            {{ greetingMessage }}，{{ userInfo.username || '管理员' }}！👋
          </h1>
          <p class="text-white/80 text-lg max-w-2xl leading-relaxed">
            这是您今天的仪表盘概览，{{ randomTip }}
          </p>
        </div>

        <!-- 快捷操作卡片 -->
        <div class="flex items-center gap-4">
          <div class="bg-white/10 backdrop-blur-sm rounded-2xl p-5 border border-white/20 min-w-[180px]">
            <p class="text-white/70 text-sm mb-1 flex items-center gap-1">
              <i class="fas fa-calendar-check"></i>
              待办事项
            </p>
            <p class="text-3xl font-bold text-white mb-1">{{ pendingTasks }}</p>
            <p class="text-white/60 text-xs">今日还需处理 {{ urgentTasks }} 项紧急任务</p>
          </div>
          <button class="w-12 h-12 bg-white/10 backdrop-blur-sm rounded-2xl border border-white/20 flex items-center justify-center text-white hover:bg-white/20 transition-all hover:scale-110">
            <i class="fas fa-arrow-right text-xl"></i>
          </button>
        </div>
      </div>

      <!-- 天气小部件 -->
      <div class="absolute top-8 right-8 hidden lg:flex items-center gap-3 text-white/80 bg-white/10 backdrop-blur-sm px-4 py-2 rounded-2xl border border-white/20">
        <i class="fas fa-sun text-yellow-300 text-xl"></i>
        <span class="font-medium">22°C</span>
        <span class="w-px h-4 bg-white/30 mx-1"></span>
        <span>上海 · 晴</span>
      </div>
    </div>

    <!-- 核心数据指标 - 现代化卡片网格 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div v-for="(stat, index) in statistics" :key="stat.label"
           class="group bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-200/50 dark:border-gray-700/50 hover:shadow-xl transition-all duration-500 hover:-translate-y-1 relative overflow-hidden"
           :style="{ animationDelay: `${index * 100}ms` }"
      >
        <!-- 装饰光效 -->
        <div class="absolute top-0 right-0 w-24 h-24 bg-gradient-to-br from-blue-500/5 to-purple-500/5 rounded-full blur-2xl group-hover:from-blue-500/10 group-hover:to-purple-500/10 transition-all duration-500"></div>

        <div class="flex items-center justify-between relative z-10">
          <div>
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1 flex items-center gap-1.5">
              <span class="w-1 h-1 bg-gray-400 dark:bg-gray-500 rounded-full"></span>
              {{ stat.label }}
            </p>
            <p class="text-3xl font-bold text-gray-900 dark:text-white tracking-tight stat-number">
              {{ stat.prefix }}{{ formatNumber(stat.value) }}{{ stat.suffix }}
            </p>
            <div class="flex items-center gap-2 mt-3">
              <span
                class="text-xs px-2 py-1 rounded-full flex items-center gap-1"
                :class="stat.trend > 0 ? 'bg-green-100 dark:bg-green-900/30 text-green-600 dark:text-green-400' : 'bg-red-100 dark:bg-red-900/30 text-red-600 dark:text-red-400'"
              >
                <i :class="['fas', stat.trend > 0 ? 'fa-arrow-up' : 'fa-arrow-down']" class="text-xs"></i>
                {{ Math.abs(stat.trend) }}%
              </span>
              <span class="text-xs text-gray-500 dark:text-gray-400">较昨日</span>
            </div>
          </div>
          <div :class="[
            'w-14 h-14 rounded-2xl flex items-center justify-center transition-all duration-500 group-hover:scale-110 group-hover:rotate-3',
            stat.bgColor
          ]">
            <i :class="['fas', stat.icon, 'text-2xl', stat.iconColor]"></i>
          </div>
        </div>

        <!-- 进度条装饰 -->
        <div class="mt-4 h-1.5 bg-gray-100 dark:bg-gray-800 rounded-full overflow-hidden">
          <div
            class="h-full rounded-full bg-gradient-to-r from-blue-500 to-purple-500 transition-all duration-1000"
            :style="{ width: stat.progress + '%' }"
          ></div>
        </div>
      </div>
    </div>

    <!-- 第二层指标 - 业务健康度 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
      <div v-for="metric in healthMetrics" :key="metric.label"
           class="bg-white/50 dark:bg-gray-900/50 backdrop-blur-sm rounded-xl p-4 border border-gray-200/50 dark:border-gray-700/50 flex items-center gap-3 hover:shadow-md transition-all">
        <div :class="['w-10 h-10 rounded-lg', metric.bgColor, 'flex items-center justify-center']">
          <i :class="['fas', metric.icon, metric.iconColor]"></i>
        </div>
        <div>
          <p class="text-xs text-gray-500 dark:text-gray-400">{{ metric.label }}</p>
          <p class="text-lg font-bold text-gray-900 dark:text-white">{{ metric.value }}</p>
        </div>
      </div>
    </div>

    <!-- 图表与分析区域 - 双栏布局 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 访问趋势图表卡片 -->
      <div class="lg:col-span-2 bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 shadow-sm hover:shadow-lg transition-all p-6">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <span class="w-1 h-5 bg-gradient-to-b from-blue-500 to-purple-500 rounded-full"></span>
              访问趋势
            </h3>
            <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">近7日流量统计</p>
          </div>
          <div class="flex items-center gap-2">
            <button class="px-3 py-1.5 text-xs bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors">
              周
            </button>
            <button class="px-3 py-1.5 text-xs bg-blue-500 text-white rounded-lg">
              月
            </button>
            <button class="px-3 py-1.5 text-xs bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors">
              年
            </button>
          </div>
        </div>

        <!-- 模拟图表 - 使用CSS柱状图 -->
        <div class="h-64 flex items-end justify-between gap-2 pt-6">
          <div v-for="(item, index) in chartData" :key="index" class="flex-1 flex flex-col items-center group">
            <div class="relative w-full flex justify-center">
              <div
                class="w-8 bg-gradient-to-t from-blue-500 to-purple-500 rounded-t-lg transition-all duration-1500 group-hover:from-blue-600 group-hover:to-purple-600 relative cursor-pointer"
                :style="{ height: item.height + 'px', animation: `chartRise 1.5s ease-out ${index * 0.1}s` }"
              >
                <div class="absolute -top-7 left-1/2 transform -translate-x-1/2 bg-gray-900 dark:bg-white text-white dark:text-gray-900 text-xs px-2 py-1 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap shadow-lg">
                  {{ item.value }} 访问量
                </div>
              </div>
            </div>
            <span class="text-xs text-gray-500 dark:text-gray-400 mt-3">{{ item.label }}</span>
          </div>
        </div>

        <!-- 统计摘要 -->
        <div class="grid grid-cols-3 gap-4 mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
          <div class="text-center">
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">总访问量</p>
            <p class="text-xl font-bold text-gray-900 dark:text-white">45.2K</p>
            <span class="text-xs text-green-600 dark:text-green-400">↑12.3%</span>
          </div>
          <div class="text-center border-x border-gray-200 dark:border-gray-700">
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">独立访客</p>
            <p class="text-xl font-bold text-gray-900 dark:text-white">23.8K</p>
            <span class="text-xs text-green-600 dark:text-green-400">↑8.7%</span>
          </div>
          <div class="text-center">
            <p class="text-sm text-gray-500 dark:text-gray-400 mb-1">平均时长</p>
            <p class="text-xl font-bold text-gray-900 dark:text-white">3m 42s</p>
            <span class="text-xs text-red-600 dark:text-red-400">↓2.1%</span>
          </div>
        </div>
      </div>

      <!-- 右侧 - 快捷操作与待办 -->
      <div class="space-y-6">
        <!-- 待办事项卡片 -->
        <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 shadow-sm hover:shadow-lg transition-all p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <span class="w-1 h-5 bg-gradient-to-b from-yellow-500 to-orange-500 rounded-full"></span>
              待办事项
            </h3>
            <span class="text-xs px-2 py-1 bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600 dark:text-yellow-400 rounded-full">
              {{ pendingTasks }}项未完成
            </span>
          </div>

          <div class="space-y-4">
            <div v-for="(task, index) in todoTasks" :key="index"
                 class="flex items-center justify-between group hover:bg-gray-50 dark:hover:bg-gray-800 p-2 rounded-xl transition-colors duration-300">
              <div class="flex items-center gap-3">
                <label class="relative flex items-center cursor-pointer">
                  <input type="checkbox" v-model="task.completed" class="w-5 h-5 rounded-lg border-2 border-gray-300 dark:border-gray-600 text-blue-500 focus:ring-blue-500/20 focus:ring-offset-0 transition-all"/>
                </label>
                <div>
                  <p class="text-sm font-medium text-gray-700 dark:text-gray-300" :class="{ 'line-through text-gray-400': task.completed }">
                    {{ task.title }}
                  </p>
                  <p class="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-1 mt-0.5">
                    <i :class="['fas', task.priority === 'high' ? 'fa-circle text-red-500' : task.priority === 'medium' ? 'fa-circle text-yellow-500' : 'fa-circle text-blue-500']" class="text-[8px]"></i>
                    {{ task.deadline }}
                  </p>
                </div>
              </div>
              <button class="opacity-0 group-hover:opacity-100 transition-opacity text-gray-400 hover:text-gray-600 dark:hover:text-gray-300">
                <i class="fas fa-ellipsis-v"></i>
              </button>
            </div>
          </div>

          <button class="w-full mt-4 py-2.5 border border-dashed border-gray-300 dark:border-gray-700 rounded-xl text-sm text-gray-500 dark:text-gray-400 hover:border-blue-500 hover:text-blue-500 dark:hover:border-blue-400 dark:hover:text-blue-400 transition-colors flex items-center justify-center gap-2">
            <i class="fas fa-plus"></i>
            添加新任务
          </button>
        </div>

        <!-- 快捷入口卡片 -->
        <div class="bg-gradient-to-br from-blue-500 to-purple-600 rounded-2xl p-6 shadow-xl relative overflow-hidden group">
          <div class="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full blur-3xl"></div>
          <div class="absolute bottom-0 left-0 w-24 h-24 bg-black/10 rounded-full blur-2xl"></div>

          <div class="relative z-10">
            <h4 class="text-white font-bold mb-1 flex items-center gap-2">
              <i class="fas fa-rocket"></i>
              快捷入口
            </h4>
            <p class="text-white/70 text-xs mb-4">常用功能一键直达</p>

            <div class="grid grid-cols-3 gap-3">
              <button class="bg-white/10 backdrop-blur-sm hover:bg-white/20 rounded-xl p-3 text-center transition-all duration-500 hover:scale-105 border border-white/20">
                <i class="fas fa-user-plus text-white text-xl mb-1"></i>
                <span class="text-white text-xs block">新增用户</span>
              </button>
              <button class="bg-white/10 backdrop-blur-sm hover:bg-white/20 rounded-xl p-3 text-center transition-all duration-500 hover:scale-105 border border-white/20">
                <i class="fas fa-store text-white text-xl mb-1"></i>
                <span class="text-white text-xs block">添加商家</span>
              </button>
              <button class="bg-white/10 backdrop-blur-sm hover:bg-white/20 rounded-xl p-3 text-center transition-all duration-500 hover:scale-105 border border-white/20">
                <i class="fas fa-box text-white text-xl mb-1"></i>
                <span class="text-white text-xs block">上架商品</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最新数据表格 - 用户/商家/店铺混合卡片 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 最新注册用户 -->
      <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 shadow-sm hover:shadow-lg transition-all p-6">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <span class="w-1 h-5 bg-gradient-to-b from-green-500 to-emerald-500 rounded-full"></span>
              最新注册用户
            </h3>
            <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">今日新增 {{ todayNewUsers }} 人</p>
          </div>
          <router-link to="/users" class="text-sm text-blue-500 hover:text-blue-600 dark:text-blue-400 flex items-center gap-1 group">
            查看全部
            <i class="fas fa-arrow-right text-xs group-hover:translate-x-1 transition-transform"></i>
          </router-link>
        </div>

        <div class="space-y-4">
          <div v-for="(user, index) in recentUsers" :key="user.id"
               class="flex items-center justify-between group hover:bg-gray-50 dark:hover:bg-gray-800 p-3 rounded-xl transition-all">
            <div class="flex items-center gap-3">
              <div class="relative">
                <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-semibold shadow-lg">
                  {{ user.name.charAt(0) }}
                </div>
                <span class="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-green-500 border-2 border-white dark:border-gray-900 rounded-full"></span>
              </div>
              <div>
                <p class="font-medium text-gray-900 dark:text-white flex items-center gap-2">
                  {{ user.name }}
                  <span v-if="user.verify" class="px-1.5 py-0.5 text-xs bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400 rounded-full flex items-center gap-0.5">
                    <i class="fas fa-check-circle text-[10px]"></i> 认证
                  </span>
                </p>
                <p class="text-xs text-gray-500 dark:text-gray-400">{{ user.email }}</p>
              </div>
            </div>
            <span class="text-xs text-gray-500 dark:text-gray-400">{{ formatTimeAgo(user.registerDate) }}</span>
          </div>
        </div>
      </div>

      <!-- 热门商家/店铺 -->
      <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 shadow-sm hover:shadow-lg transition-all p-6">
        <div class="flex items-center justify-between mb-6">
          <div>
            <h3 class="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
              <span class="w-1 h-5 bg-gradient-to-b from-purple-500 to-pink-500 rounded-full"></span>
              热门商家
            </h3>
            <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">本周活跃商家排行</p>
          </div>
          <router-link to="/merchants" class="text-sm text-purple-500 hover:text-purple-600 dark:text-purple-400 flex items-center gap-1 group">
            查看全部
            <i class="fas fa-arrow-right text-xs group-hover:translate-x-1 transition-transform"></i>
          </router-link>
        </div>

        <div class="space-y-4">
          <div v-for="(merchant, index) in topMerchants" :key="merchant.id"
               class="flex items-center justify-between group hover:bg-gray-50 dark:hover:bg-gray-800 p-3 rounded-xl transition-all">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center text-white font-bold shadow-lg">
                {{ index + 1 }}
              </div>
              <div>
                <p class="font-medium text-gray-900 dark:text-white flex items-center gap-2">
                  {{ merchant.name }}
                  <span class="px-1.5 py-0.5 text-xs bg-purple-100 dark:bg-purple-900/30 text-purple-600 dark:text-purple-400 rounded-full">
                    {{ merchant.level }}
                  </span>
                </p>
                <p class="text-xs text-gray-500 dark:text-gray-400 flex items-center gap-2">
                  <span>{{ merchant.category }}</span>
                  <span class="w-1 h-1 bg-gray-400 rounded-full"></span>
                  <span class="text-yellow-500">★ {{ merchant.rating }}</span>
                </p>
              </div>
            </div>
            <div class="text-right">
              <p class="text-sm font-bold text-gray-900 dark:text-white">¥{{ formatNumber(merchant.revenue) }}</p>
              <p class="text-xs text-green-600 dark:text-green-400">↑{{ merchant.growth }}%</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品与店铺数据卡片 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 商品分类分布 -->
      <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 shadow-sm hover:shadow-lg transition-all p-6">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2">
            <span class="w-1 h-5 bg-gradient-to-b from-blue-500 to-cyan-500 rounded-full"></span>
            商品分类
          </h3>
          <i class="fas fa-chart-pie text-gray-400 text-xl"></i>
        </div>

        <!-- 环形图模拟 -->
        <div class="flex justify-center items-center gap-8">
          <div class="relative w-32 h-32">
            <svg viewBox="0 0 100 100" class="transform -rotate-90 w-32 h-32">
              <circle cx="50" cy="50" r="40" fill="none" stroke="#f3f4f6" stroke-width="12" class="dark:stroke-gray-800"/>
              <circle cx="50" cy="50" r="40" fill="none" stroke="#3b82f6" stroke-width="12" stroke-dasharray="251.2" stroke-dashoffset="75.36" stroke-linecap="round" class="transition-all duration-1500"/>
<circle cx="50" cy="50" r="40" fill="none" stroke="#8b5cf6" stroke-width="12" stroke-dasharray="251.2" stroke-dashoffset="150.72" stroke-linecap="round" class="transition-all duration-1500"/>
<circle cx="50" cy="50" r="40" fill="none" stroke="#ec4899" stroke-width="12" stroke-dasharray="251.2" stroke-dashoffset="200.96" stroke-linecap="round" class="transition-all duration-1500"/>
            </svg>
            <div class="absolute inset-0 flex items-center justify-center flex-col">
              <span class="text-2xl font-bold text-gray-900 dark:text-white">238</span>
              <span class="text-xs text-gray-500 dark:text-gray-400">总商品</span>
            </div>
          </div>

          <div class="space-y-2">
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 bg-blue-500 rounded-full"></span>
              <span class="text-xs text-gray-600 dark:text-gray-400">电子产品</span>
              <span class="text-xs font-bold text-gray-900 dark:text-white">45%</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 bg-purple-500 rounded-full"></span>
              <span class="text-xs text-gray-600 dark:text-gray-400">服装鞋包</span>
              <span class="text-xs font-bold text-gray-900 dark:text-white">30%</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 bg-pink-500 rounded-full"></span>
              <span class="text-xs text-gray-600 dark:text-gray-400">家居生活</span>
              <span class="text-xs font-bold text-gray-900 dark:text-white">25%</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 店铺等级分布 -->
      <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 shadow-sm hover:shadow-lg transition-all p-6">
        <h3 class="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2 mb-6">
          <span class="w-1 h-5 bg-gradient-to-b from-yellow-500 to-orange-500 rounded-full"></span>
          店铺等级分布
        </h3>

        <div class="space-y-4">
          <div v-for="level in shopLevels" :key="level.name" class="flex items-center gap-3">
            <span class="text-sm text-gray-600 dark:text-gray-400 w-16">{{ level.name }}</span>
            <div class="flex-1 h-2 bg-gray-100 dark:bg-gray-800 rounded-full overflow-hidden">
              <div
                class="h-full rounded-full bg-gradient-to-r from-yellow-400 to-orange-500 transition-all duration-700"
                :style="{ width: level.percentage + '%' }"
              ></div>
            </div>
            <span class="text-sm font-bold text-gray-900 dark:text-white min-w-[40px] text-right">{{ level.percentage }}%</span>
            <span class="text-xs text-gray-500 dark:text-gray-400 min-w-[50px] text-right">{{ level.count }}家</span>
          </div>
        </div>

        <div class="mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-500 dark:text-gray-400">总店铺数</span>
            <span class="text-2xl font-bold text-gray-900 dark:text-white">2,345</span>
          </div>
          <div class="flex items-center justify-between mt-2">
            <span class="text-sm text-gray-500 dark:text-gray-400">本月新增</span>
            <span class="text-sm font-medium text-green-600 dark:text-green-400">+128 家</span>
          </div>
        </div>
      </div>

      <!-- 系统状态 -->
      <div class="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200/50 dark:border-gray-700/50 shadow-sm hover:shadow-lg transition-all p-6">
        <h3 class="text-lg font-bold text-gray-900 dark:text-white flex items-center gap-2 mb-6">
          <span class="w-1 h-5 bg-gradient-to-b from-green-500 to-emerald-500 rounded-full"></span>
          系统状态
        </h3>

        <div class="space-y-5">
          <div v-for="status in systemStatus" :key="status.name" class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div :class="['w-8 h-8 rounded-lg', status.bgColor, 'flex items-center justify-center']">
                <i :class="['fas', status.icon, status.iconColor]"></i>
              </div>
              <div>
                <p class="text-sm font-medium text-gray-700 dark:text-gray-300">{{ status.name }}</p>
                <p class="text-xs text-gray-500 dark:text-gray-400">{{ status.desc }}</p>
              </div>
            </div>
            <div class="text-right">
              <span :class="['text-sm font-medium', status.status === '正常' ? 'text-green-600 dark:text-green-400' : 'text-yellow-600 dark:text-yellow-400']">
                {{ status.status }}
              </span>
              <p class="text-xs text-gray-500 dark:text-gray-400">{{ status.value }}</p>
            </div>
          </div>
        </div>

        <div class="mt-6 p-4 bg-gray-50 dark:bg-gray-800/50 rounded-xl">
          <div class="flex items-center justify-between mb-2">
            <span class="text-sm text-gray-600 dark:text-gray-400">服务器负载</span>
            <span class="text-sm font-bold text-gray-900 dark:text-white">32%</span>
          </div>
          <div class="h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
            <div class="w-1/3 h-full bg-gradient-to-r from-green-500 to-emerald-500 rounded-full"></div>
          </div>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-2">响应时间: 126ms</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { get } from '@/net/index.js'

const router = useRouter()

// ============ 用户信息 ============
const userInfo = ref({
  username: '',
  role: '',
  avatar: ''
})

const getUserInfo=()=>{
get("api/user/selectUserById",{},(message,data)=>{
  userInfo.value=data
})
}

// 获取用户总数
const getUserCount = () => {
  get("api/user/selectUserCount", {}, (message, data) => {
    statistics.value[0].value = data
  }, (message) => {
    console.error('获取用户总数失败:', message)
  })
}

// 获取商家总数
const getMerchantCount = () => {
  get("http://localhost:8081/api/merchant/getMerchantCount", {}, (message, data) => {
    statistics.value[1].value = data
  }, (message) => {
    console.error('获取商家总数失败:', message)
  })
}

// 获取今日新增用户数
const getTodayUserCount = () => {
  get("api/user/selectUserCountByToday", {}, (message, data) => {
    healthMetrics.value[0].value = data
  }, (message) => {
    console.error('获取今日新增用户数失败:', message)
  })
}

// 获取今日新增商家数
const getTodayMerchantCount = () => {
  get("http://localhost:8081/api/merchant/getTodayMerchantCount", {}, (message, data) => {
    healthMetrics.value[1].value = data
    console.log("商家",data)
  }, (message) => {
    console.error('获取今日新增商家数失败:', message)
  })
}

// ============ 问候与日期 ============
const currentDate = computed(() => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekday = weekdays[date.getDay()]
  return `${year}.${month}.${day} ${weekday}`
})

const greetingMessage = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '深夜好'
})

const randomTip = ref('昨日访问量增长12.3%，继续保持！')

// ============ 核心统计数据 ============
const statistics = ref([
  {
    label: '总用户数',
    value: 0,
    prefix: '',
    suffix: '',
    trend: 12.5,
    icon: 'fa-users',
    bgColor: 'bg-blue-100 dark:bg-blue-900/30',
    iconColor: 'text-blue-600 dark:text-blue-400',
    progress: 75
  },
  {
    label: '商家总数',
    value: 0,
    prefix: '',
    suffix: '',
    trend: 8.2,
    icon: 'fa-store',
    bgColor: 'bg-purple-100 dark:bg-purple-900/30',
    iconColor: 'text-purple-600 dark:text-purple-400',
    progress: 60
  },
  {
    label: '店铺数量',
    value: 2345,
    prefix: '',
    suffix: '',
    trend: 15.3,
    icon: 'fa-shop',
    bgColor: 'bg-green-100 dark:bg-green-900/30',
    iconColor: 'text-green-600 dark:text-green-400',
    progress: 82
  },
  {
    label: '商品总数',
    value: 45678,
    prefix: '',
    suffix: '',
    trend: -2.1,
    icon: 'fa-box',
    bgColor: 'bg-yellow-100 dark:bg-yellow-900/30',
    iconColor: 'text-yellow-600 dark:text-yellow-400',
    progress: 45
  }
])

// ============ 业务健康度指标 ============
const healthMetrics = ref([
  { label: '今日新增用户', value: '128', icon: 'fa-user-plus', bgColor: 'bg-blue-100 dark:bg-blue-900/30', iconColor: 'text-blue-600 dark:text-blue-400' },
  { label: '今日新增商家', value: '8', icon: 'fa-store-alt', bgColor: 'bg-purple-100 dark:bg-purple-900/30', iconColor: 'text-purple-600 dark:text-purple-400' },
  { label: '待审核店铺', value: '23', icon: 'fa-clock', bgColor: 'bg-yellow-100 dark:bg-yellow-900/30', iconColor: 'text-yellow-600 dark:text-yellow-400' },
  { label: '待发货订单', value: '156', icon: 'fa-truck', bgColor: 'bg-green-100 dark:bg-green-900/30', iconColor: 'text-green-600 dark:text-green-400' },
  { label: '退款售后', value: '12', icon: 'fa-rotate-left', bgColor: 'bg-red-100 dark:bg-red-900/30', iconColor: 'text-red-600 dark:text-red-400' }
])

// ============ 图表数据 ============
const chartData = ref([
  { label: '周一', value: 1240, height: 80 },
  { label: '周二', value: 1450, height: 94 },
  { label: '周三', value: 1680, height: 108 },
  { label: '周四', value: 1890, height: 122 },
  { label: '周五', value: 2100, height: 136 },
  { label: '周六', value: 1780, height: 115 },
  { label: '周日', value: 1520, height: 98 }
])

// ============ 待办事项 ============
const pendingTasks = ref(8)
const urgentTasks = ref(3)

const todoTasks = ref([
  { title: '审核新入驻商家申请', deadline: '还剩2小时', priority: 'high', completed: false },
  { title: '处理用户退款请求', deadline: '还剩4小时', priority: 'high', completed: false },
  { title: '更新商品分类', deadline: '今日内', priority: 'medium', completed: true },
  { title: '检查系统日志', deadline: '今日内', priority: 'low', completed: false },
  { title: '周报汇总', deadline: '明天10:00', priority: 'medium', completed: false }
])

// ============ 最新注册用户 ============
const todayNewUsers = ref(128)

const recentUsers = ref([
  { id: 1101, name: '林心怡', email: 'lin.xy@example.com', verify: true, registerDate: '2024-03-28T09:23:00' },
  { id: 1102, name: '张明远', email: 'zhang.my@example.com', verify: false, registerDate: '2024-03-28T10:45:00' },
  { id: 1103, name: '陈思思', email: 'chen.ss@example.com', verify: true, registerDate: '2024-03-28T11:12:00' },
  { id: 1104, name: '王浩然', email: 'wang.hr@example.com', verify: false, registerDate: '2024-03-28T13:30:00' },
  { id: 1105, name: '赵雨桐', email: 'zhao.yt@example.com', verify: true, registerDate: '2024-03-28T15:20:00' }
])

// ============ 热门商家 ============
const topMerchants = ref([
  { id: 2001, name: '数码前沿', level: '旗舰店', category: '电子产品', rating: 4.9, revenue: 125800, growth: 23.5 },
  { id: 2002, name: '时尚衣橱', level: '品牌店', category: '服装鞋包', rating: 4.8, revenue: 98400, growth: 18.2 },
  { id: 2003, name: '居家生活馆', level: '专营店', category: '家居生活', rating: 4.7, revenue: 76300, growth: 12.8 },
  { id: 2004, name: '运动天地', level: '旗舰店', category: '运动户外', rating: 4.9, revenue: 68900, growth: 31.4 }
])

// ============ 店铺等级分布 ============
const shopLevels = ref([
  { name: '旗舰店', count: 234, percentage: 25 },
  { name: '品牌店', count: 456, percentage: 35 },
  { name: '专营店', count: 789, percentage: 30 },
  { name: '普通店', count: 866, percentage: 10 }
])

// ============ 系统状态 ============
const systemStatus = ref([
  { name: '数据库', desc: 'MySQL 8.0', icon: 'fa-database', bgColor: 'bg-blue-100 dark:bg-blue-900/30', iconColor: 'text-blue-600 dark:text-blue-400', status: '正常', value: '连接池: 85%' },
  { name: '缓存服务', desc: 'Redis 7.0', icon: 'fa-bolt', bgColor: 'bg-yellow-100 dark:bg-yellow-900/30', iconColor: 'text-yellow-600 dark:text-yellow-400', status: '正常', value: '命中率: 92%' },
  { name: '对象存储', desc: 'OSS', icon: 'fa-cloud', bgColor: 'bg-purple-100 dark:bg-purple-900/30', iconColor: 'text-purple-600 dark:text-purple-400', status: '正常', value: '可用性: 99.9%' },
  { name: '消息队列', desc: 'RocketMQ', icon: 'fa-envelope', bgColor: 'bg-green-100 dark:bg-green-900/30', iconColor: 'text-green-600 dark:text-green-400', status: '警告', value: '堆积: 1.2K' }
])

// ============ 工具方法 ============
const formatNumber = (num) => {
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const formatTimeAgo = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMins / 60)
  const diffDays = Math.floor(diffHours / 24)

  if (diffMins < 1) return '刚刚'
  if (diffMins < 60) return `${diffMins}分钟前`
  if (diffHours < 24) return `${diffHours}小时前`
  if (diffDays < 30) return `${diffDays}天前`
  return formatDate(dateString)
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// ============ 生命周期 ============
onMounted(() => {
  getUserInfo()
  getUserCount()
  getMerchantCount()
  getTodayUserCount()
  getTodayMerchantCount()
  // 随机欢迎语
  const tips = [
    '昨日访问量增长12.3%，继续保持！',
    '您有8项待办事项需要处理',
    '本周活跃用户数创历史新高',
    '系统运行状态良好，无异常告警',
    '新增23家店铺待审核，请及时处理'
  ]
  randomTip.value = tips[Math.floor(Math.random() * tips.length)]
})
</script>

<style scoped>
/* 数字增长动画 */
@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-number {
  animation: countUp 1s cubic-bezier(0.23, 1, 0.32, 1) forwards;
}

/* 图表柱状图上升动画 */
@keyframes chartRise {
  from {
    height: 0;
    opacity: 0;
  }
  to {
    height: var(--height);
    opacity: 1;
  }
}

/* 卡片入场动画 */
@keyframes cardFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.grid > div {
  animation: cardFadeIn 0.8s cubic-bezier(0.23, 1, 0.32, 1) forwards;
  opacity: 0;
}

/* 渐变边框效果 */
.border-gradient {
  border: 1px solid transparent;
  background-clip: padding-box;
  position: relative;
}

.border-gradient::after {
  content: '';
  position: absolute;
  top: -1px;
  right: -1px;
  bottom: -1px;
  left: -1px;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  border-radius: inherit;
  z-index: -1;
}

/* 自定义复选框 */
input[type="checkbox"] {
  cursor: pointer;
  transition: all 0.3s ease;
}

input[type="checkbox"]:checked {
  background-image: url("data:image/svg+xml,%3csvg viewBox='0 0 16 16' fill='white' xmlns='http://www.w3.org/2000/svg'%3e%3cpath d='M12.207 4.793a1 1 0 010 1.414l-5 5a1 1 0 01-1.414 0l-2-2a1 1 0 011.414-1.414L6.5 9.086l4.293-4.293a1 1 0 011.414 0z'/%3e%3c/svg%3e");
  background-size: 100% 100%;
  background-position: 50%;
  background-repeat: no-repeat;
  background-color: #3b82f6;
  border-color: #3b82f6;
}

/* 滚动条隐藏 */
.overflow-x-auto::-webkit-scrollbar {
  height: 4px;
}

.overflow-x-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-x-auto::-webkit-scrollbar-thumb {
  background: #e5e7eb;
  border-radius: 4px;
}

.dark .overflow-x-auto::-webkit-scrollbar-thumb {
  background: #374151;
}
</style>
