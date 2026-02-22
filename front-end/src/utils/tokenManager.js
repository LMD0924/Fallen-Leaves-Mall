// utils/tokenManager.js
class TokenManager {
  constructor() {
    // Access Token 存在内存中
    this.accessToken = null;
    // 用户信息（非敏感）
    this.userInfo = null;

    // 初始化时从localStorage恢复用户信息和token
    this.initFromStorage();
  }

  /**
   * 设置Access Token（登录成功后调用）
   */
  setAccessToken(token) {
    this.accessToken = token;
    // 同时存储到localStorage，防止页面刷新丢失
    localStorage.setItem('access_token', token);
    console.log('✅ Access Token已存入内存和localStorage');

    // 触发事件，通知应用状态变化
    window.dispatchEvent(new CustomEvent('token-changed', {
      detail: { hasToken: true }
    }));
  }

  /**
   * 获取Access Token
   */
  getAccessToken() {
    return this.accessToken;
  }

  /**
   * 清除Access Token（登出时调用）
   */
  clearAccessToken() {
    this.accessToken = null;
    // 同时从localStorage中清除
    localStorage.removeItem('access_token');
    console.log('🗑️ Access Token已清除');

    // 触发事件
    window.dispatchEvent(new CustomEvent('token-changed', {
      detail: { hasToken: false }
    }));
  }

  /**
   * 设置用户信息（存localStorage用于界面显示）
   */
  setUserInfo(info) {
    this.userInfo = info;

    // 只存非敏感信息到localStorage
    const safeInfo = {
      id: info.id,
      username: info.username,
      avatar: info.avatar,
      role: info.role,
      vipLevel: info.vipLevel
      // 注意：不要存token、密码、手机号等敏感信息
    };

    localStorage.setItem('user_info', JSON.stringify(safeInfo));
    console.log('✅ 用户信息已存储');
  }

  /**
   * 获取用户信息
   */
  getUserInfo() {
    if (this.userInfo) {
      return this.userInfo;
    }

    // 从localStorage恢复
    const saved = localStorage.getItem('user_info');
    if (saved) {
      try {
        this.userInfo = JSON.parse(saved);
        return this.userInfo;
      } catch (e) {
        localStorage.removeItem('user_info');
      }
    }

    return null;
  }

  /**
   * 获取用户名
   */
  getUsername() {
    const info = this.getUserInfo();
    return info?.username || '游客';
  }

  /**
   * 获取头像
   */
  getAvatar() {
    const info = this.getUserInfo();
    return info?.avatar || '';
  }

  /**
   * 检查是否已登录
   */
  isLoggedIn() {
    return !!this.accessToken;
  }

  /**
   * 初始化（从localStorage恢复）
   */
  initFromStorage() {
    // 恢复用户信息
    const savedUserInfo = localStorage.getItem('user_info');
    if (savedUserInfo) {
      try {
        this.userInfo = JSON.parse(savedUserInfo);
        console.log('📦 从localStorage恢复用户信息:', this.userInfo.username);
      } catch (e) {
        localStorage.removeItem('user_info');
      }
    }

    // 恢复token
    const savedToken = localStorage.getItem('access_token');
    if (savedToken) {
      this.accessToken = savedToken;
      console.log('📦 从localStorage恢复Access Token');

      // 触发事件，通知应用状态变化
      window.dispatchEvent(new CustomEvent('token-changed', {
        detail: { hasToken: true }
      }));
    }
  }

  /**
   * 完全登出（清除所有）
   */
  logout() {
    this.clearAccessToken();
    this.userInfo = null;
    localStorage.removeItem('user_info');
    console.log('👋 用户已登出');
  }
}

// 创建单例并导出
const tokenManager = new TokenManager();
export default tokenManager;