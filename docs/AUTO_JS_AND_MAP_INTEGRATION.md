# Auto.js与地图集成完整指南

## 1. Auto.js引擎集成

### 什么是Auto.js？

Auto.js是一个Android平台上的JavaScript自动化工具，可以：
- 自动点击、滑动、输入文字
- 识别图片和文字
- 控制应用启动和切换
- 执行系统级别的自动化任务

### Auto.js集成方式

#### 1.1 依赖配置

在 `android/app/build.gradle` 中已包含：
```gradle
implementation 'com.github.hyb1996:Auto.js:4.1.1'
```

#### 1.2 核心类：AutoJsEngine

位置：`android/app/src/main/java/com/autotrack/client/AutoJsEngine.kt`

功能：
- 初始化Auto.js引擎
- 执行JavaScript脚本
- 执行Auto.js脚本
- 停止脚本执行

#### 1.3 使用示例

**执行简单脚本**
```kotlin
val engine = AutoJsEngine.getInstance(context)
engine.init()

val result = engine.execute("""
    console.log('Hello from AutoTrack');
    return 'Execution successful';
""", "hello_script")

if (result.success) {
    println(result.output)
}
```

**执行Auto.js脚本**
```kotlin
val result = engine.executeAutoJs("""
    // 打开微信
    launchApp('WeChat');
    
    // 等待2秒
    sleep(2000);
    
    // 获取屏幕文本
    var text = getText();
    
    // 回到主屏幕
    back();
""", "wechat_automation")
```

### Auto.js常用API

#### 基础操作
- `click(x, y)` - 点击屏幕
- `swipe(x1, y1, x2, y2, duration)` - 滑动屏幕
- `setText(text)` - 输入文字
- `back()` - 返回键
- `home()` - 主屏幕键
- `recents()` - 最近应用键

#### 应用控制
- `launchApp(packageName)` - 启动应用
- `currentPackage()` - 获取当前应用
- `openUrl(url)` - 打开链接
- `sendEmail(...)` - 发送邮件

#### 文本识别
- `getText()` - 获取屏幕文本
- `desc(text)` - 按描述查找控件
- `text(text)` - 按文本查找控件
- `id(id)` - 按ID查找控件

#### 等待和延迟
- `sleep(ms)` - 延迟执行
- `waitFor(condition, timeout)` - 等待条件满足

#### 图片识别
- `findImage(image)` - 查找图片
- `matchImage(template)` - 匹配图片

### 脚本执行流程

```
Web管理界面
    ↓
创建脚本任务
    ↓
POST /api/scripts
    ↓
设备端ScriptWorker
    ↓
每30分钟检查一次
    ↓
GET /api/scripts/pending
    ↓
获取待执行脚本
    ↓
AutoJsEngine.execute()
    ↓
执行脚本代码
    ↓
上报执行结果
    ↓
POST /api/scripts/results
    ↓
管理界面查看"执行日志"
```

### 实际应用例子

#### 例1：自动打卡

```javascript
// 设备端执行的脚本
function autoCheckIn() {
    // 打开OA系统
    launchApp('com.company.oa');
    sleep(2000);
    
    // 点击打卡按钮
    click(500, 1000);
    sleep(1000);
    
    // 等待成功提示
    var text = getText();
    if (text.includes('打卡成功')) {
        return '打卡成功';
    } else {
        return '打卡失败：' + text;
    }
}

// 执行脚本
autoCheckIn();
```

#### 例2：自动化测试

```javascript
// APP自动化测试脚本
function testApp() {
    launchApp('com.example.app');
    sleep(1000);
    
    // 测试登录
    click(100, 200);  // 点击用户名输入框
    setText('testuser');
    
    click(100, 300);  // 点击密码输入框
    setText('password123');
    
    click(200, 400);  // 点击登录按钮
    sleep(2000);
    
    // 验证登录成功
    var currentPkg = currentPackage();
    if (currentPkg == 'com.example.app') {
        return '登录成功';
    } else {
        return '登录失败';
    }
}

testApp();
```

#### 例3：自动截图上传

```javascript
// 自动截图并上传
function captureAndUpload() {
    // 截图
    var screenshot = captureScreen();
    
    // 保存到本地
    var path = '/sdcard/Pictures/autotrack_' + Date.now() + '.png';
    screenshot.saveTo(path);
    
    return '截图已保存: ' + path;
}

captureAndUpload();
```

## 2. 地图集成

### 2.1 Web地图（百度地图）

#### 申请百度地图Key

