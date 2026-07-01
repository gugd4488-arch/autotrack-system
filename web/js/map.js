/**
 * 地图管理模块
 * 支持百度地图、Google Maps等
 */

let baiduMap = null;
let mapMarkers = [];
let mapPolylines = [];

/**
 * 初始化地图
 */
function initMap() {
    try {
        // 尝试初始化百度地图
        if (window.BMapGL) {
            const container = document.getElementById('baidu-map');
            baiduMap = new BMapGL.Map(container);
            
            // 设置默认中心点和缩放级别
            baiduMap.centerAndZoom(new BMapGL.Point(116.404, 39.915), 12);
            baiduMap.enableScrollWheelZoom(true);
            
            console.log('Baidu Map initialized');
        } else {
            console.warn('Baidu Map API not loaded');
            initSimpleMap();
        }
    } catch (error) {
        console.error('Map initialization failed:', error);
        initSimpleMap();
    }
}

/**
 * 初始化简单地图（HTML Canvas）
 */
function initSimpleMap() {
    const container = document.getElementById('baidu-map');
    container.innerHTML = `
        <div style="display: flex; align-items: center; justify-content: center; height: 100%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <div style="text-align: center; color: white;">
                <h3>地图服务</h3>
                <p style="margin-top: 20px;">配置百度地图API Key以启用实时地图</p>
                <p style="font-size: 12px; margin-top: 10px;">
                    修改 js/config.js 中的 BAIDU_MAP_KEY
                </p>
                <div id="text-map" style="margin-top: 30px; text-align: left; background: rgba(255,255,255,0.1); padding: 20px; border-radius: 8px;">
                    <!-- 文字形式显示设备位置 -->
                </div>
            </div>
        </div>
    `;
}

/**
 * 在地图上显示设备
 */
async function showDevicesOnMap() {
    try {
        clearMapMarkers();
        
        const devices = await api.getDevices();
        if (devices.length === 0) {
            return;
        }

        // 获取每个设备的最新位置
        const deviceLocations = [];
        for (const device of devices) {
            try {
                const positions = await api.getDevicePositions(device.deviceId, 1);
                if (positions.length > 0) {
                    const pos = positions[0];
                    deviceLocations.push({
                        deviceId: device.deviceId,
                        name: device.name,
                        latitude: pos.latitude,
                        longitude: pos.longitude,
                        status: device.status,
                        timestamp: pos.timestamp
                    });
                }
            } catch (error) {
                console.error(`Failed to get positions for ${device.deviceId}:`, error);
            }
        }

        if (baiduMap) {
            displayOnBaiduMap(deviceLocations);
        } else {
            displayOnTextMap(deviceLocations);
        }

        // 更新设备列表
        updateMapDevicesList(deviceLocations);
    } catch (error) {
        console.error('Failed to show devices on map:', error);
    }
}

/**
 * 在百度地图上显示设备
 */
function displayOnBaiduMap(deviceLocations) {
    clearMapMarkers();
    
    deviceLocations.forEach((device, index) => {
        const point = new BMapGL.Point(device.longitude, device.latitude);
        
        // 根据状态选择图标颜色
        const iconUrl = device.status === 'online' 
            ? 'http://api.map.baidu.com/library/GeoUtils/1.2/src/images/icon_end.png'
            : 'http://api.map.baidu.com/library/GeoUtils/1.2/src/images/icon_start.png';
        
        const marker = new BMapGL.Marker(point, {
            title: device.name,
            icon: new BMapGL.Icon(iconUrl, new BMapGL.Size(20, 25))
        });
        
        baiduMap.addOverlay(marker);
        mapMarkers.push(marker);

        // 添加信息窗口
        const infoWindow = new BMapGL.InfoWindow(
            `<div style="font-size: 12px; padding: 10px;">
                <strong>${device.name}</strong><br>
                设备ID: ${device.deviceId}<br>
                状态: <span style="color: ${device.status === 'online' ? 'green' : 'red'}">${device.status}</span><br>
                位置: ${device.latitude.toFixed(4)}, ${device.longitude.toFixed(4)}<br>
                更新时间: ${new Date(device.timestamp).toLocaleString('zh-CN')}
            </div>`
        );

        marker.addEventListener('click', function() {
            baiduMap.openInfoWindow(infoWindow, point);
        });
    });

    // 自动调整地图视图以显示所有标记
    if (deviceLocations.length > 0) {
        const points = deviceLocations.map(d => new BMapGL.Point(d.longitude, d.latitude));
        const bounds = new BMapGL.Bounds();
        points.forEach(p => bounds.extend(p));
        baiduMap.fitBounds(bounds);
    }
}

