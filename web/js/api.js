/**
 * API接口封装
 */
class API {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 通用请求方法
     */
    async request(url, options = {}) {
        try {
            const response = await fetch(`${this.baseUrl}${url}`, {
                ...options,
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    }

    /**
     * 获取所有设备
     */
    async getDevices() {
        return this.request('/api/devices');
    }

    /**
     * 获取设备详情
     */
    async getDevice(deviceId) {
        return this.request(`/api/devices/${deviceId}`);
    }

    /**
     * 获取设备位置历史
     */
    async getDevicePositions(deviceId, limit = 100) {
        return this.request(`/api/positions?deviceId=${deviceId}&limit=${limit}`);
    }

    /**
     * 获取所有脚本
     */
    async getScripts() {
        return this.request('/api/scripts');
    }

    /**
     * 创建新脚本
     */
    async createScript(script) {
        return this.request('/api/scripts', {
            method: 'POST',
            body: JSON.stringify(script)
        });
    }

    /**
     * 删除脚本
     */
    async deleteScript(id) {
        return this.request(`/api/scripts/${id}`, {
            method: 'DELETE'
        });
    }

    /**
     * 获取脚本执行结果
     */
    async getScriptResults(limit = 100) {
        return this.request(`/api/scripts/results?limit=${limit}`);
    }

    /**
     * 更新设备状态
     */
    async updateDeviceStatus(deviceId, status) {
        return this.request(`/api/devices/${deviceId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        });
    }
}

// 创建全局API实例
const api = new API(CONFIG.API_BASE_URL);
