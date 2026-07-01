# AutoTrack System

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/gugd4488-arch/autotrack-system/blob/main/LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Android](https://img.shields.io/badge/Android-6.0+-green.svg)](https://developer.android.com)

## 项目简介

AutoTrack System 是一个整合了 Auto.js 自动化脚本和 Traccar GPS追踪功能的综合管理系统。该系统允许用户通过统一的后台管理平台，同时控制Android设备的自动化脚本执行和实时位置追踪。

## 核心功能

### 1. GPS位置追踪
- ✅ 实时位置监控
- ✅ 历史轨迹查询
- ✅ 地理围栏告警
- ✅ 多设备管理
- ✅ 位置数据可视化

### 2. 自动化脚本管理
- ✅ 远程脚本推送
- ✅ 定时任务执行
- ✅ 脚本执行日志
- ✅ 多设备批量操作
- ✅ JavaScript/Auto.js支持

### 3. 统一管理后台
- ✅ Web管理界面
- ✅ 设备状态监控
- ✅ 数据统计分析
- ✅ 用户权限管理
- ✅ 实时数据刷新

## 技术架构

### 后端服务
- **框架**: Spring Boot 3.x
- **数据库**: MySQL 8.0
- **API**: RESTful
- **构建工具**: Gradle
- **JDK版本**: Java 17

### Android客户端
- **语言**: Kotlin
- **最低版本**: Android 6.0 (API 23)
- **核心组件**: 
  - Auto.js 引擎（集成中）
  - WorkManager（后台任务调度）
  - OkHttp（网络请求）
  - LocationManager（GPS定位）

### Web前端
- **技术**: HTML5 + CSS3 + JavaScript (ES6+)
- **UI**: 响应式设计
- **网络**: Fetch API
- **地图**: 支持多种地图服务（可扩展）

## 项目结构

```
autotrack-system/
├── server/                 # Spring Boot服务器端
│   ├── src/main/java/     # Java源代码
│   ├── src/main/resources/# 配置文件
│   └── build.gradle       # Gradle构建文件
├── android/               # Android客户端
│   ├── app/src/main/      # Kotlin源代码
│   └── build.gradle       # Gradle构建文件
├── web/                   # Web管理界面
│   ├── index.html         # 主页面
│   ├── css/               # 样式文件
│   └── js/                # JavaScript文件
├── docs/                  # 文档
│   ├── API.md             # API接口文档
│   ├── DEPLOYMENT.md      # 部署指南
│   └── DEVELOPMENT.md     # 开发指南
├── docker-compose.yml     # Docker编排文件
└── README.md              # 项目说明
```

## 快速开始

### 方式一：使用Docker Compose（推荐）

```bash
# 1. 克隆项目
git clone https://github.com/gugd4488-arch/autotrack-system.git
cd autotrack-system

# 2. 启动所有服务
docker-compose up -d

# 3. 访问Web管理界面
# http://localhost:80

# 4. API服务地址
# http://localhost:8080/api

# 5. 查看日志
docker-compose logs -f
```

### 方式二：手动部署

#### 1. 部署服务器端

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE autotrack;

# 构建并运行
cd server
./gradlew bootRun
```

#### 2. 部署Web界面

```bash
# 复制web目录到Web服务器
cp -r web/* /var/www/html/autotrack/

# 或使用简单HTTP服务器测试
cd web
python -m http.server 8000
```

#### 3. 编译Android客户端

```bash
cd android
./gradlew assembleRelease
# APK位置: app/build/outputs/apk/release/app-release.apk
```

详细部署说明请参考 [部署文档](docs/DEPLOYMENT.md)

## 使用说明

### 设备配置

1. 安装Android客户端APK到设备
2. 首次启动，配置服务器地址（如：http://your-server:8080）
3. 设置设备ID和名称
4. 授予必要权限（GPS、后台运行等）

### Web管理

1. 访问Web管理界面
2. 在"设备列表"查看所有在线设备
3. 在"脚本管理"创建和分发脚本
4. 在"位置地图"查看设备实时位置
5. 在"执行日志"查看脚本执行历史

### API调用

```bash
# 获取设备列表
curl http://localhost:8080/api/devices

# 上报位置
curl -X POST http://localhost:8080/api/positions \
  -H "Content-Type: application/json" \
  -d '{"deviceId":"device123","latitude":39.9,"longitude":116.4}'

# 创建脚本
curl -X POST http://localhost:8080/api/scripts \
  -H "Content-Type: application/json" \
  -d '{"name":"测试","content":"console.log(123);","type":"javascript"}'
```

完整API文档：[API接口文档](docs/API.md)

## 文档

- 📖 [API接口文档](docs/API.md) - 完整的RESTful API说明
- 🚀 [部署指南](docs/DEPLOYMENT.md) - 生产环境部署步骤
- 💻 [开发指南](docs/DEVELOPMENT.md) - 开发环境配置和代码贡献

## 使用场景

1. **企业设备管理**: 统一管理公司配发的Android设备，远程执行维护任务
2. **物流追踪**: 实时监控配送车辆和人员位置，优化配送路线
3. **自动化测试**: 远程控制多台设备执行自动化测试脚本
4. **家庭监护**: 追踪家人位置，执行定时提醒任务
5. **车队管理**: GPS追踪和车辆运行数据采集
6. **外勤管理**: 员工位置追踪和任务执行监控

## 功能演示

### Web管理界面
- 设备列表管理
- 实时位置地图
- 脚本编辑和分发
- 执行日志查询

### Android客户端
- 后台定位服务
- 定期位置上报
- 自动拉取和执行脚本
- 执行结果回传

（截图待添加）

## 开发路线图

- [x] 基础GPS定位功能
- [x] 设备管理API
- [x] Web管理界面
- [x] Android后台服务
- [ ] Auto.js引擎完全集成
- [ ] 地图可视化（百度/高德地图）
- [ ] 用户认证和权限管理
- [ ] 地理围栏告警
- [ ] 数据报表和统计
- [ ] iOS客户端支持
- [ ] 消息推送通知

## 贡献

欢迎贡献代码、报告问题或提出新功能建议！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

请查看 [开发指南](docs/DEVELOPMENT.md) 了解更多细节。

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 联系方式

- **GitHub**: [@gugd4488-arch](https://github.com/gugd4488-arch)
- **Email**: gugd4488@gmail.com
- **项目地址**: https://github.com/gugd4488-arch/autotrack-system

## 致谢

本项目整合了以下优秀开源项目的思想：
- [Auto.js](https://github.com/hyb1996/Auto.js) - Android自动化脚本框架
- [Traccar](https://github.com/traccar/traccar) - 开源GPS追踪服务器
- [Traccar Client](https://github.com/traccar/traccar-client-android) - Android GPS客户端

感谢所有开源贡献者！

## Star History

如果这个项目对你有帮助，请给一个 ⭐️ 支持一下！
