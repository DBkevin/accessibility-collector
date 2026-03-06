package com.collector.data.local.dao

import androidx.room.*
import com.collector.data.local.entity.HospitalEntity

/**
 * 医院数据访问对象
 */
@Dao
interface HospitalDao {

    @Insert
    suspend fun insert(hospital: HospitalEntity): Long

    @Update
    suspend fun update(hospital: HospitalEntity)

    @Delete
    suspend fun delete(hospital: HospitalEntity)

    @Query("SELECT * FROM hospitals WHERE id = :id")
    suspend fun getById(id: Long): HospitalEntity?

    @Query("SELECT * FROM hospitals WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): HospitalEntity?

    @Query("SELECT * FROM hospitals WHERE city = :city")
    suspend fun getByCity(city: String): List<HospitalEntity>

    @Query("SELECT * FROM hospitals ORDER BY updatedAt DESC")
    suspend fun getAll(): List<HospitalEntity>

    @Query("SELECT * FROM hospitals WHERE status = :status")
    suspend fun getByStatus(status: String): List<HospitalEntity>

    @Query("UPDATE hospitals SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, updatedAt: Long = System.currentTimeMillis())
}
