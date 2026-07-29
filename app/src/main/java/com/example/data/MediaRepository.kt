package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MediaRepository(private val mediaDao: MediaDao) {

    // Returns user collection saved in Room Database
    val userSavedItems: Flow<List<MediaItem>> = mediaDao.getAllUserItems()

    // Returns merged catalog: Seed catalog items overlaid with any user Room states
    fun getFullCatalog(): Flow<List<MediaItem>> {
        return mediaDao.getAllUserItems().map { savedList ->
            val savedMap = savedList.associateBy { it.id }
            CatalogData.initialCatalog.map { seedItem ->
                savedMap[seedItem.id] ?: seedItem
            } + savedList.filter { savedItem -> CatalogData.initialCatalog.none { it.id == savedItem.id } }
        }
    }

    suspend fun saveItemToCollection(item: MediaItem) {
        val updated = item.copy(lastUpdatedMillis = System.currentTimeMillis())
        mediaDao.insertOrUpdate(updated)
    }

    suspend fun saveItemsToCollection(items: List<MediaItem>) {
        val now = System.currentTimeMillis()
        val updated = items.map { it.copy(lastUpdatedMillis = now) }
        mediaDao.insertAll(updated)
    }

    suspend fun removeFromCollection(item: MediaItem) {
        mediaDao.deleteById(item.id)
    }

    suspend fun updateEpisodes(item: MediaItem, watchedEpisodes: Int) {
        val newStatus = if (watchedEpisodes >= item.totalEpisodes && item.totalEpisodes > 0) "WATCHED" else "WATCHING"
        val updated = item.copy(
            watchedEpisodes = watchedEpisodes,
            watchStatus = newStatus,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        mediaDao.insertOrUpdate(updated)
    }

    suspend fun updateRatingAndNotes(item: MediaItem, rating: Int?, notes: String) {
        val updated = item.copy(
            userRating = rating,
            userNotes = notes,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        mediaDao.insertOrUpdate(updated)
    }

    suspend fun updateWatchStatus(item: MediaItem, status: String?) {
        val updated = item.copy(
            watchStatus = status,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        mediaDao.insertOrUpdate(updated)
    }
}
