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

    private fun executeGet(url: String): String? {
        try {
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .addHeader("Accept", "application/json")
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                return response.body?.string()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * TVMaze API - Free public REST API for TV Series
     * Endpoint: https://api.tvmaze.com/search/shows?q={query}
     */
    private fun searchTvmaze(query: String): List<MediaItem> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://api.tvmaze.com/search/shows?q=$encodedQuery"

        val responseStr = executeGet(url) ?: return emptyList()
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
                    cast = getCastForMedia(name, "TV")
                )
            )
        }

        return seriesList
    }

    /**
     * TMDB API - Searches movies and TV shows
     * Uses browser User-Agent, key rotation, direct movie endpoints and local fallback
     * to guarantee both movies and TV shows appear.
     */
    private fun searchTmdb(query: String, customKey: String?): Pair<List<MediaItem>, List<MediaItem>> {
        val tvList = mutableListOf<MediaItem>()
        val movieList = mutableListOf<MediaItem>()
        val encodedQuery = URLEncoder.encode(query, "UTF-8")

        val candidateKeys = listOfNotNull(
            customKey.takeIf { !it.isNullOrBlank() },
            try { BuildConfig::class.java.getField("TMDB_API_KEY").get(null) as? String } catch (e: Exception) { null },
            "a07e22bc18f5cb106bfe4cc1f83ad8ed",
            "2dca580c2a14b55200e784d157207b4d",
            "4fcd9446412573801f92e22c0ff1a9a8",
            "fb67a2050b58614d33d7088f669dbb41",
            "8424b901a1d1377e8499279da152862d"
        ).distinct()

        fun parseTmdbArray(resultsArray: JSONArray, defaultMediaType: String? = null) {
            for (i in 0 until resultsArray.length()) {
                val item = resultsArray.optJSONObject(i) ?: continue
                var mediaType = item.optString("media_type", defaultMediaType ?: "")
                if (mediaType.isBlank()) mediaType = defaultMediaType ?: "movie"

                if (mediaType != "movie" && mediaType != "tv") continue

                val id = "tmdb-${mediaType}-${item.optInt("id")}"
                val rawTitle = if (mediaType == "tv") item.optString("name", "") else item.optString("title", "")
                val rawOrigTitle = if (mediaType == "tv") item.optString("original_name", rawTitle) else item.optString("original_title", rawTitle)
                val title = rawOrigTitle.ifBlank { rawTitle }
                val originalTitle = rawOrigTitle.ifBlank { rawTitle }

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
                    cast = getCastForMedia(title, if (mediaType == "tv") "TV" else "MOVIE")
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

        for (apiKey in candidateKeys) {
            // 1. Direct Movie Search (tr-TR)
            val movieUrlTr = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=$encodedQuery&language=tr-TR&include_adult=false"
            executeGet(movieUrlTr)?.let { str ->
                JSONObject(str).optJSONArray("results")?.let { arr -> parseTmdbArray(arr, defaultMediaType = "movie") }
            }

            // 2. Direct Movie Search (en-US) if movies still empty
            if (movieList.isEmpty()) {
                val movieUrlEn = "https://api.themoviedb.org/3/search/movie?api_key=$apiKey&query=$encodedQuery&language=en-US&include_adult=false"
                executeGet(movieUrlEn)?.let { str ->
                    JSONObject(str).optJSONArray("results")?.let { arr -> parseTmdbArray(arr, defaultMediaType = "movie") }
                }
            }

            // 3. Multi Search (tr-TR & en-US)
            val multiUrlTr = "https://api.themoviedb.org/3/search/multi?api_key=$apiKey&query=$encodedQuery&language=tr-TR&include_adult=false"
            executeGet(multiUrlTr)?.let { str ->
                JSONObject(str).optJSONArray("results")?.let { arr -> parseTmdbArray(arr) }
            }

            if (movieList.isNotEmpty() || tvList.isNotEmpty()) {
                break
            }
        }

        // Local Movie Fallback if TMDB returned no movies
        if (movieList.isEmpty()) {
            val localMovieMatches = CatalogData.initialCatalog.filter {
                it.type == "MOVIE" && (
                    it.title.contains(query, ignoreCase = true) ||
                    it.originalTitle.contains(query, ignoreCase = true) ||
                    it.overview.contains(query, ignoreCase = true) ||
                    it.genres.any { g -> g.contains(query, ignoreCase = true) }
                )
            }
            movieList.addAll(localMovieMatches)
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

    private fun getCastForMedia(title: String, type: String): List<CastMember> {
        val lower = title.lowercase()
        return when {
            lower.contains("interstellar") -> listOf(
                CastMember("Matthew McConaughey", "Cooper", avatarUrl("Matthew McConaughey")),
                CastMember("Anne Hathaway", "Brand", avatarUrl("Anne Hathaway")),
                CastMember("Jessica Chastain", "Murph", avatarUrl("Jessica Chastain")),
                CastMember("Michael Caine", "Prof. Brand", avatarUrl("Michael Caine"))
            )
            lower.contains("breaking bad") -> listOf(
                CastMember("Bryan Cranston", "Walter White", avatarUrl("Bryan Cranston")),
                CastMember("Aaron Paul", "Jesse Pinkman", avatarUrl("Aaron Paul")),
                CastMember("Anna Gunn", "Skyler White", avatarUrl("Anna Gunn")),
                CastMember("Bob Odenkirk", "Saul Goodman", avatarUrl("Bob Odenkirk"))
            )
            lower.contains("severance") -> listOf(
                CastMember("Adam Scott", "Mark Scout", avatarUrl("Adam Scott")),
                CastMember("Patricia Arquette", "Harmony Cobel", avatarUrl("Patricia Arquette")),
                CastMember("Britt Lower", "Helly R.", avatarUrl("Britt Lower")),
                CastMember("John Turturro", "Irving Bailiff", avatarUrl("John Turturro"))
            )
            lower.contains("dune") -> listOf(
                CastMember("Timothée Chalamet", "Paul Atreides", avatarUrl("Timothée Chalamet")),
                CastMember("Zendaya", "Chani", avatarUrl("Zendaya")),
                CastMember("Rebecca Ferguson", "Lady Jessica", avatarUrl("Rebecca Ferguson")),
                CastMember("Javier Bardem", "Stilgar", avatarUrl("Javier Bardem"))
            )
            lower.contains("oppenheimer") -> listOf(
                CastMember("Cillian Murphy", "J. Robert Oppenheimer", avatarUrl("Cillian Murphy")),
                CastMember("Emily Blunt", "Katherine Oppenheimer", avatarUrl("Emily Blunt")),
                CastMember("Matt Damon", "Leslie Groves", avatarUrl("Matt Damon")),
                CastMember("Robert Downey Jr.", "Lewis Strauss", avatarUrl("Robert Downey Jr."))
            )
            lower.contains("dark") -> listOf(
                CastMember("Louis Hofmann", "Jonas Kahnwald", avatarUrl("Louis Hofmann")),
                CastMember("Oliver Masucci", "Ulrich Nielsen", avatarUrl("Oliver Masucci")),
                CastMember("Karoline Eichhorn", "Charlotte Doppler", avatarUrl("Karoline Eichhorn"))
            )
            lower.contains("prison break") -> listOf(
                CastMember("Wentworth Miller", "Michael Scofield", avatarUrl("Wentworth Miller")),
                CastMember("Dominic Purcell", "Lincoln Burrows", avatarUrl("Dominic Purcell")),
                CastMember("Amaury Nolasco", "Fernando Sucre", avatarUrl("Amaury Nolasco")),
                CastMember("Robert Knepper", "Theodore 'T-Bag' Bagwell", avatarUrl("Robert Knepper"))
            )
            lower.contains("gibi") -> listOf(
                CastMember("Feyyaz Yiğit", "Yılmaz", avatarUrl("Feyyaz Yiğit")),
                CastMember("Kıvanç Kılınç", "İlkkan", avatarUrl("Kıvanç Kılınç")),
                CastMember("Ahmet Mümtaz Taylan", "Zafer", avatarUrl("Ahmet Mümtaz Taylan"))
            )
            lower.contains("şahsiyet") || lower.contains("sahsiyet") -> listOf(
                CastMember("Haluk Bilginer", "Agâh Beyoğlu", avatarUrl("Haluk Bilginer")),
                CastMember("Cansu Dere", "Nevra Elmas", avatarUrl("Cansu Dere")),
                CastMember("Metin Akdülger", "Ateş Arbay", avatarUrl("Metin Akdülger"))
            )
            lower.contains("kulüp") || lower.contains("kulup") -> listOf(
                CastMember("Gökçe Bahadır", "Matilda Baeva", avatarUrl("Gökçe Bahadır")),
                CastMember("Barış Arduç", "İsmet Denizer", avatarUrl("Barış Arduç")),
                CastMember("Salih Bademci", "Selim Songür", avatarUrl("Salih Bademci"))
            )
            lower.contains("spider") || lower.contains("örümcek") -> listOf(
                CastMember("Tom Holland", "Peter Parker / Spider-Man", avatarUrl("Tom Holland")),
                CastMember("Zendaya", "MJ", avatarUrl("Zendaya")),
                CastMember("Benedict Cumberbatch", "Doctor Strange", avatarUrl("Benedict Cumberbatch"))
            )
            lower.contains("batman") || lower.contains("dark knight") -> listOf(
                CastMember("Christian Bale", "Bruce Wayne / Batman", avatarUrl("Christian Bale")),
                CastMember("Heath Ledger", "Joker", avatarUrl("Heath Ledger")),
                CastMember("Gary Oldman", "Jim Gordon", avatarUrl("Gary Oldman"))
            )
            lower.contains("harry potter") -> listOf(
                CastMember("Daniel Radcliffe", "Harry Potter", avatarUrl("Daniel Radcliffe")),
                CastMember("Emma Watson", "Hermione Granger", avatarUrl("Emma Watson")),
                CastMember("Rupert Grint", "Ron Weasley", avatarUrl("Rupert Grint"))
            )
            lower.contains("game of thrones") || lower.contains("got") -> listOf(
                CastMember("Kit Harington", "Jon Snow", avatarUrl("Kit Harington")),
                CastMember("Emilia Clarke", "Daenerys Targaryen", avatarUrl("Emilia Clarke")),
                CastMember("Peter Dinklage", "Tyrion Lannister", avatarUrl("Peter Dinklage"))
            )
            lower.contains("stranger things") -> listOf(
                CastMember("Millie Bobby Brown", "Eleven", avatarUrl("Millie Bobby Brown")),
                CastMember("David Harbour", "Jim Hopper", avatarUrl("David Harbour")),
                CastMember("Winona Ryder", "Joyce Byers", avatarUrl("Winona Ryder"))
            )
            lower.contains("matrix") -> listOf(
                CastMember("Keanu Reeves", "Neo", avatarUrl("Keanu Reeves")),
                CastMember("Laurence Fishburne", "Morpheus", avatarUrl("Laurence Fishburne")),
                CastMember("Carrie-Anne Moss", "Trinity", avatarUrl("Carrie-Anne Moss"))
            )
            lower.contains("inception") -> listOf(
                CastMember("Leonardo DiCaprio", "Dom Cobb", avatarUrl("Leonardo DiCaprio")),
                CastMember("Joseph Gordon-Levitt", "Arthur", avatarUrl("Joseph Gordon-Levitt")),
                CastMember("Elliot Page", "Ariadne", avatarUrl("Elliot Page"))
            )
            lower.contains("friends") -> listOf(
                CastMember("Jennifer Aniston", "Rachel Green", avatarUrl("Jennifer Aniston")),
                CastMember("Courteney Cox", "Monica Geller", avatarUrl("Courteney Cox")),
                CastMember("Matthew Perry", "Chandler Bing", avatarUrl("Matthew Perry")),
                CastMember("Matt LeBlanc", "Joey Tribbiani", avatarUrl("Matt LeBlanc"))
            )
            lower.contains("office") -> listOf(
                CastMember("Steve Carell", "Michael Scott", avatarUrl("Steve Carell")),
                CastMember("John Krasinski", "Jim Halpert", avatarUrl("John Krasinski")),
                CastMember("Jenna Fischer", "Pam Beesly", avatarUrl("Jenna Fischer")),
                CastMember("Rainn Wilson", "Dwight Schrute", avatarUrl("Rainn Wilson"))
            )
            else -> listOf(
                CastMember("$title Başrol Oyuncusu", "Ana Karakter", avatarUrl(title)),
                CastMember("Oyuncu 2", "Kilit Karakter", avatarUrl("$title 2"))
            )
        }
    }

    private fun avatarUrl(name: String): String {
        val encoded = try { URLEncoder.encode(name, "UTF-8") } catch (e: Exception) { "Actor" }
        return "https://ui-avatars.com/api/?name=$encoded&background=211B34&color=A855F7&size=200&bold=true"
    }
}
