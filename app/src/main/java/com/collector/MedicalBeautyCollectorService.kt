package com.collector

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.collector.collector.DataExtractor
import com.collector.collector.ElementParser
import com.collector.collector.PageNavigator
import com.collector.data.local.AppDatabase
import com.collector.data.local.entity.HospitalEntity
import com.collector.data.local.entity.ProjectEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * 医美数据采集无障碍服务核心
 * 
 * 功能：
 * 1. 监听美团 App 窗口变化
 * 2. 遍历 UI 元素树
 * 3. 识别医院、项目、价格等元素
 * 4. 执行自动滚动
 * 5. 保存采集数据
 */
@AndroidEntryPoint
class MedicalBeautyCollectorService : AccessibilityService() {

    companion object {
        private const val TAG = "CollectorService"
        
        // 美团包名
        val MEITUAN_PACKAGES = setOf(
            "com.sankuai.meituan",
            "com.sankuai.meituan.business"
        )
        
        // 服务实例（供外部访问）
        var instance: MedicalBeautyCollectorService? = null
            private set
    }

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var elementParser: ElementParser

    @Inject
    lateinit var pageNavigator: PageNavigator

    @Inject
    lateinit var dataExtractor: DataExtractor

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // 采集状态
    var isCollecting = false
        private set
    
    var currentHospital: String? = null
        private set
    
    var collectedProjectCount = 0
        private set

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        
        // 配置服务
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                   AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
        }
        serviceInfo = info
        
        Log.d(TAG, "无障碍服务已连接")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || !isCollecting) return
        
        // 检查是否在美团 App
        val packageName = event.packageName?.toString() ?: return
        if (packageName !in MEITUAN_PACKAGES) return
        
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                Log.d(TAG, "窗口状态变化：${event.className}")
                handleWindowStateChanged(event)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                // 内容变化，可能是滚动加载了新数据
                Log.d(TAG, "窗口内容变化")
                handleContentChanged(event)
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "服务被中断")
        isCollecting = false
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        serviceScope.cancel()
        Log.d(TAG, "服务已销毁")
    }

    /**
     * 开始采集指定医院
     */
    fun startCollection(hospitalName: String) {
        if (isCollecting) {
            Log.w(TAG, "正在采集中，无法启动新任务")
            return
        }
        
        serviceScope.launch {
            try {
                isCollecting = true
                currentHospital = hospitalName
                collectedProjectCount = 0
                
                Log.d(TAG, "开始采集：$hospitalName")
                
                // 1. 检查当前是否在美团
                if (!isInMeituan()) {
                    Log.e(TAG, "当前不在美团 App")
                    // TODO: 提示用户打开美团
                    return@launch
                }
                
                // 2. 搜索医院（如果需要）
                // searchHospital(hospitalName)
                
                // 3. 进入医院页面后开始采集
                collectProjects(hospitalName)
                
            } catch (e: Exception) {
                Log.e(TAG, "采集失败", e)
                isCollecting = false
            }
        }
    }

    /**
     * 停止采集
     */
    fun stopCollection() {
        isCollecting = false
        currentHospital = null
        Log.d(TAG, "采集已停止，共采集 $collectedProjectCount 个项目")
    }

    /**
     * 检查当前是否在美团 App
     */
    fun isInMeituan(): Boolean {
        val event = rootInActiveWindow ?: return false
        val packageName = event.packageName?.toString() ?: return false
        return packageName in MEITUAN_PACKAGES
    }

    /**
     * 获取当前 Activity
     */
    fun getCurrentActivity(): String? {
        val event = rootInActiveWindow ?: return null
        return event.className?.toString()
    }

    /**
     * 处理窗口状态变化
     */
    private fun handleWindowStateChanged(event: AccessibilityEvent) {
        // 检测是否进入医院页面
        if (isCollecting) {
            val className = event.className?.toString() ?: return
            
            // 医院页面特征检测
            if (className.contains("HospitalDetail") || 
                className.contains("ShopDetail") ||
                className.contains("MerchantDetail")) {
                Log.d(TAG, "检测到医院页面")
                // 自动开始采集
            }
        }
    }

    /**
     * 处理内容变化
     */
    private fun handleContentChanged(event: AccessibilityEvent) {
        // 可能是滚动后加载了新内容
        if (isCollecting) {
            // 延迟处理，等待内容加载完成
            serviceScope.launch {
                delay(500)
                // 重新扫描页面元素
            }
        }
    }

    /**
     * 采集项目数据
     */
    private suspend fun collectProjects(hospitalName: String) = withContext(Dispatchers.Main) {
        try {
            // 获取根节点
            val rootNode = rootInActiveWindow ?: return@withContext
            
            // 解析页面元素
            val projects = elementParser.parseProjectList(rootNode)
            
            Log.d(TAG, "解析到 ${projects.size} 个项目")
            
            // 保存到数据库
            saveProjects(hospitalName, projects)
            
            // 滚动加载更多
            if (projects.isNotEmpty()) {
                pageNavigator.scrollDown(this@MedicalBeautyCollectorService)
                delay(2000) // 等待加载
                
                // 递归采集（直到无新数据）
                // collectProjects(hospitalName)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "采集项目失败", e)
        }
    }

    /**
     * 保存项目到数据库
     */
    private suspend fun saveProjects(hospitalName: String, projects: List<ProjectEntity>) {
        withContext(Dispatchers.IO) {
            try {
                // 查找或创建医院记录
                var hospital = database.hospitalDao().findByName(hospitalName)
                if (hospital == null) {
                    hospital = HospitalEntity(
                        name = hospitalName,
                        city = "郑州",
                        status = "collecting",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    val hospitalId = database.hospitalDao().insert(hospital)
                    hospital = hospital.copy(id = hospitalId)
                }
                
                // 保存项目
                projects.forEach { project ->
                    project.hospitalId = hospital.id
                    database.projectDao().insert(project)
                    collectedProjectCount++
                }
                
                Log.d(TAG, "保存 ${projects.size} 个项目成功")
                
            } catch (e: Exception) {
                Log.e(TAG, "保存数据失败", e)
            }
        }
    }

    /**
     * 执行点击操作
     */
    fun performClick(node: AccessibilityNodeInfo): Boolean {
        return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    /**
     * 执行滑动操作
     */
    fun performSwipe(fromX: Int, fromY: Int, toX: Int, toY: Int, durationMs: Int = 300) {
        val path = android.graphics.Path()
        path.moveTo(fromX.toFloat(), fromY.toFloat())
        path.lineTo(toX.toFloat(), toY.toFloat())
        
        // Android 7.0+ 支持手势
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dispatchGesture(path, null, null)
        }
    }
}
