package com.autotrack.client

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * API服务类
 * 统一处理与服务器的网络通信
 */
class ApiService(private val baseUrl: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * 注册设备
     */
    fun registerDevice(deviceId: String, deviceName: String, deviceModel: String): Boolean {
        val json = JSONObject().apply {
            put("deviceId", deviceId)
            put("name", deviceName)
            put("model", deviceModel)
            put("status", "online")
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/api/devices")
            .post(body)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 更新设备状态
     */
    fun updateDeviceStatus(deviceId: String, status: String): Boolean {
        val json = JSONObject().apply {
            put("status", status)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/api/devices/$deviceId/status")
            .put(body)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 上报位置
     */
    fun reportPosition(
        deviceId: String,
        latitude: Double,
        longitude: Double,
        altitude: Double,
        speed: Float,
        bearing: Float,
        accuracy: Float
    ): Boolean {
        val json = JSONObject().apply {
            put("deviceId", deviceId)
            put("latitude", latitude)
            put("longitude", longitude)
            put("altitude", altitude)
            put("speed", speed)
            put("bearing", bearing)
            put("accuracy", accuracy)
            put("timestamp", System.currentTimeMillis())
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/api/positions")
            .post(body)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取待执行脚本
     */
    fun fetchPendingScripts(deviceId: String): List<ScriptData> {
        val request = Request.Builder()
            .url("$baseUrl/api/scripts/pending?deviceId=$deviceId")
            .get()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return emptyList()
                }

                val jsonArray = JSONArray(response.body?.string() ?: "[]")
                val scripts = mutableListOf<ScriptData>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    scripts.add(
                        ScriptData(
                            id = obj.getLong("id"),
                            name = obj.getString("name"),
                            content = obj.getString("content"),
                            type = obj.getString("type"),
                            deviceId = obj.optString("deviceId", "")
                        )
                    )
                }

                scripts
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 上报脚本执行结果
     */
    fun reportScriptResult(
        scriptId: Long,
        deviceId: String,
        success: Boolean,
        output: String
    ): Boolean {
        val json = JSONObject().apply {
            put("scriptId", scriptId)
            put("deviceId", deviceId)
            put("success", success)
            put("output", output)
            put("executedAt", System.currentTimeMillis())
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/api/scripts/results")
            .post(body)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取设备信息
     */
    fun getDeviceInfo(deviceId: String): DeviceData? {
        val request = Request.Builder()
            .url("$baseUrl/api/devices/$deviceId")
            .get()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return null
                }

                val obj = JSONObject(response.body?.string() ?: "{}")
                DeviceData(
                    id = obj.getLong("id"),
                    deviceId = obj.getString("deviceId"),
                    name = obj.getString("name"),
                    model = obj.getString("model"),
                    status = obj.getString("status")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    data class ScriptData(
        val id: Long,
        val name: String,
        val content: String,
        val type: String,
        val deviceId: String
    )

    data class DeviceData(
        val id: Long,
        val deviceId: String,
        val name: String,
        val model: String,
        val status: String
    )
}