1. 访问 [百度地图API控制台](https://lbsyun.baidu.com/apiconsole/key)
2. 注册并登录账号
3. 创建应用获取API Key
4. 修改 `web/js/config.js` 中的 `BAIDU_MAP_KEY`

```javascript
BAIDU_MAP_KEY: 'YOUR_ACTUAL_KEY_HERE'
```

#### 地图功能

**显示所有设备位置**
```javascript
// 自动调用 showDevicesOnMap()
// 显示所有在线设备的最新位置
```

**显示单个设备轨迹**
```javascript
// 1. 在设备选择下拉菜单中选择设备
// 2. 点击"显示轨迹"按钮
// 3. 地图自动显示该设备的历史轨迹
```

**实时位置更新**
```javascript
// 地图每30秒自动刷新一次
// 显示设备的最新位置
```

#### 地图表示含义

- **绿色标记** - 设备在线
- **红色标记** - 设备离线
- **蓝色线条** - 设备运动轨迹
- **起点/终点** - 轨迹的开始和结束位置

#### 百度地图API使用示例

```javascript
// 初始化地图
var map = new BMapGL.Map('map-container');
map.centerAndZoom(new BMapGL.Point(116.404, 39.915), 15);

// 添加标记
var marker = new BMapGL.Marker(new BMapGL.Point(116.404, 39.915));
map.addOverlay(marker);

// 绘制轨迹线
var path = [
    new BMapGL.Point(116.404, 39.915),
    new BMapGL.Point(116.405, 39.916),
    new BMapGL.Point(116.406, 39.917)
];
var polyline = new BMapGL.Polyline(path, {
    strokeColor: 'blue',
    strokeWeight: 3
});
map.addOverlay(polyline);
```

### 2.2 Android地图（Google Maps）

#### 配置Google Maps

在 `AndroidManifest.xml` 中添加：
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_KEY" />
```

#### MapUtils类

位置：`android/app/src/main/java/com/autotrack/client/MapUtils.kt`

功能：
- 显示单个设备位置
- 显示多个设备位置
- 绘制设备轨迹
- 计算两点间距离

#### 使用示例

```kotlin
val mapUtils = MapUtils(context)

// 显示单个设备
mapUtils.showDeviceLocation(
    googleMap,
    "device123",
    39.9,
    116.4,
    "配送员A",
    true
)

// 显示多个设备
mapUtils.showMultipleDevices(googleMap, listOf(
    MapUtils.DeviceLocation("d1", "设备1", 39.9, 116.4, true),
    MapUtils.DeviceLocation("d2", "设备2", 39.91, 116.41, false)
))

// 绘制轨迹
mapUtils.drawTrajectory(googleMap, positions)

// 计算距离
val distance = mapUtils.calculateDistance(39.9, 116.4, 39.91, 116.41)
```

## 3. 集成后的完整流程

### 场景：物流配送追踪

```
1. 配送员手机安装APK
   ↓
2. 后台自动上报位置（每15分钟）
   ↓
3. 管理员访问Web地图
   ↓
4. 地图显示配送员实时位置
   ↓
5. 管理员下发脚本："到达目标点后拍照"
   ↓
6. 设备执行Auto.js脚本自动拍照
   ↓
7. 执行结果和截图上传到服务器
   ↓
8. 管理员在"执行日志"查看结果
```

### 场景：自动化测试

```
1. 创建测试脚本
   ```javascript
   // 登录测试
   launchApp('com.example.app');
   click(100, 200);
   setText('test@example.com');
   click(100, 300);
   setText('password');
   click(200, 400);
   sleep(2000);
   ```
   
2. 在Web界面下发给10台测试设备
   ↓
3. 10台设备同时执行脚本
   ↓
4. 自动上报测试结果
   ↓
5. 管理员查看测试报告
   ```
   成功: 8台
   失败: 2台
   
   失败原因：
   - 设备1: 网络超时
   - 设备5: 应用崩溃
   ```
```

## 4. 限制和注意事项

### Auto.js限制
- 需要设备有"开发者选项"启用
- 需要"执行脚本"权限
- 某些系统级操作需要ROOT权限
- 某些应用有反自动化保护

### 地图限制
- 百度地图免费版有日请求限制（100万次/天）
- Google Maps需要开启计费
- 无互联网连接时地图不可用

### 隐私和安全
- 位置数据包含用户隐私，需要加密传输
- 脚本执行需要用户授权
- 建议添加用户认证和权限控制

## 5. 问题排查

### Auto.js脚本执行失败

**问题1：脚本不执行**
- 检查设备是否有执行脚本权限
- 检查Android版本（最低6.0）
- 查看设备日志：`adb logcat | grep AutoJsEngine`

**问题2：找不到应用**
```javascript
// 使用包名而不是应用名
launchApp('com.tencent.mm');  // 正确
launchApp('微信');             // 错误
```

**问题3：点击坐标错误**
- 使用 `adb shell` 获取屏幕分辨率
- 根据分辨率调整坐标
- 使用相对坐标或元素定位

### 地图显示问题

**问题1：百度地图不显示**
- 检查API Key是否配置正确
- 检查网络连接
- 查看浏览器控制台错误信息

**问题2：位置不准确**
- 检查GPS是否已打开
- 等待GPS信号稳定（通常需要30秒）
- 户外定位效果更好

## 6. 下一步计划

- [ ] 支持更多脚本语言（Python、Lua）
- [ ] 添加脚本调试器
- [ ] 实现地理围栏告警
- [ ] 支持离线地图
- [ ] 添加脚本模板库
- [ ] 实现脚本版本控制

## 7. 常用命令

```bash
# 构建APK
cd android
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug

# 查看日志
adb logcat | grep AutoTrack

# 获取设备列表
adb devices

# 打开调试模式
adb shell setprop ro.debuggable 1
```

## 8. 相关资源

- [Auto.js官方文档](https://hyb1996.github.io/AutoJs6/)
- [百度地图API文档](https://lbsyun.baidu.com/index.php?title=api)
- [Google Maps API文档](https://developers.google.com/maps/documentation/android-sdk)
- [Kotlin官方文档](https://kotlinlang.org/docs/)

## 9. 支持

遇到问题？
- 查看项目Issue：https://github.com/gugd4488-arch/autotrack-system/issues
- 提交Bug报告
- 讨论新功能需求
