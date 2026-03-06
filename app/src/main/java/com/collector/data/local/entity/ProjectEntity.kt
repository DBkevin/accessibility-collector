package com.collector.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 项目实体
 */
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var hospitalId: Long = 0,      // 所属医院 ID
    val title: String,             // 项目名称
    val rawPrice: String,          // 原始价格文本（如 "¥1280"）
    val price: Double,             // 价格数字
    val sales: Int = 0,            // 销量
    val collectedAt: Long = System.currentTimeMillis() // 采集时间
)
