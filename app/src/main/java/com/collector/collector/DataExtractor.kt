package com.collector.collector

import android.util.Log
import com.collector.data.local.entity.ProjectEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 数据提取器
 * 
 * 功能：从 UI 元素中提取和清洗数据
 */
@Singleton
class DataExtractor @Inject constructor() {

    companion object {
        private const val TAG = "DataExtractor"
    }

    /**
     * 提取并清洗项目数据
     */
    fun extractProject(titleNode: android.view.accessibility.AccessibilityNodeInfo?): ProjectEntity? {
        val title = titleNode?.text?.toString()?.trim() ?: return null
        
        // 查找价格节点
        val priceInfo = findPriceInSiblings(titleNode)
        
        return ProjectEntity(
            title = title,
            rawPrice = priceInfo?.raw ?: "0",
            price = priceInfo?.value ?: 0.0,
            sales = priceInfo?.sales ?: 0
        )
    }

    /**
     * 在兄弟节点中查找价格
     */
    private fun findPriceInSiblings(node: android.view.accessibility.AccessibilityNodeInfo): PriceInfo? {
        val parent = node.parent ?: return null
        
        for (i in 0 until parent.childCount) {
            val sibling = parent.getChild(i) ?: continue
            
            try {
                val text = sibling.text?.toString()?.trim() ?: continue
                
                // 检查是否是价格
                val price = extractPrice(text)
                if (price != null) {
                    // 继续查找销量
                    val sales = findSalesInSiblings(sibling)
                    return PriceInfo(price, text, sales)
                }
            } finally {
                sibling.recycle()
            }
        }
        
        return null
    }

    /**
     * 查找销量
     */
    private fun findSalesInSiblings(node: android.view.accessibility.AccessibilityNodeInfo): Int {
        val parent = node.parent ?: return 0
        
        for (i in 0 until parent.childCount) {
            val sibling = parent.getChild(i) ?: continue
            
            try {
                val text = sibling.text?.toString()?.trim() ?: continue
                
                if (text.contains("已售")) {
                    return extractSales(text)
                }
            } finally {
                sibling.recycle()
            }
        }
        
        return 0
    }

    /**
     * 提取价格数字
     */
    private fun extractPrice(text: String): Double? {
        val regex = Regex("""¥?\s*(\d+\.?\d*)""")
        return regex.find(text)?.groupValues?.get(1)?.toDoubleOrNull()
    }

    /**
     * 提取销量数字
     */
    private fun extractSales(text: String): Int {
        val regex = Regex("""已售\s*(\d+)""")
        return regex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    }

    /**
     * 价格信息
     */
    data class PriceInfo(
        val value: Double,      // 价格数字
        val raw: String,        // 原始文本
        val sales: Int          // 销量
    )
}
