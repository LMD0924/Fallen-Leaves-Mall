# Fallen-Leaves-Mall
# 落叶商城——后端

### 必备步骤：

- 创建spring boot项目
- 创建数据库

### 基础工作：

- 添加相应的依赖：如`MySQL`，`Mybatis`，`Lombok`等
- 配置相关类：如`全局跨域配置`，`REST API 响应封装`，`OpenAPI 配置`(可选)等
- 统一使用json接收数据

**采用Maven多模块架构，将项目拆分为用户端、商家端、公共模块，实现代码复用和解耦**
**父项目管理统一依赖版本，子模块独立部署，提高开发效率和系统可维护性**
**使用Spring Boot + MyBatis + MySQL技术栈，实现前后端分离的RESTful API**

# 通用模块（back-end)

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

## 日志功能

`SpelExpressionParserUtil`工具类的作用、核心逻辑，以及它在商城日志体系中的具体使用场景 —— 简单来说，这个类是用来**解析 SpEL 表达式**的，能从方法参数、返回值、异常或日志上下文中动态提取数据（比如订单号、商品 ID），让日志注解的配置更灵活。

下面我会从整体功能、核心代码逐行解释、使用场景三个维度，把这个工具类讲透彻，结合之前的日志体系说明它的价值。

### 一、整体功能总结

这个工具类的核心作用是：

接收一个**SpEL 表达式字符串**（比如`#orderNo`、`#dto.productId`、`#result.orderId`），结合方法的`Method`对象、参数数组、返回值、异常等信息，解析出表达式对应的**具体值**。

在商城日志场景中，它能让你在`@MallOperationLog`注解中通过 SpEL 表达式**动态指定业务 ID / 业务单号**（比如`businessId = "#orderNo"`），而不用硬编码，极大提升日志注解的灵活性。

### 二、核心代码逐行解释

```java
package org.example.commonbackend.util;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;

@Component // 交给Spring管理，可直接注入使用
public class SpelExpressionParserUtil {

    // 1. SpEL表达式解析器（Spring内置）
    private final ExpressionParser parser = new SpelExpressionParser();
    // 2. 参数名发现器：能通过反射获取方法的参数名（比如方法是createOrder(String orderNo)，能拿到参数名"orderNo"）
    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 核心方法：解析SpEL表达式
     * @param expression SpEL表达式（如#orderNo、#dto.productId）
     * @param method 目标方法（反射获取）
     * @param args 方法参数数组
     * @param result 方法返回结果
     * @param error 方法执行异常
     * @return 解析后的具体字符串值
     */
    public String parseExpression(String expression, Method method, Object[] args, Object result, Throwable error) {
        // 表达式为空则返回空字符串
        if (!StringUtils.hasText(expression)) {
            return "";
        }

        try {
            // 步骤1：获取方法的参数名（比如方法参数是(String orderNo, Integer count)，返回["orderNo", "count"]）
            String[] paramNames = discoverer.getParameterNames(method);

            // 步骤2：创建SpEL评估上下文（MethodBasedEvaluationContext是Spring为方法场景封装的上下文）
            StandardEvaluationContext context = new MethodBasedEvaluationContext(null, method, args, discoverer);

            // 步骤3：设置根对象为参数数组（支持通过#root[0]、#root[1]访问参数）
            context.setRootObject(args);

            // 步骤4：将方法参数名和参数值绑定到上下文（支持通过#orderNo直接访问对应参数值）
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }

            // 步骤5：绑定方法返回结果（支持通过#result访问，比如#result.orderId）
            context.setVariable("result", result);

            // 步骤6：绑定方法执行异常（支持通过#error访问，比如#error.message）
            context.setVariable("error", error);

            // 步骤7：绑定日志上下文变量（比如之前LogContext存的traceId、tenantId，支持通过#traceId访问）
            Map<String, Object> contextVars = LogContext.getAll();
            contextVars.forEach(context::setVariable);

            // 步骤8：解析表达式并返回字符串结果
            return parser.parseExpression(expression).getValue(context, String.class);
        } catch (Exception e) {
            // 解析失败时返回原表达式+错误信息，避免日志记录异常
            return expression + " (解析失败: " + e.getMessage() + ")";
        }
    }

    /**
     * 简化方法：仅解析业务ID（无需返回值/异常）
     */
    public String parseBusinessId(String expression, Method method, Object[] args) {
        if (!StringUtils.hasText(expression)) {
            return null;
        }
        // 调用核心方法，result和error传null
        return parseExpression(expression, method, args, null, null);
    }
}
```

#### 1. 什么是 SpEL 表达式？

SpEL（Spring Expression Language）是 Spring 的表达式语言，支持动态取值、方法调用等，比如：

- `#orderNo`：取方法中名为`orderNo`的参数值；
- `#dto.productId`：取方法参数`dto`的`productId`属性；
- `#result.orderId`：取方法返回结果的`orderId`属性；
- `#traceId`：取 LogContext 中存储的`traceId`；
- `#root[0]`：取方法第一个参数的值。

#### 2. 这个工具类在日志体系中的核心用法

##### 步骤 1：扩展日志注解，添加 SpEL 配置项

