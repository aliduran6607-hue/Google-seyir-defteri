package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.MediaItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SeyirDefteriViewModel

@Composable
fun SearchAiScreen(viewModel: SeyirDefteriViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val liveQuery by viewModel.liveSearchQuery.collectAsState()
    val isLiveLoading by viewModel.isLiveLoading.collectAsState()
    val liveSeries by viewModel.liveSeriesResults.collectAsState()
    val liveMovies by viewModel.liveMovieResults.collectAsState()

    val aiPrompt by viewModel.aiSearchPrompt.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiSeries by viewModel.aiSeriesResults.collectAsState()
    val aiMovies by viewModel.aiMovieResults.collectAsState()
    val catalog by viewModel.catalogState.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: TVMaze & TMDB, 1: AI Bul, 2: Katalog Ara
    var liveFilterType by remember { mutableStateOf(0) } // 0: Tümü, 1: Filmler, 2: Diziler

    val filteredCatalog = remember(searchQuery, catalog) {
        if (searchQuery.isBlank()) emptyList()
        else catalog.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.originalTitle.contains(searchQuery, ignoreCase = true) ||
            it.genres.any { g -> g.contains(searchQuery, ignoreCase = true) }
        }
    }

    val presetLiveSearches = listOf(
        "Breaking Bad",
        "Inception",
        "Game of Thrones",
        "Interstellar",
        "Stranger Things",
        "Dune",
        "Prison Break",
        "The Dark Knight"
    )

    val presetPrompts = listOf(
        "Aklını başından alacak ters köşe filmler",
        "Sürükleyici psikolojik gerilim dizileri",
        "Hapishane ve kaçış temalı dramalar",
        "Beyin yakan 80'ler bilim kurgu dizileri",
        "Karanlık atmosferli polisiye ve suç"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("search_ai_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Title with Seyir Defteri Logo
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.size(40.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon_seyirdefteri),
                    contentDescription = "Seyir Defteri Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "Arama ve Keşfet",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TVMaze, TMDB veya Gemini AI ile film/dizi keşfedin",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3 Toggle Tabs: TVMaze & TMDB / AI Bul / Katalog
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = DarkSurface,
            contentColor = VioletPrimary,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = VioletPrimary
                )
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = "Live",
                            tint = if (activeTab == 0) VioletPrimary else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TVMaze & TMDB Canlı",
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = if (activeTab == 1) VioletPrimary else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Bul (Gemini)",
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "Katalog",
                            tint = if (activeTab == 2) VioletPrimary else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Katalogda Ara",
                            fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeTab) {
            0 -> {
                // TVMaze & TMDB Live Search Bar
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = VioletPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = liveQuery,
                            onValueChange = { viewModel.updateLiveSearchQuery(it) },
                            placeholder = { Text("Örn: 'Breaking Bad', 'Inception', 'Dune'", color = TextMuted, fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("live_search_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                        IconButton(
                            onClick = { viewModel.runLiveSearch(liveQuery.ifBlank { "Breaking Bad" }) },
                            modifier = Modifier
                                .size(38.dp)
                                .background(VioletPrimary, CircleShape)
                                .testTag("live_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Ara",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Search Chips
                Text(text = "Hızlı Arama Fikirleri:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presetLiveSearches) { query ->
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { viewModel.runLiveSearch(query) },
                            shape = CircleShape,
                            color = DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalMovies,
                                    contentDescription = null,
                                    tint = VioletLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = query, color = TextPrimary, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLiveLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VioletPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "TVMaze ve TMDB sunucularına bağlanılıyor...\nGerçek zamanlı afiş, puan ve detaylar çekiliyor.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SkeletonCard(modifier = Modifier.weight(1f))
                            SkeletonCard(modifier = Modifier.weight(1f))
                        }
                    }
                } else if (liveSeries.isNotEmpty() || liveMovies.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Type Filter Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = liveFilterType == 0,
                                onClick = { liveFilterType = 0 },
                                label = { Text("Tümü (${liveMovies.size + liveSeries.size})", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VioletPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = liveFilterType == 1,
                                onClick = { liveFilterType = 1 },
                                label = { Text("🎬 Filmler (${liveMovies.size})", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VioletPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = liveFilterType == 2,
                                onClick = { liveFilterType = 2 },
                                label = { Text("📺 Diziler (${liveSeries.size})", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VioletPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 1. MOVIES FIRST
                            if ((liveFilterType == 0 || liveFilterType == 1) && liveMovies.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        title = "Sinema Filmleri (TMDB)",
                                        subtitle = "'${liveQuery}' için ${liveMovies.size} film bulundu"
                                    )
                                }
                                items(liveMovies.chunked(2)) { pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        pair.forEach { item ->
                                            MediaCard(
                                                item = item,
                                                onClick = { viewModel.openDetail(item) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        if (pair.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }

                            // 2. TV SERIES SECOND
                            if ((liveFilterType == 0 || liveFilterType == 2) && liveSeries.isNotEmpty()) {
                                item {
                                    if (liveMovies.isNotEmpty() && (liveFilterType == 0)) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                    SectionHeader(
                                        title = "TV Dizileri (TVMaze & TMDB)",
                                        subtitle = "'${liveQuery}' için ${liveSeries.size} dizi bulundu"
                                    )
                                }
                                items(liveSeries.chunked(2)) { pair ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        pair.forEach { item ->
                                            MediaCard(
                                                item = item,
                                                onClick = { viewModel.openDetail(item) },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        if (pair.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CloudQueue,
                                contentDescription = "Live Search",
                                tint = VioletPrimary.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "TVMaze & TMDB Canlı Arama",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Aramak istediğiniz dizi veya film adını yukarıya yazın.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            1 -> {
                // AI Gemini Search
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = VioletPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = aiPrompt,
                            onValueChange = { viewModel.setAiPrompt(it) },
                            placeholder = { Text("Örn: 'prison escape drama' veya '80'ler gizem'", color = TextMuted, fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_search_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                        IconButton(
                            onClick = { viewModel.runAiFind(aiPrompt.ifBlank { "prison escape drama" }) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(VioletPrimary, CircleShape)
                                .testTag("ai_find_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Bul",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Hızlı AI Öneri Fikirleri:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(presetPrompts) { prompt ->
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { viewModel.runAiFind(prompt) },
                            shape = CircleShape,
                            color = DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = VioletLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = prompt, color = TextPrimary, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isAiLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = VioletPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Gemini AI sinema arşivini tarıyor...\n8 Dizi ve 8 Film seçkisi hazırlanıyor.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SkeletonCard(modifier = Modifier.weight(1f))
                            SkeletonCard(modifier = Modifier.weight(1f))
                        }
                    }
                } else if (aiSeries.isNotEmpty() || aiMovies.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (aiSeries.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = "Önerilen 8 Dizi",
                                    subtitle = "'${aiPrompt}' temasına uygun seçkiler"
                                )
                            }
                            items(aiSeries.chunked(2)) { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    pair.forEach { item ->
                                        MediaCard(
                                            item = item,
                                            onClick = { viewModel.openDetail(item) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }

                        if (aiMovies.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                SectionHeader(
                                    title = "Önerilen 8 Film",
                                    subtitle = "En yüksek uyumlu filmler"
                                )
                            }
                            items(aiMovies.chunked(2)) { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    pair.forEach { item ->
                                        MediaCard(
                                            item = item,
                                            onClick = { viewModel.openDetail(item) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI",
                                tint = VioletPrimary.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aklınızdaki Temayı Yazın",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Örneğin: 'prison escape drama' veya 'gizemli ada filmleri'",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            2 -> {
                // Direct Catalog Search
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Başlık, orijinal ad veya türe göre ara...", color = TextMuted) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = VioletPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("catalog_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VioletPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (searchQuery.isBlank()) {
                    Text(text = "Tüm Lokal Katalog (${catalog.size} İçerik)", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(catalog) { item ->
                            SearchResultRow(item = item, onSelect = { viewModel.openDetail(item) }, onAdd = { viewModel.updateWatchStatus(item, "TO_WATCH") })
                        }
                    }
                } else if (filteredCatalog.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "'${searchQuery}' için sonuç bulunamadı", color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredCatalog) { item ->
                            SearchResultRow(item = item, onSelect = { viewModel.openDetail(item) }, onAdd = { viewModel.updateWatchStatus(item, "TO_WATCH") })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultRow(
    item: MediaItem,
    onSelect: () -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder.copy(alpha = 0.2f))))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.posterUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .size(width = 50.dp, height = 70.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.originalTitle} • ${item.year}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingBadge(rating = item.rating)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.genres.joinToString(", "),
                        color = VioletLight,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = { onAdd() },
                modifier = Modifier
                    .size(36.dp)
                    .background(DarkSurfaceVariant, CircleShape)
            ) {
                Icon(
                    imageVector = if (item.watchStatus != null) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = "Ekle",
                    tint = if (item.watchStatus != null) StatusWatched else VioletPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
