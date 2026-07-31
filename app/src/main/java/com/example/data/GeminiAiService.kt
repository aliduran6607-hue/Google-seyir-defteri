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
                You are an expert global film & TV recommendation engine for Turkish cinephiles.

                CRITICAL RULES:
                1. The user query can be in Turkish OR English. You MUST understand both languages and search across the ENTIRE global database (Hollywood, European, Asian, Turkish). NEVER limit results to only Turkish productions unless specifically asked.
                2. If user writes in Turkish (e.g. "hapishane dizisi", "uzay filmi", "ters köşe filmler", "80ler bilim kurgu"), translate the intent to English internally and find the best matching international titles.
                3. If user writes in English (e.g. "prison drama", "space movies", "mind bending thriller"), also include titles that Turkish audiences know by their official Turkish names.
                4. For EACH result, provide:
                   - "title": The original English (or native language) title (e.g., "Spider-Man", "Inception", "Breaking Bad"). ALWAYS use the original title.
                   - "originalTitle": The original English (or native language) title.
                   - "overview": A detailed, compelling plot summary written in Turkish.
                   - "genres": Genre names in Turkish (e.g. "Bilim Kurgu", "Gerilim", "Suç", "Dram").
                   - "type": "TV" or "MOVIE"
                   - "year": integer
                   - "runtime": string (e.g. "2sa 22dk" for movies, "4 Sezon (39 Bölüm)" for series)
                   - "rating": float (IMDb-style, 0-10)
                   - "posterUrl": a high-quality cinematic image URL (e.g. "https://image.tmdb.org/t/p/w500/tihf8Trht9zP3scmUQfvGlAY9FU.jpg")
                   - "backdropUrl": a high-quality cinematic wide image URL (e.g. "https://image.tmdb.org/t/p/w1280/eZ239CUp1d6OryZEBPnO2n87gMG.jpg")
                   - "id": unique slug (e.g. "the-shawshank-redemption-movie")

                User query: "$prompt"

                Return ONLY a valid raw JSON object (no markdown code blocks, no backticks) with this exact structure:
                {
                  "series": [array of 8 TV series objects],
                  "movies": [array of 8 movie objects]
                }
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
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
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
            val rawTitle = obj.optString("title", "Bilinmeyen Başlık")
            val origTitle = obj.optString("originalTitle", rawTitle)
            val title = origTitle.ifBlank { rawTitle }

            list.add(
                MediaItem(
                    id = obj.optString("id", "ai-item-$i-${System.currentTimeMillis()}"),
                    title = title,
                    originalTitle = obj.optString("originalTitle", title),
                    type = type,
                    year = obj.optInt("year", 2023),
                    runtime = obj.optString("runtime", if (type == "TV") "3 Sezon" else "2sa 10dk"),
                    rating = obj.optDouble("rating", 8.5).toFloat(),
                    posterUrl = obj.optString("posterUrl", "https://image.tmdb.org/t/p/w500/tihf8Trht9zP3scmUQfvGlAY9FU.jpg"),
                    backdropUrl = obj.optString("backdropUrl", "https://image.tmdb.org/t/p/w1280/eZ239CUp1d6OryZEBPnO2n87gMG.jpg"),
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

        val series = mutableListOf(
            MediaItem(
                id = "ai-s1",
                title = if (isPrison) "Prison Break" else "MINDHUNTER",
                originalTitle = if (isPrison) "Prison Break" else "Mindhunter",
                type = "TV",
                year = if (isPrison) 2005 else 2017,
                runtime = if (isPrison) "5 Sezon (90 Bölüm)" else "2 Sezon (19 Bölüm)",
                rating = 8.1f,
                posterUrl = if (isPrison) "https://image.tmdb.org/t/p/w500/wnmNPaLvhnMeOqnWlhNkYCZxtda.jpg" else "https://image.tmdb.org/t/p/w500/fbKE87mojpIETWepSbD5Qt741fp.jpg",
                backdropUrl = if (isPrison) "https://image.tmdb.org/t/p/w1280/n3Brk7roueE9HOwVmYlJx5j462g.jpg" else "https://image.tmdb.org/t/p/w1280/lpDVJuIro21gtMj9iXMFKHuroZN.jpg",
                overview = if (isPrison) "Haksız yere suçlandığını düşündüğü abisini hapishaneden kurtarmak isteyen Michael Scofield, hapishanenin haritasını dövme yaptırıp bilerek hapse girer." else "FBI'ın Elit Seri Suçlar Biriminden ajanlar, psikopat suçluları inceleyerek seri katil profil çıkarma metodunu geliştirir.",
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
                rating = 8.3f,
                posterUrl = "https://image.tmdb.org/t/p/w500/dC7jkj2g1aU8sxKqM6D4g44xA6w.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/v8YFr8BbU9qsO8PYIulzTeM6Qk.jpg",
                overview = "Louisiana Cinayet Masası'nda görev yapan dedektif Rust Cohle ve Martin Hart bir seri katilin peşine düşer.",
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
                rating = 7.9f,
                posterUrl = "https://image.tmdb.org/t/p/w500/6rWIip9MZELAA0SKii5WqsBDCYW.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/39bifj2FNytJ2m1cqOBcWMTKgmV.jpg",
                overview = "Mayor of Kingstown, McLusky ailesinin hikayesini konu ediniyor. Gücü kendi etrafında toplamak isteyen aile hapishane düzenini kontrol eder.",
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
                rating = 8.0f,
                posterUrl = "https://image.tmdb.org/t/p/w500/oTQ9PUnCgf9CimYeWuDGp8iaT07.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/2nboc7IWrvJwiT1Dq847IuNcXqF.jpg",
                overview = "Oswald Eyalet Hapishanesi'nin deneysel Emerald City koğuşunda mahkumların ayakta kalma ve güç savaşı.",
                genres = listOf("Dram", "Suç", "Gerilim"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-s5",
                title = "Dannemora'dan Kaçış",
                originalTitle = "Escape at Dannemora",
                type = "TV",
                year = 2018,
                runtime = "1 Sezon (7 Bölüm)",
                rating = 7.5f,
                posterUrl = "https://image.tmdb.org/t/p/w500/odk6ccUtUJlGTzPApKS6s4CTTrK.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/chu6bVjN5viCdfmzBcx5R3xYfTj.jpg",
                overview = "Richard Matt ve David Sweat aynı hapishanede kalan cinayetten hüküm giymiş iki mahkumdur. Bir çalışanın yardımıyla hapishaneden kaçarlar.",
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
                rating = 8.3f,
                posterUrl = "https://image.tmdb.org/t/p/w500/seN6rRfN0I6n8iDXjlSMk1QjNcq.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/dg3OindVAGZBjlT3xYKqIAdukPL.jpg",
                overview = "İnsanlığın en kötü özelliklerini, en büyük buluşlarını ve teknolojiyle gelen distopik senaryoları gözler önüne seren antoloji dizisi.",
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
                rating = 8.3f,
                posterUrl = "https://image.tmdb.org/t/p/w500/z0XiwdrCQ9yVIr4O0pxzaAYRxdW.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/bcdUYUFk8GdpZJPiSAas9UeocLH.jpg",
                overview = "New York'ta yaşayan, dünyanın en büyük medya şirketlerinden birini yöneten Logan Roy ve dört çocuğunun güç savaşı.",
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
                rating = 8.7f,
                posterUrl = "https://image.tmdb.org/t/p/w500/vDwYRtmfBgM9zq4o0xbeUuVo3DL.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/3URK0z9PzpVNJrGE7XOuyy6KFzk.jpg",
                overview = "1986 senesinde Sovyet nükleer santralinde meydana gelen patlama sonrası santral işçileri ve itfaiyecilerin fedakarlığı.",
                genres = listOf("Tarih", "Dram", "Gerilim"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            )
        )

        val movies = mutableListOf(
            MediaItem(
                id = "ai-m1",
                title = "Esaretin Bedeli",
                originalTitle = "The Shawshank Redemption",
                type = "MOVIE",
                year = 1994,
                runtime = "2sa 22dk",
                rating = 8.7f,
                posterUrl = "https://image.tmdb.org/t/p/w500/7T2SDS5efuJiK45oyKoEzf9RKjw.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/zfbjgQE1uSd9wiPTX4VzsLi0rGG.jpg",
                overview = "Genç ve başarılı bir banker olan Andy Dufresne, karısını ve sevgilisini öldürmek suçundan ömür boyu hapse mahkûm olur.",
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
                rating = 7.5f,
                posterUrl = "https://image.tmdb.org/t/p/w500/27U2fpo9Qfux3UZOFTci801JblN.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/lfLVH3F8Xt8nITqG9cn97b54au1.jpg",
                overview = "Ömür boyu hapse mahkûm olan Frank Morris, kaçılması imkansız denilen Alcatraz adasından kaçışını planlar.",
                genres = listOf("Aksiyon", "Suç", "Biyografi"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-m3",
                title = "Papillon",
                originalTitle = "Papillon",
                type = "MOVIE",
                year = 2017,
                runtime = "2sa 13dk",
                rating = 7.3f,
                posterUrl = "https://image.tmdb.org/t/p/w500/ahF5c6vyP8HWMqIwlhecbRALkjq.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/lfMtVr5hkFeGy4dMaEf3XUQr76d.jpg",
                overview = "Haksız yere cinayetten hüküm giymiş olan Henri 'Papillon' Charriere, Fransız Guyanası'ndaki ceza kolonisinden kaçmaya çalışır.",
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
                rating = 7.0f,
                posterUrl = "https://image.tmdb.org/t/p/w500/fpCw9qWv7EWkpLcNoSBxlIin8ZG.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/bk1TitfD4YIGrM6AvljonMCtfnl.jpg",
                overview = "Capitol'de gözden düşen Coriolanus Snow, 10. Açlık Oyunları sırasında mentor olarak görevlendirilir.",
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
                posterUrl = "https://image.tmdb.org/t/p/w500/epp8lnSQEWe9cYQACEhBDZTY18L.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/rbZvGN1A1QyZuoKzhCw8QPmf2q0.jpg",
                overview = "Amerikan Marşalı Teddy Daniels, Shutter Island'daki cani delilerin bulunduğu akıl hastanesinden kaçan bir kadını arar.",
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
                rating = 8.5f,
                posterUrl = "https://image.tmdb.org/t/p/w500/6ZNnKbdDRQm0ftkq3OKiDrwZkIN.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/b6HWTOxn1xevvyHU2K9ICvaRU6g.jpg",
                overview = "Hapishanedeki idam koğuşu başgardiyanı Paul, mucizevi şifa yeteneğine sahip mahkum John Coffey ile tanışır.",
                genres = listOf("Dram", "Fantastik", "Suç"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-m7",
                title = "Başlangıç",
                originalTitle = "Inception",
                type = "MOVIE",
                year = 2010,
                runtime = "2sa 28dk",
                rating = 8.4f,
                posterUrl = "https://image.tmdb.org/t/p/w500/xn0Kcg4e6p0mLxVS3nAWhNmW2Ni.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/8ZTVqvKDQ8emSGUEMjsS4yHAwrp.jpg",
                overview = "Hedeflerinin bilinçaltına sızarak hırsızlık yapan Cobb'a, bu kez bir bilinçaltına fikir yerleştirme görevi verilir.",
                genres = listOf("Bilim Kurgu", "Aksiyon", "Gizem"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            ),
            MediaItem(
                id = "ai-m8",
                title = "Kara Şövalye",
                originalTitle = "The Dark Knight",
                type = "MOVIE",
                year = 2008,
                runtime = "2sa 32dk",
                rating = 8.5f,
                posterUrl = "https://image.tmdb.org/t/p/w500/7IPCEr7ifdH5CtU97QG7XgAAtOp.jpg",
                backdropUrl = "https://image.tmdb.org/t/p/w1280/dqK9Hag1054tghRQSqLSfrkvQnA.jpg",
                overview = "Batman, Teğmen Jim Gordon ve Savcı Harvey Dent'in yardımıyla Gotham sokaklarını kontrol eden Joker ile savaşır.",
                genres = listOf("Aksiyon", "Suç", "Dram"),
                trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                cast = CatalogData.sampleCast
            )
        )

        return Pair(series, movies)
    }
}
