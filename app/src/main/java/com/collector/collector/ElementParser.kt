package com.collector.collector

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.collector.data.local.entity.ProjectEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 元素解析器
 * 
 * 功能：遍历 UI 元素树，识别项目名称、价格、销量等
 */
@Singleton
class ElementParser @Inject constructor() {

    companion object {
        private const val TAG = "ElementParser"
    }

    /**
     * 解析项目列表
     */
    fun parseProjectList(rootNode: AccessibilityNodeInfo): List<ProjectEntity> {
        val projects = mutableListOf<ProjectEntity>()
        
        try {
            // 遍历所有 TextView
            val textNodes = findNodesByClassName(rootNode, "android.widget.TextView")
            
            var currentTitle: String? = null
            var currentPrice: String? = null
            var currentSales: String = "0"
            
            for (node in textNodes) {
                val text = node.text?.toString()?.trim() ?: continue
                
                // 检查是否是价格
                val price = extractPrice(text)
                if (price != null) {
                    // 找到价格，保存之前的项目
                    if (currentTitle != null && currentPrice != null) {
                        projects.add(
                            ProjectEntity(
                                title = currentTitle!!,
                                rawPrice = currentPrice!!,
                                price = parsePrice(currentPrice!!),
                                sales = currentSales.toInt()
                            )
                        )
                    }
                    currentPrice = text
                    currentSales = "0"
                }
                // 检查是否是销量
                else if (text.contains("已售")) {
                    currentSales = extractSales(text)
                }
                // 可能是标题
                else if (isProjectTitle(text)) {
                    // 保存之前的项目
                    if (currentTitle != null && currentPrice != null) {
                        projects.add(
                            ProjectEntity(
                                title = currentTitle!!,
                                rawPrice = currentPrice!!,
                                price = parsePrice(currentPrice!!),
                                sales = currentSales.toInt()
                            )
                        )
                    }
                    currentTitle = text
                    currentPrice = null
                    currentSales = "0"
                }
            }
            
            // 处理最后一个项目
            if (currentTitle != null && currentPrice != null) {
                projects.add(
                    ProjectEntity(
                        title = currentTitle!!,
                        rawPrice = currentPrice!!,
                        price = parsePrice(currentPrice!!),
                        sales = currentSales.toInt()
                    )
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "解析项目列表失败", e)
        }
        
        return projects
    }

    /**
     * 按类名查找节点
     */
    private fun findNodesByClassName(root: AccessibilityNodeInfo, className: String): List<AccessibilityNodeInfo> {
        val nodes = mutableListOf<AccessibilityNodeInfo>()
        
        for (i in 0 until root.childCount) {
            val child = root.getChild(i) ?: continue
            findNodesByClassNameRecursive(child, className, nodes)
        }
        
        return nodes
    }

    private fun findNodesByClassNameRecursive(
        node: AccessibilityNodeInfo,
        className: String,
        result: MutableList<AccessibilityNodeInfo>
    ) {
        if (node.className == className) {
            result.add(node)
        }
        
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            findNodesByClassNameRecursive(child, className, result)
        }
    }

    /**
     * 提取价格
     */
    private fun extractPrice(text: String): String? {
        // 匹配 "¥1280" 或 "1280"
        val regex = Regex("""¥?\s*(\d+\.?\d*)""")
        return regex.find(text)?.groupValues?.get(1)
    }

    /**
     * 解析价格数字
     */
    private fun parsePrice(priceText: String): Double {
        return priceText.replace("¥", "").replace(" ", "").toDoubleOrNull() ?: 0.0
    }

    /**
     * 提取销量
     */
    private fun extractSales(text: String): String {
        // 匹配 "已售 1234" 或 "1234 份"
        val regex = Regex("""已售\s*(\d+)""")
        return regex.find(text)?.groupValues?.get(1) ?: "0"
    }

    /**
     * 判断是否是项目标题
     */
    private fun isProjectTitle(text: String): Boolean {
        // 标题特征：
        // 1. 长度 5-60 字符
        // 2. 不包含价格符号
        // 3. 不包含"已售"
        // 4. 不是纯数字
        
        if (text.length < 5 || text.length > 60) return false
        if (text.contains("¥")) return false
        if (text.contains("已售")) return false
        if (text.matches(Regex("""^\d+$"""))) return false
        
        // 排除一些非项目文本
        val excludeKeywords = listOf("配送", "地址", "时间", "公告", "活动", "规则", "说明", "客服", "预约")
        if (excludeKeywords.any { text.contains(it) }) return false
        
        return true
    }
}
