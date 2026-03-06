package com.collector.util

/**
 * 采集配置
 */
object CollectorConfig {

    /**
     * 最多滚动次数
     */
    const val MAX_SCROLL_COUNT = 50

    /**
     * 滚动后等待时间 (毫秒)
     */
    const val SCROLL_DELAY_MS = 2000L

    /**
     * 无新增停止次数
     */
    const val NO_NEW_STOP_COUNT = 3

    /**
     * 启用随机延迟 (模拟人工)
     */
    const val RANDOM_DELAY_ENABLED = true

    /**
     * 最小延迟 (毫秒)
     */
    const val MIN_DELAY_MS = 1500L

    /**
     * 最大延迟 (毫秒)
     */
    const val MAX_DELAY_MS = 3000L

    /**
     * 美团包名
     */
    val MEITUAN_PACKAGES = setOf(
        "com.sankuai.meituan",
        "com.sankuai.meituan.business"
    )

    /**
     * 默认医院列表
     */
    val DEFAULT_HOSPITALS = listOf(
        "郑州美莱",
        "郑州天后",
        "郑州辰星",
        "郑州一科"
    )

    /**
     * 获取随机延迟
     */
    fun getRandomDelay(): Long {
        if (!RANDOM_DELAY_ENABLED) {
            return SCROLL_DELAY_MS
        }
        return (MIN_DELAY_MS..MAX_DELAY_MS).random()
    }
}
