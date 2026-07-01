# AutoTrack System 快速启动指南

## 5分钟快速部署

### 步骤1：克隆项目（1分钟）

```bash
git clone https://github.com/gugd4488-arch/autotrack-system.git
cd autotrack-system
```

### 步骤2：Docker一键启动（2分钟）

```bash
docker-compose up -d
```

**启动完成后：**
- 服务器API: http://localhost:8082
- Web管理界面: http://localhost
- MySQL数据库: localhost:3306

### 步骤3：编译Android APK（2分钟）

```bash
cd android
./gradlew assembleDebug
```

**生成位置：** `app/build/outputs/apk/debug/app-debug.apk`

### 步骤4：安装到设备

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

或直接将APK文件发送到手机安装

## 10分钟完整体验

### 1. 打开Web管理界面

访问 http://localhost （如果Docker部署）

### 2. 安装并启动APK

- 在Android设备上打开APK进行安装
- 首次启动会提示配置
- 输入服务器地址：`http://your-server-ip:8082`
- 输入设备ID（可自动生成）
- 点击"开始服务"

### 3. 查看设备在线

在Web界面"设备列表"中应该能看到新设备：
- 设备ID
- 设备状态（在线）
- 最后更新时间

### 4. 查看位置

在Web界面"位置地图"中：
- 看到设备实时位置（需要配置百度地图Key）
- 或在文字形式显示位置坐标

### 5. 下发脚本

在"脚本管理"中：
1. 点击"添加脚本"
2. 输入脚本名称：测试
3. 选择类型：JavaScript
4. 选择目标设备
5. 输入脚本内容：
   ```javascript
   console.log('Hello from AutoTrack');
   ```
6. 点击保存

### 6. 查看执行结果

在"执行日志"中查看脚本执行情况：
- 脚本名称
- 执行设备
- 执行状态（成功/失败）
- 输出内容

## 常见任务

### 任务1：实时GPS追踪

```
1. 安装APK到配送员设备
2. 后台自动上报位置
3. 在地图上实时查看位置
4. 支持显示历史轨迹
```

**所需：** 无

### 任务2：自动化脚本执行

```
1. 编写Auto.js脚本
2. 在Web界面创建任务
3. 下发到设备
4. 设备自动执行
5. 查看执行结果
```

**所需：** Auto.js脚本知识

### 任务3：远程拍照上传

```
1. 创建脚本：
   - 自动打开相机
   - 拍照
   - 保存到指定位置
   
2. 下发到设备
3. 设备自动执行
4. 查看截图
```

**脚本示例：**
```javascript
// Auto.js脚本
launchApp('com.android.camera');
sleep(1000);
click(500, 1500);  // 拍照按钮
sleep(1000);
back();
```

### 任务4：批量设备管理

```
1. 在Web界面创建脚本
2. 选择"所有设备"
3. 下发脚本
4. 所有设备同时执行
5. 查看执行报告
```

## 配置说明

### 服务器地址配置

编辑 `docker-compose.yml`：

```yaml
server:
  ports:
    - "8082:8080"  # 修改这里改变服务器端口
```

### 数据库配置

编辑 `docker-compose.yml`：

```yaml
mysql:
  environment:
    MYSQL_ROOT_PASSWORD: autotrack2026  # 修改密码
    MYSQL_DATABASE: autotrack
```

### 地图配置

编辑 `web/js/config.js`：

```javascript
BAIDU_MAP_KEY: 'YOUR_ACTUAL_KEY_HERE'
```

申请地址：https://lbsyun.baidu.com/apiconsole/key

## 常见问题

### Q1：APK安装后无法连接服务器

**A：** 
1. 检查服务器是否运行：`docker-compose ps`
2. 检查网络连接
3. 确保设备和服务器在同一网络
4. 检查服务器地址格式（不要用localhost）

### Q2：没有看到设备在线

**A：**
1. 检查APK是否正在运行
2. 检查后台运行权限是否已授予
3. 查看服务器日志：`docker-compose logs server`

### Q3：脚本不执行

**A：**
1. 检查设备是否在线
2. 检查脚本语法是否正确
3. 查看执行日志中的错误信息
4. 某些命令可能需要ROOT权限

### Q4：地图不显示

**A：**
1. 配置百度地图Key
2. 检查网络连接
3. 查看浏览器控制台错误

### Q5：位置不准确

**A：**
1. 需要GPS打开
2. 户外定位更准确
3. 等待GPS信号稳定
4. 室内可使用基站定位

## 下一步

1. **配置https** - 用于生产环境
2. **添加用户认证** - 保护数据安全
3. **集成地理围栏** - 自动告警
4. **导出数据报表** - 用于分析

## 获取帮助

- 📖 [完整文档](docs/)
- 🔧 [开发指南](docs/DEVELOPMENT.md)
- 📍 [地图集成指南](docs/AUTO_JS_AND_MAP_INTEGRATION.md)
- 🐛 [报告问题](https://github.com/gugd4488-arch/autotrack-system/issues)

## 生产部署清单

- [ ] 配置https证书
- [ ] 修改数据库密码
- [ ] 添加用户认证
- [ ] 配置备份策略
- [ ] 设置监控告警
- [ ] 优化性能配置
- [ ] 进行安全测试
- [ ] 准备故障恢复方案

**祝您使用愉快！** 🚀
