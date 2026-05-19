# MyPetStore Backend

MyPetStore 的后端项目，基于 Spring Boot 提供商品、用户、购物车、订单、收藏、商品对比和后台管理相关 API。

## 技术栈

- Java 8+（本地已用 JDK 21 验证）
- Spring Boot 2.7.18
- MyBatis-Plus
- MySQL
- Maven

## 环境要求

- JDK 8 或更高版本
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

如果你的 MySQL 用户名或密码不同，请修改 `src/main/resources/application.properties` 中的 datasource 配置。

## 数据库初始化

仓库内提供了初始化 SQL：

```text
jpetstore.sql       基础表结构和演示数据
compare_schema.sql  收藏和商品对比扩展表
```

创建数据库：

```bash
mysql -u root -p
```

进入 MySQL 后执行：

```sql
CREATE DATABASE IF NOT EXISTS mps DEFAULT CHARACTER SET utf8;
```

回到项目目录后导入数据：

```bash
mysql -u root -p mps < jpetstore.sql
mysql -u root -p mps < compare_schema.sql
```

如果本机 root 密码就是默认的 `root`，也可以使用：

```bash
mysql -u root -proot mps < jpetstore.sql
mysql -u root -proot mps < compare_schema.sql
```

## 本地运行

```bash
./mvnw spring-boot:run
```

服务默认启动在：

```text
http://localhost:1145
```

## 测试

```bash
./mvnw test
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
- `/favorite` 商品收藏
- `/compare` 商品对比
- `/admin` 后台管理

前端开发服务器默认通过 `/api` 代理访问本服务。

## 测试账号

初始化数据包含以下测试账号：

```text
j2ee / j2ee
ACID / ACID
a / a
```
