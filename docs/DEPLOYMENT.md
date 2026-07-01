# 部署指南

## 系统要求

### 服务器端
- Java 17 或更高版本
- MySQL 8.0 或更高版本
- 2GB+ RAM
- Docker 和 Docker Compose（可选）

### Android客户端
- Android 6.0 (API 23) 或更高版本
- GPS定位权限
- 后台运行权限
- 网络访问权限

## 部署方式

### 方式一：Docker Compose部署（推荐）

1. 克隆项目：
```bash
git clone https://github.com/gugd4488-arch/autotrack-system.git
cd autotrack-system
```

2. 修改配置文件：
编辑 `docker-compose.yml`，根据需要修改环境变量

3. 启动服务：
```bash
docker-compose up -d
```

4. 查看日志：
```bash
docker-compose logs -f
```

5. 停止服务：
```bash
docker-compose down
```

### 方式二：手动部署

#### 1. 数据库设置

创建数据库：
```sql
CREATE DATABASE autotrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'autotrack'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON autotrack.* TO 'autotrack'@'%';
FLUSH PRIVILEGES;
```

#### 2. 服务器端部署

1. 编译项目：
```bash
cd server
./gradlew build
```

2. 修改配置：
编辑 `src/main/resources/application.yml`，配置数据库连接信息

3. 运行服务：
```bash
java -jar build/libs/autotrack-server-1.0.0.jar
```

或者使用Gradle：
```bash
./gradlew bootRun
```

#### 3. Web界面部署

将 `web` 目录下的文件部署到Web服务器（Nginx、Apache等）：

```bash
# 复制文件到Web服务器目录
cp -r web/* /var/www/html/autotrack/

# 修改配置
vim /var/www/html/autotrack/js/config.js
# 修改 API_BASE_URL 为实际的服务器地址
```

#### 4. Android客户端部署

1. 编译APK：
```bash
cd android
./gradlew assembleRelease
```

2. 安装到设备：
```bash
adb install app/build/outputs/apk/release/app-release.apk
```

或者直接将APK文件传输到设备安装。

## 配置说明

### 服务器配置

`server/src/main/resources/application.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/autotrack?useSSL=false&serverTimezone=UTC
    username: autotrack
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Android客户端配置

首次启动时，在应用内配置：
- 服务器地址：http://your-server-ip:8080
- 设备ID：自动生成或手动指定
- 位置更新间隔：默认15分钟
- 脚本检查间隔：默认30分钟

## 安全建议

1. 使用HTTPS：
   - 配置SSL证书
   - 修改客户端服务器地址为https://

2. 数据库安全：
   - 使用强密码
   - 限制访问IP
   - 定期备份

3. API安全：
   - 添加身份验证（JWT等）
   - 限制API访问频率
   - 使用防火墙规则

4. Android客户端：
   - 代码混淆
   - 签名保护
   - 安全存储配置

## 监控与维护

### 日志位置

- 服务器日志：`server/logs/`
- Docker日志：`docker-compose logs`

### 性能优化

1. 数据库优化：
   - 添加索引
   - 定期清理历史数据
   - 配置连接池

2. 服务器优化：
   - 调整JVM参数
   - 使用缓存（Redis）
   - 配置负载均衡

### 故障排查

1. 服务器无法启动：
   - 检查端口占用
   - 检查数据库连接
   - 查看日志文件

2. 客户端无法连接：
   - 检查网络连接
   - 验证服务器地址
   - 检查防火墙设置

3. 位置上报失败：
   - 检查GPS权限
   - 验证服务器API
   - 查看设备日志

## 升级说明

### 服务器升级

1. 备份数据库
2. 停止服务
3. 部署新版本
4. 运行数据库迁移（如需要）
5. 启动服务
6. 验证功能

### 客户端升级

1. 编译新版本APK
2. 通过OTA或手动方式分发
3. 客户端自动升级（需实现）
