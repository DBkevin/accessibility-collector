package com.collector.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.collector.data.local.dao.HospitalDao
import com.collector.data.local.dao.ProjectDao
import com.collector.data.local.entity.HospitalEntity
import com.collector.data.local.entity.ProjectEntity

/**
 * 本地数据库
 */
@Database(
    entities = [
        HospitalEntity::class,
        ProjectEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun hospitalDao(): HospitalDao
    abstract fun projectDao(): ProjectDao

    companion object {
        const val DATABASE_NAME = "meituan_collector_db"
    }
}