```java
// 改造@MallOperationLog注解，增加businessIdExpression配置项
public @interface MallOperationLog {
    // 其他配置项...
    /**
     * 业务ID的SpEL表达式（比如#orderNo、#dto.productId）
     */
    String businessIdExpression() default "";
    
    /**
     * 业务单号的SpEL表达式（比如#result.orderNo）
     */
    String businessNoExpression() default "";
}
```

##### 步骤 2：在 Controller 方法上标注注解（用 SpEL 指定业务 ID）

```java
@PostMapping("/order/create")
@MallOperationLog(
    logType = 2,
    businessModule = "order",
    operationAction = "INSERT",
    businessType = "订单-创建",
    operationDesc = "用户创建订单，订单号：#result.orderNo", // 用SpEL动态生成描述
    businessIdExpression = "#dto.productId", // 解析商品ID作为业务ID
    businessNoExpression = "#result.orderNo" // 解析返回结果的订单号作为业务单号
)
public OrderVO createOrder(@RequestBody OrderCreateDTO dto) {
    // 业务逻辑：创建订单，返回OrderVO（包含orderNo）
    OrderVO vo = orderService.create(dto);
    return vo;
}
```

步骤 3：在 AOP 切面中调用工具类解析 SpEL

```java
// 在MallOperationLogAspect的parseAnnotation方法后添加：
// 注入SpEL工具类
@Resource
private SpelExpressionParserUtil spelUtil;

// 在环绕通知中解析业务ID/单号
private void fillBusinessInfo(ProceedingJoinPoint joinPoint, MallOperationLog logEntity, MallOperationLog annotation) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    Object[] args = joinPoint.getArgs();
    
    // 解析业务ID（从注解的SpEL表达式中提取）
    String businessId = spelUtil.parseBusinessId(annotation.businessIdExpression(), method, args);
    logEntity.setBusinessId(businessId);
    
    // 解析操作描述（支持SpEL，比如"用户创建订单，订单号：#result.orderNo"）
    String desc = spelUtil.parseExpression(annotation.operationDesc(), method, args, result, null);
    logEntity.setOperationDesc(desc);
    
    // 解析业务单号（需等方法执行完，用返回值解析）
    String businessNo = spelUtil.parseExpression(annotation.businessNoExpression(), method, args, result, null);
    logEntity.setBusinessNo(businessNo);
}
```

#### 3. 核心价值

- **动态化配置**：日志的业务 ID、操作描述等不再需要硬编码，而是通过 SpEL 从方法参数 / 返回值中动态提取；
- **灵活性高**：同一个注解可以适配不同方法，比如有的方法用`#orderNo`，有的用`#dto.productId`；
- **无侵入**：无需修改业务方法代码，仅通过注解配置就能提取所需数据；
- **上下文联动**：能结合 LogContext 中的全局变量（如 traceId、租户 ID），实现更丰富的表达式解析。

### 四、常见问题与注意事项

1. **参数名获取失败**：

   

   - `LocalVariableTableParameterNameDiscoverer`依赖编译时的 “局部变量表”，如果项目编译时开启了`-parameters`参数（Java 8+），或使用 Lombok，参数名能正确获取；
   - 若获取失败，可改用`#root[0]`、`#root[1]`访问参数。

   

2. **解析失败处理**：

   

   - 工具类已做异常处理，解析失败时会返回 “原表达式 + 错误信息”，避免日志记录中断；
   - 建议表达式尽量简单（如`#orderNo`、`#dto.productId`），减少复杂表达式导致的解析失败。

   

3. **返回值解析时机**：

   

   - 解析`#result`必须在方法执行完成后（`joinPoint.proceed()`之后），否则`result`为 null。

   

### 总结

1. `SpelExpressionParserUtil`是**SpEL 表达式解析工具类**，核心是从方法参数、返回值、异常、日志上下文中动态提取数据；
2. 它让商城日志注解的配置从 “静态字符串” 变为 “动态表达式”，能灵活提取业务 ID、订单号等核心数据；
3. 使用时需结合日志注解扩展 SpEL 配置项，并在 AOP 切面中调用工具类解析，最终填充到日志实体中。

# 商家模块（merchant-backend)

## **1.申请为商家**

```
首先普通用户可以申请为商家，需提交相应材料，等待管理员审核
审核成功：将商家基本信息存储到商家表，然后对应的用户表的role修改为商家
审核失败：返回失败原因，可以重新申请提交
```

## 2. 功能对应关系

| 功能         | 后端说明                                              |
| :----------- | :---------------------------------------------------- |
| 申请商家     | applyMerchant → insert，状态 0 待审核                 |
| 查询申请进度 | queryApplyProgress(userId) → 按 userId 查询           |
| 条件查询商家 | selectMerchant → 支持 id/userId/类型/状态/名称模糊    |
| 按 ID 查详情 | selectMerchantById → 按 id 查一条                     |
| 按状态查询   | selectMerchantByStatus → 按 status 查                 |
| 模糊搜索     | searchMerchant → 按 keyword 对 merchantName like      |
| 修改商家信息 | updateMerchant → updateById，通过时改用户角色         |
| 逻辑删除     | deleteMerchant → deleteById（@TableLogic 做逻辑删除） |

------

##### 按当前实现，商户的增删改查、申请、审核、逻辑删除和前端列表/表单/删除已全部用 MyBatis-Plus 完成并打通。
