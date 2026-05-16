# MyPetStore RESTful API 文档

## 项目概述

MyPetStore 是一个基于 Spring Boot + MyBatis Plus 的宠物在线商城系统。本文档定义了前后端分离后的 RESTful API 规范。

**基础 URL**: `/api`  
**版本**: v1  
**API 前缀**: `/api/v1`

---

## 通用规范

### 响应格式

所有 API 响应均采用统一的 JSON 格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2025-05-16T10:00:00Z"
}
```

### 状态码说明

| 状态码 | 说明 |
|-------|------|
| 200 | 成功 |
| 201 | 创建成功 |
| 204 | 删除成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（未登录） |
| 403 | 禁止访问（权限不足） |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

### 通用错误响应

```json
{
  "code": 400,
  "message": "error message",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

### 认证方式

采用 Session/Token 认证机制（可选择 JWT）：
- 登录后返回 Token 或 SessionID
- 后续请求在 `Authorization` Header 中携带：`Authorization: Bearer {token}`
- 或使用 Cookie 存储 SessionID

---

## API 端点设计

### 1. 账户管理 API (`/api/v1/auth` 和 `/api/v1/account`)

#### 1.1 用户登录

```http
POST /api/v1/auth/sign-in
Content-Type: application/json

{
  "username": "user123",
  "password": "password123",
  "captcha": "1234"  // 可选
}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "user123",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "favoriteCategory": "DOGS"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

**错误响应 (401 Unauthorized)**:
```json
{
  "code": 401,
  "message": "Username or password incorrect",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 1.2 用户登出

```http
POST /api/v1/auth/sign-out
Authorization: Bearer {token}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Logout successful",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 1.3 用户注册

```http
POST /api/v1/auth/sign-up
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "confirmPassword": "password123",
  "captcha": "1234"  // 可选
}
```

**响应 (201 Created)**:
```json
{
  "code": 201,
  "message": "Registration successful",
  "data": {
    "userId": 2,
    "username": "newuser",
    "email": "",
    "status": "active"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

**错误响应 (400 Bad Request)**:
```json
{
  "code": 400,
  "message": "Username already exists",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 1.4 获取当前登录用户信息

```http
GET /api/v1/account/profile
Authorization: Bearer {token}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "user123",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "status": "active",
    "address1": "123 Main St",
    "address2": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "zip": "10001",
    "country": "USA",
    "phone": "555-1234",
    "languagePreference": "English",
    "favoriteCategory": "DOGS",
    "myListOption": 1,
    "bannerOption": 1,
    "createTime": "2025-05-16T10:00:00Z",
    "updateTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 1.5 更新用户信息

```http
PUT /api/v1/account/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "555-5678",
  "address1": "456 Oak Ave",
  "address2": "Suite 200",
  "city": "Boston",
  "state": "MA",
  "zip": "02101",
  "country": "USA",
  "languagePreference": "English",
  "favoriteCategory": "CATS"
}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Profile updated successfully",
  "data": {
    "userId": 1,
    "username": "user123",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "updateTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 1.6 修改密码

```http
POST /api/v1/account/change-password
Authorization: Bearer {token}
Content-Type: application/json

{
  "oldPassword": "oldpass123",
  "newPassword": "newpass123",
  "confirmPassword": "newpass123"
}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Password changed successfully",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

### 2. 商品目录 API (`/api/v1/catalog`)

#### 2.1 获取所有分类

```http
GET /api/v1/catalog/categories
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "categoryId": "DOGS",
      "name": "Dogs",
      "description": "A variety of dog products",
      "createTime": "2025-05-16T10:00:00Z",
      "updateTime": "2025-05-16T10:00:00Z"
    },
    {
      "id": 2,
      "categoryId": "CATS",
      "name": "Cats",
      "description": "A variety of cat products",
      "createTime": "2025-05-16T10:00:00Z",
      "updateTime": "2025-05-16T10:00:00Z"
    }
  ],
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 2.2 获取单个分类详情

```http
GET /api/v1/catalog/categories/{categoryId}
```

**示例**: `GET /api/v1/catalog/categories/DOGS`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "categoryId": "DOGS",
    "name": "Dogs",
    "description": "A variety of dog products",
    "products": [
      {
        "id": 1,
        "productId": "FI-SW-01",
        "categoryId": "DOGS",
        "name": "Labrador Puppy",
        "description": "Friendly and energetic",
        "createTime": "2025-05-16T10:00:00Z"
      }
    ],
    "createTime": "2025-05-16T10:00:00Z",
    "updateTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 2.3 获取分类下的所有产品

```http
GET /api/v1/catalog/categories/{categoryId}/products
```

**查询参数**:
- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10
- `sortBy` (可选): 排序字段，如 `name`, `createTime`
- `order` (可选): 排序顺序，`asc` 或 `desc`

**示例**: `GET /api/v1/catalog/categories/DOGS/products?page=1&pageSize=10`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 50,
    "page": 1,
    "pageSize": 10,
    "totalPages": 5,
    "items": [
      {
        "id": 1,
        "productId": "FI-SW-01",
        "categoryId": "DOGS",
        "name": "Labrador Puppy",
        "description": "Friendly and energetic",
        "createTime": "2025-05-16T10:00:00Z",
        "updateTime": "2025-05-16T10:00:00Z"
      }
    ]
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 2.4 获取单个产品详情

```http
GET /api/v1/catalog/products/{productId}
```

**示例**: `GET /api/v1/catalog/products/FI-SW-01`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "productId": "FI-SW-01",
    "categoryId": "DOGS",
    "name": "Labrador Puppy",
    "description": "Friendly and energetic",
    "items": [
      {
        "id": 1,
        "itemId": "EST-1",
        "productId": "FI-SW-01",
        "listPrice": 99.99,
        "unitCost": 50.00,
        "supplierId": 1,
        "status": "P",
        "attribute1": "Color: Brown",
        "attribute2": null,
        "attribute3": null,
        "attribute4": null,
        "attribute5": null
      }
    ],
    "createTime": "2025-05-16T10:00:00Z",
    "updateTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 2.5 获取单个物品详情

```http
GET /api/v1/catalog/items/{itemId}
```

**示例**: `GET /api/v1/catalog/items/EST-1`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "itemId": "EST-1",
    "productId": "FI-SW-01",
    "listPrice": 99.99,
    "unitCost": 50.00,
    "supplierId": 1,
    "status": "P",
    "attribute1": "Color: Brown",
    "attribute2": null,
    "attribute3": null,
    "attribute4": null,
    "attribute5": null,
    "createTime": "2025-05-16T10:00:00Z",
    "updateTime": "2025-05-16T10:00:00Z",
    "product": {
      "id": 1,
      "productId": "FI-SW-01",
      "categoryId": "DOGS",
      "name": "Labrador Puppy",
      "description": "Friendly and energetic"
    }
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 2.6 搜索产品

```http
GET /api/v1/catalog/search
```

**查询参数**:
- `keyword` (必需): 搜索关键词
- `categoryId` (可选): 限制在某个分类内搜索
- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10

**示例**: `GET /api/v1/catalog/search?keyword=puppy&page=1`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 2,
    "page": 1,
    "pageSize": 10,
    "totalPages": 1,
    "keyword": "puppy",
    "items": [
      {
        "id": 1,
        "productId": "FI-SW-01",
        "categoryId": "DOGS",
        "name": "Labrador Puppy",
        "description": "Friendly and energetic",
        "items": []
      }
    ]
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

### 3. 购物车 API (`/api/v1/cart`)

#### 3.1 获取购物车

```http
GET /api/v1/cart
Authorization: Bearer {token}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "cartId": 1,
    "userId": "user123",
    "items": [
      {
        "itemId": "EST-1",
        "productId": "FI-SW-01",
        "productName": "Labrador Puppy",
        "quantity": 2,
        "listPrice": 99.99,
        "subtotal": 199.98,
        "attribute1": "Color: Brown"
      },
      {
        "itemId": "EST-2",
        "productId": "FI-SW-02",
        "productName": "German Shepherd",
        "quantity": 1,
        "listPrice": 149.99,
        "subtotal": 149.99,
        "attribute1": "Color: Black"
      }
    ],
    "totalItems": 3,
    "totalPrice": 349.97,
    "createTime": "2025-05-16T10:00:00Z",
    "updateTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 3.2 添加物品到购物车

```http
POST /api/v1/cart/items
Authorization: Bearer {token}
Content-Type: application/json

{
  "itemId": "EST-1",
  "quantity": 2
}
```

**响应 (201 Created)**:
```json
{
  "code": 201,
  "message": "Item added to cart successfully",
  "data": {
    "cartId": 1,
    "itemId": "EST-1",
    "quantity": 2,
    "listPrice": 99.99,
    "subtotal": 199.98
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 3.3 更新购物车物品数量

```http
PUT /api/v1/cart/items/{itemId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "quantity": 3
}
```

**示例**: `PUT /api/v1/cart/items/EST-1`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Cart item updated successfully",
  "data": {
    "itemId": "EST-1",
    "quantity": 3,
    "listPrice": 99.99,
    "subtotal": 299.97
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 3.4 从购物车删除物品

```http
DELETE /api/v1/cart/items/{itemId}
Authorization: Bearer {token}
```

**示例**: `DELETE /api/v1/cart/items/EST-1`

**响应 (204 No Content)**:
```
(Empty response body)
```

---

#### 3.5 清空购物车

```http
DELETE /api/v1/cart
Authorization: Bearer {token}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Cart cleared successfully",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

### 4. 订单 API (`/api/v1/orders`)

#### 4.1 创建订单

```http
POST /api/v1/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "billToFirstName": "John",
  "billToLastName": "Doe",
  "billAddress1": "456 Oak Ave",
  "billAddress2": "Suite 200",
  "billCity": "Boston",
  "billState": "MA",
  "billZip": "02101",
  "billCountry": "USA",
  "shipToFirstName": "John",
  "shipToLastName": "Doe",
  "shipAddress1": "456 Oak Ave",
  "shipAddress2": "Suite 200",
  "shipCity": "Boston",
  "shipState": "MA",
  "shipZip": "02101",
  "shipCountry": "USA",
  "creditCard": "4111-1111-1111-1111",
  "cardType": "Visa"
}
```

**响应 (201 Created)**:
```json
{
  "code": 201,
  "message": "Order created successfully",
  "data": {
    "orderId": 1001,
    "userId": "user123",
    "orderDate": "2025-05-16",
    "totalPrice": 349.97,
    "billToFirstName": "John",
    "billToLastName": "Doe",
    "shipToFirstName": "John",
    "shipToLastName": "Doe",
    "cardType": "Visa",
    "status": "pending",
    "lineItems": [
      {
        "lineNumber": 1,
        "itemId": "EST-1",
        "quantity": 2,
        "unitPrice": 99.99
      }
    ],
    "createTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 4.2 获取我的订单

```http
GET /api/v1/orders
Authorization: Bearer {token}
```

**查询参数**:
- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10
- `status` (可选): 按状态过滤，如 `pending`, `shipped`, `delivered`

**示例**: `GET /api/v1/orders?page=1&status=pending`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 5,
    "page": 1,
    "pageSize": 10,
    "totalPages": 1,
    "items": [
      {
        "orderId": 1001,
        "userId": "user123",
        "orderDate": "2025-05-16",
        "totalPrice": 349.97,
        "status": "pending",
        "billToFirstName": "John",
        "billToLastName": "Doe",
        "courier": "USP",
        "createTime": "2025-05-16T10:00:00Z"
      }
    ]
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 4.3 获取订单详情

```http
GET /api/v1/orders/{orderId}
Authorization: Bearer {token}
```

**示例**: `GET /api/v1/orders/1001`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": 1001,
    "userId": "user123",
    "orderDate": "2025-05-16",
    "totalPrice": 349.97,
    "status": "pending",
    "billToFirstName": "John",
    "billToLastName": "Doe",
    "billAddress1": "456 Oak Ave",
    "billAddress2": "Suite 200",
    "billCity": "Boston",
    "billState": "MA",
    "billZip": "02101",
    "billCountry": "USA",
    "shipToFirstName": "John",
    "shipToLastName": "Doe",
    "shipAddress1": "456 Oak Ave",
    "shipAddress2": "Suite 200",
    "shipCity": "Boston",
    "shipState": "MA",
    "shipZip": "02101",
    "shipCountry": "USA",
    "courier": "USP",
    "cardType": "Visa",
    "lineItems": [
      {
        "lineNumber": 1,
        "itemId": "EST-1",
        "productName": "Labrador Puppy",
        "quantity": 2,
        "unitPrice": 99.99,
        "subtotal": 199.98
      },
      {
        "lineNumber": 2,
        "itemId": "EST-2",
        "productName": "German Shepherd",
        "quantity": 1,
        "unitPrice": 149.99,
        "subtotal": 149.99
      }
    ],
    "createTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 4.4 取消订单

```http
DELETE /api/v1/orders/{orderId}
Authorization: Bearer {token}
```

**示例**: `DELETE /api/v1/orders/1001`

**条件**：仅允许删除状态为 `pending` 的订单

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Order cancelled successfully",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

### 5. 管理员 API (`/api/v1/admin`)

#### 5.1 仪表板统计信息

```http
GET /api/v1/admin/dashboard
Authorization: Bearer {token}
X-Admin-Role: admin
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalOrders": 250,
    "totalRevenue": 12500.50,
    "pendingOrders": 15,
    "totalUsers": 100,
    "totalProducts": 500,
    "totalItems": 1200,
    "recentOrders": [
      {
        "orderId": 1001,
        "userId": "user123",
        "orderDate": "2025-05-16",
        "totalPrice": 349.97,
        "status": "pending"
      }
    ]
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 5.2 分类管理

##### 5.2.1 创建分类

```http
POST /api/v1/admin/categories
Authorization: Bearer {token}
X-Admin-Role: admin
Content-Type: application/json

{
  "categoryId": "BIRDS",
  "name": "Birds",
  "description": "A variety of bird products"
}
```

**响应 (201 Created)**:
```json
{
  "code": 201,
  "message": "Category created successfully",
  "data": {
    "id": 5,
    "categoryId": "BIRDS",
    "name": "Birds",
    "description": "A variety of bird products",
    "createTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.2.2 更新分类

```http
PUT /api/v1/admin/categories/{categoryId}
Authorization: Bearer {token}
X-Admin-Role: admin
Content-Type: application/json

{
  "name": "Bird Species",
  "description": "Updated description"
}
```

**示例**: `PUT /api/v1/admin/categories/BIRDS`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Category updated successfully",
  "data": {
    "id": 5,
    "categoryId": "BIRDS",
    "name": "Bird Species",
    "description": "Updated description",
    "updateTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.2.3 删除分类

```http
DELETE /api/v1/admin/categories/{categoryId}
Authorization: Bearer {token}
X-Admin-Role: admin
```

**示例**: `DELETE /api/v1/admin/categories/BIRDS`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Category deleted successfully",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.2.4 搜索分类

```http
GET /api/v1/admin/categories/search
Authorization: Bearer {token}
X-Admin-Role: admin
```

**查询参数**:
- `keyword` (必需): 搜索关键词
- `page` (可选): 页码
- `pageSize` (可选): 每页数量

**示例**: `GET /api/v1/admin/categories/search?keyword=dog`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "page": 1,
    "items": [
      {
        "id": 1,
        "categoryId": "DOGS",
        "name": "Dogs",
        "description": "A variety of dog products"
      }
    ]
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 5.3 产品管理

##### 5.3.1 创建产品

```http
POST /api/v1/admin/products
Authorization: Bearer {token}
X-Admin-Role: admin
Content-Type: application/json

{
  "categoryId": "DOGS",
  "productId": "FI-SW-02",
  "name": "German Shepherd",
  "description": "Strong and loyal dog breed"
}
```

**响应 (201 Created)**:
```json
{
  "code": 201,
  "message": "Product created successfully",
  "data": {
    "id": 2,
    "categoryId": "DOGS",
    "productId": "FI-SW-02",
    "name": "German Shepherd",
    "description": "Strong and loyal dog breed",
    "createTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.3.2 更新产品

```http
PUT /api/v1/admin/products/{productId}
Authorization: Bearer {token}
X-Admin-Role: admin
Content-Type: application/json

{
  "name": "German Shepherd (Updated)",
  "description": "Updated description"
}
```

**示例**: `PUT /api/v1/admin/products/FI-SW-02`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Product updated successfully",
  "data": {
    "id": 2,
    "productId": "FI-SW-02",
    "name": "German Shepherd (Updated)",
    "description": "Updated description",
    "updateTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.3.3 删除产品

```http
DELETE /api/v1/admin/products/{productId}
Authorization: Bearer {token}
X-Admin-Role: admin
```

**示例**: `DELETE /api/v1/admin/products/FI-SW-02`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Product deleted successfully",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.3.4 搜索产品

```http
GET /api/v1/admin/products/search
Authorization: Bearer {token}
X-Admin-Role: admin
```

**查询参数**:
- `keyword` (必需): 搜索关键词
- `categoryId` (可选): 分类过滤
- `page` (可选): 页码
- `pageSize` (可选): 每页数量

**示例**: `GET /api/v1/admin/products/search?keyword=puppy&categoryId=DOGS`

---

#### 5.4 物品管理

##### 5.4.1 创建物品

```http
POST /api/v1/admin/items
Authorization: Bearer {token}
X-Admin-Role: admin
Content-Type: application/json

{
  "productId": "FI-SW-01",
  "itemId": "EST-2",
  "listPrice": 129.99,
  "unitCost": 60.00,
  "supplierId": 1,
  "status": "P",
  "attribute1": "Color: White",
  "attribute2": "Size: Large",
  "attribute3": null,
  "attribute4": null,
  "attribute5": null
}
```

**响应 (201 Created)**:
```json
{
  "code": 201,
  "message": "Item created successfully",
  "data": {
    "id": 2,
    "productId": "FI-SW-01",
    "itemId": "EST-2",
    "listPrice": 129.99,
    "unitCost": 60.00,
    "supplierId": 1,
    "status": "P",
    "createTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.4.2 更新物品

```http
PUT /api/v1/admin/items/{itemId}
Authorization: Bearer {token}
X-Admin-Role: admin
Content-Type: application/json

{
  "listPrice": 139.99,
  "unitCost": 65.00,
  "status": "P"
}
```

**示例**: `PUT /api/v1/admin/items/EST-2`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Item updated successfully",
  "data": {
    "id": 2,
    "itemId": "EST-2",
    "listPrice": 139.99,
    "unitCost": 65.00,
    "updateTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.4.3 删除物品

```http
DELETE /api/v1/admin/items/{itemId}
Authorization: Bearer {token}
X-Admin-Role: admin
```

**示例**: `DELETE /api/v1/admin/items/EST-2`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Item deleted successfully",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 5.5 订单管理

##### 5.5.1 获取所有订单

```http
GET /api/v1/admin/orders
Authorization: Bearer {token}
X-Admin-Role: admin
```

**查询参数**:
- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10
- `status` (可选): 按状态过滤，如 `pending`, `shipped`, `delivered`
- `userId` (可选): 按用户筛选

**示例**: `GET /api/v1/admin/orders?status=pending&page=1`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 250,
    "page": 1,
    "pageSize": 10,
    "totalPages": 25,
    "items": [
      {
        "orderId": 1001,
        "userId": "user123",
        "orderDate": "2025-05-16",
        "totalPrice": 349.97,
        "status": "pending",
        "courier": "USP",
        "lineItems": [
          {
            "lineNumber": 1,
            "itemId": "EST-1",
            "quantity": 2,
            "unitPrice": 99.99
          }
        ]
      }
    ]
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.5.2 发送订单

```http
POST /api/v1/admin/orders/{orderId}/ship
Authorization: Bearer {token}
X-Admin-Role: admin
```

**示例**: `POST /api/v1/admin/orders/1001/ship`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Order shipped successfully",
  "data": {
    "orderId": 1001,
    "status": "shipped",
    "updateTime": "2025-05-16T10:00:00Z"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.5.3 删除订单

```http
DELETE /api/v1/admin/orders/{orderId}
Authorization: Bearer {token}
X-Admin-Role: admin
```

**示例**: `DELETE /api/v1/admin/orders/1001`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Order deleted successfully",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.5.4 搜索订单

```http
GET /api/v1/admin/orders/search
Authorization: Bearer {token}
X-Admin-Role: admin
```

**查询参数**:
- `keyword` (必需): 搜索关键词（订单ID或用户名）
- `page` (可选): 页码

**示例**: `GET /api/v1/admin/orders/search?keyword=1001`

---

#### 5.6 用户管理

##### 5.6.1 获取所有用户

```http
GET /api/v1/admin/users
Authorization: Bearer {token}
X-Admin-Role: admin
```

**查询参数**:
- `page` (可选): 页码，默认 1
- `pageSize` (可选): 每页数量，默认 10
- `status` (可选): 按状态过滤

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "totalPages": 10,
    "items": [
      {
        "id": 1,
        "username": "user123",
        "email": "user@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "status": "active",
        "favoriteCategory": "DOGS",
        "createTime": "2025-05-16T10:00:00Z",
        "updateTime": "2025-05-16T10:00:00Z"
      }
    ]
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.6.2 获取用户详情

```http
GET /api/v1/admin/users/{username}
Authorization: Bearer {token}
X-Admin-Role: admin
```

**示例**: `GET /api/v1/admin/users/user123`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "user123",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "status": "active",
    "address1": "123 Main St",
    "address2": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "zip": "10001",
    "country": "USA",
    "phone": "555-1234",
    "favoriteCategory": "DOGS",
    "createTime": "2025-05-16T10:00:00Z",
    "recentOrders": [
      {
        "orderId": 1001,
        "orderDate": "2025-05-16",
        "totalPrice": 349.97,
        "status": "pending"
      }
    ]
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.6.3 重置用户密码

```http
POST /api/v1/admin/users/{username}/reset-password
Authorization: Bearer {token}
X-Admin-Role: admin
Content-Type: application/json

{
  "newPassword": "defaultPassword123"
}
```

**示例**: `POST /api/v1/admin/users/user123/reset-password`

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Password reset successfully",
  "data": {
    "username": "user123",
    "message": "Password has been reset to default value"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

##### 5.6.4 搜索用户

```http
GET /api/v1/admin/users/search
Authorization: Bearer {token}
X-Admin-Role: admin
```

**查询参数**:
- `keyword` (必需): 搜索关键词（用户名或邮箱）
- `page` (可选): 页码

**示例**: `GET /api/v1/admin/users/search?keyword=john`

---

### 6. 其他 API

#### 6.1 获取验证码

```http
GET /api/v1/captcha
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "captchaId": "abc123def456",
    "captchaImage": "data:image/png;base64,...",
    "sessionId": "sess_12345"
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

#### 6.2 验证验证码

```http
POST /api/v1/captcha/verify
Content-Type: application/json

{
  "captchaId": "abc123def456",
  "captchaCode": "1234"
}
```

**响应 (200 OK)**:
```json
{
  "code": 200,
  "message": "Captcha verified successfully",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

## 错误处理

### 常见错误响应

#### 未授权 (401)
```json
{
  "code": 401,
  "message": "Authentication required. Please sign in first.",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

#### 权限不足 (403)
```json
{
  "code": 403,
  "message": "You do not have permission to perform this action.",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

#### 资源不存在 (404)
```json
{
  "code": 404,
  "message": "Resource not found.",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

#### 请求参数错误 (400)
```json
{
  "code": 400,
  "message": "Invalid request parameters.",
  "data": {
    "errors": [
      {
        "field": "email",
        "message": "Invalid email format"
      }
    ]
  },
  "timestamp": "2025-05-16T10:00:00Z"
}
```

#### 服务器错误 (500)
```json
{
  "code": 500,
  "message": "Internal server error. Please try again later.",
  "data": null,
  "timestamp": "2025-05-16T10:00:00Z"
}
```

---

## 数据模型

### Account 对象

```json
{
  "id": 1,
  "username": "user123",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "status": "active",
  "address1": "123 Main St",
  "address2": "Apt 4B",
  "city": "New York",
  "state": "NY",
  "zip": "10001",
  "country": "USA",
  "phone": "555-1234",
  "languagePreference": "English",
  "favoriteCategory": "DOGS",
  "myListOption": 1,
  "bannerOption": 1,
  "createTime": "2025-05-16T10:00:00Z",
  "updateTime": "2025-05-16T10:00:00Z"
}
```

### Category 对象

```json
{
  "id": 1,
  "categoryId": "DOGS",
  "name": "Dogs",
  "description": "A variety of dog products",
  "createTime": "2025-05-16T10:00:00Z",
  "updateTime": "2025-05-16T10:00:00Z"
}
```

### Product 对象

```json
{
  "id": 1,
  "productId": "FI-SW-01",
  "categoryId": "DOGS",
  "name": "Labrador Puppy",
  "description": "Friendly and energetic",
  "createTime": "2025-05-16T10:00:00Z",
  "updateTime": "2025-05-16T10:00:00Z"
}
```

### Item 对象

```json
{
  "id": 1,
  "itemId": "EST-1",
  "productId": "FI-SW-01",
  "listPrice": 99.99,
  "unitCost": 50.00,
  "supplierId": 1,
  "status": "P",
  "attribute1": "Color: Brown",
  "attribute2": null,
  "attribute3": null,
  "attribute4": null,
  "attribute5": null,
  "createTime": "2025-05-16T10:00:00Z",
  "updateTime": "2025-05-16T10:00:00Z"
}
```

### Cart 对象

```json
{
  "cartId": 1,
  "userId": "user123",
  "items": [
    {
      "itemId": "EST-1",
      "quantity": 2,
      "listPrice": 99.99
    }
  ],
  "totalItems": 2,
  "totalPrice": 199.98,
  "createTime": "2025-05-16T10:00:00Z",
  "updateTime": "2025-05-16T10:00:00Z"
}
```

### Order 对象

```json
{
  "orderId": 1001,
  "userId": "user123",
  "orderDate": "2025-05-16",
  "status": "pending",
  "totalPrice": 349.97,
  "billToFirstName": "John",
  "billToLastName": "Doe",
  "shipToFirstName": "John",
  "shipToLastName": "Doe",
  "cardType": "Visa",
  "courier": "USP",
  "lineItems": [],
  "createTime": "2025-05-16T10:00:00Z",
  "updateTime": "2025-05-16T10:00:00Z"
}
```

### LineItem 对象

```json
{
  "lineNumber": 1,
  "orderId": 1001,
  "itemId": "EST-1",
  "quantity": 2,
  "unitPrice": 99.99,
  "createTime": "2025-05-16T10:00:00Z"
}
```

---

## 安全性要求

1. **身份验证**：所有需要用户登录的端点均需有效的认证令牌
2. **授权**：管理员操作需要额外的 `X-Admin-Role: admin` 头验证
3. **HTTPS**：所有 API 调用必须使用 HTTPS
4. **CORS**：前端应配置跨域资源共享
5. **速率限制**：建议对 API 设置速率限制，防止滥用
6. **输入验证**：所有用户输入需进行有效性验证
7. **SQL 注入防护**：使用参数化查询
8. **敏感数据**：密码、支付信息等应加密存储和传输

---

## 版本控制

| 版本 | 日期 | 变更内容 |
|------|------|--------|
| 1.0 | 2025-05-16 | 初始版本 |

---

## 附录：HTTP 方法说明

| 方法 | 说明 | 幂等性 |
|------|------|--------|
| GET | 获取资源 | 是 |
| POST | 创建新资源 | 否 |
| PUT | 更新资源 | 是 |
| DELETE | 删除资源 | 是 |
| PATCH | 部分更新资源 | 否 |

---

## 联系支持

如有问题或建议，请联系开发团队。


