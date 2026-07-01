/**
 * 配置文件
 */
const CONFIG = {
    // API基础URL，根据实际部署修改
    API_BASE_URL: 'http://localhost:8080',
    
    // 数据刷新间隔（毫秒）
    REFRESH_INTERVAL: 30000,
    
    // 地图配置
    MAP_CONFIG: {
        center: [116.404, 39.915], // 默认中心点（北京）
        zoom: 13
    },
    
    // 百度地图API Key（免费版，需要申请自己的Key）
    // 申请地址：https://lbsyun.baidu.com/apiconsole/key
    BAIDU_MAP_KEY: 'YOUR_BAIDU_MAP_KEY_HERE',
    
    // Google Maps API Key（可选）
    GOOGLE_MAPS_KEY: 'YOUR_GOOGLE_MAPS_KEY_HERE',
    
    // 位置更新间隔（秒）
    LOCATION_UPDATE_INTERVAL: 15 * 60, // 15分钟
    
    // 脚本检查间隔（秒）
    SCRIPT_CHECK_INTERVAL: 30 * 60, // 30分钟
};

