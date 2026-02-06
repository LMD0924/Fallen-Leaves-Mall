# Fallen-Leaves-Mall
# 落叶商城——后端

### 必备步骤：

- 创建spring boot项目
- 创建数据库

### 基础工作：

- 添加相应的依赖：如`MySQL`，`Mybatis`，`Lombok`等
- 配置相关类：如`全局跨域配置`，`REST API 响应封装`，`OpenAPI 配置`(可选)等
- 统一使用json接收数据

## 登录功能

### **1. 登录流程**

```
1. 用户输入账号密码进行验证，通过存储在redis中的key来判断有没有登录过，如果有将以前的token加入黑名单在生成新的令牌，否则就直接生成新令牌，存储在redis中
2. 验证成功后生成 JWT（包含用户信息）
3. 将 JWT 签名部分作为 key 存入 Redis
4. 同时存储用户-令牌映射（单设备登录）
5. 返回 access_token 和 refresh_token
```

### **2. 验证流程**

```
1. 拦截器提取请求头中的 token
2. 检查 Redis 黑名单
3. 验证 JWT 签名和过期时间
4. 检查 Redis 中用户令牌是否匹配
5. 验证通过，将用户ID存入请求上下文
```

### **3. 登出流程**

```
1. 将当前 token 加入 Redis 黑名单
2. 删除用户-令牌映射
3. 刷新令牌保留（可自然过期）
```

### **4. 刷新流程**

```
1. 验证 refresh_token 在 Redis 中是否有效
2. 生成新的 access_token 和 refresh_token
3. 更新 Redis 中的存储
4. 返回新令牌
```
