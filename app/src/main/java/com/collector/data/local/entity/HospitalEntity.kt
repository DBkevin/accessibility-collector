package com.collector.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 医院实体
 */
@Entity(tableName = "hospitals")
data class HospitalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,              // 医院名称
    val city: String = "郑州",      // 城市
    val status: String = "pending",// 采集状态：pending, collecting, completed, failed
    val createdAt: Long,           // 创建时间
    val updatedAt: Long            // 更新时间
)
