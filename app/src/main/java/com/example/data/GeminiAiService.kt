package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    suspend fun findAiContent(prompt: String): Pair<List<MediaItem>, List<MediaItem>> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateFallbackAiResults(prompt)
        }

        try {
            val jsonPrompt = """
                You are a movie and TV show recommendation system for Turkish cinephiles.
                User theme query: "$prompt"
                Return a JSON object with two arrays:
                "series": array of 8 recommended TV series
                "movies": array of 8 recommended Movies

                Each item inside series or movies must have these exact JSON fields:
                "id": string (unique slug e.g. "shawshank-movie")
                "title": string (Turkish or widely known title)
                "originalTitle": string
                "type": string ("TV" or "MOVIE")
                "year": integer
                "runtime": string (e.g. "2sa 22dk" or "4 Sezon")
                "rating": float (e.g. 8.8)
                "posterUrl": string (a high quality unsplash image URL like "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop")
                "backdropUrl": string (a high quality unsplash image URL)
                "overview": string (detailed plot summary in Turkish)
                "genres": array of strings (e.g. ["Suç", "Dram"])

                ONLY return valid raw JSON without markdown codeblock backticks if possible, or inside ```json.
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", jsonPrompt)
                            })
                        })
                    })
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext generateFallbackAiResults(prompt)
            }

            val responseStr = response.body?.string() ?: ""
            val jsonResp = JSONObject(responseStr)
            val candidates = jsonResp.optJSONArray("candidates")
            val rawText = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text") ?: ""

            val cleanJsonStr = rawText.replace("```json", "").replace("```", "").trim()
            val parsedObj = JSONObject(cleanJsonStr)

            val seriesArray = parsedObj.optJSONArray("series") ?: JSONArray()
            val moviesArray = parsedObj.optJSONArray("movies") ?: JSONArray()

            val seriesList = parseMediaArray(seriesArray, "TV")
            val moviesList = parseMediaArray(moviesArray, "MOVIE")

            if (seriesList.isEmpty() && moviesList.isEmpty()) {
                return@withContext generateFallbackAiResults(prompt)
            }

            return@withContext Pair(seriesList, moviesList)
        } catch (e: Exception) {
            return@withContext generateFallbackAiResults(prompt)
        }
    }

    private fun parseMediaArray(array: JSONArray, defaultType: String): List<MediaItem> {
        val list = mutableListOf<MediaItem>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            val genresJson = obj.optJSONArray("genres")
            val genres = mutableListOf<String>()
            if (genresJson != null) {
                for (j in 0 until genresJson.length()) {
                    genres.add(genresJson.optString(j))
                }
            }
            if (genres.isEmpty()) genres.add("Dram")

            val type = obj.optString("type", defaultType)
            val title = obj.optString("title", "Bilinmeyen Başlık")

            list.add(
                MediaItem(
                    id = obj.optString("id", "ai-item-$i-${System.currentTimeMillis()}"),
                    title = title,
                    originalTitle = obj.optString("originalTitle", title),
                    type = type,
                    year = obj.optInt("year", 2023),
                    runtime = obj.optString("runtime", if (type == "TV") "3 Sezon" else "2sa 10dk"),
                    rating = obj.optDouble("rating", 8.5).toFloat(),
                    posterUrl = obj.optString("posterUrl", "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop"),
                    backdropUrl = obj.optString("backdropUrl", "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=1200&auto=format&fit=crop"),
                    overview = obj.optString("overview", "Yapay zeka tarafından önerilen özel içerik."),
                    genres = genres,
                    trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    cast = CatalogData.sampleCast
                )
            )
        }
        return list
    }

    fun generateFallbackAiResults(prompt: String): Pair<List<MediaItem>, List<MediaItem>> {
        val pLower = prompt.lowercase()
        val isPrison = pLower.contains("prison") || pLower.contains("hapishane") || pLower.contains("kaçış")
        val isSciFi = pLower.contains("bilim") || pLower.contains("kurgu") || pLower.contains("uzay") || pLower.contains("80")

        val series = mutableListOf(
            MediaItem(
                id = "ai-s1",
                title = if (isPrison) "Prison Break" else "Mindhunter",
                originalTitle = if (isPrison) "Prison Break" else "Mindhunter",
                type = "TV",
                year = 2017,
                runtime = "2 Sezon (19 Bölüm)",
                rating = 8.6f,
                posterUrl = "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1518173946687-a4c8a383392e?w=1200&auto=format&fit=crop",
                overview = "FBI ajanları Holden Ford ve Bill Tench, suç psikolojisinin doğasını anlayabilmek için seri katillerle görüşmeler yapar.",
                genres = listOf("Suç", "Psikolojik", "Gerilim"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-s2",
                title = "True Detective",
                originalTitle = "True Detective",
                type = "TV",
                year = 2014,
                runtime = "4 Sezon (30 Bölüm)",
                rating = 8.9f,
                posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=1200&auto=format&fit=crop",
                overview = "Farklı dönemlerde işlenen gizemli cinayetleri çözen dedektiflerin felsefi, karanlık ve takıntılı soruşturmaları.",
                genres = listOf("Gizem", "Suç", "Dram"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-s3",
                title = "Mayor of Kingstown",
                originalTitle = "Mayor of Kingstown",
                type = "TV",
                year = 2021,
                runtime = "3 Sezon (30 Bölüm)",
                rating = 8.2f,
                posterUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=1200&auto=format&fit=crop",
                overview = "Kingstown kasabasındaki hapishane sistemini ve sokak çetelerini kontrol eden McLusky ailesinin güç mücadelesi.",
                genres = listOf("Suç", "Dram", "Aksiyon"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-s4",
                title = "Oz",
                originalTitle = "Oz",
                type = "TV",
                year = 1997,
                runtime = "6 Sezon (56 Bölüm)",
                rating = 8.7f,
                posterUrl = "https://images.unsplash.com/photo-1518173946687-a4c8a383392e?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1200&auto=format&fit=crop",
                overview = "Oswald Eyalet Hapishanesi'nin deneysel 'Emerald City' koğuşunda mahkumların ayakta kalma ve güç savaşı.",
                genres = listOf("Dram", "Suç", "Gerilim"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-s5",
                title = "Escape at Dannemora",
                originalTitle = "Escape at Dannemora",
                type = "TV",
                year = 2018,
                runtime = "1 Sezon (7 Bölüm)",
                rating = 8.0f,
                posterUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=1200&auto=format&fit=crop",
                overview = "Gerçek bir olaydan uyarlanan minidizide, iki mahkum hapishane çalışanının yardımıyla eyalet hapishanesinden kaçar.",
                genres = listOf("Gerçek Suç", "Biyografi", "Gerilim"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-s6",
                title = "Black Mirror",
                originalTitle = "Black Mirror",
                type = "TV",
                year = 2011,
                runtime = "6 Sezon (27 Bölüm)",
                rating = 8.7f,
                posterUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=1200&auto=format&fit=crop",
                overview = "Gelişen teknolojinin insani zayıflıklarla birleştiğinde yol açabileceği karanlık ve huzursuz edici senaryolar.",
                genres = listOf("Bilim Kurgu", "Psikolojik", "Antoloji"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-s7",
                title = "Succession",
                originalTitle = "Succession",
                type = "TV",
                year = 2018,
                runtime = "4 Sezon (39 Bölüm)",
                rating = 8.9f,
                posterUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=1200&auto=format&fit=crop",
                overview = "Ailesinin devasa medya imparatorluğunu yöneten Logan Roy'un sağlığı bozulunca çocukları arasında taht kavgası başlar.",
                genres = listOf("Dram", "İş", "Mizah"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-s8",
                title = "Chernobyl",
                originalTitle = "Chernobyl",
                type = "TV",
                year = 2019,
                runtime = "1 Sezon (5 Bölüm)",
                rating = 9.4f,
                posterUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=1200&auto=format&fit=crop",
                overview = "1986'daki Çernobil nükleer santral felaketini ve olayı bastırmak için hayatlarını feda eden kahramanların gerilim dolu mücadelesi.",
                genres = listOf("Tarih", "Dram", "Gerilim"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            )
        )

        val movies = mutableListOf(
            MediaItem(
                id = "ai-m1",
                title = if (isPrison) "Esaretin Bedeli" else "Esaretin Bedeli",
                originalTitle = "The Shawshank Redemption",
                type = "MOVIE",
                year = 1994,
                runtime = "2sa 22dk",
                rating = 9.3f,
                posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=1200&auto=format&fit=crop",
                overview = "Karısını öldürmediğini savunan masum bankacı Andy Dufresne, Shawshank Hapishanesi'nde umudunu kaybetmeden direnir.",
                genres = listOf("Dram", "Suç"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-m2",
                title = "Alcatraz'dan Kaçış",
                originalTitle = "Escape from Alcatraz",
                type = "MOVIE",
                year = 1979,
                runtime = "1sa 52dk",
                rating = 7.6f,
                posterUrl = "https://images.unsplash.com/photo-1518173946687-a4c8a383392e?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1200&auto=format&fit=crop",
                overview = "Clint Eastwood'un başrolde olduğu filmde, kaçılması imkansız denilen Alcatraz Adası Hapishanesi'nden dahi akılla kaçış planı.",
                genres = listOf("Aksiyon", "Suç", "Biyografi"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-m3",
                title = "Kelebek",
                originalTitle = "Papillon",
                type = "MOVIE",
                year = 1973,
                runtime = "2sa 31dk",
                rating = 8.0f,
                posterUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?w=1200&auto=format&fit=crop",
                overview = "Fransız Guyanası'ndaki acımasız ceza kolonisine gönderilen 'Kelebek' lakaplı Henri Charrière'in özgürlük uğruna pes etmeyen mücadelesi.",
                genres = listOf("Macera", "Biyografi", "Dram"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-m4",
                title = "Açlık Oyunları: Kuşların ve Yılanların Şarkısı",
                originalTitle = "The Ballad of Songbirds & Snakes",
                type = "MOVIE",
                year = 2023,
                runtime = "2sa 37dk",
                rating = 7.2f,
                posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop",
                overview = "Genç Coriolanus Snow'un Panem'deki 10. Açlık Oyunları sırasında mentor olarak yükselişi ve güç tutkusu.",
                genres = listOf("Aksiyon", "Macera", "Bilim Kurgu"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-m5",
                title = "Zindan Adası",
                originalTitle = "Shutter Island",
                type = "MOVIE",
                year = 2010,
                runtime = "2sa 18dk",
                rating = 8.2f,
                posterUrl = "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=1200&auto=format&fit=crop",
                overview = "1954 yılında adadaki akıl hastanesinden kaçan bir kadın hastayı araştırmak için görevlendirilen iki dedektifin kabus dolu soruşturması.",
                genres = listOf("Psikolojik", "Gizem", "Gerilim"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-m6",
                title = "Yeşil Yol",
                originalTitle = "The Green Mile",
                type = "MOVIE",
                year = 1999,
                runtime = "3sa 09dk",
                rating = 8.6f,
                posterUrl = "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1518173946687-a4c8a383392e?w=1200&auto=format&fit=crop",
                overview = "İdam mahkumlarının bulunduğu Cold Mountain Hapishanesi'nde başgardiyan Paul, mucizevi şifa yeteneğine sahip olan mahkum John Coffey ile tanışır.",
                genres = listOf("Dram", "Fantastik", "Suç"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-m7",
                title = "Inception",
                originalTitle = "Inception",
                type = "MOVIE",
                year = 2010,
                runtime = "2sa 28dk",
                rating = 8.8f,
                posterUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=1200&auto=format&fit=crop",
                overview = "İnsanların rüyalarına girerek bilinçaltındaki sırları çalan bir hırsızlık ekibine bu kez bilinçaltına fikir yerleştirme (inception) görevi verilir.",
                genres = listOf("Bilim Kurgu", "Aksiyon", "Gizem"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-m8",
                title = "Karanlık Şövalye",
                originalTitle = "The Dark Knight",
                type = "MOVIE",
                year = 2008,
                runtime = "2sa 32dk",
                rating = 9.0f,
                posterUrl = "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600&auto=format&fit=crop",
                backdropUrl = "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=1200&auto=format&fit=crop",
                overview = "Gotham'da adalet sağlayan Batman, kaos ve anarşinin simgesi olan psikopat suç dehası Joker ile amansız bir savaşa girer.",
                genres = listOf("Aksiyon", "Suç", "Dram"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            )
        )

        return Pair(series, movies)
    }
}
