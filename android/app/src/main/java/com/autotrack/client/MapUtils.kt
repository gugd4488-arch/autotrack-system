package com.autotrack.client

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

/**
 * 地图工具类
 * 支持Google Maps和其他地图服务
 */
class MapUtils(private val context: Context) {

    companion object {
        private const val TAG = "MapUtils"
        private const val MARKER_COLOR_ONLINE = 120f      // 绿色
        private const val MARKER_COLOR_OFFLINE = 0f       // 红色
    }

    /**
     * 在Google Map上显示单个设备位置
     */
    fun showDeviceLocation(
        googleMap: GoogleMap,
        deviceId: String,
        latitude: Double,
        longitude: Double,
        deviceName: String,
        isOnline: Boolean
    ) {
        val location = LatLng(latitude, longitude)
        
        val markerColor = if (isOnline) MARKER_COLOR_ONLINE else MARKER_COLOR_OFFLINE
        
        val marker = MarkerOptions()
            .position(location)
            .title(deviceName)
            .snippet("设备ID: $deviceId")
        
        googleMap.addMarker(marker)
        
        // 移动相机到该位置
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(location, 15f)
        )
        
        Log.d(TAG, "Device marked: $deviceName at $latitude, $longitude")
    }

    /**
     * 在Google Map上显示多个设备位置
     */
    fun showMultipleDevices(
        googleMap: GoogleMap,
        devices: List<DeviceLocation>
    ) {
        if (devices.isEmpty()) {
            Log.w(TAG, "No devices to show on map")
            return
        }

        googleMap.clear()
        
        devices.forEach { device ->
            val location = LatLng(device.latitude, device.longitude)
            val markerColor = if (device.isOnline) MARKER_COLOR_ONLINE else MARKER_COLOR_OFFLINE
            
            val marker = MarkerOptions()
                .position(location)
                .title(device.name)
                .snippet("状态: ${if (device.isOnline) "在线" else "离线"}")
            
            googleMap.addMarker(marker)
        }

        // 如果只有一个设备，直接定位到该设备
        if (devices.size == 1) {
            val firstDevice = devices[0]
            val location = LatLng(firstDevice.latitude, firstDevice.longitude)
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(location, 15f)
            )
        } else {
            // 多个设备时，自动适配所有设备在屏幕内
            val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder().apply {
                devices.forEach { device ->
                    include(LatLng(device.latitude, device.longitude))
                }
            }.build()
            
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, 100)
            )
        }
        
        Log.d(TAG, "Displayed ${devices.size} devices on map")
    }

    /**
     * 在地图上绘制设备轨迹
     */
    fun drawTrajectory(
        googleMap: GoogleMap,
        positions: List<PositionData>
    ) {
        if (positions.size < 2) {
            Log.w(TAG, "Not enough positions to draw trajectory")
            return
        }

        googleMap.clear()
        
        val polylineOptions = PolylineOptions()
            .color(Color.BLUE)
            .width(5f)

        positions.forEach { position ->
            polylineOptions.add(LatLng(position.latitude, position.longitude))
        }

        googleMap.addPolyline(polylineOptions)

        // 标记起点
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(positions.first().latitude, positions.first().longitude))
                .title("起点")
        )

        // 标记终点
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(positions.last().latitude, positions.last().longitude))
                .title("终点")
        )

        // 自动适配轨迹
        val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder().apply {
            positions.forEach { position ->
                include(LatLng(position.latitude, position.longitude))
            }
        }.build()

        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, 100)
        )

        Log.d(TAG, "Trajectory drawn with ${positions.size} positions")
    }

    /**
     * 计算两点间的距离（单位：米）
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    /**
     * 设备位置数据类
     */
    data class DeviceLocation(
        val deviceId: String,
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val isOnline: Boolean,
        val lastUpdate: Long = System.currentTimeMillis()
    )

    /**
     * 位置数据类
     */
    data class PositionData(
        val id: Long,
        val deviceId: String,
        val latitude: Double,
        val longitude: Double,
        val altitude: Double,
        val speed: Float,
        val accuracy: Float,
        val timestamp: Long
    )
}
