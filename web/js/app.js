/**
 * 主应用逻辑
 */

// 当前选中的标签页
let currentTab = 'devices';

// 定时刷新定时器
let refreshTimer = null;

/**
 * 页面加载完成后初始化
 */
document.addEventListener('DOMContentLoaded', function() {
    // 绑定导航点击事件
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const tab = this.getAttribute('data-tab');
            switchTab(tab);
        });
    });

    // 绑定表单提交事件
    document.getElementById('add-script-form').addEventListener('submit', handleAddScript);

    // 初始加载数据
    loadDevices();
    loadScripts();
    loadLogs();

    // 启动定时刷新
    startAutoRefresh();
});

/**
 * 切换标签页
 */
function switchTab(tab) {
    // 隐藏所有标签页
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });

    // 移除所有导航激活状态
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });

    // 显示目标标签页
    document.getElementById(tab).classList.add('active');
    document.querySelector(`[data-tab="${tab}"]`).classList.add('active');

    currentTab = tab;

    // 根据标签页加载数据
    if (tab === 'map') {
        loadMapDevices();
    }
}

/**
 * 加载设备列表
 */
async function loadDevices() {
    try {
        const devices = await api.getDevices();
        const tbody = document.getElementById('devices-list');
        
        if (devices.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center">暂无设备</td></tr>';
            return;
        }

        tbody.innerHTML = devices.map(device => `
            <tr>
                <td>${device.deviceId}</td>
                <td>${device.name}</td>
                <td>${device.model || '-'}</td>
                <td><span class="status-badge status-${device.status}">${device.status}</span></td>
                <td>-</td>
                <td>${formatDate(device.lastUpdate)}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="viewDevice('${device.deviceId}')">查看</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('加载设备列表失败:', error);
        document.getElementById('devices-list').innerHTML = 
            '<tr><td colspan="7" class="text-center">加载失败</td></tr>';
    }
}

/**
 * 刷新设备列表
 */
function refreshDevices() {
    loadDevices();
}

/**
 * 查看设备详情
 */
async function viewDevice(deviceId) {
    try {
        const device = await api.getDevice(deviceId);
        alert(`设备详情:\n设备ID: ${device.deviceId}\n名称: ${device.name}\n型号: ${device.model}\n状态: ${device.status}`);
    } catch (error) {
        console.error('加载设备详情失败:', error);
        alert('加载设备详情失败');
    }
}

/**
 * 加载地图设备选项
 */
async function loadMapDevices() {
    try {
        const devices = await api.getDevices();
        const select = document.getElementById('device-select');
        
        select.innerHTML = '<option value="">全部设备</option>' + 
            devices.map(device => 
                `<option value="${device.deviceId}">${device.name} (${device.deviceId})</option>`
            ).join('');
        
        // 显示所有设备在地图上
        await showDevicesOnMap();
    } catch (error) {
        console.error('加载地图设备失败:', error);
    }
}

/**
 * 在地图上显示设备
 */
async function showDeviceOnMap() {
    const deviceId = document.getElementById('device-select').value;
    if (!deviceId) {
        // 如果未选择设备，显示所有设备
        await showDevicesOnMap();
    } else {
        // 显示指定设备的轨迹
        await showTrajectory();
    }
}

/**
 * 加载脚本列表
 */
async function loadScripts() {
    try {
        const scripts = await api.getScripts();
        const tbody = document.getElementById('scripts-list');
        
        if (scripts.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">暂无脚本</td></tr>';
            return;
        }

        tbody.innerHTML = scripts.map(script => `
            <tr>
                <td>${script.name}</td>
                <td>${script.type}</td>
                <td>${script.deviceId || '所有设备'}</td>
                <td>${formatDate(script.createdAt)}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="viewScript(${script.id})">查看</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteScript(${script.id})">删除</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('加载脚本列表失败:', error);
        document.getElementById('scripts-list').innerHTML = 
            '<tr><td colspan="5" class="text-center">加载失败</td></tr>';
    }
}

/**
 * 显示添加脚本模态框
 */
async function showAddScriptModal() {
    document.getElementById('add-script-modal').classList.add('show');
    
    // 加载设备列表
    try {
        const devices = await api.getDevices();
        const select = document.getElementById('script-device');
        select.innerHTML = '<option value="">所有设备</option>' + 
            devices.map(device => 
                `<option value="${device.deviceId}">${device.name} (${device.deviceId})</option>`
            ).join('');
    } catch (error) {
        console.error('加载设备列表失败:', error);
    }
}

/**
 * 关闭添加脚本模态框
 */
function closeAddScriptModal() {
    document.getElementById('add-script-modal').classList.remove('show');
    document.getElementById('add-script-form').reset();
}

/**
 * 处理添加脚本表单提交
 */
async function handleAddScript(e) {
    e.preventDefault();
    
    const script = {
        name: document.getElementById('script-name').value,
        type: document.getElementById('script-type').value,
        deviceId: document.getElementById('script-device').value || null,
        content: document.getElementById('script-content').value,
        status: 'pending'
    };

    try {
        await api.createScript(script);
        alert('脚本添加成功');
        closeAddScriptModal();
        loadScripts();
    } catch (error) {
        console.error('添加脚本失败:', error);
        alert('添加脚本失败');
    }
}

/**
 * 查看脚本
 */
function viewScript(id) {
    alert('查看脚本: ' + id);
}

/**
 * 删除脚本
 */
async function deleteScript(id) {
    if (!confirm('确定要删除这个脚本吗？')) {
        return;
    }

    try {
        await api.deleteScript(id);
        alert('脚本删除成功');
        loadScripts();
    } catch (error) {
        console.error('删除脚本失败:', error);
        alert('删除脚本失败');
    }
}

/**
 * 加载执行日志
 */
async function loadLogs() {
    try {
        const results = await api.getScriptResults();
        const tbody = document.getElementById('logs-list');
        
        if (results.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="text-center">暂无日志</td></tr>';
            return;
        }

        tbody.innerHTML = results.map(result => `
            <tr>
                <td>脚本#${result.scriptId}</td>
                <td>${result.deviceId}</td>
                <td><span class="status-badge status-${result.success ? 'online' : 'offline'}">${result.success ? '成功' : '失败'}</span></td>
                <td>${result.output}</td>
                <td>${formatDate(result.executedAt)}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('加载执行日志失败:', error);
        document.getElementById('logs-list').innerHTML = 
            '<tr><td colspan="5" class="text-center">加载失败</td></tr>';
    }
}

/**
 * 刷新日志
 */
function refreshLogs() {
    loadLogs();
}

/**
 * 启动自动刷新
 */
function startAutoRefresh() {
    refreshTimer = setInterval(() => {
        if (currentTab === 'devices') {
            loadDevices();
        } else if (currentTab === 'logs') {
            loadLogs();
        }
    }, CONFIG.REFRESH_INTERVAL);
}

/**
 * 格式化日期
 */
function formatDate(timestamp) {
    if (!timestamp) return '-';
    const date = new Date(timestamp);
    return date.toLocaleString('zh-CN');
}
