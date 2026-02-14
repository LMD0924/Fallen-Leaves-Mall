// net/index.js
import axios from "axios";
import { ElMessage } from "element-plus";
import router from "../router";
import tokenManager from "../utils/tokenManager";

const defaultError = () => ElMessage.warning('token无效，请重新登录');
const defaultFailure = (message) => ElMessage.warning(message);

// 是否正在刷新token
let isRefreshing = false;
// 等待队列
let pendingRequests = [];

// 处理响应数据（统一处理）
function handleResponse(data, success, failure) {
  if (data.code === 200) {
    // 成功
    if (data.type === 'success') {
      ElMessage.success(data.message || '操作成功');
    } else if (data.type === 'info') {
      ElMessage.info(data.message || '提示信息');
    }
    success && success(data.message, data.data);
  } else {
    // 失败
    if (data.type === 'warning') {
      ElMessage.warning(data.message || '操作失败');
    } else if (data.type === 'error') {
      ElMessage.error(data.message || '系统错误');
    }
    failure && failure(data.message);
  }
}

// 处理401错误，刷新token
async function handle401Error(originalRequest) {
  // 避免重复刷新
  if (isRefreshing) {
    // 正在刷新，将请求加入队列
    return new Promise((resolve, reject) => {
      pendingRequests.push({ config: originalRequest, resolve, reject });
    });
  }

  originalRequest._retry = true;
  isRefreshing = true;

  try {
    console.log('🔄 开始刷新Token...');

    // 调用刷新接口（会自动携带Cookie）
    const response = await axios.post('http://localhost:8080/api/user/refresh', {}, {
      withCredentials: true, // 必须携带Cookie
      headers: {
        'Content-Type': 'application/json'
      }
    });

    const newToken = response.data?.data?.accessToken;

    if (!newToken) {
      throw new Error('刷新失败：未返回新令牌');
    }

    console.log('✅ Token刷新成功');

    // 更新内存中的token
    tokenManager.setAccessToken(newToken);

    // 重试所有等待的请求
    const pendingCount = pendingRequests.length;
    pendingRequests.forEach(({ config, resolve, reject }) => {
      // 更新请求头中的token
      if (config.headers) {
        config.headers.Authorization = `Bearer ${newToken}`;
      }
      axios(config).then(resolve).catch(reject);
    });

    // 清空队列
    pendingRequests = [];

    if (pendingCount > 0) {
      console.log(`🔄 重试 ${pendingCount} 个等待中的请求`);
    }

    // 重试当前请求
    if (originalRequest.headers) {
      originalRequest.headers.Authorization = `Bearer ${newToken}`;
    }
    return axios(originalRequest);

  } catch (error) {
    console.error('❌ 刷新Token失败:', error);

    // 刷新失败，清除token
    tokenManager.clearAccessToken();

    // 拒绝所有等待的请求
    pendingRequests.forEach(({ reject }) => {
      reject(new Error('登录已过期'));
    });
    pendingRequests = [];

    // 跳转到登录页
    await router.push('/');
    ElMessage.error('登录已过期，请重新登录');

    throw error;
  } finally {
    isRefreshing = false;
  }
}

// POST 请求
function post(url, data, success, failure = defaultFailure, error = defaultError, skip401 = false) {
  const config = {
    url,
    method: 'post',
    data,
    headers: {
      "Content-Type": "application/json",
      "Authorization": tokenManager.getAccessToken() ? `Bearer ${tokenManager.getAccessToken()}` : ''
    },
    withCredentials: true // 必须携带Cookie
  };

  return axios(config)
    .then(({ data: responseData }) => {
      handleResponse(responseData, success, failure);
      return responseData;
    })
    .catch(async err => {
      console.error('请求错误:', err);

      // 处理401错误
      if (err.response?.status === 401 && !skip401 && err.config && !err.config._retry) {
        try {
          // 尝试刷新token并重试
          const newResponse = await handle401Error(err.config);
          // 处理重试后的响应
          handleResponse(newResponse.data, success, failure);
          return newResponse.data;
        } catch (refreshError) {
          // 刷新失败，调用错误回调
          error && error();
          throw refreshError;
        }
      }

      // 其他错误
      error && error();
      throw err;
    });
}

