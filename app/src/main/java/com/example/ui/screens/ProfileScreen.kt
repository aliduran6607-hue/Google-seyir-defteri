package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.SeyirDefteriViewModel

@Composable
fun ProfileScreen(viewModel: SeyirDefteriViewModel) {
    val context = LocalContext.current
    val collection by viewModel.collectionState.collectAsState()

    var showJsonDialog by remember { mutableStateOf(false) }
    var jsonTextState by remember { mutableStateOf("") }
    var dialogMode by remember { mutableStateOf(0) } // 0: Export, 1: Import

    val newEpNotify by viewModel.newEpisodeNotify.collectAsState()
    val sequelNotify by viewModel.sequelMovieNotify.collectAsState()
    val weeklyNotify by viewModel.weeklyDigestNotify.collectAsState()
    val selectedLang by viewModel.selectedLanguage.collectAsState()
    val customTmdbKey by viewModel.customTmdbKey.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    LaunchedEffect(isDarkMode) {
        AppThemeState.isDark = isDarkMode
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 100.dp)
            .testTag("profile_screen")
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Profil ve Ayarlar",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Avatar Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop",
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, VioletPrimary, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Ahmet Yılmaz", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = AmberRating.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Usta Sinefil",
                            color = AmberRatingLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Üye: Ocak 2024", color = TextSecondary, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Firebase Cloud Sync Indicator
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = StatusWatched, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Firebase Bulut Senkronizasyonu Aktif", color = StatusWatched, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Notification Toggles Section
        Text(text = "Bildirim Tercihleri", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                SettingToggleRow(
                    title = "Yeni Bölüm Bildirimleri",
                    subtitle = "Takip ettiğiniz dizilerin yeni bölümleri yayınlandığında haber ver",
                    isChecked = newEpNotify,
                    onCheckedChange = { viewModel.newEpisodeNotify.value = it }
                )
                Divider(color = DarkBorder)
                SettingToggleRow(
                    title = "Devam Filmleri & Fragmanlar",
                    subtitle = "İzlediğiniz filmlerin devam halkaları duyurulduğunda bildirim gönder",
                    isChecked = sequelNotify,
                    onCheckedChange = { viewModel.sequelMovieNotify.value = it }
                )
                Divider(color = DarkBorder)
                SettingToggleRow(
                    title = "Haftalık Sinema Özeti",
                    subtitle = "Her Pazar haftalık izleme istatistiklerinizi özetle",
                    isChecked = weeklyNotify,
                    onCheckedChange = { viewModel.weeklyDigestNotify.value = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Data Backup & Restore Section
        Text(text = "Yedekleme ve Veri", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            dialogMode = 0
                            jsonTextState = viewModel.exportBackupJson()
                            showJsonDialog = true
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "JSON Dışa Aktar (Yedekle)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Kütüphanenizi başkalarıyla paylaşın veya yedekleyin", color = TextSecondary, fontSize = 11.sp)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = DarkBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            dialogMode = 1
                            jsonTextState = ""
                            showJsonDialog = true
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, tint = VioletPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "JSON İçe Aktar (Geri Yükle)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Daha önce yedeklenen kütüphane dosyasını yükleyin", color = TextSecondary, fontSize = 11.sp)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Language Settings Section
        Text(text = if (selectedLang == "tr") "Uygulama Dili" else "App Language", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Turkish Option
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.selectedLanguage.value = "tr" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedLang == "tr") VioletPrimary.copy(alpha = 0.25f) else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedLang == "tr") VioletPrimary else DarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🇹🇷", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Türkçe",
                                color = if (selectedLang == "tr") VioletLight else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // English Option
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.selectedLanguage.value = "en" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedLang == "en") VioletPrimary.copy(alpha = 0.25f) else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedLang == "en") VioletPrimary else DarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🇬🇧", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "English",
                                color = if (selectedLang == "en") VioletLight else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Theme Section
        Text(text = if (selectedLang == "tr") "Uygulama Teması" else "App Theme", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Dark Theme Option
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.isDarkMode.value = true },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDarkMode) VioletPrimary.copy(alpha = 0.25f) else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDarkMode) VioletPrimary else DarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🌙", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedLang == "tr") "Koyu Tema" else "Dark Theme",
                                color = if (isDarkMode) VioletLight else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Light Theme Option
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.isDarkMode.value = false },
                        shape = RoundedCornerShape(12.dp),
                        color = if (!isDarkMode) VioletPrimary.copy(alpha = 0.25f) else DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!isDarkMode) VioletPrimary else DarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "☀️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedLang == "tr") "Açık Tema" else "Light Theme",
                                color = if (!isDarkMode) VioletLight else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live API Data Services Info Card
        Text(text = if (selectedLang == "tr") "Canlı Veri Servisleri" else "Live Data Services", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = StatusWatched, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedLang == "tr") "TMDB & TVMaze Canlı Bağlantı Aktif" else "TMDB & TVMaze Live Connection Active",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (selectedLang == "tr")
                        "Uygulamada dahili TMDB v3 ve TVMaze canlı arama servisi aktiftir. Yazmaya başladığınız an tüm dünyadaki film ve diziler anında listelenir. Dilerseniz aşağıya kendi özel TMDB API Key'inizi de ekleyebilirsiniz."
                    else
                        "Built-in active TMDB v3 & TVMaze search is enabled. Movies and series worldwide are fetched live as you type. You may also enter your custom TMDB API Key below if desired.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = customTmdbKey,
                    onValueChange = { viewModel.customTmdbKey.value = it },
                    label = { Text(if (selectedLang == "tr") "Özel TMDB API Key (İsteğe Bağlı)" else "Custom TMDB API Key (Optional)", fontSize = 11.sp, color = TextMuted) },
                    placeholder = { Text("Örn: 38b3017a...", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VioletPrimary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Seyir Defteri v1.0.0", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = "Created by Muhammed Ali Duran", color = TextSecondary, fontSize = 11.sp)
            }
        }
    }

    // JSON Export / Import Dialog
    if (showJsonDialog) {
        AlertDialog(
            onDismissRequest = { showJsonDialog = false },
            containerColor = DarkSurfaceVariant,
            title = {
                Text(
                    text = if (dialogMode == 0) "Kütüphane Yedeği (JSON)" else "Yedek İçe Aktar",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (dialogMode == 0) "Aşağıdaki metni kopyalayarak yedekleyebilirsiniz:" else "Yedek kütüphane JSON metnini yapıştırın:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = jsonTextState,
                        onValueChange = { jsonTextState = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        readOnly = dialogMode == 0,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (dialogMode == 0) {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Yedek", jsonTextState))
                        } else {
                            viewModel.importBackupJson(jsonTextState)
                        }
                        showJsonDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VioletPrimary)
                ) {
                    Text(text = if (dialogMode == 0) "Kopyala" else "Yükle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJsonDialog = false }) {
                    Text(text = "Kapat", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VioletPrimary,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkSurfaceVariant
            )
        )
    }
}
