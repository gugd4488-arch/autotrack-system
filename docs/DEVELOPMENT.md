# 开发指南

## 项目结构

```
autotrack-system/
├── server/                 # 服务器端（Spring Boot）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/autotrack/server/
│   │   │   │       ├── AutoTrackApplication.java
│   │   │   │       ├── controller/
│   │   │   │       ├── entity/
│   │   │   │       └── repository/
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   └── build.gradle
├── android/               # Android客户端（Kotlin）
│   ├── app/
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── java/com/autotrack/client/
│   │   │       │   ├── MainActivity.kt
│   │   │       │   ├── AutoTrackService.kt
│   │   │       │   ├── GpsWorker.kt
│   │   │       │   ├── ScriptWorker.kt
│   │   │       │   └── ApiService.kt
│   │   │       ├── res/
│   │   │       └── AndroidManifest.xml
│   │   └── build.gradle
│   └── build.gradle
├── web/                   # Web管理界面
│   ├── index.html
│   ├── css/
│   │   └── style.css
│   └── js/
│       ├── app.js
│       ├── api.js
│       └── config.js
├── docs/                  # 文档
│   ├── API.md
│   ├── DEPLOYMENT.md
│   └── DEVELOPMENT.md
├── docker-compose.yml
└── README.md
```

## 技术栈

### 后端
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- MySQL 8.0
- Gradle

### Android
- Kotlin
- Android Jetpack (WorkManager)
- OkHttp
- SharedPreferences

### 前端
- HTML5/CSS3
- JavaScript (ES6+)
- Fetch API

## 开发环境设置

### 服务器端

1. 安装JDK 17
2. 安装MySQL 8.0
3. 安装IDE（IntelliJ IDEA推荐）

**构建和运行：**
```bash
cd server
./gradlew bootRun
```

**运行测试：**
```bash
./gradlew test
```

### Android客户端

1. 安装Android Studio
2. 安装Android SDK (API 33+)
3. 配置模拟器或真机

**构建APK：**
```bash
cd android
./gradlew assembleDebug
```

**安装到设备：**
```bash
./gradlew installDebug
```

### Web界面

使用任何Web服务器（如Nginx、Apache）或简单的HTTP服务器：

```bash
cd web
python -m http.server 8000
```

然后访问 http://localhost:8000

## 核心功能开发

### 1. 添加新的API端点

**步骤：**

1. 创建实体类（Entity）
```java
@Entity
public class MyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 其他字段...
}
```

2. 创建Repository接口
```java
public interface MyEntityRepository extends JpaRepository<MyEntity, Long> {
    // 自定义查询方法
}
```

3. 创建Controller
```java
@RestController
@RequestMapping("/api/myentity")
public class MyEntityController {
    @Autowired
    private MyEntityRepository repository;
    
    @GetMapping
    public List<MyEntity> getAll() {
        return repository.findAll();
    }
}
```

### 2. Android客户端添加新功能

**创建新的Worker：**

```kotlin
class MyWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    
    override fun doWork(): Result {
        return try {
            // 执行任务
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
```

**注册Worker：**

```kotlin
val workRequest = PeriodicWorkRequestBuilder<MyWorker>(
    15, TimeUnit.MINUTES
).build()

WorkManager.getInstance(context)
    .enqueue(workRequest)
```

### 3. Web界面添加新页面

1. 在 `index.html` 添加导航和内容区域
2. 在 `app.js` 添加加载和事件处理逻辑
3. 在 `api.js` 添加API调用方法

## Auto.js集成指南

### 集成步骤

1. 添加Auto.js依赖

在 `android/app/build.gradle` 中添加：
```gradle
dependencies {
    implementation 'com.github.hyb1996:Auto.js:4.1.1-alpha'
}
```

2. 初始化Auto.js引擎

```kotlin
class AutoJsEngine {
    private lateinit var scriptEngine: ScriptEngine
    
    fun init(context: Context) {
        scriptEngine = ScriptEngineService.getInstance()
    }
    
    fun execute(script: String): String {
        val result = scriptEngine.execute(script)
        return result.toString()
    }
}
```

3. 在ScriptWorker中使用

```kotlin
private fun executeScript(script: ScriptInfo) {
    val engine = AutoJsEngine()
    engine.init(applicationContext)
    val result = engine.execute(script.content)
    reportScriptResult(script.id, true, result)
}
```

### Auto.js脚本示例

```javascript
// 点击屏幕示例
click(100, 200);

// 文本识别
var text = getText();
console.log(text);

// 应用操作
launchApp("微信");
sleep(2000);
back();
```

## 调试技巧

### 服务器端调试

1. 启用详细日志
```yaml
logging:
  level:
    com.autotrack: DEBUG
```

2. 使用断点调试（IntelliJ IDEA）

3. 查看SQL语句
```yaml
spring:
  jpa:
    show-sql: true
```

### Android调试

1. 使用Logcat
```kotlin
Log.d("TAG", "Debug message")
```

2. 使用Android Studio Profiler监控性能

3. 远程调试
```bash
adb tcpip 5555
adb connect device-ip:5555
```

### Web调试

1. 使用浏览器开发者工具
2. 查看Network标签检查API请求
3. 使用Console查看JavaScript错误

## 测试

### 单元测试

**服务器端：**
```java
@SpringBootTest
public class DeviceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testGetDevices() throws Exception {
        mockMvc.perform(get("/api/devices"))
            .andExpect(status().isOk());
    }
}
```

**Android：**
```kotlin
@Test
fun testApiService() {
    val api = ApiService("http://localhost:8080")
    val devices = api.getDevices()
    assertNotNull(devices)
}
```

### 集成测试

使用Docker Compose启动完整环境：
```bash
docker-compose -f docker-compose.test.yml up
```

## 常见问题

### Q: 数据库连接失败
A: 检查MySQL是否运行，用户名密码是否正确，数据库是否已创建

### Q: Android无法上报位置
A: 检查GPS权限，网络连接，服务器地址配置

### Q: CORS跨域问题
A: 在服务器端添加CORS配置：
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("*")
                    .allowedMethods("*");
            }
        };
    }
}
```

## 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 代码规范

- Java: 遵循Google Java Style Guide
- Kotlin: 遵循Kotlin Coding Conventions
- JavaScript: 使用ESLint

## 发布流程

1. 更新版本号
2. 运行所有测试
3. 构建生产版本
4. 创建Git标签
5. 部署到生产环境
6. 编写Release Notes
