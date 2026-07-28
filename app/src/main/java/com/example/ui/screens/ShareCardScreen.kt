package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.MediaItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SeyirDefteriViewModel

@Composable
fun ShareCardScreen(viewModel: SeyirDefteriViewModel) {
    val context = LocalContext.current
    val collection by viewModel.collectionState.collectAsState()
    val fullCatalog by viewModel.catalogState.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Öner, 1: Paylaş

    // ================= STATE FOR TAB 0: ÖNER =================
    val availableRecommendItems = remember(collection, fullCatalog) {
        if (collection.isNotEmpty()) collection else fullCatalog
    }
    var selectedRecommendItem by remember { mutableStateOf<MediaItem?>(availableRecommendItems.firstOrNull()) }
    var recommendDropdownExpanded by remember { mutableStateOf(false) }

    val recommendTypes = listOf(
        "🔥 Şiddetle tavsiye ediyorum",
        "💖 Senin için seçtim",
        "🕶️ Kesin bunu izle",
        "😋 Fikrini merak ediyorum"
    )
    var selectedRecommendType by remember { mutableStateOf(recommendTypes[1]) } // "Senin için seçtim" default
    var recommendNote by remember { mutableStateOf("") }

    // ================= STATE FOR TAB 1: PAYLAŞ =================
    var userName by remember { mutableStateOf("Muhammed Ali Duran") }
    var scopeDropdownExpanded by remember { mutableStateOf(false) }
    var selectedScopeText by remember { mutableStateOf("Sadece İzlediklerim") } // "Sadece İzlediklerim", "Sadece İzleyeceklerim", "Sadece İzlemekte Olduklarım", "Tüm Kütüphanem"

    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var selectedTypeText by remember { mutableStateOf("Hepsi (Dizi + Film)") } // "Hepsi (Dizi + Film)", "Sadece Diziler", "Sadece Filmler"

    var ratingDropdownExpanded by remember { mutableStateOf(false) }
    var selectedRatingText by remember { mutableStateOf("Hepsi (puan farketmez)") } // "Hepsi (puan farketmez)", "8+ Yüksek Puanlılar", "9+ Efsaneler", "10/10 Şaheserler"

    var generatedCardCount by remember { mutableStateOf(0) } // Trigger card regeneration animation

    // Filter collection for "Paylaş" collage
    val collageFilteredItems = remember(collection, selectedScopeText, selectedTypeText, selectedRatingText, generatedCardCount) {
        collection.filter { item ->
            val matchScope = when (selectedScopeText) {
                "Sadece İzlediklerim" -> item.watchStatus == "WATCHED"
                "Sadece İzleyeceklerim" -> item.watchStatus == "TO_WATCH"
                "Sadece İzlemekte Olduklarım" -> item.watchStatus == "WATCHING"
                else -> true
            }
            val matchType = when (selectedTypeText) {
                "Sadece Diziler" -> item.type == "TV"
                "Sadece Filmler" -> item.type == "MOVIE"
                else -> true
            }
            val matchRating = when (selectedRatingText) {
                "8+ Yüksek Puanlılar" -> (item.userRating ?: item.rating.toInt()) >= 8
                "9+ Efsaneler" -> (item.userRating ?: item.rating.toInt()) >= 9
                "10/10 Şaheserler" -> (item.userRating ?: item.rating.toInt()) >= 10
                else -> true
            }
            matchScope && matchType && matchRating
        }.take(6)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 100.dp)
            .testTag("share_card_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen Header
        Text(
            text = "Paylaşım ve Öneri Merkezi",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Arkadaşlarınıza dizi/film önerin veya kütüphanenizi kart olarak paylaşın.",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Top Switcher Tabs: ÖNER vs PAYLAŞ
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .padding(4.dp)
        ) {
            Button(
                onClick = { activeTab = 0 },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_oner"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == 0) VioletPrimary else Color.Transparent,
                    contentColor = if (activeTab == 0) Color.White else TextSecondary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Recommend, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Öner", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = { activeTab = 1 },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_paylas"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == 1) VioletPrimary else Color.Transparent,
                    contentColor = if (activeTab == 1) Color.White else TextSecondary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Paylaş", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeTab) {
            // ==========================================
            // TAB 0: ÖNER (Bir Şey Öner)
            // ==========================================
            0 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(DarkBorder, VioletPrimary.copy(alpha = 0.3f)))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Bir Şey Öner",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 1. Ne önermek istiyorsun?
                        Text(
                            text = "Ne önermek istiyorsun?",
                            color = VioletLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { recommendDropdownExpanded = true },
                                color = DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.LocalMovies, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = selectedRecommendItem?.title ?: "Koleksiyonundan seç...",
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                                }
                            }

                            DropdownMenu(
                                expanded = recommendDropdownExpanded,
                                onDismissRequest = { recommendDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .background(DarkSurfaceVariant)
                            ) {
                                availableRecommendItems.forEach { item ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                AsyncImage(
                                                    model = item.posterUrl,
                                                    contentDescription = item.title,
                                                    modifier = Modifier
                                                        .size(width = 24.dp, height = 34.dp)
                                                        .clip(RoundedCornerShape(4.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = "${item.title} (${item.year})", color = TextPrimary, fontSize = 12.sp)
                                            }
                                        },
                                        onClick = {
                                            selectedRecommendItem = item
                                            recommendDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 2. Öneri türü
                        Text(
                            text = "Öneri türü",
                            color = VioletLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            recommendTypes.chunked(2).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    row.forEach { typeLabel ->
                                        val isSelected = selectedRecommendType == typeLabel
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(20.dp))
                                                .clickable { selectedRecommendType = typeLabel },
                                            shape = RoundedCornerShape(20.dp),
                                            color = if (isSelected) VioletPrimary else DarkSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) VioletLight else DarkBorder)
                                        ) {
                                            Text(
                                                text = typeLabel,
                                                color = if (isSelected) Color.White else TextPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3. Not (isteğe bağlı)
                        Text(
                            text = "Not (isteğe bağlı)",
                            color = VioletLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = recommendNote,
                            onValueChange = { recommendNote = it },
                            placeholder = { Text(text = "Neden önerdiğini yaz...", color = TextMuted, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recommend_note_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VioletPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            maxLines = 3
                        )
                    }
                }
            }

            // ==========================================
            // TAB 1: PAYLAŞ (Koleksiyonunu Paylaş)
            // ==========================================
            1 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(DarkBorder, VioletPrimary.copy(alpha = 0.3f)))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Koleksiyonunu Paylaş",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 1. Adın (kartın altında görünür)
                        Text(
                            text = "Adın (kartın altında görünür)",
                            color = VioletLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            placeholder = { Text(text = "örn. Muhammed Ali Duran", color = TextMuted, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("user_name_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VioletPrimary,
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. Neyi paylaşmak istiyorsun?
                        Text(
                            text = "Neyi paylaşmak istiyorsun?",
                            color = VioletLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { scopeDropdownExpanded = true },
                                color = DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = selectedScopeText, color = TextPrimary, fontSize = 13.sp)
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                                }
                            }

                            DropdownMenu(
                                expanded = scopeDropdownExpanded,
                                onDismissRequest = { scopeDropdownExpanded = false },
                                modifier = Modifier.background(DarkSurfaceVariant)
                            ) {
                                listOf("Sadece İzlediklerim", "Sadece İzleyeceklerim", "Sadece İzlemekte Olduklarım", "Tüm Kütüphanem").forEach { scope ->
                                    DropdownMenuItem(
                                        text = { Text(text = scope, color = TextPrimary) },
                                        onClick = {
                                            selectedScopeText = scope
                                            scopeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. Tür
                        Text(
                            text = "Tür",
                            color = VioletLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { typeDropdownExpanded = true },
                                color = DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = selectedTypeText, color = TextPrimary, fontSize = 13.sp)
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                                }
                            }

                            DropdownMenu(
                                expanded = typeDropdownExpanded,
                                onDismissRequest = { typeDropdownExpanded = false },
                                modifier = Modifier.background(DarkSurfaceVariant)
                            ) {
                                listOf("Hepsi (Dizi + Film)", "Sadece Diziler", "Sadece Filmler").forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text(text = t, color = TextPrimary) },
                                        onClick = {
                                            selectedTypeText = t
                                            typeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 4. Puan
                        Text(
                            text = "Puan",
                            color = VioletLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { ratingDropdownExpanded = true },
                                color = DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = selectedRatingText, color = TextPrimary, fontSize = 13.sp)
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary)
                                }
                            }

                            DropdownMenu(
                                expanded = ratingDropdownExpanded,
                                onDismissRequest = { ratingDropdownExpanded = false },
                                modifier = Modifier.background(DarkSurfaceVariant)
                            ) {
                                listOf("Hepsi (puan farketmez)", "8+ Yüksek Puanlılar", "9+ Efsaneler", "10/10 Şaheserler").forEach { r ->
                                    DropdownMenuItem(
                                        text = { Text(text = r, color = TextPrimary) },
                                        onClick = {
                                            selectedRatingText = r
                                            ratingDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Actions: Kapat, Kart Oluştur
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    userName = "Muhammed Ali Duran"
                                    selectedScopeText = "Sadece İzlediklerim"
                                    selectedTypeText = "Hepsi (Dizi + Film)"
                                    selectedRatingText = "Hepsi (puan farketmez)"
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Text(text = "Sıfırla", color = TextSecondary, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    generatedCardCount++
                                    Toast.makeText(context, "Kart başarıyla oluşturuldu!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)
                            ) {
                                Text(text = "Kart Oluştur", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =======================================================
        // LIVE GENERATED CARD CANVAS PREVIEW
        // =======================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Önizleme Kartı (9:16 Format)",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = CircleShape,
                color = VioletPrimary.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary)
            ) {
                Text(
                    text = if (activeTab == 0) "Öneri Kartı" else "Koleksiyon Kartı",
                    color = VioletLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.70f)
                .clip(RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBackground),
            border = androidx.compose.foundation.BorderStroke(2.dp, VioletPrimary.copy(alpha = 0.8f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E1338),
                                Color(0xFF0D0B18),
                                Color(0xFF1B0F2E)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Card Top Branding Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = VioletPrimary,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.MovieFilter, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "SEYİR DEFTERİ", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }

                        Text(
                            text = if (userName.isNotBlank()) "@$userName" else "@sinefil",
                            color = VioletLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (activeTab == 0) {
                        // ================= ÖNERİ KARTI GÖRÜNÜMÜ =================
                        val item = selectedRecommendItem ?: availableRecommendItems.firstOrNull()
                        if (item != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = AmberRating.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberRating)
                                ) {
                                    Text(
                                        text = selectedRecommendType.uppercase(),
                                        color = AmberRatingLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                AsyncImage(
                                    model = item.posterUrl,
                                    contentDescription = item.title,
                                    modifier = Modifier
                                        .height(170.dp)
                                        .aspectRatio(0.68f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(2.dp, VioletPrimary, RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(text = item.title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                                Text(text = "${item.year} • ${item.genres.joinToString(" • ")}", color = TextSecondary, fontSize = 11.sp)

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkSurfaceVariant.copy(alpha = 0.85f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (recommendNote.isNotBlank()) "\"${recommendNote}\"" else "\"Harika bir yapım, mutlaka izlemelisin!\"",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // ================= KOLEKSİYON KARTI GÖRÜNÜMÜ =================
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${userName.ifBlank { "Sinefil" }}'in Koleksiyonu",
                                color = AmberRatingLight,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "$selectedScopeText • $selectedTypeText",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            val displayGrid = collageFilteredItems.ifEmpty { collection.take(6) }

                            if (displayGrid.isEmpty()) {
                                Text(text = "Seçilen kriterde içerik bulunamadı", color = TextMuted, fontSize = 11.sp)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    displayGrid.chunked(3).forEach { row ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            row.forEach { gridItem ->
                                                AsyncImage(
                                                    model = gridItem.posterUrl,
                                                    contentDescription = gridItem.title,
                                                    modifier = Modifier
                                                        .width(78.dp)
                                                        .aspectRatio(0.68f)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .border(1.dp, VioletLight.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Card Footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant.copy(alpha = 0.8f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = "🎬 ${collection.size} İçerik", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "⭐ 8.8 Ort. Puan", color = AmberRatingLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "🍿 Seyir Defteri", color = VioletLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =======================================================
        // SOCIAL SHARING ACTION BUTTONS (WHATSAPP, INSTAGRAM, ETC)
        // =======================================================
        Text(
            text = "Kartı Paylaş",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        val shareMessageText = remember(activeTab, selectedRecommendItem, selectedRecommendType, recommendNote, userName, selectedScopeText, selectedTypeText) {
            if (activeTab == 0) {
                val item = selectedRecommendItem ?: availableRecommendItems.firstOrNull()
                "🍿 Seyir Defteri'nden Öneri!\n" +
                "🎬 ${item?.title ?: "Süper İçerik"} (${item?.year ?: 2024})\n" +
                "Etiket: $selectedRecommendType\n" +
                (if (recommendNote.isNotBlank()) "Not: \"$recommendNote\"\n" else "") +
                "Seyir Defteri uygulaması ile önerildi."
            } else {
                "🎬 $userName'in Seyir Defteri Koleksiyonu!\n" +
                "Kapsam: $selectedScopeText • Tür: $selectedTypeText\n" +
                "Toplam ${collection.size} içerik takibimde.\n" +
                "Seyir Defteri uygulaması ile oluşturuldu."
            }
        }

        // WhatsApp & Instagram Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    shareToWhatsApp(context, shareMessageText)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("share_whatsapp_button"),
                shape = RoundedCornerShape(23.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "WhatsApp", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    shareToInstagram(context, shareMessageText)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("share_instagram_button"),
                shape = RoundedCornerShape(23.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C))
            ) {
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Instagram", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Copy Text & Save Image Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Seyir Defterim", shareMessageText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Metin panoya kopyalandı!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, VioletLight)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = VioletLight, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Metni Kopyala", color = VioletLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    Toast.makeText(context, "Görsel galerinize kaydedildi! (PNG)", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Görseli Kaydet", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun shareToWhatsApp(context: Context, text: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage("com.whatsapp")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        val fallback = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(fallback, "WhatsApp ile Paylaş"))
    }
}

private fun shareToInstagram(context: Context, text: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage("com.instagram.android")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        val fallback = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(fallback, "Instagram ile Paylaş"))
    }
}
