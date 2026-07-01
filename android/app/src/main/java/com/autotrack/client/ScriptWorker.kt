package com.autotrack.client

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * 脚本检查和执行Worker
 * 负责从服务器获取待执行脚本并执行
 */
class ScriptWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun doWork(): Result {
        return try {
            val scripts = fetchPendingScripts()
            scripts.forEach { script ->
                executeScript(script)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    /**
     * 从服务器获取待执行脚本
     */
    private fun fetchPendingScripts(): List<ScriptInfo> {
        val prefs = applicationContext.getSharedPreferences("autotrack", Context.MODE_PRIVATE)
        val serverUrl = prefs.getString("server_url", "") ?: ""
        val deviceId = prefs.getString("device_id", "") ?: ""

        if (serverUrl.isEmpty() || deviceId.isEmpty()) {
            return emptyList()
        }

        val request = Request.Builder()
            .url("$serverUrl/api/scripts/pending?deviceId=$deviceId")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Fetch scripts failed: ${response.code}")
            }

            val jsonArray = JSONArray(response.body?.string() ?: "[]")
            val scripts = mutableListOf<ScriptInfo>()
            
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                scripts.add(
                    ScriptInfo(
                        id = obj.getLong("id"),
                        name = obj.getString("name"),
                        content = obj.getString("content"),
                        type = obj.getString("type")
                    )
                )
            }
            
            return scripts
        }
    }

    /**
     * 执行脚本
     * 注意：这里需要集成Auto.js引擎
     */
    private fun executeScript(script: ScriptInfo) {
        val prefs = applicationContext.getSharedPreferences("autotrack", Context.MODE_PRIVATE)
        val serverUrl = prefs.getString("server_url", "") ?: ""
        val deviceId = prefs.getString("device_id", "") ?: ""

        if (serverUrl.isEmpty() || deviceId.isEmpty()) {
            return
        }

        try {
            // TODO: 集成Auto.js引擎执行脚本
            // 这里需要添加Auto.js相关依赖和代码
            // val engine = ScriptEngineService.getInstance()
            // val result = engine.execute(script.content)
            
            // 模拟执行结果
            val success = true
            val output = "Script executed successfully (placeholder)"
            
            // 上报执行结果
            reportScriptResult(script.id, success, output)
        } catch (e: Exception) {
            e.printStackTrace()
            reportScriptResult(script.id, false, e.message ?: "Unknown error")
        }
    }

    /**
     * 上报脚本执行结果
     */
    private fun reportScriptResult(scriptId: Long, success: Boolean, output: String) {
        val prefs = applicationContext.getSharedPreferences("autotrack", Context.MODE_PRIVATE)
        val serverUrl = prefs.getString("server_url", "") ?: ""
        val deviceId = prefs.getString("device_id", "") ?: ""

        if (serverUrl.isEmpty() || deviceId.isEmpty()) {
            return
        }

        val json = JSONObject().apply {
            put("scriptId", scriptId)
            put("deviceId", deviceId)
            put("success", success)
            put("output", output)
            put("timestamp", System.currentTimeMillis())
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$serverUrl/api/scripts/results")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Report result failed: ${response.code}")
            }
        }
    }

    data class ScriptInfo(
        val id: Long,
        val name: String,
        val content: String,
        val type: String
    )
}

// 扩展函数
private fun String.toRequestBody(mediaType: okhttp3.MediaType) = 
    okhttp3.RequestBody.create(mediaType, this)
