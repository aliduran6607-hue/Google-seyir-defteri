package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM user_media_items ORDER BY lastUpdatedMillis DESC")
    fun getAllUserItems(): Flow<List<MediaItem>>

    @Query("SELECT * FROM user_media_items WHERE id = :id")
    suspend fun getItemById(id: String): MediaItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: MediaItem)

    @Delete
    suspend fun delete(item: MediaItem)

    @Query("DELETE FROM user_media_items WHERE id = :id")
    suspend fun deleteById(id: String)
}
