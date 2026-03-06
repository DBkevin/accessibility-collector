package com.collector.util

import android.content.Context
import android.widget.Toast
import com.collector.data.local.entity.ProjectEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * CSV 导出工具
 */
class CsvExporter(private val context: Context) {

    companion object {
        private const val EXPORT_DIR = "export"
        private const val FILE_PREFIX = "projects_"
    }

    /**
     * 导出项目数据到 CSV
     */
    suspend fun exportProjects(projects: List<ProjectEntity>, hospitalName: String? = null): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 创建导出目录
                val exportDir = File(context.getExternalFilesDir(null), EXPORT_DIR)
                if (!exportDir.exists()) {
                    exportDir.mkdirs()
                }

                // 生成文件名
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
                val fileName = if (hospitalName != null) {
                    "${hospitalName}_$timestamp.csv"
                } else {
                    "${FILE_PREFIX}$timestamp.csv"
                }

                val csvFile = File(exportDir, fileName)

                // 写入 CSV 内容
                val bom = "\uFEFF" // BOM for Excel Chinese support
                val header = "医院名称，项目名称，价格，销量，采集时间\n"
                
                val rows = projects.joinToString("\n") { project ->
                    "\"${project.hospitalId}\",\"${escapeCsv(project.title)}\",${project.price},${project.sales},\"${formatTime(project.collectedAt)}\""
                }

                csvFile.writeText(bom + header + rows)

                csvFile.absolutePath

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 导出所有项目
     */
    suspend fun exportAllProjects(
        hospitals: List<Pair<Long, String>>,
        projects: List<ProjectEntity>
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                val exportDir = File(context.getExternalFilesDir(null), EXPORT_DIR)
                if (!exportDir.exists()) {
                    exportDir.mkdirs()
                }

                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
                val csvFile = File(exportDir, "all_hospitals_$timestamp.csv")

                val bom = "\uFEFF"
                val header = "医院 ID，医院名称，项目名称，价格，销量，采集时间\n"

                val hospitalMap = hospitals.associate { it.first to it.second }

                val rows = projects.joinToString("\n") { project ->
                    val hospitalName = hospitalMap[project.hospitalId] ?: "未知"
                    "\"${project.hospitalId}\",\"${escapeCsv(hospitalName)}\",\"${escapeCsv(project.title)}\",${project.price},${project.sales},\"${formatTime(project.collectedAt)}\""
                }

                csvFile.writeText(bom + header + rows)
                csvFile.absolutePath

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * CSV 转义
     */
    private fun escapeCsv(text: String): String {
        return text.replace("\"", "\"\"")
    }

    /**
     * 格式化时间
     */
    private fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date(timestamp))
    }

    /**
     * 显示导出成功提示
     */
    fun showExportSuccess(path: String) {
        val message = "导出成功\n$path"
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 显示导出失败提示
     */
    fun showExportFailure() {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "导出失败", Toast.LENGTH_SHORT).show()
        }
    }
}
