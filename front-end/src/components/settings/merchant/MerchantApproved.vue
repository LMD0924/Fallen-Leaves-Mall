<!-- components/settings/merchant/MerchantApproved.vue - 已通过状态组件 -->
<template>
  <div class="py-8">
    <!-- 成功动画 -->
    <div class="text-center mb-8">
      <div class="relative inline-block">
        <div class="w-28 h-28 mx-auto mb-6 rounded-full bg-gradient-to-r from-emerald-100 to-green-100 dark:from-emerald-900/30 dark:to-green-900/30 flex items-center justify-center">
          <div class="absolute inset-0 rounded-full animate-ping bg-emerald-400/20"></div>
          <i class="fa fa-check-circle text-5xl text-emerald-500 relative z-10"></i>
        </div>
        <div class="absolute -bottom-2 -right-2 w-10 h-10 rounded-full bg-emerald-500 flex items-center justify-center text-white text-lg border-4 border-white dark:border-black animate-bounce">
          <i class="fa fa-crown"></i>
        </div>
      </div>
      <h4 class="text-2xl font-light text-gray-900 dark:text-gray-100 mb-2">恭喜！您已成为认证商家</h4>
      <p class="text-gray-500 dark:text-gray-400 mb-6 max-w-md mx-auto">您现在可以享受所有商家特权并开始管理您的店铺</p>

      <!-- 欢迎徽章 -->
      <div class="inline-flex items-center gap-2 px-5 py-2.5 bg-gradient-to-r from-amber-100 to-amber-200 dark:from-amber-900/30 dark:to-amber-800/30 rounded-full">
        <i class="fa fa-gem text-amber-500"></i>
        <span class="text-sm font-medium text-amber-700 dark:text-amber-400">尊贵商家 · 专属标识</span>
      </div>
    </div>

    <!-- 商家特权数据 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
      <div class="p-5 bg-gradient-to-br from-purple-50 to-fuchsia-50 dark:from-purple-950/30 dark:to-fuchsia-950/30 rounded-xl border border-purple-200 dark:border-purple-800">
        <div class="flex items-center gap-3 mb-2">
          <div class="w-10 h-10 rounded-lg bg-purple-500/20 flex items-center justify-center">
            <i class="fa fa-calendar-alt text-purple-600 dark:text-purple-400"></i>
          </div>
          <div>
            <div class="text-2xl font-light text-purple-600 dark:text-purple-400">30天</div>
            <div class="text-xs text-gray-600 dark:text-gray-400">免费试用期</div>
          </div>
        </div>
        <div class="text-xs text-gray-500 dark:text-gray-400">期间所有功能免费体验</div>
      </div>

      <div class="p-5 bg-gradient-to-br from-emerald-50 to-green-50 dark:from-emerald-950/30 dark:to-green-950/30 rounded-xl border border-emerald-200 dark:border-emerald-800">
        <div class="flex items-center gap-3 mb-2">
          <div class="w-10 h-10 rounded-lg bg-emerald-500/20 flex items-center justify-center">
            <i class="fa fa-percentage text-emerald-600 dark:text-emerald-400"></i>
          </div>
          <div>
            <div class="text-2xl font-light text-emerald-600 dark:text-emerald-400">0.5%</div>
            <div class="text-xs text-gray-600 dark:text-gray-400">优惠佣金率</div>
          </div>
        </div>
        <div class="text-xs text-gray-500 dark:text-gray-400">远低于市场平均费率</div>
      </div>

      <div class="p-5 bg-gradient-to-br from-blue-50 to-cyan-50 dark:from-blue-950/30 dark:to-cyan-950/30 rounded-xl border border-blue-200 dark:border-blue-800">
        <div class="flex items-center gap-3 mb-2">
          <div class="w-10 h-10 rounded-lg bg-blue-500/20 flex items-center justify-center">
            <i class="fa fa-headset text-blue-600 dark:text-blue-400"></i>
          </div>
          <div>
            <div class="text-2xl font-light text-blue-600 dark:text-blue-400">24h</div>
            <div class="text-xs text-gray-600 dark:text-gray-400">专属客服</div>
          </div>
        </div>
        <div class="text-xs text-gray-500 dark:text-gray-400">全天候优先响应</div>
      </div>
    </div>

    <!-- 商家权益列表 -->
    <div class="mb-8 p-5 bg-white dark:bg-black rounded-xl border border-gray-200 dark:border-gray-800">
      <h5 class="font-medium text-gray-700 dark:text-gray-300 mb-3 flex items-center gap-2">
        <i class="fa fa-gift text-purple-500"></i>
        已解锁的商家权益
      </h5>
      <div class="grid grid-cols-2 gap-3">
        <div v-for="(benefit, index) in benefits" :key="index" class="flex items-center gap-2 text-sm">
          <i class="fa fa-check-circle text-emerald-500"></i>
          <span class="text-gray-600 dark:text-gray-400">{{ benefit }}</span>
        </div>
      </div>
    </div>

    <!-- 操作按钮组 -->
    <div class="flex flex-col sm:flex-row justify-center gap-4">
      <button @click="goToDashboard" class="px-8 py-3 bg-gradient-to-r from-purple-500 to-fuchsia-500 text-white rounded-lg hover:from-purple-600 hover:to-fuchsia-600 shadow-lg hover:shadow-xl transition-all transform hover:-translate-y-0.5">
        <i class="fa fa-store-alt mr-2"></i>
        进入商家后台
      </button>
      <button @click="startTutorial" class="px-8 py-3 border border-gray-300 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-all transform hover:-translate-y-0.5">
        <i class="fa fa-play-circle mr-2"></i>
        新手引导教程
      </button>
      <button @click="viewMerchantInfo" class="px-8 py-3 border border-gray-300 dark:border-gray-700 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-900 transition-all transform hover:-translate-y-0.5">
        <i class="fa fa-info-circle mr-2"></i>
        查看商家信息
      </button>
    </div>

    <!-- 店铺快速设置 -->
    <div class="mt-8 p-5 bg-gray-50 dark:bg-gray-900 rounded-xl">
      <h5 class="font-medium text-gray-700 dark:text-gray-300 mb-3">快速设置您的店铺</h5>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
        <button @click="setupShop('info')" class="p-3 rounded-lg bg-white dark:bg-black border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 transition-all">
          <i class="fa fa-info-circle text-purple-500 mb-1"></i>
          <div class="text-xs text-gray-600 dark:text-gray-400">基本信息</div>
        </button>
        <button @click="setupShop('logo')" class="p-3 rounded-lg bg-white dark:bg-black border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 transition-all">
          <i class="fa fa-image text-purple-500 mb-1"></i>
          <div class="text-xs text-gray-600 dark:text-gray-400">店铺Logo</div>
        </button>
        <button @click="setupShop('banner')" class="p-3 rounded-lg bg-white dark:bg-black border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 transition-all">
          <i class="fa fa-scroll text-purple-500 mb-1"></i>
          <div class="text-xs text-gray-600 dark:text-gray-400">店铺横幅</div>
        </button>
        <button @click="setupShop('products')" class="p-3 rounded-lg bg-white dark:bg-black border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 transition-all">
          <i class="fa fa-box text-purple-500 mb-1"></i>
          <div class="text-xs text-gray-600 dark:text-gray-400">添加商品</div>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { message } from 'ant-design-vue'

const [messageApi] = message.useMessage()

const benefits = ref([
  '免费创建店铺页面',
  '0.5% 优惠佣金率',
  '24小时专属客服',
  '数据分析工具',
  '营销活动参与权',
  '品牌展示特权'
])

const goToDashboard = () => {
  messageApi.info('正在跳转至商家后台...')
}

const startTutorial = () => {
  messageApi.info('新手引导教程功能开发中')
}

const viewMerchantInfo = () => {
  messageApi.info('查看商家信息功能开发中')
}

const setupShop = (type) => {
  const actions = {
    info: '基本信息设置',
    logo: 'Logo上传功能',
    banner: '横幅设置功能',
    products: '商品添加功能'
  }
  messageApi.info(actions[type] + '开发中')
}
</script>
