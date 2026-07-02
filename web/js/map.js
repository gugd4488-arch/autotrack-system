/**
 * 地图管理模块 - 谷歌地图版本
 * 支持Google Maps显示设备位置和轨迹
 */

let googleMap = null;
let mapMarkers = [];
let mapPolylines = [];

/**
 * 初始化地图
 */
function initMap() {
    try {
        // 尝试初始化谷歌地图
        if (window.google && window.google.maps) {
            const container = document.getElementById('google-map');
            
            googleMap = new google.maps.Map(container, {
                center: { lat: 39.915, lng: 116.404 }, // 北京
                zoom: 12,
                mapTypeId: 'roadmap'
            });
            
            console.log('Google Maps initialized');
        } else {
            console.warn('Google Maps API not loaded');
            initSimpleMap();
        }
    } catch (error) {
        console.error('Map initialization failed:', error);
        initSimpleMap();
    }
}

/**
 * 初始化简单地图（文字版）
 */
function initSimpleMap() {
    const container = document.getElementById('google-map');
    container.innerHTML = `
        <div style="display: flex; align-items: center; justify-content: center; height: 100%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <div style="text-align: center; color: white;">
                <h3>地图服务</h3>
                <p style="margin-top: 20px;">配置Google Maps API Key以启用实时地图</p>
                <p style="font-size: 12px; margin-top: 10px;">
                    修改 index.html 中的 YOUR_GOOGLE_MAPS_KEY
                </p>
                <div id="text-map" style="margin-top: 30px; text-align: left; background: rgba(255,255,255,0.1); padding: 20px; border-radius: 8px; max-height: 400px; overflow-y: auto;">
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

        if (googleMap) {
            displayOnGoogleMap(deviceLocations);
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
 * 在谷歌地图上显示设备
 */
function displayOnGoogleMap(deviceLocations) {
    clearMapMarkers();
    
    const bounds = new google.maps.LatLngBounds();
    
    deviceLocations.forEach((device, index) => {
        const position = { lat: device.latitude, lng: device.longitude };
        
        // 创建标记
        const marker = new google.maps.Marker({
            position: position,
            map: googleMap,
            title: device.name,
            label: {
                text: (index + 1).toString(),
                color: 'white'
            },
            icon: {
                path: google.maps.SymbolPath.CIRCLE,
                scale: 12,
                fillColor: device.status === 'online' ? '#4CAF50' : '#f44336',
                fillOpacity: 1,
                strokeColor: 'white',
                strokeWeight: 2
            }
        });
        
        // 创建信息窗口
        const infoWindow = new google.maps.InfoWindow({
            content: `
                <div style="font-size: 12px; padding: 10px; min-width: 200px;">
                    <strong style="font-size: 14px;">${device.name}</strong><br><br>
                    <b>设备ID:</b> ${device.deviceId}<br>
                    <b>状态:</b> <span style="color: ${device.status === 'online' ? 'green' : 'red'}">${device.status}</span><br>
                    <b>位置:</b> ${device.latitude.toFixed(6)}, ${device.longitude.toFixed(6)}<br>
                    <b>更新时间:</b> ${new Date(device.timestamp).toLocaleString('zh-CN')}
                </div>
            `
        });
        
        marker.addListener('click', () => {
            infoWindow.open(googleMap, marker);
        });
        
        mapMarkers.push(marker);
        bounds.extend(position);
    });

    // 自动调整视图以显示所有标记
    if (deviceLocations.length > 0) {
        googleMap.fitBounds(bounds);
    }
}

/**
 * 在文字地图上显示设备（当谷歌地图不可用时）
 */
function displayOnTextMap(deviceLocations) {
    const textMapContainer = document.getElementById('text-map');
    
    if (textMapContainer) {
        textMapContainer.innerHTML = deviceLocations.map((device, index) => `
            <div style="margin-bottom: 15px; padding: 10px; background: rgba(255,255,255,0.2); border-radius: 4px;">
                <strong style="font-size: 14px;">${index + 1}. ${device.name}</strong><br>
                <span style="font-size: 12px;">
                    📍 ${device.latitude.toFixed(6)}, ${device.longitude.toFixed(6)}<br>
                    ✓ 状态: <span style="color: ${device.status === 'online' ? '#4CAF50' : '#f44336'}">${device.status}</span><br>
                    🕒 更新: ${formatDate(device.timestamp)}<br>
                    🌐 <a href="https://www.google.com/maps?q=${device.latitude},${device.longitude}" target="_blank" style="color: #90CAF9;">在Google Maps中打开</a>
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

        if (googleMap) {
            displayTrajectoryOnGoogleMap(positions);
        } else {
            alert('请配置Google Maps API以显示轨迹');
        }
    } catch (error) {
        console.error('Failed to show trajectory:', error);
        alert('加载轨迹失败: ' + error.message);
    }
}

