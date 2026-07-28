package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_media_items")
data class MediaItem(
    @PrimaryKey val id: String,
    val title: String,
    val originalTitle: String,
    val type: String, // "TV" or "MOVIE"
    val year: Int,
    val runtime: String, // e.g. "2sa 15dk" or "4 Sezon"
    val rating: Float, // e.g. 8.9f
    val posterUrl: String,
    val backdropUrl: String,
    val overview: String,
    val genres: List<String>,
    val trailerUrl: String,
    val cast: List<CastMember>,
    
    // User collection states
    val watchStatus: String? = null, // "TO_WATCH", "WATCHING", "WATCHED", or null
    val userRating: Int? = null, // 1 to 10
    val userNotes: String = "",
    val watchedEpisodes: Int = 0,
    val totalEpisodes: Int = 0,
    val addedDateMillis: Long = System.currentTimeMillis(),
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)

data class CastMember(
    val name: String,
    val character: String,
    val photoUrl: String
)
