package com.example.ui.screens

import androidx.activity.compose.BackHandler
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
fun ContentDetailScreen(
    item: MediaItem,
    viewModel: SeyirDefteriViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val catalog by viewModel.catalogState.collectAsState()

    var userRatingState by remember(item.id, item.userRating) { mutableStateOf(item.userRating ?: 8) }
    var userNotesState by remember(item.id, item.userNotes) { mutableStateOf(item.userNotes) }
    var watchedEpState by remember(item.id, item.watchedEpisodes) { mutableStateOf(item.watchedEpisodes) }

    val similarItems = remember(item, catalog) {
        catalog.filter { it.id != item.id && it.genres.any { g -> item.genres.contains(g) } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
            .testTag("content_detail_screen")
    ) {
        // Parallax Backdrop Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            AsyncImage(
                model = item.backdropUrl,
                contentDescription = item.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                DarkBackground.copy(alpha = 0.8f),
                                DarkBackground
                            )
                        )
                    )
            )

            // Back Button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .testTag("detail_back_button")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Geri", tint = Color.White)
            }

            // Title & Meta Overlay on Bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            color = TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${item.originalTitle} • ${item.year} • ${item.runtime}",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    Box(modifier = Modifier.padding(start = 8.dp)) {
                        RatingBadge(rating = item.rating)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Genre Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(item.genres) { genre ->
                    Surface(
                        shape = CircleShape,
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = genre,
                            color = VioletLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action CTAs (Add to collection & Find Similar & Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val nextStatus = when (item.watchStatus) {
                            "WATCHED" -> "WATCHING"
                            "WATCHING" -> "TO_WATCH"
                            "TO_WATCH" -> null
                            else -> "WATCHED"
                        }
                        viewModel.updateWatchStatus(item, nextStatus)
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp),
                    shape = RoundedCornerShape(23.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.watchStatus != null) StatusWatched else VioletPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (item.watchStatus != null) Icons.Default.CheckCircle else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (item.watchStatus) {
                            "WATCHED" -> "İzlendi"
                            "WATCHING" -> "İzleniyor"
                            "TO_WATCH" -> "İzlenecek"
                            else -> "Ekle"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        viewModel.selectTab(1)
                        viewModel.runAiFind(item.title + " benzeri dizi filmler")
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp),
                    shape = RoundedCornerShape(23.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VioletLight)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = VioletLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Benzerleri", color = VioletLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = {
                        val shareText = "🎬 ${item.title} (${item.year})\nPuan: ${item.userRating ?: item.rating}/10\n\n${item.overview}\n\n#SeyirDefteri"
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Paylaş"))
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(DarkSurfaceVariant, CircleShape)
                        .border(1.dp, DarkBorder, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Paylaş", tint = TextPrimary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Plot Summary in Turkish
            Text(text = "Özet", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.overview,
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Embedded Trailer Preview Player Card
            Text(text = "Resmi Fragman", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.77f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.trailerUrl))
                        context.startActivity(intent)
                    },
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = item.backdropUrl,
                        contentDescription = "Trailer",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = VioletPrimary,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Oynat",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Cast Horizontal Scroll
            if (item.cast.isNotEmpty()) {
                Text(text = "Oyuncu Kadrosu", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(item.cast) { actor ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(80.dp)
                        ) {
                            AsyncImage(
                                model = actor.photoUrl,
                                contentDescription = actor.name,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, VioletPrimary, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = actor.name,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = actor.character,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // TV Episode Progress Tracker
            if (item.type == "TV" && item.totalEpisodes > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Bölüm İlerlemesi", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "$watchedEpState / ${item.totalEpisodes} Bölüm", color = VioletLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { watchedEpState.toFloat() / item.totalEpisodes.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = VioletPrimary,
                            trackColor = DarkSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (watchedEpState > 0) {
                                        watchedEpState--
                                        viewModel.updateEpisodes(item, watchedEpState)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "-1 Bölüm", color = TextPrimary, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    if (watchedEpState < item.totalEpisodes) {
                                        watchedEpState++
                                        viewModel.updateEpisodes(item, watchedEpState)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)
                            ) {
                                Text(text = "+1 Bölüm İzledim", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Personal Rating & Notes
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Kişisel Değerlendirmeniz", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Puanınız: $userRatingState / 10", color = AmberRatingLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = userRatingState.toFloat(),
                            onValueChange = { userRatingState = it.toInt() },
                            valueRange = 1f..10f,
                            steps = 8,
                            modifier = Modifier.width(180.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = AmberRating,
                                activeTrackColor = AmberRating
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = userNotesState,
                        onValueChange = { userNotesState = it },
                        placeholder = { Text("Seyir notlarınız (ör. favori sahneler, yorumlar)...", color = TextMuted, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VioletPrimary,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.updateRatingAndNotes(item, userRatingState, userNotesState) },
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)
                    ) {
                        Text(text = "Notu Kaydet", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Similar Content Carousel
            if (similarItems.isNotEmpty()) {
                SectionHeader(title = "Benzer İçerikler", subtitle = "Aynı türdeki popüler yapımlar")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(similarItems) { simItem ->
                        MediaCard(item = simItem, onClick = { viewModel.openDetail(simItem) })
                    }
                }
            }
        }
    }
}
