<!-- components/settings/merchant/MerchantBenefits.vue - 商家特权组件 -->
<template>
  <div class="bg-white dark:bg-black rounded-xl shadow-lg p-6 border border-gray-100 dark:border-gray-800">
    <div class="flex items-center justify-between mb-6">
      <h4 class="font-medium text-gray-700 dark:text-gray-300">商家特权</h4>
      <span class="text-xs text-gray-500 dark:text-gray-400">共 {{ benefits.length }} 项特权</span>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div v-for="benefit in benefits" :key="benefit.id"
           class="group p-4 rounded-lg border border-gray-200 dark:border-gray-800 hover:border-purple-300 dark:hover:border-purple-700 hover:shadow-lg transition-all duration-300"
           @mouseenter="handleMouseEnter(benefit.id)"
           @mouseleave="handleMouseLeave(benefit.id)">
        <!-- 图标区域 -->
        <div class="relative mb-3">
          <div :class="['w-12 h-12 rounded-lg flex items-center justify-center transition-all duration-300', benefit.bgColor,
                       { 'scale-110': hoveredBenefit === benefit.id }]">
            <i :class="['text-xl transition-all duration-300', benefit.icon, benefit.textColor,
                       { 'scale-110': hoveredBenefit === benefit.id }]"></i>
          </div>
          <!-- 热门标签 -->
          <div v-if="benefit.isHot" class="absolute -top-2 -right-2">
            <span class="px-2 py-0.5 bg-gradient-to-r from-amber-500 to-red-500 text-white text-[10px] font-bold rounded-full shadow-lg">
              热门
            </span>
          </div>
        </div>

        <!-- 内容区域 -->
        <h5 class="font-medium text-gray-800 dark:text-gray-200 mb-1 group-hover:text-purple-600 dark:group-hover:text-purple-400 transition-colors">
          {{ benefit.title }}
        </h5>
        <p class="text-xs text-gray-500 dark:text-gray-400 mb-2">{{ benefit.description }}</p>

        <!-- 详细说明 -->
        <div v-if="hoveredBenefit === benefit.id" class="text-xs text-purple-600 dark:text-purple-400 animate-fadeIn">
          <i class="fa fa-chevron-right mr-1"></i>
          {{ benefit.detail }}
        </div>

        <!-- 状态标签 -->
        <div class="flex flex-wrap gap-1 mt-2">
          <span v-for="tag in benefit.tags" :key="tag"
                class="px-2 py-0.5 bg-gray-100 dark:bg-gray-900 text-gray-600 dark:text-gray-400 rounded-full text-[10px]">
            {{ tag }}
          </span>
        </div>
      </div>
    </div>

    <!-- 底部提示 -->
    <div class="mt-6 p-4 bg-gradient-to-r from-purple-50 to-fuchsia-50 dark:from-purple-950/30 dark:to-fuchsia-950/30 rounded-lg">
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 rounded-full bg-purple-500/20 flex items-center justify-center">
          <i class="fa fa-rocket text-purple-500"></i>
        </div>
        <div>
          <p class="text-sm font-medium text-gray-700 dark:text-gray-300">成为商家，立即解锁所有特权</p>
          <p class="text-xs text-gray-500 dark:text-gray-400">现在申请还可享受首年优惠</p>
        </div>
        <button class="ml-auto px-4 py-2 bg-purple-500 text-white rounded-lg hover:bg-purple-600 transition-colors text-sm">
          立即申请
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const hoveredBenefit = ref(null)

const benefits = ref([
  {
    id: 1,
    title: '免费店铺',
    description: '免费创建专属店铺页面',
    detail: '包含店铺装修、商品展示、自定义域名',
    icon: 'fa fa-store',
    bgColor: 'bg-purple-100 dark:bg-purple-950/30',
    textColor: 'text-purple-600 dark:text-purple-400',
    tags: ['免费', '自定义'],
    isHot: true
  },
  {
    id: 2,
    title: '低佣金率',
    description: '享受低于市场平均的佣金费率',
    detail: '最低可至0.5%，销售额越高费率越低',
    icon: 'fa fa-percentage',
    bgColor: 'bg-emerald-100 dark:bg-emerald-950/30',
    textColor: 'text-emerald-600 dark:text-emerald-400',
    tags: ['优惠', '阶梯'],
    isHot: true
  },
  {
    id: 3,
    title: '营销工具',
    description: '丰富的营销推广工具',
    detail: '优惠券、秒杀、拼团、满减等多种营销活动',
    icon: 'fa fa-bullhorn',
    bgColor: 'bg-amber-100 dark:bg-amber-950/30',
    textColor: 'text-amber-600 dark:text-amber-400',
    tags: ['推广', '转化'],
    isHot: false
  },
  {
    id: 4,
    title: '数据分析',
    description: '详细的店铺数据分析报告',
    detail: '流量分析、销售报表、用户画像、竞品分析',
    icon: 'fa fa-chart-line',
    bgColor: 'bg-blue-100 dark:bg-blue-950/30',
    textColor: 'text-blue-600 dark:text-blue-400',
    tags: ['数据', '洞察'],
    isHot: false
  },
  {
    id: 5,
    title: '优先客服',
    description: '24小时优先客服支持',
    detail: '专属客服通道，15分钟内响应',
    icon: 'fa fa-headset',
    bgColor: 'bg-rose-100 dark:bg-rose-950/30',
    textColor: 'text-rose-600 dark:text-rose-400',
    tags: ['专属', '快速'],
    isHot: true
  },
  {
    id: 6,
    title: '促销活动',
    description: '参与平台大型促销活动',
    detail: '618、双11、年货节等平台级活动优先参与',
    icon: 'fa fa-gift',
    bgColor: 'bg-indigo-100 dark:bg-indigo-950/30',
    textColor: 'text-indigo-600 dark:text-indigo-400',
    tags: ['流量', '曝光'],
    isHot: false
  },
  {
    id: 7,
    title: '物流服务',
    description: '合作物流商优惠价格',
    detail: '顺丰、京东、圆通等多家物流合作，享受协议价',
    icon: 'fa fa-truck',
    bgColor: 'bg-cyan-100 dark:bg-cyan-950/30',
    textColor: 'text-cyan-600 dark:text-cyan-400',
    tags: ['配送', '优惠'],
    isHot: false
  },
  {
    id: 8,
    title: '金融服务',
    description: '快速结算与供应链金融',
    detail: 'T+1快速结算，凭订单可申请贷款',
    icon: 'fa fa-coins',
    bgColor: 'bg-yellow-100 dark:bg-yellow-950/30',
    textColor: 'text-yellow-600 dark:text-yellow-400',
    tags: ['结算', '融资'],
    isHot: false
  },
  {
    id: 9,
    title: '培训支持',
    description: '专业商家培训课程',
    detail: '运营、营销、客服等全流程培训',
    icon: 'fa fa-graduation-cap',
    bgColor: 'bg-pink-100 dark:bg-pink-950/30',
    textColor: 'text-pink-600 dark:text-pink-400',
    tags: ['学习', '成长'],
    isHot: false
  }
])

const handleMouseEnter = (id) => {
  hoveredBenefit.value = id
}

const handleMouseLeave = () => {
  hoveredBenefit.value = null
}
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.animate-fadeIn {
  animation: fadeIn 0.2s ease-out forwards;
}
</style>
