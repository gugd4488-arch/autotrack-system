package com.autotrack.client

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * GPS位置上报Worker
 * 负责获取设备位置并上报到服务器
 */
class GpsWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun doWork(): Result {
        return try {
            val location = getLastKnownLocation()
            if (location != null) {
                uploadLocation(location)
                Result.success()
            } else {
                // 如果获取不到位置，返回重试
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    /**
     * 获取最后已知位置
     */
    private fun getLastKnownLocation(): Location? {
        return try {
            // 先尝试GPS
            val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (gpsLocation != null) {
                return gpsLocation
            }
            
            // 如果GPS不可用，尝试网络定位
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 上传位置到服务器
     */
    private fun uploadLocation(location: Location) {
        val prefs = applicationContext.getSharedPreferences("autotrack", Context.MODE_PRIVATE)
        val serverUrl = prefs.getString("server_url", "") ?: ""
        val deviceId = prefs.getString("device_id", "") ?: ""

        if (serverUrl.isEmpty() || deviceId.isEmpty()) {
            return
        }

        val json = JSONObject().apply {
            put("deviceId", deviceId)
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("altitude", location.altitude)
            put("speed", location.speed)
            put("bearing", location.bearing)
            put("accuracy", location.accuracy)
            put("timestamp", location.time)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$serverUrl/api/positions")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Upload failed: ${response.code}")
            }
        }
    }
}
