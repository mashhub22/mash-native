package com.mash.tacticalhub

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mash.tacticalhub.ui.screens.*
import com.mash.tacticalhub.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            // States
            var currentScreen by remember { mutableStateOf("screen-hub") }
            var isStealth by remember { mutableStateOf(false) }
            val favsList = remember { mutableStateListOf<String>() }

            // Persistence
            val prefs = getSharedPreferences("tactical_hub_prefs", Context.MODE_PRIVATE)

            LaunchedEffect(Unit) {
                isStealth = prefs.getBoolean("is_stealth", false)
                val savedFavs = prefs.getStringSet("favs", emptySet()) ?: emptySet()
                savedFavs.forEach { favsList.add(it) }
            }

            TacticalHubTheme(isStealth = isStealth) {
                val primaryColor = if (isStealth) StealthPrimary else StandardOnPrimary
                val headerBg = if (isStealth) StealthBackground else StandardSurface
                val borderCol = if (isStealth) StealthBorder else StandardBorder

                Scaffold(
                    topBar = {
                        // Header panel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(headerBg)
                                .border(width = 1.dp, color = borderCol)
                                .statusBarsPadding()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Title & Logo Link
                            Row(
                                modifier = Modifier
                                    .clickable { currentScreen = "screen-hub" }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(if (isStealth) Color(0xFF2A0000) else StandardPrimary, RoundedCornerShape(4.dp))
                                        .border(1.dp, primaryColor, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "M",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "MASH TACTICAL",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = primaryColor,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "v1.0 OFF-LINE",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp,
                                        color = Color.Gray
                                    )
                                }
                            }

                            // Dynamic Actions
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Hub Shortcut
                                if (currentScreen != "screen-hub") {
                                    IconButton(
                                        onClick = { currentScreen = "screen-hub" }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.List,
                                            contentDescription = "Hub Menu",
                                            tint = primaryColor
                                        )
                                    }
                                }

                                // Toggle Stealth MODE
                                Button(
                                    onClick = {
                                        isStealth = !isStealth
                                        prefs.edit().putBoolean("is_stealth", isStealth).apply()
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isStealth) Color(0xFF2A0000) else Color(0xFF2B2B2B)
                                    ),
                                    border = BorderStroke(1.dp, if (isStealth) StealthPrimary else Color.Gray),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(
                                        text = "СТЕЛС",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isStealth) StealthPrimary else Color.White
                                    )
                                }

                                // Timer shortcut
                                Button(
                                    onClick = { currentScreen = "screen-timer" },
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentScreen == "screen-timer") {
                                            if (isStealth) StealthPrimary else StandardPrimary
                                        } else {
                                            Color.Black
                                        }
                                    ),
                                    border = BorderStroke(1.dp, borderCol),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(
                                        text = "ТАЙМЕР",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (currentScreen == "screen-timer") Color.White else if (isStealth) StealthPrimary else Color(0xFFF39C12)
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(if (isStealth) StealthBackground else StandardBackground)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn() with fadeOut()
                            }
                        ) { screenId ->
                            when (screenId) {
                                "screen-hub" -> HubScreen(
                                    isStealth = isStealth,
                                    favs = favsList,
                                    onToggleFav = { id ->
                                        if (favsList.contains(id)) {
                                            favsList.remove(id)
                                        } else {
                                            favsList.add(id)
                                        }
                                        prefs.edit().putStringSet("favs", favsList.toSet()).apply()
                                    },
                                    onNavigate = { id -> currentScreen = id },
                                    onClipboardCopy = { address ->
                                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = android.content.ClipData.newPlainText("TRC20 Address", address)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(this@MainActivity, "Адрес скопирован!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                "calculator_infusion" -> InfusionScreen(isStealth)
                                "calculator_titration" -> TitrationScreen(isStealth)
                                "calculator_percent" -> PercentToMgScreen(isStealth)
                                "calculator_gfr" -> GFRScreen(isStealth)
                                "calculator_ibw" -> IBWScreen(isStealth)
                                "calculator_anest" -> AnesthesiologistScreen(isStealth)
                                "screen-timer" -> TimerScreen(isStealth)
                            }
                        }
                    }
                }
            }
        }
    }
}