// GET 请求
function get(url, params = null, success, failure = defaultFailure, error = defaultError, skip401 = false) {
  const config = {
    url,
    method: 'get',
    params,
    headers: {
      "Authorization": tokenManager.getAccessToken() ? `Bearer ${tokenManager.getAccessToken()}` : ''
    },
    withCredentials: true
  };

  return axios(config)
    .then(({ data: responseData }) => {
      handleResponse(responseData, success, failure);
      return responseData;
    })
    .catch(async err => {
      console.error('请求错误:', err);

      if (err.response?.status === 401 && !skip401 && err.config && !err.config._retry) {
        try {
          const newResponse = await handle401Error(err.config);
          handleResponse(newResponse.data, success, failure);
          return newResponse.data;
        } catch (refreshError) {
          error && error();
          throw refreshError;
        }
      }

      error && error();
      throw err;
    });
}

// PUT 请求
function put(url, data, success, failure = defaultFailure, error = defaultError, skip401 = false) {
  const config = {
    url,
    method: 'put',
    data,
    headers: {
      "Content-Type": "application/json",
      "Authorization": tokenManager.getAccessToken() ? `Bearer ${tokenManager.getAccessToken()}` : ''
    },
    withCredentials: true
  };

  return axios(config)
    .then(({ data: responseData }) => {
      handleResponse(responseData, success, failure);
      return responseData;
    })
    .catch(async err => {
      console.error('请求错误:', err);

      if (err.response?.status === 401 && !skip401 && err.config && !err.config._retry) {
        try {
          const newResponse = await handle401Error(err.config);
          handleResponse(newResponse.data, success, failure);
          return newResponse.data;
        } catch (refreshError) {
          error && error();
          throw refreshError;
        }
      }

      error && error();
      throw err;
    });
}

// DELETE 请求
function del(url, data = null, success, failure = defaultFailure, error = defaultError, skip401 = false) {
  const config = {
    url,
    method: 'delete',
    data, // delete请求的data要放在这里
    headers: {
      "Content-Type": "application/json",
      "Authorization": tokenManager.getAccessToken() ? `Bearer ${tokenManager.getAccessToken()}` : ''
    },
    withCredentials: true
  };

  return axios(config)
    .then(({ data: responseData }) => {
      handleResponse(responseData, success, failure);
      return responseData;
    })
    .catch(async err => {
      console.error('请求错误:', err);

      if (err.response?.status === 401 && !skip401 && err.config && !err.config._retry) {
        try {
          const newResponse = await handle401Error(err.config);
          handleResponse(newResponse.data, success, failure);
          return newResponse.data;
        } catch (refreshError) {
          error && error();
          throw refreshError;
        }
      }

      error && error();
      throw err;
    });
}

// 上传文件
function upload(url, formData, success, failure = defaultFailure, error = defaultError, skip401 = false) {
  const config = {
    url,
    method: 'post',
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
      "Authorization": tokenManager.getAccessToken() ? `Bearer ${tokenManager.getAccessToken()}` : ''
    },
    withCredentials: true,
    onUploadProgress: (progressEvent) => {
      // 可以添加上传进度回调
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
      console.log(`上传进度: ${percentCompleted}%`);
    }
  };

  return axios(config)
    .then(({ data: responseData }) => {
      handleResponse(responseData, success, failure);
      return responseData;
    })
    .catch(async err => {
      console.error('上传错误:', err);

      if (err.response?.status === 401 && !skip401 && err.config && !err.config._retry) {
        try {
          const newResponse = await handle401Error(err.config);
          handleResponse(newResponse.data, success, failure);
          return newResponse.data;
        } catch (refreshError) {
          error && error();
          throw refreshError;
        }
      }

      error && error();
      throw err;
    });
}

// 导出所有方法
export { get, post, put, del, upload };
