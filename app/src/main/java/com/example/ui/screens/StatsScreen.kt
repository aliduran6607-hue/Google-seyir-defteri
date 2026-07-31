package com.example.ui.screens

import java.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun StatsScreen(viewModel: SeyirDefteriViewModel) {
    val collection by viewModel.collectionState.collectAsState()

    val totalContent = collection.size
    val completedCount = collection.count { it.watchStatus == "WATCHED" }
    val watchingCount = collection.count { it.watchStatus == "WATCHING" }
    val toWatchCount = collection.count { it.watchStatus == "TO_WATCH" }

    val addedThisMonthCount = remember(collection) {
        val currentCal = Calendar.getInstance()
        val currentMonth = currentCal.get(Calendar.MONTH)
        val currentYear = currentCal.get(Calendar.YEAR)
        collection.count { item ->
            val cal = Calendar.getInstance().apply { timeInMillis = item.addedDateMillis }
            cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
        }
    }

    val heatmapData = remember(collection) {
        val now = System.currentTimeMillis()
        val dayMillis = 86400000L
        val counts = IntArray(80) { 0 }
        collection.forEach { item ->
            val daysAgoAdded = ((now - item.addedDateMillis) / dayMillis).toInt()
            if (daysAgoAdded in 0..79) {
                counts[79 - daysAgoAdded] += 1
            }
            val daysAgoUpdated = ((now - item.lastUpdatedMillis) / dayMillis).toInt()
            if (daysAgoUpdated in 0..79 && daysAgoUpdated != daysAgoAdded) {
                counts[79 - daysAgoUpdated] += 1
            }
        }
        counts
    }

    val ratedItems = collection.mapNotNull { it.userRating }
    val avgRating = if (ratedItems.isNotEmpty()) ratedItems.average() else 0.0

    val top5Items = remember(collection) {
        collection.sortedByDescending { it.userRating ?: it.rating.toInt() }.take(5)
    }

    // Genre distribution map (based on watched items first, or collection if no watched items)
    val watchedItems = remember(collection) { collection.filter { it.watchStatus == "WATCHED" } }
    val targetAnalysisList = if (watchedItems.isNotEmpty()) watchedItems else collection

    val genreCounts = remember(targetAnalysisList) {
        val map = mutableMapOf<String, Int>()
        targetAnalysisList.forEach { item ->
            item.genres.forEach { genre ->
                map[genre] = (map[genre] ?: 0) + 1
            }
        }
        map.entries.sortedByDescending { it.value }.take(6)
    }

    // Dynamic watch profile calculation based strictly on real user items
    val watchProfile = remember(genreCounts, ratedItems, collection, watchedItems) {
        if (collection.isEmpty()) {
            Triple(
                "Henüz Veri Yok",
                "Kütüphanenizde henüz film veya dizi bulunmuyor. Eklediğiniz içeriklere göre izleme profiliniz burada otomatik hesaplanacaktır.",
                ""
            )
        } else if (genreCounts.isEmpty()) {
            Triple(
                "Tür Bilgisi Eksik",
                "Eklediğiniz içeriklerin tür bilgisi tanımlanmamış.",
                ""
            )
        } else {
            val totalTarget = targetAnalysisList.size
            val topGenres = genreCounts.take(2)

            val mainTitle = when (topGenres.size) {
                2 -> "${topGenres[0].key} & ${topGenres[1].key} Ağırlıklı"
                1 -> "${topGenres[0].key} Odaklı"
                else -> "Genel Sinema & Dizi Sever"
            }

            val isWatchedBased = watchedItems.isNotEmpty()
            val scopeText = if (isWatchedBased) "İzlediğiniz $totalTarget içerik" else "Kütüphanenizdeki $totalTarget içerik"

            val topGenreText = if (topGenres.size >= 2) {
                val pct1 = (topGenres[0].value * 100) / totalTarget
                val pct2 = (topGenres[1].value * 100) / totalTarget
                "en çok ${topGenres[0].key} (%$pct1) ve ${topGenres[1].key} (%$pct2) türlerinden oluşuyor."
            } else {
                val pct = (topGenres[0].value * 100) / totalTarget
                "ağırlıklı olarak ${topGenres[0].key} (%$pct) türünden oluşuyor."
            }

            val ratingText = if (ratedItems.isNotEmpty()) {
                " Puanladığınız ${ratedItems.size} içeriğin ortalaması ${String.format("%.1f", avgRating)}/10."
            } else {
                " Henüz kişisel puan verdiğiniz içerik bulunmuyor."
            }

            val desc = "$scopeText $topGenreText$ratingText"
            Triple(mainTitle, desc, topGenres.firstOrNull()?.key ?: "")
        }
    }

    // Rating histogram map (1 to 10)
    val ratingHist = remember(collection) {
        val map = (1..10).associateWith { 0 }.toMutableMap()
        collection.forEach { item ->
            val r = item.userRating ?: item.rating.toInt().coerceIn(1, 10)
            map[r] = (map[r] ?: 0) + 1
        }
        map
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 100.dp)
            .testTag("stats_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sinema İstatistikleri",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "İzleme alışkanlıklarınız ve kişisel grafikleriniz",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Summary Metric Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryStatCard(
                title = "Toplam İçerik",
                value = "$totalContent",
                subtext = "Kütüphanede",
                icon = Icons.Default.MovieFilter,
                accentColor = VioletPrimary,
                modifier = Modifier.weight(1f)
            )
            SummaryStatCard(
                title = "Ort. Puanın",
                value = if (ratedItems.isNotEmpty()) String.format("%.1f", avgRating) else "-",
                subtext = if (ratedItems.isNotEmpty()) "/ 10 (${ratedItems.size} puanlama)" else "Puanlanmadı",
                icon = Icons.Default.Star,
                accentColor = AmberRating,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryStatCard(
                title = "Tamamlanan",
                value = "$completedCount",
                subtext = "Bitirilen film/dizi",
                icon = Icons.Default.CheckCircle,
                accentColor = StatusWatched,
                modifier = Modifier.weight(1f)
            )
            SummaryStatCard(
                title = "Bu Ay Eklenen",
                value = "$addedThisMonthCount",
                subtext = "Yeni kayıt",
                icon = Icons.Default.TrendingUp,
                accentColor = StatusWatching,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // "İzleme Profili" Insight Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = VioletPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = VioletLight, modifier = Modifier.size(26.dp))
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "İzleme Profili Analizi", color = VioletLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = watchProfile.first,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = watchProfile.second,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Status Distribution Progress Bars
        Text(text = "Durum Dağılımı", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                ProgressBarRow(label = "İzlendi", count = completedCount, total = totalContent, color = StatusWatched)
                Spacer(modifier = Modifier.height(10.dp))
                ProgressBarRow(label = "İzleniyor", count = watchingCount, total = totalContent, color = StatusWatching)
                Spacer(modifier = Modifier.height(10.dp))
                ProgressBarRow(label = "İzlenecek", count = toWatchCount, total = totalContent, color = StatusToWatch)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Genre Breakdown Horizontal Bar Chart
        Text(text = "En Çok İzlenen Türler", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                val maxGenreCount = (genreCounts.maxOfOrNull { it.value } ?: 1).coerceAtLeast(1)
                genreCounts.forEach { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = entry.key, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.width(90.dp))
                        LinearProgressIndicator(
                            progress = { entry.value.toFloat() / maxGenreCount.toFloat() },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(CircleShape),
                            color = VioletPrimary,
                            trackColor = DarkSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "${entry.value}", color = VioletLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Rating Histogram (1-10)
        Text(text = "Puan Dağılım Histogramı", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                val maxRatingCount = (ratingHist.values.maxOrNull() ?: 1).coerceAtLeast(1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    (1..10).forEach { ratingNum ->
                        val count = ratingHist[ratingNum] ?: 0
                        val heightFraction = (count.toFloat() / maxRatingCount.toFloat()).coerceAtLeast(0.08f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "$count", color = AmberRatingLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .fillMaxHeight(heightFraction)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (ratingNum >= 8) AmberRating else VioletPrimary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$ratingNum", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // GitHub Contribution Style Activity Heatmap (12 Months / Weeks)
        Text(text = "Yıllık İzleme Isı Haritası (2026)", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Son 12 Ay Aktivite Yoğunluğu", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(10.dp))

                // Heatmap Grid (5 rows x 16 cols)
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    repeat(5) { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            repeat(16) { col ->
                                val index = row * 16 + col
                                val count = heatmapData.getOrElse(index) { 0 }
                                val cellColor = when {
                                    count >= 3 -> VioletPrimary
                                    count == 2 -> VioletDark
                                    count == 1 -> VioletPrimary.copy(alpha = 0.5f)
                                    else -> Color(0xFF1E1830)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(cellColor)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Top 5 Highest Rated Ranked List
        Text(text = "Favori 5 Yapımınız", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            top5Items.forEachIndexed { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#${index + 1}",
                            color = AmberRating,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.width(32.dp)
                        )
                        AsyncImage(
                            model = item.posterUrl,
                            contentDescription = item.title,
                            modifier = Modifier
                                .size(width = 40.dp, height = 56.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            Text(text = item.genres.joinToString(", "), color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                        }
                        RatingBadge(rating = item.rating)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryStatCard(
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(text = subtext, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProgressBarRow(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val fraction = if (total > 0) count.toFloat() / total.toFloat() else 0f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = "$count içerik (%${(fraction * 100).toInt()})", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = DarkSurfaceVariant
        )
    }
}
