# MyPetStore Backend

MyPetStore 的后端项目，基于 Spring Boot 提供商品、用户、购物车、订单和后台管理相关 API。

## 技术栈

- Java 21
- Spring Boot 4
- MyBatis-Plus
- MySQL
- Maven

## 环境要求

- JDK 21
- Maven
- MySQL

默认数据库配置位于：

```text
src/main/resources/application.properties
```

默认配置：

```properties
server.port=1145
spring.datasource.url=jdbc:mysql://localhost:3306/mps
spring.datasource.username=root
spring.datasource.password=root
```

运行前请确认本机 MySQL 中已存在 `mps` 数据库和项目所需表结构。

## 本地运行

```bash
mvn spring-boot:run
```

服务默认启动在：

```text
http://localhost:1145
```

## 测试

```bash
mvn test
```

## 主要模块

```text
controller/     REST API 控制器
service/        业务逻辑
persistence/    MyBatis-Plus Mapper
entity/         数据库实体
dto/            请求和响应 DTO
config/         Web、CORS、MyBatis-Plus 配置
utils/          JWT、时间等工具类
```

## 主要 API 分组

- `/auth` 登录、注册、退出
- `/account` 用户资料和密码
- `/catalog` 商品分类、商品和搜索
- `/cart` 购物车
- `/orders` 订单创建、查询和取消
- `/admin` 后台管理

前端开发服务器默认通过 `/api` 代理访问本服务。
