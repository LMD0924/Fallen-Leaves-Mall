import axios from "axios";
import {ElMessage} from "element-plus";

function getAuthToken() {
  return localStorage.getItem('authToken') || '';
}

const defaultError = () => ElMessage.error('发生错误，请联系管理员。')
const defaultFailure = (message) => ElMessage.warning(message)

// 根据 RestBean 的类型显示不同的消息
function handleResponse(data, successCallback, failureCallback) {
  if (data.success) {
    // 成功：success=true
    if (data.type === 'success') {
      ElMessage.success(data.message || '操作成功');
    } else if (data.type === 'info') {
      ElMessage.info(data.message || '提示信息');
    }
    successCallback && successCallback(data.message, data.data, data.code, data.type);
  } else {
    // 失败：success=false
    if (data.type === 'warning') {
      ElMessage.warning(data.message || '操作失败');
    } else if (data.type === 'error') {
      ElMessage.error(data.message || '系统错误');
    }
    failureCallback && failureCallback(data.message, data.data, data.code, data.type);
  }
}

// POST 请求（修复核心：直接发送JSON数据，不再转URL参数）
function post(url, data, success, failure = defaultFailure, error = defaultError) {
  // 返回Promise，支持await调用
  return axios.post(url, data, {  // 直接传递JSON对象，不再转换
    headers: {
      "Content-Type": "application/json",  // 匹配JSON格式
      "Authorization": getAuthToken()
    },
    withCredentials: true
  }).then(({data}) => {
    handleResponse(data, success, failure);
    return data; // 返回数据，方便调用方处理
  }).catch(err => {
    console.error('请求错误:', err);
    error();
    throw err; // 抛出异常，让调用方能捕获
  })
}

// GET 请求（保持不变）
function get(url, data = null, success, failure = defaultFailure, error = defaultError) {
  const config = {
    withCredentials: true,
    params: data,
    headers: {
      "Authorization": getAuthToken()
    }
  };

  return axios.get(url, config)
    .then(({data}) => {
      handleResponse(data, success, failure);
      return data;
    })
    .catch(err => {
      console.error('请求错误:', err);
      error();
      throw err;
    });
}

// PUT 请求（同步修复：直接发送JSON）
function put(url, data, success, failure = defaultFailure, error = defaultError) {
  return axios.put(url, data, {  // 直接传递JSON对象
    headers: {
      "Content-Type": "application/json",
      "Authorization": getAuthToken(),
    },
    withCredentials: true,
  })
    .then(({ data }) => {
      handleResponse(data, success, failure);
      return data;
    })
    .catch(err => {
      console.error('请求错误:', err);
      error();
      throw err;
    });
}

// DELETE 请求（保持不变）
function del(url, data, success, failure = defaultFailure, error = defaultError) {
  const config = {
    withCredentials: true,
    params: data,
    headers: {
      "Authorization": getAuthToken(),
    },
  };

  return axios.delete(url, config)
    .then(({ data }) => {
      handleResponse(data, success, failure);
      return data;
    })
    .catch(err => {
      console.error('请求错误:', err);
      error();
      throw err;
    });
}

// 上传文件（保持不变）
function upload(url, formData, success, failure = defaultFailure, error = defaultError) {
  return axios.post(url, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
      "Authorization": getAuthToken()
    },
    withCredentials: true
  }).then(({data}) => {
    handleResponse(data, success, failure);
    return data;
  }).catch(err => {
    console.error('上传错误:', err);
    error();
    throw err;
  })
}

// 移除无用的objectToURLSearchParams方法（已不需要）

export { get, post, put, del, upload };
