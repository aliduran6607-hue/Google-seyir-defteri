package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.MediaItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SeyirDefteriViewModel

@Composable
fun CollectionScreen(viewModel: SeyirDefteriViewModel) {
    val collection by viewModel.collectionState.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val ratingFilter by viewModel.selectedRatingFilter.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()

    var showRatingMenu by remember { mutableStateOf(false) }

    // Stat counts
    val totalCount = collection.size
    val toWatchCount = collection.count { it.watchStatus == "TO_WATCH" }
    val watchingCount = collection.count { it.watchStatus == "WATCHING" }
    val watchedCount = collection.count { it.watchStatus == "WATCHED" }

    // Filter collection items
    val filteredCollection = remember(collection, filterStatus, filterType, selectedGenre, ratingFilter) {
        collection.filter { item ->
            val matchesStatus = when (filterStatus) {
                "TO_WATCH" -> item.watchStatus == "TO_WATCH"
                "WATCHING" -> item.watchStatus == "WATCHING"
                "WATCHED" -> item.watchStatus == "WATCHED"
                else -> true
            }
            val matchesType = when (filterType) {
                "TV" -> item.type == "TV"
                "MOVIE" -> item.type == "MOVIE"
                else -> true
            }
            val matchesGenre = if (selectedGenre == "Tümü") true else item.genres.contains(selectedGenre)
            val matchesRating = when (ratingFilter) {
                "Unrated" -> item.userRating == null
                "10" -> item.userRating == 10
                "9" -> item.userRating == 9
                "7-8" -> item.userRating in 7..8
                "5-6" -> item.userRating in 5..6
                "1-4" -> item.userRating in 1..4
                else -> true
            }
            matchesStatus && matchesType && matchesGenre && matchesRating
        }
    }

    val genres = listOf("Tümü", "Aksiyon", "Drama", "Komedi", "Bilim Kurgu", "Suç", "Gerilim", "Romantik", "Tarih")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
            .testTag("collection_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Title + Grid/List Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Kütüphanem",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Takip ettiğiniz tüm dizi ve filmler",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = { viewModel.toggleGridView() },
                modifier = Modifier
                    .background(DarkSurfaceVariant, CircleShape)
                    .testTag("view_toggle_button")
            ) {
                Icon(
                    imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                    contentDescription = "Görünüm Değiştir",
                    tint = VioletPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sticky Stats Row (Tap to filter)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatHeaderPill(label = "Toplam", count = totalCount, isSelected = filterStatus == "ALL") {
                viewModel.setFilterStatus("ALL")
            }
            StatHeaderPill(label = "İzlenecek", count = toWatchCount, isSelected = filterStatus == "TO_WATCH") {
                viewModel.setFilterStatus("TO_WATCH")
            }
            StatHeaderPill(label = "İzleniyor", count = watchingCount, isSelected = filterStatus == "WATCHING") {
                viewModel.setFilterStatus("WATCHING")
            }
            StatHeaderPill(label = "İzlendi", count = watchedCount, isSelected = filterStatus == "WATCHED") {
                viewModel.setFilterStatus("WATCHED")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Type Filter Bar (Tümü / Dizi / Film) + Status & Rating Dropdowns
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterTypePill(label = "Tümü", isSelected = filterType == "ALL") { viewModel.setFilterType("ALL") }
                FilterTypePill(label = "Dizi", isSelected = filterType == "TV") { viewModel.setFilterType("TV") }
                FilterTypePill(label = "Film", isSelected = filterType == "MOVIE") { viewModel.setFilterType("MOVIE") }
            }

            var showStatusMenu by remember { mutableStateOf(false) }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Filter Dropdown Menu (İzlendi, İzleniyor, İzlenecek, Tümü)
                Box {
                    val statusText = when (filterStatus) {
                        "WATCHED" -> "İzlendi"
                        "WATCHING" -> "İzleniyor"
                        "TO_WATCH" -> "İzlenecek"
                        else -> "Tüm Durumlar"
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showStatusMenu = true },
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = statusText, color = TextPrimary, fontSize = 11.sp)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false },
                        modifier = Modifier.background(DarkSurfaceVariant)
                    ) {
                        listOf(
                            "ALL" to "Tümü",
                            "WATCHED" to "İzlendi",
                            "WATCHING" to "İzleniyor",
                            "TO_WATCH" to "İzlenecek"
                        ).forEach { (code, label) ->
                            DropdownMenuItem(
                                text = { Text(text = label, color = TextPrimary) },
                                onClick = {
                                    viewModel.setFilterStatus(code)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }

                // Rating Filter Dropdown Menu
                Box {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showRatingMenu = true },
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AmberRating, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "Puan: $ratingFilter", color = TextPrimary, fontSize = 11.sp)
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                        }
                    }

                    DropdownMenu(
                        expanded = showRatingMenu,
                        onDismissRequest = { showRatingMenu = false },
                        modifier = Modifier.background(DarkSurfaceVariant)
                    ) {
                        val ratingOpts = listOf("Tümü", "Unrated", "10", "9", "7-8", "5-6", "1-4")
                        ratingOpts.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(text = opt, color = TextPrimary) },
                                onClick = {
                                    viewModel.setSelectedRatingFilter(opt)
                                    showRatingMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Genre filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(genres) { genre ->
                CategoryPill(
                    title = genre,
                    isSelected = genre == selectedGenre,
                    onClick = { viewModel.setSelectedGenre(genre) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Items Content (Grid or List)
        if (filteredCollection.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = null, tint = TextMuted, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Seçilen filtrelerde içerik bulunamadı", color = TextSecondary, fontSize = 14.sp)
                }
            }
        } else if (isGridView) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredCollection.chunked(2)) { pair ->
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
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCollection) { item ->
                    CollectionListRow(
                        item = item,
                        onClick = { viewModel.openDetail(item) },
                        onUpdateStatus = { status -> viewModel.updateWatchStatus(item, status) },
                        onDelete = { viewModel.removeItem(item) },
                        onEpisodeChange = { watched -> viewModel.updateEpisodes(item, watched) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatHeaderPill(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$count",
            color = if (isSelected) VioletLight else TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            color = if (isSelected) VioletPrimary else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun FilterTypePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() },
        shape = CircleShape,
        color = if (isSelected) VioletPrimary else DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) VioletLight else DarkBorder)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun CollectionListRow(
    item: MediaItem,
    onClick: () -> Unit,
    onUpdateStatus: (String?) -> Unit,
    onDelete: () -> Unit,
    onEpisodeChange: (Int) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder.copy(alpha = 0.2f))))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.posterUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(width = 54.dp, height = 76.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.title,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        StatusBadge(status = item.watchStatus ?: "TO_WATCH")
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${item.year} • ${item.genres.joinToString(", ")}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingBadge(rating = item.rating)
                        if (item.userRating != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AmberRating.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Puanın: ${item.userRating}/10",
                                    color = AmberRatingLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu", tint = TextSecondary)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(DarkSurfaceVariant)
                    ) {
                        DropdownMenuItem(
                            text = { Text("İzlendi Yap", color = TextPrimary) },
                            onClick = { onUpdateStatus("WATCHED"); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("İzleniyor Yap", color = TextPrimary) },
                            onClick = { onUpdateStatus("WATCHING"); showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("İzlenecek Yap", color = TextPrimary) },
                            onClick = { onUpdateStatus("TO_WATCH"); showMenu = false }
                        )
                        Divider(color = DarkBorder)
                        DropdownMenuItem(
                            text = { Text("Kaldır", color = Color.Red) },
                            onClick = { onDelete(); showMenu = false }
                        )
                    }
                }
            }

            // Episode Progress Bar for TV shows
            if (item.type == "TV" && item.totalEpisodes > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bölüm Takibi: ${item.watchedEpisodes} / ${item.totalEpisodes}",
                            color = VioletLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        if (item.watchedEpisodes > 0) onEpisodeChange(item.watchedEpisodes - 1)
                                    },
                                color = DarkSurfaceVariant
                            ) {
                                Text("-1", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        if (item.watchedEpisodes < item.totalEpisodes) onEpisodeChange(item.watchedEpisodes + 1)
                                    },
                                color = VioletPrimary
                            ) {
                                Text("+1 Bölüm", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { item.watchedEpisodes.toFloat() / item.totalEpisodes.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = VioletPrimary,
                        trackColor = DarkSurfaceVariant
                    )
                }
            }
        }
    }
}
