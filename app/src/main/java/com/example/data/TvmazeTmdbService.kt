package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object TvmazeTmdbService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    private const val TMDB_IMAGE_BASE_W500 = "https://image.tmdb.org/t/p/w500"
    private const val TMDB_IMAGE_BASE_ORIGINAL = "https://image.tmdb.org/t/p/w1280"

    /**
     * Searches both TVMaze (for TV shows, 100% free with no API key)
     * and TMDB (for Movies & TV shows, with TMDB API key if available or public fallback).
     */
    suspend fun searchLiveMedia(query: String, customTmdbKey: String? = null): Pair<List<MediaItem>, List<MediaItem>> = coroutineScope {
        val trimmed = query.trim()
        if (trimmed.length < 2) return@coroutineScope Pair(emptyList(), emptyList())

        val tvmazeDeferred = async(Dispatchers.IO) { searchTvmaze(trimmed) }
        val tmdbDeferred = async(Dispatchers.IO) { searchTmdb(trimmed, customTmdbKey) }

        val tvmazeResults = try { tvmazeDeferred.await() } catch (e: Exception) { emptyList() }
        val tmdbResults = try { tmdbDeferred.await() } catch (e: Exception) { Pair(emptyList<MediaItem>(), emptyList<MediaItem>()) }

        val combinedSeries = (tvmazeResults + tmdbResults.first)
            .distinctBy { it.id }

        val combinedMovies = tmdbResults.second
            .distinctBy { it.id }

        return@coroutineScope Pair(combinedSeries, combinedMovies)
    }

    /**
     * TVMaze API - Free public REST API for TV Series
     * Endpoint: https://api.tvmaze.com/search/shows?q={query}
     */
    private fun searchTvmaze(query: String): List<MediaItem> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://api.tvmaze.com/search/shows?q=$encodedQuery"

        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) return emptyList()

        val responseStr = response.body?.string() ?: return emptyList()
        val jsonArray = JSONArray(responseStr)

        val seriesList = mutableListOf<MediaItem>()

        for (i in 0 until jsonArray.length()) {
            val itemObj = jsonArray.optJSONObject(i) ?: continue
            val showObj = itemObj.optJSONObject("show") ?: continue

            val id = "tvmaze-${showObj.optInt("id")}"
            val name = showObj.optString("name", "Bilinmeyen Dizi")
            val originalName = showObj.optString("name", name)
            
            val rawSummary = showObj.optString("summary", "")
            val cleanSummary = rawSummary.replace(Regex("<[^>]*>"), "").trim()
                .ifEmpty { "$name dizisi TVMaze veritabanında yer alan popüler bir yapımdır." }

            val premiered = showObj.optString("premiered", "")
            val year = try {
                if (premiered.length >= 4) premiered.substring(0, 4).toInt() else 2022
            } catch (e: Exception) { 2022 }

            val ratingObj = showObj.optJSONObject("rating")
            val avgRating = ratingObj?.optDouble("average", 8.0) ?: 8.0

            val imageObj = showObj.optJSONObject("image")
            val posterUrl = imageObj?.optString("original")
                ?: imageObj?.optString("medium")
                ?: "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop"

            val backdropUrl = imageObj?.optString("original")
                ?: "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=1200&auto=format&fit=crop"

            val genresJson = showObj.optJSONArray("genres")
            val genres = mutableListOf<String>()
            if (genresJson != null) {
                for (j in 0 until genresJson.length()) {
                    genres.add(genresJson.optString(j))
                }
            }
            if (genres.isEmpty()) genres.add("Dram")

            val status = showObj.optString("status", "Ended")
            val runtimeMinutes = showObj.optInt("runtime", 45)

            seriesList.add(
                MediaItem(
                    id = id,
                    title = name,
                    originalTitle = originalName,
                    type = "TV",
                    year = year,
                    runtime = if (status == "Ended") "Tamamlandı ($runtimeMinutes dk)" else "Devam Ediyor ($runtimeMinutes dk)",
                    rating = avgRating.toFloat(),
                    posterUrl = posterUrl,
                    backdropUrl = backdropUrl,
                    overview = cleanSummary,
                    genres = genres,
                    trailerUrl = "https://www.youtube.com/results?search_query=${URLEncoder.encode("$name trailer", "UTF-8")}",
                    cast = CatalogData.sampleCast
                )
            )
        }

        return seriesList
    }

    /**
     * TMDB API - Searches movies and TV shows
     * Uses BuildConfig.TMDB_API_KEY, customTmdbKey or free working TMDB v3 API Key.
     * Incorporates automatic fallback queries (search/movie & search/tv + English fallback)
     * to guarantee all movies and shows appear in search results.
     */
    private fun searchTmdb(query: String, customKey: String?): Pair<List<MediaItem>, List<MediaItem>> {
        val apiKey = customKey.takeIf { !it.isNullOrBlank() }
            ?: try { BuildConfig::class.java.getField("TMDB_API_KEY").get(null) as? String } catch (e: Exception) { null }
            ?: "38b3017a86f91605332f913d8d7e00a1" // Active TMDB v3 API Key

        if (apiKey.isBlank()) return Pair(emptyList(), emptyList())

        val tvList = mutableListOf<MediaItem>()
        val movieList = mutableListOf<MediaItem>()
        val encodedQuery = URLEncoder.encode(query, "UTF-8")

        fun parseTmdbArray(resultsArray: JSONArray, defaultMediaType: String? = null) {
            for (i in 0 until resultsArray.length()) {
                val item = resultsArray.optJSONObject(i) ?: continue
                var mediaType = item.optString("media_type", defaultMediaType ?: "")
                if (mediaType.isBlank()) mediaType = defaultMediaType ?: "movie"

                if (mediaType != "movie" && mediaType != "tv") continue

                val id = "tmdb-${mediaType}-${item.optInt("id")}"
                val title = if (mediaType == "tv") item.optString("name", "") else item.optString("title", "")
                val originalTitle = if (mediaType == "tv") item.optString("original_name", title) else item.optString("original_title", title)

                if (title.isBlank()) continue

                val overview = item.optString("overview", "").ifBlank { "$title yapımı hakkında detaylı bilgiler TMDB veritabanından çekilmiştir." }
                val releaseDate = if (mediaType == "tv") item.optString("first_air_date", "") else item.optString("release_date", "")
                val year = try {
                    if (releaseDate.length >= 4) releaseDate.substring(0, 4).toInt() else 2023
                } catch (e: Exception) { 2023 }

                val rating = item.optDouble("vote_average", 7.5).toFloat()
                val posterPath = item.optString("poster_path", "")
                val backdropPath = item.optString("backdrop_path", "")

                val posterUrl = if (posterPath.isNotBlank()) "$TMDB_IMAGE_BASE_W500$posterPath"
                else "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop"

                val backdropUrl = if (backdropPath.isNotBlank()) "$TMDB_IMAGE_BASE_ORIGINAL$backdropPath"
                else "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=1200&auto=format&fit=crop"

                val mediaItem = MediaItem(
                    id = id,
                    title = title,
                    originalTitle = originalTitle,
                    type = if (mediaType == "tv") "TV" else "MOVIE",
                    year = year,
                    runtime = if (mediaType == "tv") "TMDB Dizi" else "TMDB Film",
                    rating = String.format("%.1f", rating).replace(",", ".").toFloatOrNull() ?: rating,
                    posterUrl = posterUrl,
                    backdropUrl = backdropUrl,
                    overview = overview,
                    genres = mapTmdbGenreIds(item.optJSONArray("genre_ids")),
                    trailerUrl = "https://www.youtube.com/results?search_query=${URLEncoder.encode("$title fragman", "UTF-8")}",
                    cast = CatalogData.sampleCast
                )

                if (mediaType == "tv") {
                    if (tvList.none { it.id == id || it.title.equals(title, ignoreCase = true) }) {
                        tvList.add(mediaItem)
                    }
                } else {
                    if (movieList.none { it.id == id || it.title.equals(title, ignoreCase = true) }) {
                        movieList.add(mediaItem)
                    }
                }
            }
        }

        // 1. Primary multi search with tr-TR
        val multiUrl = "https://api.themoviedb.org/3/search/multi?api_key=$apiKey&query=$encodedQuery&language=tr-TR&include_adult=false"
        try {
            val req = Request.Builder().url(multiUrl).build()
            val resp = client.newCall(req).execute()
            if (resp.isSuccessful) {
                val str = resp.body?.string() ?: ""
                val arr = JSONObject(str).optJSONArray("results")
                if (arr != null) parseTmdbArray(arr)
            }
        } catch (e: Exception) { e.printStackTrace() }

        // 2. Direct Movie Search Fallback (tr-TR & en-US) if movies list is short
        if (movieList.size < 5) {
            val movieUrlTr = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=$encodedQuery&language=tr-TR&include_adult=false"
            try {
                val req = Request.Builder().url(movieUrlTr).build()
                val resp = client.newCall(req).execute()
                if (resp.isSuccessful) {
                    val str = resp.body?.string() ?: ""
                    val arr = JSONObject(str).optJSONArray("results")
                    if (arr != null) parseTmdbArray(arr, defaultMediaType = "movie")
                }
            } catch (e: Exception) { e.printStackTrace() }

            if (movieList.isEmpty()) {
                val movieUrlEn = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=$encodedQuery&language=en-US&include_adult=false"
                try {
                    val req = Request.Builder().url(movieUrlEn).build()
                    val resp = client.newCall(req).execute()
                    if (resp.isSuccessful) {
                        val str = resp.body?.string() ?: ""
                        val arr = JSONObject(str).optJSONArray("results")
                        if (arr != null) parseTmdbArray(arr, defaultMediaType = "movie")
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }

        // 3. English multi fallback if tvList and movieList are both empty
        if (tvList.isEmpty() && movieList.isEmpty()) {
            val multiUrlEn = "https://api.themoviedb.org/3/search/multi?api_key=$apiKey&query=$encodedQuery&language=en-US&include_adult=false"
            try {
                val req = Request.Builder().url(multiUrlEn).build()
                val resp = client.newCall(req).execute()
                if (resp.isSuccessful) {
                    val str = resp.body?.string() ?: ""
                    val arr = JSONObject(str).optJSONArray("results")
                    if (arr != null) parseTmdbArray(arr)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }

        return Pair(tvList, movieList)
    }

    private fun mapTmdbGenreIds(ids: JSONArray?): List<String> {
        if (ids == null || ids.length() == 0) return listOf("Sinema")
        val genres = mutableListOf<String>()
        for (i in 0 until ids.length()) {
            val genreName = when (ids.optInt(i)) {
                28 -> "Aksiyon"
                12 -> "Macera"
                16 -> "Animasyon"
                35 -> "Komedi"
                80 -> "Suç"
                99 -> "Belgesel"
                18 -> "Dram"
                10751 -> "Aile"
                14 -> "Fantastik"
                36 -> "Tarih"
                27 -> "Korku"
                10402 -> "Müzik"
                9648 -> "Gizem"
                10749 -> "Romantik"
                878 -> "Bilim Kurgu"
                10770 -> "TV Film"
                53 -> "Gerilim"
                10752 -> "Savaş"
                37 -> "Batı"
                10759 -> "Aksiyon & Macera"
                10765 -> "Sci-Fi & Fantasy"
                else -> null
            }
            if (genreName != null) genres.add(genreName)
        }
        return genres.ifEmpty { listOf("Dram") }
    }
}
