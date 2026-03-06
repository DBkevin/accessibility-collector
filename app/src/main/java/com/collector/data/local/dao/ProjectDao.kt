package com.collector.data.local.dao

import androidx.room.*
import com.collector.data.local.entity.ProjectEntity

/**
 * 项目数据访问对象
 */
@Dao
interface ProjectDao {

    @Insert
    suspend fun insert(project: ProjectEntity): Long

    @Insert
    suspend fun insertAll(projects: List<ProjectEntity>): List<Long>

    @Update
    suspend fun update(project: ProjectEntity)

    @Delete
    suspend fun delete(project: ProjectEntity)

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getById(id: Long): ProjectEntity?

    @Query("SELECT * FROM projects WHERE hospitalId = :hospitalId ORDER BY collectedAt DESC")
    suspend fun getByHospitalId(hospitalId: Long): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE hospitalId = :hospitalId AND title = :title LIMIT 1")
    suspend fun getByTitle(hospitalId: Long, title: String): ProjectEntity?

    @Query("SELECT * FROM projects ORDER BY collectedAt DESC")
    suspend fun getAll(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE price < :maxPrice")
    suspend fun getByMaxPrice(maxPrice: Double): List<ProjectEntity>

    @Query("SELECT COUNT(*) FROM projects WHERE hospitalId = :hospitalId")
    suspend fun countByHospitalId(hospitalId: Long): Int

    @Query("DELETE FROM projects WHERE hospitalId = :hospitalId")
    suspend fun deleteByHospitalId(hospitalId: Long)

    @Query("SELECT * FROM projects WHERE title LIKE '%' || :keyword || '%' ORDER BY collectedAt DESC")
    suspend fun searchByKeyword(keyword: String): List<ProjectEntity>
}