/**
 * 在谷歌地图上显示轨迹
 */
function displayTrajectoryOnGoogleMap(positions) {
    // 绘制轨迹线
    const path = positions.map(p => ({ lat: p.latitude, lng: p.longitude }));
    
    const polyline = new google.maps.Polyline({
        path: path,
        geodesic: true,
        strokeColor: '#2196F3',
        strokeOpacity: 0.8,
        strokeWeight: 3
    });
    
    polyline.setMap(googleMap);
    mapPolylines.push(polyline);

    // 标记起点
    if (positions.length > 0) {
        const startMarker = new google.maps.Marker({
            position: { lat: positions[0].latitude, lng: positions[0].longitude },
            map: googleMap,
            label: '起',
            icon: {
                path: google.maps.SymbolPath.CIRCLE,
                scale: 10,
                fillColor: '#4CAF50',
                fillOpacity: 1,
                strokeColor: 'white',
                strokeWeight: 2
            }
        });
        mapMarkers.push(startMarker);

        // 标记终点
        const endMarker = new google.maps.Marker({
            position: { lat: positions[positions.length - 1].latitude, lng: positions[positions.length - 1].longitude },
            map: googleMap,
            label: '终',
            icon: {
                path: google.maps.SymbolPath.CIRCLE,
                scale: 10,
                fillColor: '#f44336',
                fillOpacity: 1,
                strokeColor: 'white',
                strokeWeight: 2
            }
        });
        mapMarkers.push(endMarker);

        // 自动调整视图
        const bounds = new google.maps.LatLngBounds();
        path.forEach(p => bounds.extend(p));
        googleMap.fitBounds(bounds);
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
            <td>
                <a href="https://www.google.com/maps?q=${device.latitude},${device.longitude}" target="_blank">
                    ${device.latitude.toFixed(6)}, ${device.longitude.toFixed(6)}
                </a>
            </td>
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
 * 显示单个设备在地图上
 */
async function showDeviceOnMap() {
    const deviceId = document.getElementById('device-select').value;
    
    if (!deviceId) {
        // 显示所有设备
        await showDevicesOnMap();
    } else {
        // 显示单个设备
        try {
            clearMapMarkers();
            const positions = await api.getDevicePositions(deviceId, 1);
            
            if (positions.length > 0 && googleMap) {
                const pos = positions[0];
                const position = { lat: pos.latitude, lng: pos.longitude };
                
                const marker = new google.maps.Marker({
                    position: position,
                    map: googleMap,
                    animation: google.maps.Animation.DROP
                });
                
                mapMarkers.push(marker);
                googleMap.setCenter(position);
                googleMap.setZoom(15);
            }
        } catch (error) {
            console.error('Failed to show device:', error);
        }
    }
}

/**
 * 清除地图上的所有标记
 */
function clearMapMarkers() {
    mapMarkers.forEach(marker => {
        marker.setMap(null);
    });
    mapMarkers = [];
}

/**
 * 清除地图上的所有折线
 */
function clearMapPolylines() {
    mapPolylines.forEach(polyline => {
        polyline.setMap(null);
    });
    mapPolylines = [];
}

/**
 * 格式化日期
 */
function formatDate(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

/**
 * 页面加载时初始化地图
 */
document.addEventListener('DOMContentLoaded', function() {
    // 延迟初始化以确保DOM完全加载
    setTimeout(initMap, 100);
});
