package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.MediaItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SeyirDefteriViewModel

@Composable
fun HomeScreen(
    viewModel: SeyirDefteriViewModel,
    onOpenSearch: () -> Unit
) {
    val catalog by viewModel.catalogState.collectAsState()
    val heroItem = catalog.firstOrNull() ?: return
    val selectedGenre by viewModel.selectedGenre.collectAsState()

    val genres = listOf("Tümü", "Aksiyon", "Drama", "Komedi", "Bilim Kurgu", "Suç", "Gerilim", "Romantik", "Tarih")

    val trendingItems = catalog.filter { it.rating >= 8.5f }
    val topRatedItems = catalog.sortedByDescending { it.rating }
    val newThisWeek = catalog.filter { it.year >= 2023 }
    val becauseYouWatched = catalog.filter { it.genres.any { g -> g == "Bilim Kurgu" || g == "Suç" || g == "Gerilim" } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
            .testTag("home_screen")
    ) {
        // Hero Backdrop with Header and Search Controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
        ) {
            AsyncImage(
                model = heroItem.backdropUrl,
                contentDescription = heroItem.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.85f),
                                Color.Black.copy(alpha = 0.4f),
                                DarkBackground.copy(alpha = 0.85f),
                                DarkBackground
                            ),
                            startY = 0f,
                            endY = 1100f
                        )
                    )
            )

            // Top Header & Search Controls Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .align(Alignment.TopCenter)
            ) {
                // Top Header Row (Logo + Title + Action Badges)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Logo & Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            AsyncImage(
                                model = R.drawable.app_icon_seyirdefteri,
                                contentDescription = "Seyir Defteri Logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row {
                                Text(
                                    text = "Seyir ",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Defteri",
                                    color = Color(0xFFFF79C6),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // Right: Action Badges (Admin Crown & Notification Bell)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Crown Admin Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurface.copy(alpha = 0.9f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberRating),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = "Admin",
                                    tint = AmberRating,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Notification Bell
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurface.copy(alpha = 0.9f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Bildirimler",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle
                Text(
                    text = "Dizi ve filmlerini ara, keşfet, takip et",
                    color = TextPrimary.copy(alpha = 0.95f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Main Search Input Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable { onOpenSearch() }
                        .border(1.dp, DarkBorder, CircleShape)
                        .testTag("hero_search_bar"),
                    color = DarkSurface.copy(alpha = 0.88f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Ara",
                                tint = VioletPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Dizi ara... (örn: Vikings, Shogun)",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = VioletPrimary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Ara",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Hero Details on Bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = VioletPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "TREND1",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = heroItem.genres.joinToString(" • "),
                        color = VioletLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = heroItem.title,
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = AmberRating,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${heroItem.rating} / 10",
                            color = AmberRatingLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(text = "${heroItem.year}", color = TextSecondary, fontSize = 13.sp)
                    Text(text = heroItem.runtime, color = TextSecondary, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hero CTA Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.openDetail(heroItem) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Detay",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Detayları İncele", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.updateWatchStatus(heroItem, "TO_WATCH") },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VioletLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = "Ekle",
                            tint = VioletLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "İzleneceklere Ekle", color = VioletLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Pills Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre ->
                CategoryPill(
                    title = genre,
                    isSelected = genre == selectedGenre,
                    onClick = { viewModel.setSelectedGenre(genre) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Carousel 1: Trendler
        SectionHeader(
            title = "Trendler",
            subtitle = "Şu an en çok konuşulan yapımlar"
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trendingItems) { item ->
                MediaCard(item = item, onClick = { viewModel.openDetail(item) })
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Carousel 2: En İyiler (Top Rated)
        SectionHeader(
            title = "En Yüksek Puanlılar",
            subtitle = "Sinefillerden tam not alanlar"
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(topRatedItems) { item ->
                MediaCard(item = item, onClick = { viewModel.openDetail(item) })
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Carousel 3: Bu Hafta Yeni
        SectionHeader(
            title = "Bu Hafta Yeni",
            subtitle = "Platformlara yeni eklenen diziler ve filmler"
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(newThisWeek) { item ->
                MediaCard(item = item, onClick = { viewModel.openDetail(item) })
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Carousel 4: İzlediklerinize Göre
        SectionHeader(
            title = "İzlediklerinize Göre",
            subtitle = "Interstellar & Dark sevenler için özel seçkiler"
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(becauseYouWatched) { item ->
                MediaCard(item = item, onClick = { viewModel.openDetail(item) })
            }
        }
    }
}
