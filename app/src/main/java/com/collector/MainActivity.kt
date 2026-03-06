package com.collector

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.collector.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主界面
 * 
 * 功能：
 * 1. 显示服务状态
 * 2. 引导开启无障碍权限
 * 3. 开始/停止采集
 * 4. 查看采集数据
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        checkAccessibilityPermission()
    }

    override fun onResume() {
        super.onResume()
        // 每次返回时检查权限状态
        checkAccessibilityPermission()
    }

    private fun setupUI() {
        // 开启无障碍服务按钮
        binding.btnEnableAccessibility.setOnClickListener {
            openAccessibilitySettings()
        }

        // 开始采集按钮
        binding.btnStartCollection.setOnClickListener {
            startCollection()
        }

        // 停止采集按钮
        binding.btnStopCollection.setOnClickListener {
            stopCollection()
        }

        // 查看数据按钮
        binding.btnViewData.setOnClickListener {
            viewData()
        }

        // 初始状态
        updateUI()
    }

    private fun checkAccessibilityPermission() {
        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        
        val isServiceEnabled = enabledServices.any { 
            it.resolveInfo.serviceInfo.packageName == packageName 
        }

        if (isServiceEnabled) {
            binding.tvPermissionStatus.text = "✅ 无障碍服务已开启"
            binding.btnEnableAccessibility.isEnabled = false
            binding.btnEnableAccessibility.text = "已开启"
        } else {
            binding.tvPermissionStatus.text = "❌ 无障碍服务未开启"
            binding.btnEnableAccessibility.isEnabled = true
            binding.btnEnableAccessibility.text = "去开启"
        }

        updateUI()
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
        Toast.makeText(this, "请找到 MeituanCollector 并开启服务", Toast.LENGTH_LONG).show()
    }

    private fun startCollection() {
        // TODO: 打开采集配置对话框
        Toast.makeText(this, "请打开美团 App 并进入医院页面", Toast.LENGTH_LONG).show()
        
        // 打开美团
        try {
            val intent = packageManager.getLaunchIntentForPackage("com.sankuai.meituan")
            if (intent != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "未安装美团 App", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "打开美团失败：${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopCollection() {
        MedicalBeautyCollectorService.instance?.stopCollection()
        Toast.makeText(this, "采集已停止", Toast.LENGTH_SHORT).show()
        updateUI()
    }

    private fun viewData() {
        // TODO: 打开数据页面
        Toast.makeText(this, "数据页面开发中", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        val service = MedicalBeautyCollectorService.instance
        val isCollecting = service?.isCollecting == true

        binding.btnStartCollection.isEnabled = !isCollecting
        binding.btnStopCollection.isEnabled = isCollecting

        if (isCollecting) {
            binding.tvCollectionStatus.text = "🟢 采集中..."
            binding.tvProjectCount.text = "已采集：${service.collectedProjectCount} 个项目"
        } else {
            binding.tvCollectionStatus.text = "⚪ 未采集"
            binding.tvProjectCount.text = "点击开始采集"
        }
    }
}
