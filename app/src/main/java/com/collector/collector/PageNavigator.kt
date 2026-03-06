package com.collector.collector

import android.accessibilityservice.AccessibilityService
import android.graphics.Path
import android.os.Build
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 页面导航器
 * 
 * 功能：执行滚动、点击等手势操作
 */
@Singleton
class PageNavigator @Inject constructor() {

    companion object {
        private const val TAG = "PageNavigator"
    }

    /**
     * 向下滑动（加载更多）
     */
    fun scrollDown(service: AccessibilityService, scale: Float = 0.7f) {
        try {
            val displayMetrics = service.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels
            
            val startX = screenWidth / 2
            val startY = (screenHeight * 0.7).toInt()
            val endX = screenWidth / 2
            val endY = (screenHeight * 0.3).toInt()
            
            Log.d(TAG, "向下滑动：($startX, $startY) -> ($endX, $endY)")
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val path = Path()
                path.moveTo(startX.toFloat(), startY.toFloat())
                path.lineTo(endX.toFloat(), endY.toFloat())
                service.dispatchGesture(path, null, null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "滑动失败", e)
        }
    }

    /**
     * 向上滑动
     */
    fun scrollUp(service: AccessibilityService, scale: Float = 0.7f) {
        try {
            val displayMetrics = service.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels
            
            val startX = screenWidth / 2
            val startY = (screenHeight * 0.3).toInt()
            val endX = screenWidth / 2
            val endY = (screenHeight * 0.7).toInt()
            
            Log.d(TAG, "向上滑动：($startX, $startY) -> ($endX, $endY)")
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val path = Path()
                path.moveTo(startX.toFloat(), startY.toFloat())
                path.lineTo(endX.toFloat(), endY.toFloat())
                service.dispatchGesture(path, null, null)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "滑动失败", e)
        }
    }

    /**
     * 滚动到顶部
     */
    fun scrollToTop(service: AccessibilityService, times: Int = 10) {
        Log.d(TAG, "滚动到顶部，次数：$times")
        
        repeat(times) {
            scrollUp(service)
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                Log.e(TAG, "等待中断", e)
            }
        }
    }
}
