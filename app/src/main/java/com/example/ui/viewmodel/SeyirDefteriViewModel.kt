package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class SeyirDefteriViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = MediaRepository(db.mediaDao())

    // All items (merged catalog + user Room items)
    val catalogState: StateFlow<List<MediaItem>> = repository.getFullCatalog()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogData.initialCatalog)

    // User collection items (only items where watchStatus != null)
    val collectionState: StateFlow<List<MediaItem>> = catalogState.map { items ->
        items.filter { it.watchStatus != null }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected item for detail view
    private val _selectedMediaItem = MutableStateFlow<MediaItem?>(null)
    val selectedMediaItem: StateFlow<MediaItem?> = _selectedMediaItem.asStateFlow()

    // Navigation current tab
    private val _currentTab = MutableStateFlow(0) // 0: Discover, 1: Search/AI, 2: Collection, 3: Stats, 4: Profile
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // Filter states for Collection
    private val _filterStatus = MutableStateFlow<String?>("ALL") // "ALL", "TO_WATCH", "WATCHING", "WATCHED"
    val filterStatus: StateFlow<String?> = _filterStatus.asStateFlow()

    private val _filterType = MutableStateFlow("ALL") // "ALL", "TV", "MOVIE"
    val filterType: StateFlow<String> = _filterType.asStateFlow()

    private val _selectedGenre = MutableStateFlow("Tümü")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val _selectedRatingFilter = MutableStateFlow("Tümü") // "Tümü", "Unrated", "10", "9", "7-8", "5-6", "1-4"
    val selectedRatingFilter: StateFlow<String> = _selectedRatingFilter.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    // Search and AI Find States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _liveSearchQuery = MutableStateFlow("")
    val liveSearchQuery: StateFlow<String> = _liveSearchQuery.asStateFlow()

    private val _isLiveLoading = MutableStateFlow(false)
    val isLiveLoading: StateFlow<Boolean> = _isLiveLoading.asStateFlow()

    private val _liveSeriesResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val liveSeriesResults: StateFlow<List<MediaItem>> = _liveSeriesResults.asStateFlow()

    private val _liveMovieResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val liveMovieResults: StateFlow<List<MediaItem>> = _liveMovieResults.asStateFlow()

    private val _aiSearchPrompt = MutableStateFlow("")
    val aiSearchPrompt: StateFlow<String> = _aiSearchPrompt.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiSeriesResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val aiSeriesResults: StateFlow<List<MediaItem>> = _aiSeriesResults.asStateFlow()

    private val _aiMovieResults = MutableStateFlow<List<MediaItem>>(emptyList())
    val aiMovieResults: StateFlow<List<MediaItem>> = _aiMovieResults.asStateFlow()

    // Toast message trigger
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // Notifications toggle state
    private val _newEpisodeNotify = MutableStateFlow(true)
    val newEpisodeNotify: StateFlow<Boolean> = _newEpisodeNotify.asStateFlow()

    private val _sequelMovieNotify = MutableStateFlow(true)
    val sequelMovieNotify: StateFlow<Boolean> = _sequelMovieNotify.asStateFlow()

    private val _weeklyDigestNotify = MutableStateFlow(false)
    val weeklyDigestNotify: StateFlow<Boolean> = _weeklyDigestNotify.asStateFlow()

    // Language state ("tr" or "en")
    private val _selectedLanguage = MutableStateFlow("tr")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    // Theme state (true: Dark, false: Light)
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // User Profile & Admin States
    private val _userEmail = MutableStateFlow("aliduran6607@gmail.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _profileName = MutableStateFlow("Ali Duran")
    val profileName: StateFlow<String> = _profileName.asStateFlow()

    private val _isAdmin = MutableStateFlow(true)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    fun setNewEpisodeNotify(enabled: Boolean) {
        _newEpisodeNotify.value = enabled
    }

    fun setSequelMovieNotify(enabled: Boolean) {
        _sequelMovieNotify.value = enabled
    }

    fun setWeeklyDigestNotify(enabled: Boolean) {
        _weeklyDigestNotify.value = enabled
    }

    fun setSelectedLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    init {
        // Clean up any initial pre-seeded demo items and auto-import user's personal backup data
        viewModelScope.launch(Dispatchers.IO) {
            val demoIds = listOf(
                "interstellar-movie", "breaking-bad-tv", "severance-tv", "dune2-movie",
                "oppenheimer-movie", "dark-tv", "prison-break-tv", "gibi-tv", "sahsiyet-tv", "kulup-tv"
            )
            demoIds.forEach { id ->
                db.mediaDao().deleteById(id)
            }
            // Auto import user backup library in batch on IO thread
            val currentItemsCount = db.mediaDao().getAllUserItems().firstOrNull()?.size ?: 0
            if (currentItemsCount == 0) {
                importBackupJsonInternal(UserBackupData.initialUserBackupJson, showToast = false)
            }
        }
    }

    fun updateProfileName(name: String) {
        if (name.isNotBlank()) {
            _profileName.value = name.trim()
        }
    }

    fun selectTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    fun openDetail(item: MediaItem) {
        _selectedMediaItem.value = item
    }

    fun closeDetail() {
        _selectedMediaItem.value = null
    }

    fun setFilterStatus(status: String?) {
        _filterStatus.value = status
    }

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    fun setSelectedGenre(genre: String) {
        _selectedGenre.value = genre
    }

    fun setSelectedRatingFilter(rating: String) {
        _selectedRatingFilter.value = rating
    }

    fun toggleGridView() {
        _isGridView.value = !_isGridView.value
    }

    private var liveSearchJob: Job? = null
    val customTmdbKey = MutableStateFlow("")

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateLiveSearchQuery(query: String) {
        _liveSearchQuery.value = query
        liveSearchJob?.cancel()
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            liveSearchJob = viewModelScope.launch {
                delay(300) // 300ms debounced search as user types letters
                performLiveSearch(trimmed, showToast = false)
            }
        } else {
            _liveSeriesResults.value = emptyList()
            _liveMovieResults.value = emptyList()
        }
    }

    fun runLiveSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        _liveSearchQuery.value = trimmed
        liveSearchJob?.cancel()
        liveSearchJob = viewModelScope.launch {
            performLiveSearch(trimmed, showToast = true)
        }
    }

    private suspend fun performLiveSearch(query: String, showToast: Boolean) {
        _isLiveLoading.value = true
        val results = TvmazeTmdbService.searchLiveMedia(query, customTmdbKey.value.ifBlank { null })
        _liveSeriesResults.value = results.first
        _liveMovieResults.value = results.second
        _isLiveLoading.value = false
        if (showToast) {
            _toastMessage.emit("TVMaze & TMDB: '${query}' için ${results.first.size} Dizi ve ${results.second.size} Film bulundu.")
        }
    }

    fun setAiPrompt(prompt: String) {
        _aiSearchPrompt.value = prompt
    }

    fun runAiFind(prompt: String) {
        if (prompt.isBlank()) return
        _aiSearchPrompt.value = prompt
        _isAiLoading.value = true
        viewModelScope.launch {
            val results = GeminiAiService.findAiContent(prompt)
            _aiSeriesResults.value = results.first
            _aiMovieResults.value = results.second
            _isAiLoading.value = false
            _toastMessage.emit("'${prompt}' için 8 Dizi ve 8 Film önerisi hazırlandı.")
        }
    }

    fun updateWatchStatus(item: MediaItem, status: String?) {
        viewModelScope.launch {
            repository.updateWatchStatus(item, status)
            val updated = item.copy(watchStatus = status)
            if (_selectedMediaItem.value?.id == item.id) {
                _selectedMediaItem.value = updated
            }
            val statusName = when (status) {
                "WATCHED" -> "İzlendi"
                "WATCHING" -> "İzleniyor"
                "TO_WATCH" -> "İzlenecek"
                else -> "Kütüphaneden Çıkarıldı"
            }
            _toastMessage.emit("${item.title} -> $statusName olarak güncellendi.")
        }
    }

    fun updateEpisodes(item: MediaItem, watchedEpisodes: Int) {
        viewModelScope.launch {
            repository.updateEpisodes(item, watchedEpisodes)
            val updated = item.copy(
                watchedEpisodes = watchedEpisodes,
                watchStatus = if (watchedEpisodes >= item.totalEpisodes && item.totalEpisodes > 0) "WATCHED" else "WATCHING"
            )
            if (_selectedMediaItem.value?.id == item.id) {
                _selectedMediaItem.value = updated
            }
            _toastMessage.emit("${item.title}: $watchedEpisodes / ${item.totalEpisodes} bölüm kaydedildi.")
        }
    }

    fun updateRatingAndNotes(item: MediaItem, rating: Int?, notes: String) {
        viewModelScope.launch {
            repository.updateRatingAndNotes(item, rating, notes)
            val updated = item.copy(userRating = rating, userNotes = notes)
            if (_selectedMediaItem.value?.id == item.id) {
                _selectedMediaItem.value = updated
            }
            _toastMessage.emit("Puan ve notlarınız kaydedildi.")
        }
    }

    fun removeItem(item: MediaItem) {
        viewModelScope.launch {
            repository.removeFromCollection(item)
            if (_selectedMediaItem.value?.id == item.id) {
                _selectedMediaItem.value = item.copy(watchStatus = null)
            }
            _toastMessage.emit("${item.title} kütüphaneden kaldırıldı.")
        }
    }

    fun exportBackupJson(): String {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(List::class.java, MediaItem::class.java)
        val adapter = moshi.adapter<List<MediaItem>>(type)
        val jsonStr = adapter.toJson(collectionState.value)
        viewModelScope.launch {
            _toastMessage.emit("Kütüphane verileri JSON olarak dışa aktarıldı.")
        }
        return jsonStr
    }

    fun importBackupJson(jsonStr: String, showToast: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            importBackupJsonInternal(jsonStr, showToast)
        }
    }

    private suspend fun importBackupJsonInternal(jsonStr: String, showToast: Boolean = true) {
        try {
            val list = parseAnyJsonBackup(jsonStr)
            if (list.isNotEmpty()) {
                repository.saveItemsToCollection(list)
                if (showToast) {
                    _toastMessage.emit("${list.size} içerik başarıyla kütüphaneye aktarıldı.")
                }
            } else if (showToast) {
                _toastMessage.emit("Geçersiz veya boş JSON dosyası!")
            }
        } catch (e: Exception) {
            if (showToast) {
                _toastMessage.emit("Geçersiz JSON dosyası: ${e.localizedMessage}")
            }
        }
    }

    private fun parseAnyJsonBackup(jsonStr: String): List<MediaItem> {
        val resultList = mutableListOf<MediaItem>()
        val trimmed = jsonStr.trim()
        try {
            if (trimmed.startsWith("{")) {
                val rootObj = JSONObject(trimmed)
                val array = if (rootObj.has("veriler")) {
                    rootObj.getJSONArray("veriler")
                } else if (rootObj.has("items")) {
                    rootObj.getJSONArray("items")
                } else {
                    null
                }

                if (array != null) {
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val rawId = obj.opt("id")?.toString() ?: "imp_$i"
                        val isim = obj.optString("isim", obj.optString("title", "İsimsiz İçerik"))
                        val poster = obj.optString("poster", obj.optString("posterUrl", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=600&auto=format&fit=crop"))
                        val turStr = obj.optString("tur", obj.optString("type", "Film"))
                        val type = if (turStr.equals("Dizi", ignoreCase = true) || turStr.equals("TV", ignoreCase = true)) "TV" else "MOVIE"

                        val durumStr = obj.optString("durum", obj.optString("watchStatus", "İzlendi"))
                        val status = when (durumStr) {
                            "İzlendi", "WATCHED" -> "WATCHED"
                            "İzleniyor", "WATCHING" -> "WATCHING"
                            "İzlenecek", "TO_WATCH" -> "TO_WATCH"
                            else -> "WATCHED"
                        }

                        val userRating = if (obj.has("puan") && !obj.isNull("puan")) obj.optInt("puan")
                        else if (obj.has("userRating") && !obj.isNull("userRating")) obj.optInt("userRating")
                        else null

                        val genresList = mutableListOf<String>()
                        val catArr = obj.optJSONArray("kategoriler") ?: obj.optJSONArray("genres")
                        if (catArr != null) {
                            for (j in 0 until catArr.length()) {
                                genresList.add(catArr.getString(j))
                            }
                        }
                        if (genresList.isEmpty()) {
                            genresList.add(if (type == "TV") "Dizi" else "Film")
                        }

                        val encodedName = try { java.net.URLEncoder.encode(isim, "UTF-8") } catch (e: Exception) { "Actor" }
                        val avatar = "https://ui-avatars.com/api/?name=$encodedName&background=211B34&color=A855F7&size=200&bold=true"

                        val item = MediaItem(
                            id = rawId,
                            title = isim,
                            originalTitle = isim,
                            type = type,
                            year = 2024,
                            runtime = if (type == "TV") "1+ Sezon" else "2sa 10dk",
                            rating = 8.5f,
                            posterUrl = poster,
                            backdropUrl = poster,
                            overview = "$isim - Kişisel Seyir Defteri Kütüphanenizde kayıtlı içerik.",
                            genres = genresList,
                            trailerUrl = "https://www.youtube.com/results?search_query=$encodedName",
                            cast = listOf(
                                CastMember(isim + " Oyuncusu", "Başrol", avatar)
                            ),
                            watchStatus = status,
                            userRating = userRating,
                            userNotes = "",
                            watchedEpisodes = 0,
                            totalEpisodes = if (type == "TV") 12 else 0
                        )
                        resultList.add(item)
                    }
                    return resultList
                }
            } else if (trimmed.startsWith("[")) {
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val type = Types.newParameterizedType(List::class.java, MediaItem::class.java)
                val adapter = moshi.adapter<List<MediaItem>>(type)
                val list = adapter.fromJson(trimmed)
                if (list != null) return list
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return try {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val type = Types.newParameterizedType(List::class.java, MediaItem::class.java)
            val adapter = moshi.adapter<List<MediaItem>>(type)
            adapter.fromJson(jsonStr) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
