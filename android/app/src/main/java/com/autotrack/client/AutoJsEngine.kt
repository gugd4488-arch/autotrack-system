package com.autotrack.client

import android.content.Context
import android.os.Build
import android.util.Log
import com.stardust.autojs.execution.ScriptEngineService
import com.stardust.autojs.script.JavaScriptSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Auto.js脚本执行引擎
 * 负责执行JavaScript和Auto.js脚本
 */
class AutoJsEngine(private val context: Context) {

    companion object {
        private const val TAG = "AutoJsEngine"
        private var instance: AutoJsEngine? = null

        fun getInstance(context: Context): AutoJsEngine {
            return instance ?: AutoJsEngine(context).also { instance = it }
        }
    }

    private var scriptEngineService: ScriptEngineService? = null

    /**
     * 初始化引擎
     */
    fun init() {
        try {
            scriptEngineService = ScriptEngineService.getInstance()
            Log.d(TAG, "Auto.js engine initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Auto.js engine", e)
        }
    }

    /**
     * 执行脚本
     */
    suspend fun execute(scriptCode: String, scriptName: String = "script"): ExecutionResult {
        return withContext(Dispatchers.Default) {
            try {
                if (scriptEngineService == null) {
                    init()
                }

                // 创建JavaScript源对象
                val source = JavaScriptSource(scriptName, scriptCode)
                
                // 执行脚本
                val result = scriptEngineService?.execute(source)
                
                Log.d(TAG, "Script executed: $scriptName")
                
                ExecutionResult(
                    success = true,
                    output = result?.toString() ?: "Script executed successfully",
                    scriptName = scriptName
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to execute script: $scriptName", e)
                ExecutionResult(
                    success = false,
                    output = "Error: ${e.message}",
                    scriptName = scriptName,
                    error = e
                )
            }
        }
    }

    /**
     * 执行Auto.js脚本（包含特殊的Auto.js API）
     */
    suspend fun executeAutoJs(scriptCode: String, scriptName: String = "autojs"): ExecutionResult {
        return withContext(Dispatchers.Default) {
            try {
                if (scriptEngineService == null) {
                    init()
                }

                // Auto.js脚本需要特殊处理
                val enhancedCode = """
                    // 引入Auto.js全局对象
                    importClass(android.util.Log);
                    
                    // 执行用户脚本
                    $scriptCode
                """.trimIndent()

                val source = JavaScriptSource(scriptName, enhancedCode)
                val result = scriptEngineService?.execute(source)

                Log.d(TAG, "Auto.js script executed: $scriptName")

                ExecutionResult(
                    success = true,
                    output = result?.toString() ?: "Auto.js script executed successfully",
                    scriptName = scriptName
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to execute Auto.js script: $scriptName", e)
                ExecutionResult(
                    success = false,
                    output = "Error: ${e.message}",
                    scriptName = scriptName,
                    error = e
                )
            }
        }
    }

    /**
     * 停止所有正在执行的脚本
     */
    fun stopAll() {
        try {
            scriptEngineService?.stopAll()
            Log.d(TAG, "All scripts stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop scripts", e)
        }
    }

    /**
     * 释放资源
     */
    fun destroy() {
        try {
            stopAll()
            scriptEngineService = null
            Log.d(TAG, "Engine destroyed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to destroy engine", e)
        }
    }

    /**
     * 脚本执行结果数据类
     */
    data class ExecutionResult(
        val success: Boolean,
        val output: String,
        val scriptName: String,
        val error: Exception? = null,
        val executedAt: Long = System.currentTimeMillis()
    )
}
