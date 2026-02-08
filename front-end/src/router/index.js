import { createRouter, createWebHistory } from 'vue-router'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
      {
        path:'/',
        name:'登录页面',
        component:()=>import('@/views/Login.vue')
      },
    {
      path:'/User',
      name:'个人主页',
      component:()=>import('@/views/User.vue'),
      meta: { requiresAuth: true }
    }
  ],
})

// 导航守卫，检查token
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.meta.requiresAuth) {
    // 获取本地存储的token
    const token = localStorage.getItem('落叶商城token');
    
    // 如果有token，放行
    if (token) {
      next();
    } else {
      // 如果没有token，重定向到登录页
      next('/');
    }
  } else {
    // 不需要认证的路由，直接放行
    next();
  }
});

export default router
