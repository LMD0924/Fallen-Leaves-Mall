<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { message } from "ant-design-vue";
import { get } from "@/net/index.js";
import tokenManager from '@/utils/tokenManager';

const router = useRouter();
const [messageApi, contextHolder] = message.useMessage()

// 用户信息
const userInfo = ref({
  username: '',
  balance: 0,
  vipLevel: 1,
  avatar: '',
  role:''
});

// 用户菜单状态
const userMenuOpen = ref(false);
const userMenuRef = ref(null);

// 推荐商品数据
const recommendedProducts = ref([
  {
    id: 1,
    name: '无线蓝牙耳机',
    description: '高音质降噪，超长续航',
    price: 299,
    originalPrice: 399,
    image: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop',
    isNew: true,
    isFavorite: false
  },
  {
    id: 2,
    name: '智能手表',
    description: '运动健康监测，NFC支付',
    price: 1299,
    originalPrice: 1499,
    image: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=300&fit=crop',
    isNew: false,
    isFavorite: true
  },
  {
    id: 3,
    name: '笔记本电脑',
    description: '高性能轻薄本，办公游戏两不误',
    price: 5999,
    originalPrice: 6999,
    image: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=300&fit=crop',
    isNew: true,
    isFavorite: false
  },
  {
    id: 4,
    name: '运动相机',
    description: '4K防抖防水，户外运动必备',
    price: 1999,
    originalPrice: 2499,
    image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=300&fit=crop',
    isNew: false,
    isFavorite: false
  }
]);

