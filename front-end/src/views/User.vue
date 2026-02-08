<template>
  <contextHolder />
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors duration-300">
    <!-- 导航栏 -->
    <nav class="bg-white dark:bg-gray-800 shadow-lg sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <!-- 左侧：Logo和导航 -->
          <div class="flex items-center">
            <div class="flex-shrink-0 flex items-center">
              <span class="text-2xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-500 to-purple-600">
                落叶商城
              </span>
            </div>
            <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
              <a href="#" class="border-blue-500 text-gray-900 dark:text-white inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                首页
              </a>
              <a href="#" class="border-transparent text-gray-500 dark:text-gray-300 hover:border-gray-300 hover:text-gray-700 dark:hover:text-gray-200 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                商品分类
              </a>
              <a href="#" class="border-transparent text-gray-500 dark:text-gray-300 hover:border-gray-300 hover:text-gray-700 dark:hover:text-gray-200 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                我的收藏
              </a>
              <a href="#" class="border-transparent text-gray-500 dark:text-gray-300 hover:border-gray-300 hover:text-gray-700 dark:hover:text-gray-200 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                购物车
              </a>
            </div>
          </div>

          <!-- 右侧：用户信息和搜索 -->
          <div class="flex items-center space-x-4">
            <!-- 搜索框 -->
            <div class="relative">
              <input
                type="text"
                placeholder="搜索商品..."
                class="pl-10 pr-4 py-2 w-48 lg:w-64 rounded-lg bg-gray-100 dark:bg-gray-700 border border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
              />
              <i class="fa fa-search absolute left-3 top-3 text-gray-400"></i>
            </div>

            <!-- 购物车 -->
            <button class="relative p-2 text-gray-600 dark:text-gray-300 hover:text-blue-500 dark:hover:text-blue-400">
              <i class="fa fa-shopping-cart text-xl"></i>
              <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">3</span>
            </button>

            <!-- 用户菜单 -->
            <div class="relative group">
              <button class="flex items-center space-x-2 focus:outline-none">
                <div class="w-9 h-9 rounded-full bg-gradient-to-r from-blue-400 to-purple-500 flex items-center justify-center text-white font-semibold">
                  {{ userInfo.username?.charAt(0) || 'U' }}
                </div>
                <span class="text-sm font-medium text-gray-700 dark:text-gray-200 hidden md:block">
                  {{ userInfo.username || '用户' }}
                </span>
                <i class="fa fa-chevron-down text-gray-400 text-xs"></i>
              </button>

              <!-- 下拉菜单 -->
              <div class="absolute right-0 mt-2 w-48 bg-white dark:bg-gray-800 rounded-lg shadow-xl py-1 z-10 hidden group-hover:block border border-gray-200 dark:border-gray-700">
                <a href="#" class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700">
                  <i class="fa fa-user mr-2"></i>个人中心
                </a>
                <a href="#" class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700">
                  <i class="fa fa-heart mr-2"></i>我的收藏
                </a>
                <a href="#" class="block px-4 py-2 text-sm text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700">
                  <i class="fa fa-history mr-2"></i>订单历史
                </a>
                <div class="border-t border-gray-200 dark:border-gray-700 my-1"></div>
                <button @click="handleLogout" class="w-full text-left px-4 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-gray-100 dark:hover:bg-gray-700">
                  <i class="fa fa-sign-out mr-2"></i>退出登录
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 欢迎横幅 -->
      <div class="bg-gradient-to-r from-blue-50 to-purple-50 dark:from-gray-800 dark:to-gray-800 rounded-2xl p-6 mb-8 border border-blue-100 dark:border-gray-700">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white mb-2">
              下午好，{{ userInfo.username || '亲爱的用户' }}！👋
            </h1>
            <p class="text-gray-600 dark:text-gray-400">
              今日推荐已为您准备好，发现心仪好物
            </p>
          </div>
          <div class="text-right">
            <p class="text-sm text-gray-500 dark:text-gray-400">账户余额</p>
            <p class="text-2xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600">
              ¥{{ formatMoney(userInfo.balance || 0) }}
            </p>
          </div>
        </div>
      </div>

      <!-- 功能区 -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        <div class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-shadow">
          <div class="flex items-center">
            <div class="w-12 h-12 rounded-lg bg-blue-100 dark:bg-blue-900 flex items-center justify-center mr-4">
              <i class="fa fa-box text-blue-600 dark:text-blue-400 text-xl"></i>
            </div>
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-400">待发货</p>
              <p class="text-2xl font-bold text-gray-900 dark:text-white">2</p>
            </div>
          </div>
        </div>

        <div class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-shadow">
          <div class="flex items-center">
            <div class="w-12 h-12 rounded-lg bg-green-100 dark:bg-green-900 flex items-center justify-center mr-4">
              <i class="fa fa-check-circle text-green-600 dark:text-green-400 text-xl"></i>
            </div>
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-400">已完成</p>
              <p class="text-2xl font-bold text-gray-900 dark:text-white">15</p>
            </div>
          </div>
        </div>

        <div class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-shadow">
          <div class="flex items-center">
            <div class="w-12 h-12 rounded-lg bg-yellow-100 dark:bg-yellow-900 flex items-center justify-center mr-4">
              <i class="fa fa-heart text-yellow-600 dark:text-yellow-400 text-xl"></i>
            </div>
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-400">收藏夹</p>
              <p class="text-2xl font-bold text-gray-900 dark:text-white">24</p>
            </div>
          </div>
        </div>

        <div class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm border border-gray-200 dark:border-gray-700 hover:shadow-md transition-shadow">
          <div class="flex items-center">
            <div class="w-12 h-12 rounded-lg bg-purple-100 dark:bg-purple-900 flex items-center justify-center mr-4">
              <i class="fa fa-star text-purple-600 dark:text-purple-400 text-xl"></i>
            </div>
            <div>
              <p class="text-sm text-gray-500 dark:text-gray-400">会员等级</p>
              <p class="text-2xl font-bold text-gray-900 dark:text-white">VIP{{ userInfo.vipLevel || 1 }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 商品推荐 -->
      <div class="mb-8">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-xl font-bold text-gray-900 dark:text-white">猜你喜欢</h2>
          <a href="#" class="text-blue-500 hover:text-blue-600 dark:text-blue-400 dark:hover:text-blue-300 text-sm font-medium">
            查看更多 <i class="fa fa-arrow-right ml-1"></i>
          </a>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <div v-for="product in recommendedProducts" :key="product.id"
               class="bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 border border-gray-200 dark:border-gray-700">
            <!-- 商品图片 -->
            <div class="relative h-48 overflow-hidden">
              <img :src="product.image" :alt="product.name" class="w-full h-full object-cover">
              <div v-if="product.isNew" class="absolute top-3 left-3">
                <span class="px-2 py-1 bg-red-500 text-white text-xs font-bold rounded">新品</span>
              </div>
              <button @click="toggleFavorite(product)"
                      class="absolute top-3 right-3 w-8 h-8 rounded-full bg-white dark:bg-gray-800 flex items-center justify-center shadow-md hover:shadow-lg">
                <i :class="['fa', product.isFavorite ? 'fa-heart text-red-500' : 'fa-heart-o text-gray-400']"></i>
              </button>
            </div>

            <!-- 商品信息 -->
            <div class="p-4">
              <h3 class="font-medium text-gray-900 dark:text-white mb-2 line-clamp-1">{{ product.name }}</h3>
              <p class="text-sm text-gray-500 dark:text-gray-400 mb-3 line-clamp-2">{{ product.description }}</p>

              <div class="flex items-center justify-between">
                <div>
                  <span class="text-xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600">
                    ¥{{ formatMoney(product.price) }}
                  </span>
                  <span v-if="product.originalPrice" class="ml-2 text-sm text-gray-400 line-through">
                    ¥{{ formatMoney(product.originalPrice) }}
                  </span>
                </div>
                <button @click="addToCart(product)"
                        class="px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-500 text-white text-sm font-medium rounded-lg hover:from-blue-600 hover:to-purple-600 transition-all transform hover:scale-105 active:scale-95">
                  加入购物车
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 最近浏览 -->
      <div class="mb-8">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-xl font-bold text-gray-900 dark:text-white">最近浏览</h2>
          <button @click="clearHistory" class="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300 text-sm">
            清空记录
          </button>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-6 gap-4">
          <div v-for="item in recentViewed" :key="item.id"
               class="bg-white dark:bg-gray-800 rounded-lg p-3 border border-gray-200 dark:border-gray-700 hover:border-blue-300 dark:hover:border-blue-500 transition-colors cursor-pointer">
            <img :src="item.image" :alt="item.name" class="w-full h-24 object-cover rounded mb-2">
            <p class="text-sm font-medium text-gray-900 dark:text-white line-clamp-1 mb-1">{{ item.name }}</p>
            <p class="text-xs text-gray-500 dark:text-gray-400">¥{{ formatMoney(item.price) }}</p>
          </div>
        </div>
      </div>

      <!-- 装饰元素 -->
      <div class="fixed bottom-0 left-0 w-full h-1/2 -z-10 overflow-hidden pointer-events-none">
        <div class="absolute bottom-0 left-1/4 w-96 h-96 bg-blue-500 rounded-full filter blur-[120px] opacity-10 animate-pulse"></div>
        <div class="absolute bottom-0 right-1/4 w-96 h-96 bg-purple-500 rounded-full filter blur-[120px] opacity-10 animate-pulse delay-1000"></div>
      </div>
    </main>

    <!-- 底部信息 -->
    <footer class="bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 mt-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
          <div>
            <span class="text-xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-500 to-purple-600">
              落叶商城
            </span>
            <p class="mt-2 text-sm text-gray-600 dark:text-gray-400">
              致力于为您提供最优质的商品和最贴心的服务
            </p>
          </div>
          <div>
            <h3 class="font-semibold text-gray-900 dark:text-white mb-4">购物指南</h3>
            <ul class="space-y-2 text-sm text-gray-600 dark:text-gray-400">
              <li><a href="#" class="hover:text-blue-500 dark:hover:text-blue-400">购物流程</a></li>
              <li><a href="#" class="hover:text-blue-500 dark:hover:text-blue-400">会员介绍</a></li>
              <li><a href="#" class="hover:text-blue-500 dark:hover:text-blue-400">常见问题</a></li>
            </ul>
          </div>
          <div>
            <h3 class="font-semibold text-gray-900 dark:text-white mb-4">支付方式</h3>
            <ul class="space-y-2 text-sm text-gray-600 dark:text-gray-400">
              <li><a href="#" class="hover:text-blue-500 dark:hover:text-blue-400">微信支付</a></li>
              <li><a href="#" class="hover:text-blue-500 dark:hover:text-blue-400">支付宝</a></li>
              <li><a href="#" class="hover:text-blue-500 dark:hover:text-blue-400">银行卡</a></li>
            </ul>
          </div>
          <div>
            <h3 class="font-semibold text-gray-900 dark:text-white mb-4">联系我们</h3>
            <ul class="space-y-2 text-sm text-gray-600 dark:text-gray-400">
              <li><i class="fa fa-phone mr-2"></i> 400-123-4567</li>
              <li><i class="fa fa-envelope mr-2"></i> service@luoye.com</li>
              <li><i class="fa fa-clock-o mr-2"></i> 9:00-18:00</li>
            </ul>
          </div>
        </div>
        <div class="border-t border-gray-200 dark:border-gray-700 mt-8 pt-8 text-center text-sm text-gray-500 dark:text-gray-400">
          <p>© 2024 落叶商城. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { message } from "ant-design-vue";
import { get } from "@/net/index.js";

const router = useRouter();
const [messageApi, contextHolder] = message.useMessage()

// 用户信息
const userInfo = reactive({
  username: '',
  balance: 0,
  vipLevel: 1,
  avatar: ''
});

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
    image: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w-400&h=300&fit=crop',
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

// 页面加载时获取用户信息
onMounted(async () => {
  try {
    const token = localStorage.getItem('落叶商城token');
    if (!token) {
      router.push('/login');
      return;
    }

    // 模拟获取用户信息
    // 实际项目中应该调用API
    userInfo.username = '落叶用户';
    userInfo.balance = 1588.50;
    userInfo.vipLevel = 3;
  } catch (error) {
    console.error('获取用户信息失败:', error);
    messageApi.error('获取用户信息失败');
  }
});

// 格式化金额
const formatMoney = (amount) => {
  return amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
};

// 切换收藏状态
const toggleFavorite = (product) => {
  product.isFavorite = !product.isFavorite;
  messageApi.success(product.isFavorite ? '已添加到收藏夹' : '已取消收藏');
};

// 添加到购物车
const addToCart = (product) => {
  messageApi.success(`已添加 ${product.name} 到购物车`);
  // 这里可以添加实际的购物车逻辑
};

// 清空浏览记录
const clearHistory = () => {
  recentViewed.value = [];
  messageApi.success('已清空浏览记录');
};

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('落叶商城token');
  messageApi.success('已安全退出');
  router.push('/login');
};
</script>

<style>
/* 导入Font Awesome图标库 */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 文本溢出显示省略号 */
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

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}

/* 深色模式滚动条 */
.dark ::-webkit-scrollbar-track {
  background: #1a1a1a;
}

.dark ::-webkit-scrollbar-thumb {
  background: #333;
}

.dark ::-webkit-scrollbar-thumb:hover {
  background: #444;
}

/* 动画效果 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 悬停效果 */
.hover-float {
  animation: float 3s ease-in-out infinite;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
