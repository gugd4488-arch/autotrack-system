# API接口文档

## 基础信息

- 基础URL: `http://your-server:8080/api`
- 数据格式: JSON
- 字符编码: UTF-8

## 设备管理

### 获取所有设备

**请求**
```
GET /devices
```

**响应**
```json
[
  {
    "id": 1,
    "deviceId": "device123",
    "name": "测试设备",
    "model": "Pixel 6",
    "status": "online",
    "lastUpdate": 1688123456789
  }
]
```

### 获取设备详情

**请求**
```
GET /devices/{deviceId}
```

**响应**
```json
{
  "id": 1,
  "deviceId": "device123",
  "name": "测试设备",
  "model": "Pixel 6",
  "status": "online",
  "lastUpdate": 1688123456789
}
```

### 注册设备

**请求**
```
POST /devices
Content-Type: application/json

{
  "deviceId": "device123",
  "name": "测试设备",
  "model": "Pixel 6",
  "status": "online"
}
```

**响应**
```json
{
  "id": 1,
  "deviceId": "device123",
  "name": "测试设备",
  "model": "Pixel 6",
  "status": "online",
  "lastUpdate": 1688123456789
}
```

### 更新设备状态

**请求**
```
PUT /devices/{deviceId}/status
Content-Type: application/json

{
  "status": "offline"
}
```

**响应**
```json
{
  "success": true,
  "message": "设备状态更新成功"
}
```

## 位置管理

### 上报位置

**请求**
```
POST /positions
Content-Type: application/json

{
  "deviceId": "device123",
  "latitude": 39.9042,
  "longitude": 116.4074,
  "altitude": 50.0,
  "speed": 0.0,
  "bearing": 0.0,
  "accuracy": 10.0,
  "timestamp": 1688123456789
}
```

**响应**
```json
{
  "id": 1,
  "deviceId": "device123",
  "latitude": 39.9042,
  "longitude": 116.4074,
  "altitude": 50.0,
  "speed": 0.0,
  "bearing": 0.0,
  "accuracy": 10.0,
  "timestamp": 1688123456789
}
```

### 查询设备位置历史

**请求**
```
GET /positions?deviceId=device123&limit=100
```

**参数**
- deviceId: 设备ID（必需）
- limit: 返回记录数量，默认100

**响应**
```json
[
  {
    "id": 1,
    "deviceId": "device123",
    "latitude": 39.9042,
    "longitude": 116.4074,
    "altitude": 50.0,
    "speed": 0.0,
    "bearing": 0.0,
    "accuracy": 10.0,
    "timestamp": 1688123456789
  }
]
```

## 脚本管理

### 获取所有脚本

**请求**
```
GET /scripts
```

**响应**
```json
[
  {
    "id": 1,
    "name": "测试脚本",
    "content": "console.log('hello');",
    "type": "javascript",
    "deviceId": "device123",
    "status": "pending",
    "createdAt": 1688123456789
  }
]
```

### 创建脚本

**请求**
```
POST /scripts
Content-Type: application/json

{
  "name": "测试脚本",
  "content": "console.log('hello');",
  "type": "javascript",
  "deviceId": "device123",
  "status": "pending"
}
```

**响应**
```json
{
  "id": 1,
  "name": "测试脚本",
  "content": "console.log('hello');",
  "type": "javascript",
  "deviceId": "device123",
  "status": "pending",
  "createdAt": 1688123456789
}
```

### 获取待执行脚本

**请求**
```
GET /scripts/pending?deviceId=device123
```

**响应**
```json
[
  {
    "id": 1,
    "name": "测试脚本",
    "content": "console.log('hello');",
    "type": "javascript",
    "deviceId": "device123",
    "status": "pending",
    "createdAt": 1688123456789
  }
]
```

### 删除脚本

**请求**
```
DELETE /scripts/{id}
```

**响应**
```json
{
  "success": true,
  "message": "脚本删除成功"
}
```

## 执行结果管理

### 上报执行结果

**请求**
```
POST /scripts/results
Content-Type: application/json

{
  "scriptId": 1,
  "deviceId": "device123",
  "success": true,
  "output": "执行成功",
  "executedAt": 1688123456789
}
```

**响应**
```json
{
  "id": 1,
  "scriptId": 1,
  "deviceId": "device123",
  "success": true,
  "output": "执行成功",
  "executedAt": 1688123456789
}
```

### 查询执行结果

**请求**
```
GET /scripts/results?limit=100
```

**参数**
- limit: 返回记录数量，默认100

**响应**
```json
[
  {
    "id": 1,
    "scriptId": 1,
    "deviceId": "device123",
    "success": true,
    "output": "执行成功",
    "executedAt": 1688123456789
  }
]
```

## 错误码

| 错误码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 示例

### 完整流程示例

1. 设备注册
```bash
curl -X POST http://localhost:8080/api/devices \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "device123",
    "name": "测试设备",
    "model": "Pixel 6",
    "status": "online"
  }'
```

2. 上报位置
```bash
curl -X POST http://localhost:8080/api/positions \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "device123",
    "latitude": 39.9042,
    "longitude": 116.4074,
    "altitude": 50.0,
    "speed": 0.0,
    "bearing": 0.0,
    "accuracy": 10.0,
    "timestamp": 1688123456789
  }'
```

3. 创建脚本
```bash
curl -X POST http://localhost:8080/api/scripts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试脚本",
    "content": "console.log(\"hello\");",
    "type": "javascript",
    "deviceId": "device123",
    "status": "pending"
  }'
```

4. 获取待执行脚本
```bash
curl http://localhost:8080/api/scripts/pending?deviceId=device123
```

5. 上报执行结果
```bash
curl -X POST http://localhost:8080/api/scripts/results \
  -H "Content-Type: application/json" \
  -d '{
    "scriptId": 1,
    "deviceId": "device123",
    "success": true,
    "output": "执行成功",
    "executedAt": 1688123456789
  }'
```
