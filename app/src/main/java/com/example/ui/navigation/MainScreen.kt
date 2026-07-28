package com.example.ui.navigation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SeyirDefteriViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainScreen(viewModel: SeyirDefteriViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedItem by viewModel.selectedMediaItem.collectAsState()

    // Observe ViewModel Toast Messages
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Core Tab Screens
        when (currentTab) {
            0 -> HomeScreen(viewModel = viewModel, onOpenSearch = { viewModel.selectTab(1) })
            1 -> SearchAiScreen(viewModel = viewModel)
            2 -> CollectionScreen(viewModel = viewModel)
            3 -> StatsScreen(viewModel = viewModel)
            4 -> ProfileScreen(viewModel = viewModel)
            5 -> ShareCardScreen(viewModel = viewModel)
        }

        // Detail View Overlay if selected
        AnimatedVisibility(
            visible = selectedItem != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            selectedItem?.let { item ->
                ContentDetailScreen(
                    item = item,
                    viewModel = viewModel,
                    onBack = { viewModel.closeDetail() }
                )
            }
        }

        // Bottom Navigation Bar
        if (selectedItem == null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .clip(CircleShape)
                    .border(1.dp, DarkBorder, CircleShape)
                    .testTag("bottom_nav_bar"),
                color = DarkSurface.copy(alpha = 0.92f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavItem(
                        icon = Icons.Default.Explore,
                        label = "Keşfet",
                        isSelected = currentTab == 0,
                        onClick = { viewModel.selectTab(0) }
                    )
                    NavItem(
                        icon = Icons.Default.AutoAwesome,
                        label = "Arama",
                        isSelected = currentTab == 1,
                        onClick = { viewModel.selectTab(1) }
                    )
                    NavItem(
                        icon = Icons.Default.Bookmarks,
                        label = "Kütüphanem",
                        isSelected = currentTab == 2,
                        onClick = { viewModel.selectTab(2) }
                    )
                    NavItem(
                        icon = Icons.Default.BarChart,
                        label = "İstatistik",
                        isSelected = currentTab == 3,
                        onClick = { viewModel.selectTab(3) }
                    )
                    NavItem(
                        icon = Icons.Default.Share,
                        label = "Paylaş",
                        isSelected = currentTab == 5,
                        onClick = { viewModel.selectTab(5) }
                    )
                    NavItem(
                        icon = Icons.Default.Person,
                        label = "Profil",
                        isSelected = currentTab == 4,
                        onClick = { viewModel.selectTab(4) }
                    )
                }
            }
        }
    }
}

@Composable
fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
            .testTag("nav_item_${label.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) VioletPrimary else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = if (isSelected) VioletLight else TextSecondary,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}