/**
 * 在文字地图上显示设备（当百度地图不可用时）
 */
function displayOnTextMap(deviceLocations) {
    const textMapContainer = document.getElementById('text-map');
    
    if (textMapContainer) {
        textMapContainer.innerHTML = deviceLocations.map((device, index) => `
            <div style="margin-bottom: 15px; padding: 10px; background: rgba(255,255,255,0.2); border-radius: 4px;">
                <strong style="font-size: 14px;">${index + 1}. ${device.name}</strong><br>
                <span style="font-size: 12px;">
                    📍 ${device.latitude.toFixed(4)}, ${device.longitude.toFixed(4)}<br>
                    ✓ 状态: <span style="color: ${device.status === 'online' ? '#4CAF50' : '#f44336'}">${device.status}</span><br>
                    🕒 更新: ${formatDate(device.timestamp)}
                </span>
            </div>
        `).join('');
    }
}

/**
 * 显示单个设备的轨迹
 */
async function showTrajectory() {
    const deviceId = document.getElementById('device-select').value;
    if (!deviceId) {
        alert('请先选择设备');
        return;
    }

    try {
        clearMapMarkers();
        clearMapPolylines();
        
        const positions = await api.getDevicePositions(deviceId, 100);
        if (positions.length < 2) {
            alert('该设备历史位置数据不足');
            return;
        }

        if (baiduMap) {
            displayTrajectoryOnBaiduMap(positions);
        }
    } catch (error) {
        console.error('Failed to show trajectory:', error);
        alert('加载轨迹失败');
    }
}

/**
 * 在百度地图上显示轨迹
 */
function displayTrajectoryOnBaiduMap(positions) {
    // 绘制轨迹线
    const path = positions.map(p => new BMapGL.Point(p.longitude, p.latitude));
    
    const polyline = new BMapGL.Polyline(path, {
        strokeColor: 'blue',
        strokeWeight: 3,
        strokeOpacity: 0.8
    });
    
    baiduMap.addOverlay(polyline);
    mapPolylines.push(polyline);

    // 标记起点
    if (positions.length > 0) {
        const startPoint = new BMapGL.Point(
            positions[0].longitude,
            positions[0].latitude
        );
        const startMarker = new BMapGL.Marker(startPoint, {
            title: '起点'
        });
        baiduMap.addOverlay(startMarker);
        mapMarkers.push(startMarker);

        // 标记终点
        const endPoint = new BMapGL.Point(
            positions[positions.length - 1].longitude,
            positions[positions.length - 1].latitude
        );
        const endMarker = new BMapGL.Marker(endPoint, {
            title: '终点'
        });
        baiduMap.addOverlay(endMarker);
        mapMarkers.push(endMarker);

        // 自动调整视图
        const bounds = new BMapGL.Bounds();
        path.forEach(p => bounds.extend(p));
        baiduMap.fitBounds(bounds);
    }
}

/**
 * 刷新地图设备列表
 */
function refreshMapDevices() {
    showDevicesOnMap();
}

/**
 * 更新地图下方的设备列表
 */
function updateMapDevicesList(deviceLocations) {
    const tbody = document.getElementById('map-devices-list');
    
    if (deviceLocations.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">暂无设备位置数据</td></tr>';
        return;
    }

    tbody.innerHTML = deviceLocations.map(device => `
        <tr>
            <td>${device.name}</td>
            <td>${device.latitude.toFixed(4)}, ${device.longitude.toFixed(4)}</td>
            <td>-</td>
            <td>${formatDate(device.timestamp)}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="showDeviceTrajectory('${device.deviceId}')">轨迹</button>
            </td>
        </tr>
    `).join('');
}

/**
 * 显示指定设备的轨迹
 */
async function showDeviceTrajectory(deviceId) {
    document.getElementById('device-select').value = deviceId;
    await showTrajectory();
}

/**
 * 清除地图上的所有标记
 */
function clearMapMarkers() {
    if (baiduMap) {
        mapMarkers.forEach(marker => {
            baiduMap.removeOverlay(marker);
        });
    }
    mapMarkers = [];
}

/**
 * 清除地图上的所有折线
 */
function clearMapPolylines() {
    if (baiduMap) {
        mapPolylines.forEach(polyline => {
            baiduMap.removeOverlay(polyline);
        });
    }
    mapPolylines = [];
}

/**
 * 页面加载时初始化地图
 */
document.addEventListener('DOMContentLoaded', function() {
    // 延迟初始化以确保DOM完全加载
    setTimeout(initMap, 100);
});