// 最近浏览记录
const recentViewed = ref([
  { id: 1, name: '手机支架', price: 39, image: 'https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=200&h=150&fit=crop' },
  { id: 2, name: '充电宝', price: 129, image: 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=200&h=150&fit=crop' },
  { id: 3, name: '机械键盘', price: 499, image: 'https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=200&h=150&fit=crop' },
  { id: 4, name: '游戏手柄', price: 299, image: 'https://images.unsplash.com/photo-1535228483100-0b494c6cae77?w=200&h=150&fit=crop' },
  { id: 5, name: '平板电脑', price: 3299, image: 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=200&h=150&fit=crop' },
  { id: 6, name: '智能音箱', price: 399, image: 'https://images.unsplash.com/photo-1543512214-318c7553f230?w=200&h=150&fit=crop' }
]);

// 切换用户菜单
const toggleUserMenu = () => {
  userMenuOpen.value = !userMenuOpen.value;
};

// 点击外部关闭菜单
onMounted(() => {
  document.addEventListener('click', (e) => {
    if (userMenuRef.value && !userMenuRef.value.contains(e.target)) {
      userMenuOpen.value = false;
    }
  });
});

// 获取用户信息
onMounted(async () => {
  try {
    const token = tokenManager.getAccessToken();
    if (!token) {
      await router.push('/');
      return;
    }
    await get('api/user/selectUserById', {},
      (message, data) => {
        userInfo.value = data;
      },
      (message) => {
        messageApi.error(message || '获取用户信息失败');
        router.push('/');
      },
      () => {
        messageApi.error('网络错误，请稍后重试');
      }
    );
  } catch (error) {
    messageApi.error('获取用户信息失败');
    await router.push('/');
  }
});

// 格式化金额
const formatMoney = (amount) => {
  return amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
};

// 切换收藏
const toggleFavorite = (product) => {
  product.isFavorite = !product.isFavorite;
  messageApi.success(product.isFavorite ? '已添加到收藏夹' : '已取消收藏');
};

// 加入购物车
const addToCart = (product) => {
  messageApi.success(`已添加 ${product.name} 到购物车`);
};

// 清空浏览记录
const clearHistory = () => {
  recentViewed.value = [];
  messageApi.success('已清空浏览记录');
};

// 退出登录
const handleLogout = () => {
  tokenManager.clearAccessToken();
  messageApi.success('已安全退出');
  router.push('/');
};
</script>

<template>
  <contextHolder />
  <div class="min-h-screen bg-gray-50 dark:bg-black transition-colors duration-1000">
    <!-- 导航栏 - 紫色点缀玻璃态，延迟入场 -->
    <nav class="sticky top-0 z-50 bg-white/70 dark:bg-black/70 backdrop-blur-xl border-b border-gray-200/50 dark:border-gray-800/50 animate-slideDown" style="animation-delay: 0.1s">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <!-- Logo - 紫色点 -->
          <div class="flex items-center gap-2">
            <div class="w-2 h-2 rounded-full bg-purple-500 animate-pulse"></div>
            <span class="text-xl font-light tracking-tight text-gray-900 dark:text-gray-100">
              <span class="font-medium">落叶</span>商店
            </span>
          </div>

          <!-- 桌面导航链接 -->
          <div class="hidden md:flex items-center space-x-1">
            <a href="#" class="px-4 py-2 text-sm font-medium text-gray-900 dark:text-gray-100 bg-gray-100 dark:bg-gray-900 rounded-full transition-all hover:bg-purple-50 dark:hover:bg-purple-950/30 hover:text-purple-600 dark:hover:text-purple-400">首页</a>
            <a href="#" class="px-4 py-2 text-sm font-medium text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-100 hover:bg-gray-100 dark:hover:bg-gray-900 rounded-full transition-all">分类</a>
            <a href="#" class="px-4 py-2 text-sm font-medium text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-100 hover:bg-gray-100 dark:hover:bg-gray-900 rounded-full transition-all">收藏</a>
            <a href="#" class="px-4 py-2 text-sm font-medium text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-100 hover:bg-gray-100 dark:hover:bg-gray-900 rounded-full transition-all">购物车</a>
          </div>

          <!-- 右侧功能区 -->
          <div class="flex items-center space-x-3">
            <!-- 搜索框 -->
            <div class="relative hidden sm:block">
              <input
                type="text"
                placeholder="搜索..."
                class="w-48 lg:w-64 py-2 pl-4 pr-10 text-sm bg-gray-100 dark:bg-gray-900 border-0 rounded-full focus:ring-1 focus:ring-purple-500 transition-all"
              />
              <i class="fa fa-search absolute right-4 top-2.5 text-gray-400"></i>
            </div>

            <!-- 购物车图标 + 徽章 -->
            <button class="relative p-2 text-gray-700 dark:text-gray-300 hover:text-purple-600 dark:hover:text-purple-400 transition-colors group">
              <i class="fa fa-shopping-bag text-lg"></i>
              <span class="absolute -top-1 -right-1 bg-purple-600 text-white text-[10px] font-bold rounded-full h-4 w-4 flex items-center justify-center group-hover:scale-110 transition-transform">3</span>
            </button>

            <!-- 用户菜单 -->
            <div class="relative" ref="userMenuRef">
              <button @click="toggleUserMenu" class="flex items-center space-x-2 group">
                <div class="w-8 h-8 rounded-full bg-gradient-to-r from-purple-500 to-fuchsia-500 flex items-center justify-center text-white text-sm font-medium shadow-sm group-hover:shadow-md transition-shadow">
                  {{ userInfo.username?.charAt(0) || 'U' }}
                </div>
                <span class="text-sm font-medium text-gray-700 dark:text-gray-300 hidden md:block group-hover:text-purple-600 dark:group-hover:text-purple-400 transition-colors">
                  {{ userInfo.username || '用户' }}
                </span>
                <i class="fa fa-chevron-down text-gray-400 text-xs transition-transform duration-300" :class="{ 'rotate-180': userMenuOpen }"></i>
              </button>

              <!-- 下拉菜单 - 毛玻璃 + 淡入淡出动效 -->
              <transition
                enter-active-class="transition duration-200 ease-out"
                enter-from-class="opacity-0 -translate-y-2"
                enter-to-class="opacity-100 translate-y-0"
                leave-active-class="transition duration-150 ease-in"
                leave-from-class="opacity-100 translate-y-0"
                leave-to-class="opacity-0 -translate-y-2"
              >
                <div v-if="userMenuOpen" class="absolute right-0 mt-2 w-48 bg-white/80 dark:bg-black/80 backdrop-blur-xl rounded-xl shadow-lg py-1 z-10 border border-gray-200 dark:border-gray-800">
                  <a v-if="userInfo.role==='管理员'|| userInfo.role==='商家'" href="/BEManagement" class="flex items-center px-4 py-2.5 text-sm text-gray-700 dark:text-gray-300 hover:bg-purple-50 dark:hover:bg-purple-950/30 hover:text-purple-600 dark:hover:text-purple-400 transition-all">
                    <i class="fa fa-sliders-h w-5 mr-3"></i>后台管理
                  </a>
                  <a href="/SystemSettings" class="flex items-center px-4 py-2.5 text-sm text-gray-700 dark:text-gray-300 hover:bg-purple-50 dark:hover:bg-purple-950/30 hover:text-purple-600 dark:hover:text-purple-400 transition-all">
                    <i class="fa fa-sliders-h w-5 mr-3"></i>系统设置
                  </a>
                  <a href="#" class="flex items-center px-4 py-2.5 text-sm text-gray-700 dark:text-gray-300 hover:bg-purple-50 dark:hover:bg-purple-950/30 hover:text-purple-600 dark:hover:text-purple-400 transition-all">
                    <i class="fa fa-heart w-5 mr-3"></i>我的收藏
                  </a>
                  <a href="#" class="flex items-center px-4 py-2.5 text-sm text-gray-700 dark:text-gray-300 hover:bg-purple-50 dark:hover:bg-purple-950/30 hover:text-purple-600 dark:hover:text-purple-400 transition-all">
                    <i class="fa fa-history w-5 mr-3"></i>订单历史
                  </a>
                  <div class="border-t border-gray-200 dark:border-gray-800 my-1"></div>
                  <button @click="handleLogout" class="w-full flex items-center px-4 py-2.5 text-sm text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/30 transition-all">
                    <i class="fa fa-sign-out w-5 mr-3"></i>退出登录
                  </button>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 欢迎横幅 - 淡入向上动画 -->
      <div class="mb-10 p-6 bg-white dark:bg-black rounded-2xl border border-gray-100 dark:border-gray-800 shadow-sm hover:shadow-md transition-shadow animate-fadeInUp" style="animation-delay: 0.2s">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 rounded-xl bg-purple-50 dark:bg-purple-950/30 flex items-center justify-center">
              <i class="fa fa-sun text-purple-600 text-xl"></i>
            </div>
            <div>
              <h1 class="text-2xl font-light text-gray-900 dark:text-gray-100 mb-1">
                下午好，<span class="font-medium text-purple-600 dark:text-purple-400">{{ userInfo.username || '用户' }}</span>
              </h1>
              <p class="text-sm text-gray-500 dark:text-gray-400">探索今日精选好物</p>
            </div>
          </div>
          <div class="text-right">
            <p class="text-xs text-gray-500 dark:text-gray-400 tracking-wider mb-1">账户余额</p>
            <p class="text-2xl font-light text-gray-900 dark:text-gray-100">
              ¥{{ formatMoney(userInfo.balance || 0) }}
            </p>
          </div>
        </div>
      </div>

      <!-- 数据卡片 - 紫色点缀，交错淡入 -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-12">
        <!-- 待发货 - 紫色调 -->
        <div class="group bg-white dark:bg-black rounded-xl p-5 border border-gray-100 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 hover:shadow-lg hover:-translate-y-1 transition-all duration-300 animate-fadeInUp" style="animation-delay: 0.3s">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-400 mb-1 group-hover:text-purple-600 transition-colors">待发货</p>
              <p class="text-2xl font-light text-gray-900 dark:text-gray-100">2</p>
            </div>
            <div class="w-10 h-10 rounded-lg bg-purple-50 dark:bg-purple-950/30 flex items-center justify-center group-hover:scale-110 transition-transform">
              <i class="fa fa-box text-purple-600"></i>
            </div>
          </div>
        </div>

        <!-- 已完成 - 紫色调 -->
        <div class="group bg-white dark:bg-black rounded-xl p-5 border border-gray-100 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 hover:shadow-lg hover:-translate-y-1 transition-all duration-300 animate-fadeInUp" style="animation-delay: 0.4s">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-400 mb-1 group-hover:text-purple-600 transition-colors">已完成</p>
              <p class="text-2xl font-light text-gray-900 dark:text-gray-100">15</p>
            </div>
            <div class="w-10 h-10 rounded-lg bg-purple-50 dark:bg-purple-950/30 flex items-center justify-center group-hover:scale-110 transition-transform">
              <i class="fa fa-check text-purple-600"></i>
            </div>
          </div>
        </div>

        <!-- 收藏夹 - 紫色调 -->
        <div class="group bg-white dark:bg-black rounded-xl p-5 border border-gray-100 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 hover:shadow-lg hover:-translate-y-1 transition-all duration-300 animate-fadeInUp" style="animation-delay: 0.5s">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-400 mb-1 group-hover:text-purple-600 transition-colors">收藏夹</p>
              <p class="text-2xl font-light text-gray-900 dark:text-gray-100">24</p>
            </div>
            <div class="w-10 h-10 rounded-lg bg-purple-50 dark:bg-purple-950/30 flex items-center justify-center group-hover:scale-110 transition-transform">
              <i class="fa fa-heart text-purple-600"></i>
            </div>
          </div>
        </div>

        <!-- 会员等级 - 紫色调 -->
        <div class="group bg-white dark:bg-black rounded-xl p-5 border border-gray-100 dark:border-gray-800 hover:border-purple-200 dark:hover:border-purple-800 hover:shadow-lg hover:-translate-y-1 transition-all duration-300 animate-fadeInUp" style="animation-delay: 0.6s">
          <div class="flex items-start justify-between">
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-400 mb-1 group-hover:text-purple-600 transition-colors">会员等级</p>
              <p class="text-2xl font-light text-gray-900 dark:text-gray-100">VIP{{ userInfo.vipLevel || 1 }}</p>
            </div>
            <div class="w-10 h-10 rounded-lg bg-purple-50 dark:bg-purple-950/30 flex items-center justify-center group-hover:scale-110 transition-transform">
              <i class="fa fa-star text-purple-600"></i>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品推荐区域 -->
      <section class="mb-12">
        <div class="flex items-center justify-between mb-6 animate-fadeInUp" style="animation-delay: 0.7s">
          <h2 class="text-xl font-light text-gray-900 dark:text-gray-100">猜你喜欢</h2>
          <a href="#" class="text-sm text-gray-500 dark:text-gray-400 hover:text-purple-600 dark:hover:text-purple-400 transition-colors flex items-center gap-1 group">
            浏览更多
            <i class="fa fa-arrow-right text-xs group-hover:translate-x-1 transition-transform"></i>
          </a>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div v-for="(product, index) in recommendedProducts" :key="product.id"
               class="group bg-white dark:bg-black rounded-xl border border-gray-100 dark:border-gray-800 overflow-hidden hover:border-purple-200 dark:hover:border-purple-800 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 animate-fadeInUp"
               :style="{ animationDelay: (0.8 + index * 0.1) + 's' }">
            <!-- 图片区域 -->
            <div class="relative h-48 overflow-hidden bg-gray-100 dark:bg-gray-900">
              <img :src="product.image" :alt="product.name" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700">
              <div v-if="product.isNew" class="absolute top-3 left-3">
                <span class="px-2 py-1 bg-purple-600 text-white text-[10px] font-bold rounded-full shadow-lg">NEW</span>
              </div>
              <button @click="toggleFavorite(product)"
                      class="absolute top-3 right-3 w-8 h-8 bg-white/90 dark:bg-black/90 backdrop-blur-sm rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all hover:scale-110 shadow-md">
                <i :class="['fa text-sm', product.isFavorite ? 'fa-heart text-rose-500' : 'fa-heart-o text-gray-400']"></i>
              </button>
            </div>

            <!-- 商品信息 -->
            <div class="p-4">
              <h3 class="font-medium text-gray-900 dark:text-gray-100 text-base mb-1 line-clamp-1">{{ product.name }}</h3>
              <p class="text-xs text-gray-500 dark:text-gray-400 mb-3 line-clamp-2">{{ product.description }}</p>
              <div class="flex items-center justify-between">
                <div>
                  <span class="text-xl font-light text-gray-900 dark:text-gray-100">¥{{ formatMoney(product.price) }}</span>
                  <span v-if="product.originalPrice" class="ml-2 text-xs text-gray-400 line-through">¥{{ formatMoney(product.originalPrice) }}</span>
                </div>
                <button @click="addToCart(product)"
                        class="w-8 h-8 rounded-full bg-purple-600 text-white hover:bg-purple-700 hover:scale-110 active:scale-95 transition-all flex items-center justify-center shadow-sm">
                  <i class="fa fa-plus text-xs"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 最近浏览区域 -->
      <section>
        <div class="flex items-center justify-between mb-6 animate-fadeInUp" style="animation-delay: 1.2s">
          <h2 class="text-xl font-light text-gray-900 dark:text-gray-100">最近浏览</h2>
          <button @click="clearHistory" class="text-sm text-gray-500 dark:text-gray-400 hover:text-rose-600 dark:hover:text-rose-400 transition-colors">
            清空记录
          </button>
        </div>

        <div class="grid grid-cols-3 md:grid-cols-6 gap-3">
          <div v-for="(item, index) in recentViewed" :key="item.id"
               class="group cursor-pointer animate-fadeInUp"
               :style="{ animationDelay: (1.3 + index * 0.05) + 's' }">
            <div class="aspect-square bg-gray-100 dark:bg-gray-900 rounded-lg overflow-hidden mb-2 border border-gray-200 dark:border-gray-800 group-hover:border-purple-300 dark:group-hover:border-purple-700 group-hover:shadow-md transition-all">
              <img :src="item.image" :alt="item.name" class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300">
            </div>
            <p class="text-xs font-medium text-gray-900 dark:text-gray-100 truncate">{{ item.name }}</p>
            <p class="text-[10px] text-gray-500 dark:text-gray-400">¥{{ formatMoney(item.price) }}</p>
          </div>
        </div>
      </section>
    </main>

    <!-- 底部 - 延迟入场 -->
    <footer class="border-t border-gray-200 dark:border-gray-800 mt-16 py-10 bg-white dark:bg-black animate-fadeInUp" style="animation-delay: 1.6s">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-8 text-sm">
          <div>
            <div class="flex items-center gap-2 mb-3">
              <div class="w-2 h-2 rounded-full bg-purple-500"></div>
              <span class="text-lg font-light text-gray-900 dark:text-gray-100">落叶商店</span>
            </div>
            <p class="text-xs text-gray-500 dark:text-gray-400 leading-relaxed">
              简约 · 品质 · 生活
            </p>
          </div>
          <div>
            <h3 class="text-xs font-medium text-gray-900 dark:text-gray-100 uppercase tracking-wider mb-3">购物指南</h3>
            <ul class="space-y-2 text-gray-500 dark:text-gray-400">
              <li><a href="#" class="hover:text-purple-600 dark:hover:text-purple-400 transition-colors">购物流程</a></li>
              <li><a href="#" class="hover:text-purple-600 dark:hover:text-purple-400 transition-colors">会员介绍</a></li>
              <li><a href="#" class="hover:text-purple-600 dark:hover:text-purple-400 transition-colors">常见问题</a></li>
            </ul>
          </div>
          <div>
            <h3 class="text-xs font-medium text-gray-900 dark:text-gray-100 uppercase tracking-wider mb-3">支付方式</h3>
            <ul class="space-y-2 text-gray-500 dark:text-gray-400">
              <li><a href="#" class="hover:text-purple-600 dark:hover:text-purple-400 transition-colors">微信支付</a></li>
              <li><a href="#" class="hover:text-purple-600 dark:hover:text-purple-400 transition-colors">支付宝</a></li>
              <li><a href="#" class="hover:text-purple-600 dark:hover:text-purple-400 transition-colors">银行卡</a></li>
            </ul>
          </div>
          <div>
            <h3 class="text-xs font-medium text-gray-900 dark:text-gray-100 uppercase tracking-wider mb-3">联系我们</h3>
            <ul class="space-y-2 text-gray-500 dark:text-gray-400">
              <li>400-123-4567</li>
              <li>service@luoye.com</li>
              <li>9:00-18:00</li>
            </ul>
          </div>
        </div>
        <div class="border-t border-gray-200 dark:border-gray-800 mt-10 pt-6 text-center text-xs text-gray-500 dark:text-gray-400">
          © 2024 落叶商店 · 紫色美学实践
        </div>
      </div>
    </footer>
  </div>
</template>

<style>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  -webkit-font-smoothing: antialiased;
}

.line-clamp-1 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
}

.line-clamp-2 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

/* 自定义动画 */
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-slideDown {
  animation: slideDown 0.8s cubic-bezier(0.2, 0.9, 0.3, 1) forwards;
}

.animate-fadeInUp {
  opacity: 0;
  animation: fadeInUp 0.8s cubic-bezier(0.2, 0.9, 0.3, 1) forwards;
}

/* 极简滚动条 */
::-webkit-scrollbar {
  width: 4px;
  height: 4px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.dark ::-webkit-scrollbar-track {
  background: #000;
}

::-webkit-scrollbar-thumb {
  background: #ccc;
}

.dark ::-webkit-scrollbar-thumb {
  background: #333;
}

::-webkit-scrollbar-thumb:hover {
  background: #999;
}

.dark ::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>
