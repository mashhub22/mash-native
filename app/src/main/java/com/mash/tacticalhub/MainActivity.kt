package com.mash.tacticalhub

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.floor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            var currentScreen by remember { mutableStateOf("screen-hub") }
            var isStealth by remember { mutableStateOf(false) }
            val favsList = remember { mutableStateListOf<String>() }

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

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                        val clip = ClipData.newPlainText("TRC20 Address", address)
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

// --- CONFIG & THEME ---
val StealthBackground = Color(0xFF000000)
val StealthPrimary = Color(0xFFFF0000)
val StealthOnPrimary = Color(0xFF000000)
val StealthSurface = Color(0xFF0B0000)
val StealthBorder = Color(0xFF2E0000)

val StandardBackground = Color(0xFF111111)
val StandardPrimary = Color(0xFF34495E)
val StandardOnPrimary = Color(0xFFFFFFFF)
val StandardSecondary = Color(0xFF222222)
val StandardSurface = Color(0xFF1A1A1A)
val StandardBorder = Color(0xFF444444)
val StandardText = Color(0xFFE0E0E0)

val StealthColorScheme = darkColorScheme(
    primary = StealthPrimary,
    onPrimary = StealthOnPrimary,
    background = StealthBackground,
    onBackground = StealthPrimary,
    surface = StealthSurface,
    onSurface = StealthPrimary,
    outline = StealthBorder
)

val StandardColorScheme = darkColorScheme(
    primary = StandardPrimary,
    onPrimary = StandardOnPrimary,
    background = StandardBackground,
    onBackground = StandardText,
    surface = StandardSurface,
    onSurface = StandardText,
    outline = StandardBorder
)

@Composable
fun TacticalHubTheme(
    isStealth: Boolean,
    content: @Composable () -> Unit
) {
    val colors = if (isStealth) StealthColorScheme else StandardColorScheme
    MaterialTheme(colorScheme = colors, content = content)
}

// --- HELPER COMPOSABLES ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TacticalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isStealth: Boolean,
    keyboardType: KeyboardType = KeyboardType.Decimal
) {
    val borderCol = if (isStealth) StealthBorder else StandardBorder
    val primaryColor = if (isStealth) StealthPrimary else StandardOnPrimary

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStealth) StealthPrimary else Color.LightGray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (isStealth) StealthPrimary else StandardPrimary,
                unfocusedBorderColor = borderCol,
                focusedTextColor = primaryColor,
                unfocusedTextColor = primaryColor,
                cursorColor = primaryColor
            )
        )
    }
}

@Composable
fun ResetButton(
    isStealth: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isStealth) Color(0xFF150000) else StandardSecondary
        ),
        border = BorderStroke(2.dp, if (isStealth) StealthPrimary else StandardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "СБРОСИТЬ",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStealth) StealthPrimary else StandardOnPrimary
        )
    }
}

@Composable
fun ResultCard(
    isStealth: Boolean,
    val1: String,
    desc1: String,
    val2: String? = null,
    desc2: String? = null
) {
    val borderCol = if (isStealth) StealthBorder else StandardBorder
    val primaryColor = if (isStealth) StealthPrimary else StandardOnPrimary
    val accentBg = if (isStealth) Color(0xFF1A0000) else StandardPrimary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(2.dp, borderCol), RoundedCornerShape(6.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(accentBg)
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = val1,
                fontFamily = FontFamily.Monospace,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = desc1.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                textAlign = TextAlign.Center
            )
        }

        if (val2 != null && desc2 != null) {
            Divider(color = borderCol, thickness = 2.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentBg)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = val2,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = desc2.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    isStealth: Boolean,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val borderCol = if (isStealth) StealthBorder else StandardBorder
    val primaryColor = if (isStealth) StealthPrimary else StandardOnPrimary

    Column(
        modifier = modifier
            .border(BorderStroke(1.dp, borderCol), RoundedCornerShape(4.dp))
            .background(if (isStealth) Color.Black else StandardSurface)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStealth) StealthPrimary else Color.LightGray,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = primaryColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ClinicalGroupHeader(
    isStealth: Boolean,
    title: String
) {
    val borderCol = if (isStealth) StealthBorder else StandardBorder
    val primaryColor = if (isStealth) StealthPrimary else StandardOnPrimary
    val fillBg = if (isStealth) Color(0xFF1B0000) else StandardSecondary

    Text(
        text = title.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = primaryColor,
        modifier = Modifier
            .fillMaxWidth()
            .background(fillBg)
            .border(BorderStroke(1.dp, borderCol), RoundedCornerShape(2.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
fun InteractiveRow(
    isStealth: Boolean,
    name: String,
    value: String,
    tooltipText: String? = null,
    onHelpClick: (String) -> Unit
) {
    val borderCol = if (isStealth) StealthBorder else StandardBorder
    val primaryColor = if (isStealth) StealthPrimary else StandardOnPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(0.dp, Color.Transparent))
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStealth) StealthPrimary else Color.White
            )

            if (tooltipText != null) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(if (isStealth) Color(0xFF2A0000) else StandardPrimary, RoundedCornerShape(8.dp))
                        .clickable { onHelpClick(tooltipText) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = primaryColor
        )
    }
}

// --- SCREENS ---

@Composable
fun HubScreen(
    isStealth: Boolean,
    favs: List<String>,
    onToggleFav: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onClipboardCopy: (String) -> Unit
) {
    var donateExpanded by remember { mutableStateOf(false) }
    var copyStatus by remember { mutableStateOf(false) }

    val defaultModules = listOf(
        Pair("calculator_infusion", "Скорость инфузии (Капли)"),
        Pair("calculator_titration", "Скорость титрования (Инфузомат)"),
        Pair("calculator_percent", "Перевод Процент в Миллиграмм"),
        Pair("calculator_gfr", "Скорость клубочковой фильтрации"),
        Pair("calculator_ibw", "Идеальная масса (Devine)"),
        Pair("calculator_anest", "Шпаргалка анестезиолога")
    )

    val sortedModules = defaultModules.sortedByDescending { favs.contains(it.first) }

    val primaryColor = if (isStealth) StealthPrimary else StandardOnPrimary
    val bgColor = if (isStealth) StealthBackground else StandardBackground
    val surfaceColor = if (isStealth) StealthSurface else StandardSurface
    val borderCol = if (isStealth) StealthBorder else StandardBorder

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { donateExpanded = !donateExpanded },
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(2.dp, borderCol),
                colors = CardDefaults.cardColors(containerColor = surfaceColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Приложение бесплатно и спасает жизни. Поддержать проект",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (donateExpanded) "▲" else "▼",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = primaryColor
                    )
                }
            }
        }

        item {
            AnimatedVisibility(visible = donateExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(2.dp, borderCol), RoundedCornerShape(4.dp))
                        .background(surfaceColor)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Монета: USDT (Сеть TRC20)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isStealth) StealthPrimary else Color(0xFFA0A0A0)
                    )

                    Text(
                        text = "TJCudgihaL2bmnLdH2mDCRohzevQz9PqWB",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (isStealth) StealthPrimary else Color(0xFF00FF00),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .border(BorderStroke(1.dp, borderCol), RoundedCornerShape(2.dp))
                            .padding(8.dp)
                    )

                    Button(
                        onClick = {
                            onClipboardCopy("TJCudgihaL2bmnLdH2mDCRohzevQz9PqWB")
                            copyStatus = true
                        },
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (copyStatus) Color(0xFF27AE60) else if (isStealth) Color(0xFF2A0000) else Color(0xFF222222)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, if (isStealth) StealthPrimary else Color.Gray)
                    ) {
                        Text(
                            text = if (copyStatus) "СКОПИРОВАНО!" else "Скопировать адрес",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    LaunchedEffect(donateExpanded) {
                        if (!donateExpanded) {
                            copyStatus = false
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Оперативная панель",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStealth) StealthPrimary else Color(0xFFA0A0A0),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            Divider(color = borderCol, thickness = 2.dp)
        }

        items(sortedModules) { module ->
            val isFav = favs.contains(module.first)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(2.dp, borderCol), RoundedCornerShape(6.dp))
                    .background(surfaceColor)
                    .clickable { onNavigate(module.first) }
                    .padding(vertical = 4.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = module.second.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp)
                )

                IconButton(
                    onClick = { onToggleFav(module.first) }
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = if (isFav) {
                            if (isStealth) StealthPrimary else Color(0xFFFFD700)
                        } else {
                            if (isStealth) StealthBorder else Color.DarkGray
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InfusionScreen(isStealth: Boolean) {
    var vol by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var unitIsHours by remember { mutableStateOf(true) }

    val volD = vol.toDoubleOrNull() ?: 0.0
    val timeD = time.toDoubleOrNull() ?: 0.0
    val res = if (volD > 0 && timeD > 0) TacticalMath.calcInfusion(volD, timeD, unitIsHours) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isStealth) StealthBackground else StandardBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Скорость инфузии (Капли)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStealth) StealthPrimary else StandardOnPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().border(BorderStroke(0.dp, Color.Transparent)).padding(bottom = 8.dp)
        )

        TacticalTextField(
            value = vol,
            onValueChange = { vol = it },
            label = "Объем раствора (миллилитры)",
            isStealth = isStealth
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                TacticalTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = "Желаемое время",
                    isStealth = isStealth
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Единицы времени".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isStealth) StealthPrimary else Color.LightGray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                var dropExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(BorderStroke(2.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                        .background(Color.Black)
                        .clickable { dropExpanded = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (unitIsHours) "ЧАСЫ" else "МИНУТЫ",
                        color = if (isStealth) StealthPrimary else StandardOnPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    DropdownMenu(
                        expanded = dropExpanded,
                        onDismissRequest = { dropExpanded = false },
                        modifier = Modifier.background(if (isStealth) Color.Black else StandardSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("ЧАСЫ", color = if (isStealth) StealthPrimary else Color.White) },
                            onClick = {
                                unitIsHours = true
                                dropExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("МИНУТЫ", color = if (isStealth) StealthPrimary else Color.White) },
                            onClick = {
                                unitIsHours = false
                                dropExpanded = false
                            }
                        )
                    }
                }
            }
        }

        ResetButton(isStealth) {
            vol = ""
            time = ""
            unitIsHours = true
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (res != null) {
            ResultCard(
                isStealth = isStealth,
                val1 = res.dropsPerMin.toString(),
                desc1 = res.dropsPerMinText,
                val2 = res.dropsPerSec.toString(),
                desc2 = res.dropsPerSecText
            )
        } else {
            Text(
                text = "Введите корректные параметры для калькулятора капель.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = if (isStealth) StealthBorder else Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}

@Composable
fun PercentToMgScreen(isStealth: Boolean) {
    var perc by remember { mutableStateOf("") }
    var vol by remember { mutableStateOf("") }

    val percD = perc.toDoubleOrNull() ?: 0.0
    val volD = vol.toDoubleOrNull() ?: 0.0
    val res = if (percD > 0 && volD > 0) TacticalMath.calcPercentToMg(percD, volD) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isStealth) StealthBackground else StandardBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Перевод Процент в Миллиграмм",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStealth) StealthPrimary else StandardOnPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(Modifier.weight(1f)) {
                TacticalTextField(
                    value = perc,
                    onValueChange = { perc = it },
                    label = "Процент (%)",
                    isStealth = isStealth
                )
            }
            Box(Modifier.weight(1f)) {
                TacticalTextField(
                    value = vol,
                    onValueChange = { vol = it },
                    label = "Объем ампулы (мл)",
                    isStealth = isStealth
                )
            }
        }

        ResetButton(isStealth) {
            perc = ""
            vol = ""
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (res != null) {
            ResultCard(
                isStealth = isStealth,
                val1 = res.mg.toString(),
                desc1 = res.mgText,
                val2 = res.mcg.toString(),
                desc2 = res.mcgText
            )
        } else {
            Text(
                text = "Заполните процент и объем ампулы.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = if (isStealth) StealthBorder else Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}

@Composable
fun TitrationScreen(isStealth: Boolean) {
    var count by remember { mutableStateOf("") }
    var vol by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("70") }
    var dosage by remember { mutableStateOf("") }
    var modeFindMlh by remember { mutableStateOf(true) }

    val cD = count.toDoubleOrNull() ?: 0.0
    val vD = vol.toDoubleOrNull() ?: 0.0
    val wD = weight.toDoubleOrNull() ?: 0.0
    val dD = dosage.toDoubleOrNull() ?: 0.0

    val res = if (cD > 0 && vD > 0 && wD > 0 && dD > 0) {
        TacticalMath.calcTitration(cD, vD, wD, dD, modeFindMlh)
    } else {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isStealth) StealthBackground else StandardBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Скорость титрования (Инфузомат)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStealth) StealthPrimary else StandardOnPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(Modifier.weight(1f)) {
                TacticalTextField(
                    value = count,
                    onValueChange = { count = it },
                    label = "Кол-во препарата в ампуле (мг)",
                    isStealth = isStealth
                )
            }
            Box(Modifier.weight(1f)) {
                TacticalTextField(
                    value = vol,
                    onValueChange = { vol = it },
                    label = "Объем раствора (мл)",
                    isStealth = isStealth
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(Modifier.weight(1f)) {
                TacticalTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = "Вес пациента (кг)",
                    isStealth = isStealth
                )
            }
            Box(Modifier.weight(1f)) {
                TacticalTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = if (modeFindMlh) "Желаемая доза (мкг/кг/мин)" else "Текущая скорость (мл/час)",
                    isStealth = isStealth
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Определяемый показатель".uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStealth) StealthPrimary else Color.LightGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            var dropExpanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(BorderStroke(2.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                    .background(Color.Black)
                    .clickable { dropExpanded = true }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (modeFindMlh) "Найти скорость (мл/час)" else "Найти дозу (мкг/кг/мин)",
                    color = if (isStealth) StealthPrimary else StandardOnPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                DropdownMenu(
                    expanded = dropExpanded,
                    onDismissRequest = { dropExpanded = false },
                    modifier = Modifier.background(if (isStealth) Color.Black else StandardSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Найти скорость (мл/час)", color = if (isStealth) StealthPrimary else Color.White) },
                        onClick = {
                            modeFindMlh = true
                            dosage = ""
                            dropExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Найти дозу (мкг/кг/мин)", color = if (isStealth) StealthPrimary else Color.White) },
                        onClick = {
                            modeFindMlh = false
                            dosage = ""
                            dropExpanded = false
                        }
                    )
                }
            }
        }

        ResetButton(isStealth) {
            count = ""
            vol = ""
            weight = "70"
            dosage = ""
            modeFindMlh = true
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (res != null) {
            ResultCard(
                isStealth = isStealth,
                val1 = res.primaryVal.toString(),
                desc1 = res.primaryUnit,
                val2 = res.secondaryVal,
                desc2 = res.secondaryUnit
            )
        } else {
            Text(
                text = "Заполните поля для титрационных расчетов.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = if (isStealth) StealthBorder else Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}

@Composable
fun GFRScreen(isStealth: Boolean) {
    var genderIsFemale by remember { mutableStateOf(true) }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var creat by remember { mutableStateOf("") }
    var raceIsNegroid by remember { mutableStateOf(false) }
    var idmsIsYes by remember { mutableStateOf(false) }

    val ageD = age.toDoubleOrNull() ?: 0.0
    val heightD = height.toDoubleOrNull() ?: 0.0
    val creatD = creat.toDoubleOrNull() ?: 0.0

    val showHeight = ageD > 0 && ageD <= 18.0
    val res = if (ageD > 0 && creatD > 0 && (ageD > 18.0 || heightD > 0)) {
        TacticalMath.calcGFR(genderIsFemale, ageD, heightD, creatD, raceIsNegroid, idmsIsYes)
    } else {
        null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isStealth) StealthBackground else StandardBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Скорость клубочковой фильтрации",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStealth) StealthPrimary else StandardOnPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ПОЛ".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isStealth) StealthPrimary else Color.LightGray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                var dropExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(BorderStroke(2.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                        .background(Color.Black)
                        .clickable { dropExpanded = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (genderIsFemale) "ЖЕНЩИНА" else "МУЖЧИНА",
                        color = if (isStealth) StealthPrimary else StandardOnPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    DropdownMenu(
                        expanded = dropExpanded,
                        onDismissRequest = { dropExpanded = false },
                        modifier = Modifier.background(if (isStealth) Color.Black else StandardSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("ЖЕНЩИНА", color = if (isStealth) StealthPrimary else Color.White) },
                            onClick = {
                                genderIsFemale = true
                                dropExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("МУЖЧИНА", color = if (isStealth) StealthPrimary else Color.White) },
                            onClick = {
                                genderIsFemale = false
                                dropExpanded = false
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                TacticalTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = "Возраст (лет)",
                    isStealth = isStealth
                )
            }
        }

        if (showHeight) {
            TacticalTextField(
                value = height,
                onValueChange = { height = it },
                label = "Рост (сантиметры)",
                isStealth = isStealth
            )
        }

        TacticalTextField(
            value = creat,
            onValueChange = { creat = it },
            label = "Креатинин сыворотки (микромоль/литр)",
            isStealth = isStealth
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "РАСА".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isStealth) StealthPrimary else Color.LightGray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                var dropExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(BorderStroke(2.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                        .background(Color.Black)
                        .clickable { dropExpanded = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (raceIsNegroid) "НЕГРОИДНАЯ" else "ДРУГАЯ РАСА",
                        color = if (isStealth) StealthPrimary else StandardOnPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    DropdownMenu(
                        expanded = dropExpanded,
                        onDismissRequest = { dropExpanded = false },
                        modifier = Modifier.background(if (isStealth) Color.Black else StandardSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("ДРУГАЯ РАСА", color = if (isStealth) StealthPrimary else Color.White) },
                            onClick = {
                                raceIsNegroid = false
                                dropExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("НЕГРОИДНАЯ", color = if (isStealth) StealthPrimary else Color.White) },
                            onClick = {
                                raceIsNegroid = true
                                dropExpanded = false
                            }
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Метод IDMS".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isStealth) StealthPrimary else Color.LightGray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                var dropExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(BorderStroke(2.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                        .background(Color.Black)
                        .clickable { dropExpanded = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (idmsIsYes) "ДА" else "НЕТ",
                        color = if (isStealth) StealthPrimary else StandardOnPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    DropdownMenu(
                        expanded = dropExpanded,
                        onDismissRequest = { dropExpanded = false },
                        modifier = Modifier.background(if (isStealth) Color.Black else StandardSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("НЕТ", color = if (isStealth) StealthPrimary else Color.White) },
                            onClick = {
                                idmsIsYes = false
                                dropExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("ДА", color = if (isStealth) StealthPrimary else Color.White) },
                            onClick = {
                                idmsIsYes = true
                                dropExpanded = false
                            }
                        )
                    }
                }
            }
        }

        ResetButton(isStealth) {
            genderIsFemale = true
            age = ""
            height = ""
            creat = ""
            raceIsNegroid = false
            idmsIsYes = false
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (res != null) {
            ResultCard(
                isStealth = isStealth,
                val1 = res.gfr.toString(),
                desc1 = "СКФ = ${res.gfr}",
                val2 = res.stage,
                desc2 = "Стадия ХБП"
            )
        } else {
            Text(
                text = "Заполните возраст и уровень креатинина.",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = if (isStealth) StealthBorder else Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
        }
    }
}

@Composable
fun IBWScreen(isStealth: Boolean) {
    var isMale by remember { mutableStateOf(true) }
    var height by remember { mutableStateOf("") }

    val heightD = height.toDoubleOrNull() ?: 0.0
    val res = if (heightD > 0) TacticalMath.calcIBW(isMale, heightD) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isStealth) StealthBackground else StandardBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Идеальная масса (Devine)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStealth) StealthPrimary else StandardOnPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ПОЛ".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isStealth) StealthPrimary else Color.LightGray,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                var dropExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(BorderStroke(2.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                        .background(Color.Black)
                        .clickable { dropExpanded = true }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (isMale) "МУЖЧИНА" else "ЖЕНЩИНА",
                        color = if (isStealth) StealthPrimary else StandardOnPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    DropdownMenu(
                        expanded = dropExpanded,
                        onDismissRequest = { dropExpanded = false },
                        modifier = Modifier.background(if (isStealth) Color.Black else StandardSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("МУЖЧИНА", color = if (isStealth) StealthPrimary else Color.White) },
                            onClick = {
                                isMale = true
                                dropExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("ЖЕНЩИНА", color = if (isStealth) StealthPrimary else Color.White) },
                            onClick = {
                                isMale = false
                                dropExpanded = false
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                TacticalTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = "Рост (сантиметры)",
                    isStealth = isStealth
                )
            }
        }

        ResetButton(isStealth) {
            isMale = true
            height = ""
        }

        Spacer(modifier = Modifier.height(8.dp))

        ResultCard(
            isStealth = isStealth,
            val1 = if (res != null) "$res кг" else "-- кг",
            desc1 = "Вычисленная идеальная масса"
        )
    }
}

@Composable
fun AnesthesiologistScreen(isStealth: Boolean) {
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var showInMg by remember { mutableStateOf(true) }
    var alertMessage by remember { mutableStateOf<String?>(null) }

    val ageD = age.toDoubleOrNull()
    val weightD = weight.toDoubleOrNull()
    val data = TacticalMath.calcAnesthesia(ageD, weightD)

    val getDrugVal = { key: String ->
        val entry = data.drugs[key]
        if (entry != null) {
            if (showInMg) entry.mg else entry.ml
        } else "-"
    }

    val getMaintenanceVal = { key: String ->
        val entry = data.drugs[key]
        if (entry != null) {
            if (showInMg) entry.mgMaintenance else entry.mlMaintenance
        } else "-"
    }

    if (alertMessage != null) {
        AlertDialog(
            onDismissRequest = { alertMessage = null },
            confirmButton = {
                TextButton(onClick = { alertMessage = null }) {
                    Text("ЗАКРЫТЬ", color = if (isStealth) StealthPrimary else StandardPrimary, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text("АННОТАЦИЯ ПРЕПАРАТА", fontSize = 12.sp, fontWeight = FontWeight.Black, color = if (isStealth) StealthPrimary else StandardOnPrimary)
            },
            text = {
                Text(alertMessage ?: "", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            },
            containerColor = if (isStealth) Color.Black else StandardSurface,
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 6.dp
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isStealth) StealthBackground else StandardBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Шпаргалка анестезиолога",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStealth) StealthPrimary else StandardOnPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(Modifier.weight(1f)) {
                TacticalTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = "Возраст (лет)",
                    isStealth = isStealth
                )
            }
            Box(Modifier.weight(1f)) {
                TacticalTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = "Масса тела (кг)",
                    isStealth = isStealth
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Отображать дозировки".uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStealth) StealthPrimary else Color.LightGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            var dropExpanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(BorderStroke(2.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                    .background(Color.Black)
                    .clickable { dropExpanded = true }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (showInMg) "Миллиграммы (мг)" else "Миллилитры (мл)",
                    color = if (isStealth) StealthPrimary else StandardOnPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                DropdownMenu(
                    expanded = dropExpanded,
                    onDismissRequest = { dropExpanded = false },
                    modifier = Modifier.background(if (isStealth) Color.Black else StandardSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Миллиграммы (мг)", color = if (isStealth) StealthPrimary else Color.White) },
                        onClick = {
                            showInMg = true
                            dropExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Миллилитры (мл)", color = if (isStealth) StealthPrimary else Color.White) },
                        onClick = {
                            showInMg = false
                            dropExpanded = false
                        }
                    )
                }
            }
        }

        ResetButton(isStealth) {
            age = ""
            weight = ""
            showInMg = true
        }

        Spacer(modifier = Modifier.height(8.dp))

        ClinicalGroupHeader(isStealth, "Жизненные показатели")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(isStealth, "АД", data.ad, modifier = Modifier.weight(1f))
            MetricCard(isStealth, "ЧСС", data.bpm, modifier = Modifier.weight(1f))
            MetricCard(isStealth, "ЧДД", data.ipm, modifier = Modifier.weight(1f))
        }

        ClinicalGroupHeader(isStealth, "Оборудование и объемы")
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard(isStealth, "Дыхательный объем", data.inspVol, modifier = Modifier.weight(1f))
                MetricCard(isStealth, "Минутный объем", data.minVol, modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard(isStealth, "Мертвое пространство", data.deadSpace, modifier = Modifier.weight(1f))
                MetricCard(isStealth, "Объем цирк. крови", data.ock, modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard(isStealth, "ЦВД", data.cvd, modifier = Modifier.weight(1f))
                MetricCard(isStealth, "Диурез (суточный)", data.dailyDiuresis, modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard(isStealth, "Размер ЭТ трубки", data.ett, modifier = Modifier.weight(1f))
                MetricCard(isStealth, "Ларингеальная маска", data.larMask, modifier = Modifier.weight(1f))
            }
        }

        ClinicalGroupHeader(isStealth, "Премедикация")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                .background(if (isStealth) Color.Black else StandardSurface)
                .padding(12.dp)
        ) {
            InteractiveRow(isStealth, "Атропин", getDrugVal("atr"), "Максимальная в сутки 3 mg.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Диазепам", getDrugVal("diaz"), onHelpClick = {})
            InteractiveRow(isStealth, "Димедрол", getDrugVal("dimedrol"), onHelpClick = {})
            InteractiveRow(isStealth, "Дроперидол", getDrugVal("drop"), onHelpClick = {})
            InteractiveRow(isStealth, "Мидазолам", getDrugVal("mid"), onHelpClick = {})
            InteractiveRow(isStealth, "Тримеперидин (промедол)", getDrugVal("prom"), onHelpClick = {})
        }

        ClinicalGroupHeader(isStealth, "Средства для наркоза")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                .background(if (isStealth) Color.Black else StandardSurface)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                InteractiveRow(isStealth, "Кетамин [Индукция]", getDrugVal("ket"), "Пожилым и в шоке 0.5 мг/кг. Капельно 0.1% скорость 20-60 капель/мин.", onHelpClick = { alertMessage = it })
                Text(text = "ПОДДЕРЖАНИЕ: " + getMaintenanceVal("ket"), fontSize = 10.sp, color = Color.Gray)
            }
            Divider(color = if (isStealth) StealthBorder else Color.DarkGray, thickness = 1.dp)
            Column(modifier = Modifier.fillMaxWidth()) {
                InteractiveRow(isStealth, "Пропофол [Индукция]", getDrugVal("prop"), onHelpClick = {})
                Text(text = "ПОДДЕРЖАНИЕ: " + getMaintenanceVal("prop"), fontSize = 10.sp, color = Color.Gray)
            }
            Divider(color = if (isStealth) StealthBorder else Color.DarkGray, thickness = 1.dp)
            Column(modifier = Modifier.fillMaxWidth()) {
                InteractiveRow(isStealth, "Тиопентал [Индукция]", getDrugVal("tiop"), "При Cl креатинина < 10 мл/мин — 75% дозы. Разовая доза до 1 г. Концентрация до 2.5%.", onHelpClick = { alertMessage = it })
                Text(text = "ПОДДЕРЖАНИЕ: " + getMaintenanceVal("tiop"), fontSize = 10.sp, color = Color.Gray)
            }
            Divider(color = if (isStealth) StealthBorder else Color.DarkGray, thickness = 1.dp)
            Column(modifier = Modifier.fillMaxWidth()) {
                InteractiveRow(isStealth, "Оксибутират [Индукция]", getDrugVal("oxib"), "Детям 100 мг/кг в растворе глюкозы.", onHelpClick = { alertMessage = it })
                Text(text = "ПОДДЕРЖАНИЕ: " + getMaintenanceVal("oxib"), fontSize = 10.sp, color = Color.Gray)
            }
        }

        ClinicalGroupHeader(isStealth, "Миорелаксанты (интубация)")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                .background(if (isStealth) Color.Black else StandardSurface)
                .padding(12.dp)
        ) {
            InteractiveRow(isStealth, "Атракурий (тракриум)", getDrugVal("i_atr"), onHelpClick = {})
            InteractiveRow(isStealth, "Пипекуроний (ардуан)", getDrugVal("i_pip"), onHelpClick = {})
            InteractiveRow(isStealth, "Сукцинилхолин (дитилин)", getDrugVal("i_suk"), onHelpClick = {})
        }

        ClinicalGroupHeader(isStealth, "Миорелаксанты (поддержание)")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                .background(if (isStealth) Color.Black else StandardSurface)
                .padding(12.dp)
        ) {
            InteractiveRow(isStealth, "Атракурий", getDrugVal("p_atr"), onHelpClick = {})
            InteractiveRow(isStealth, "Пипекуроний", getDrugVal("p_pip"), onHelpClick = {})
            InteractiveRow(isStealth, "Сукцинилхолин", getDrugVal("p_suk"), onHelpClick = {})
        }

        ClinicalGroupHeader(isStealth, "Местные и Анальгетики")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                .background(if (isStealth) Color.Black else StandardSurface)
                .padding(12.dp)
        ) {
            InteractiveRow(isStealth, "Лидокаин (местный)", getDrugVal("lido_an"), "Для проводниковой анестезии 1% раствор, инфильтрационной - 0.5%.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Бупивакаин", getDrugVal("bupi"), "В расчетах используется 0.25% раствор.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Анальгин", getDrugVal("analgin"), "Максимальная разовая доза — 1 г. Максимальная суточная доза — 2000 мг.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Кеторолак", getDrugVal("ketor"), "Разовая 30 мг. Суточная 90 мг.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Морфин", getDrugVal("morphin"), "Максимальная разовая доза — 20 мг. Максимальная суточная доза — 50 мг.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Тримеперидин (промедол)", getDrugVal("trim"), "Максимальная разовая доза — 40 мг. Максимальная суточная доза — 160 мг.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Трамадол", getDrugVal("tramadol"), onHelpClick = {})
            InteractiveRow(isStealth, "Фентанил", getDrugVal("fent"), onHelpClick = {})
        }

        ClinicalGroupHeader(isStealth, "ГКС и Диуретики")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                .background(if (isStealth) Color.Black else StandardSurface)
                .padding(12.dp)
        ) {
            InteractiveRow(isStealth, "Преднизолон", getDrugVal("prednisolon"), "При острых и неотложных состояниях вводят 50–150 мг.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Метилпреднизолон", getDrugVal("metpred"), onHelpClick = {})
            InteractiveRow(isStealth, "Дексаметазон", getDrugVal("dexa"), "При острых состояниях вводят 4–20 мг (до 80 мг/сутки).", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Фуросемид", getDrugVal("furo"), "При отеках или почечной недостаточности до 600 мг/сутки.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Маннит", getDrugVal("mannit"), "Обычно 1-1.5 г/кг.", onHelpClick = { alertMessage = it })
        }

        ClinicalGroupHeader(isStealth, "Гемостатики и антиаритмики")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, if (isStealth) StealthBorder else StandardBorder), RoundedCornerShape(4.dp))
                .background(if (isStealth) Color.Black else StandardSurface)
                .padding(12.dp)
        ) {
            InteractiveRow(isStealth, "Этамзилат", getDrugVal("etam"), "Суточная доза — 10-20 мг/кг.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Аминокапроновая кислота", getDrugVal("amc"), "При кровотечении капельно в/в 4-5 г за часовой цикл.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Лидокаин (кардио)", getDrugVal("lido"), "Начальная доза 1-1.5 мг/кг.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Амиодарон", getDrugVal("amio"), "Начальная доза 5 мг/кг.", onHelpClick = { alertMessage = it })
            InteractiveRow(isStealth, "Налоксон", getDrugVal("nalox"), onHelpClick = {})
            InteractiveRow(isStealth, "Метоклопрамид", getDrugVal("metoclop"), onHelpClick = {})
        }
    }
}

@Composable
fun TimerScreen(isStealth: Boolean) {
    val context = LocalContext.current

    var swRunning by remember { mutableStateOf(false) }
    var swTime by remember { mutableStateOf(0L) }
    var swStart by remember { mutableStateOf(0L) }
    var swAccum by remember { mutableStateOf(0L) }

    var cdRunning by remember { mutableStateOf(false) }
    var cdTime by remember { mutableStateOf(0L) }
    var cdEnd by remember { mutableStateOf(0L) }
    var cdAlarm by remember { mutableStateOf(false) }

    var manualMin by remember { mutableStateOf("") }
    var manualSec by remember { mutableStateOf("") }

    LaunchedEffect(swRunning) {
        if (swRunning) {
            swStart = System.currentTimeMillis()
            while (swRunning) {
                swTime = swAccum + (System.currentTimeMillis() - swStart)
                delay(100)
            }
        }
    }

    LaunchedEffect(cdRunning) {
        if (cdRunning) {
            while (cdRunning) {
                val remaining = cdEnd - System.currentTimeMillis()
                if (remaining <= 0) {
                    cdTime = 0
                    cdRunning = false
                    cdAlarm = true
                    triggerHapticAlarm(context)
                } else {
                    cdTime = remaining
                }
                delay(100)
            }
        }
    }

    val formatTime = { totalMs: Long, showHrs: Boolean ->
        val totalSec = floor(totalMs / 1000.0).toLong()
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = totalSec % 60
        val pad = { v: Long -> if (v < 10) "0$v" else "$v" }

        if (showHrs || h > 0) "${pad(h)}:${pad(m)}:${pad(s)}" else "${pad(m)}:${pad(s)}"
    }

    val startCdMs = { durationMs: Long ->
        cdAlarm = false
        cdEnd = System.currentTimeMillis() + durationMs
        cdTime = durationMs
        cdRunning = true
    }

    val startCdMins = { minutes: Int ->
        startCdMs(minutes * 60 * 1000L)
    }

    val borderCol = if (isStealth) StealthBorder else StandardBorder
    val primaryColor = if (isStealth) StealthPrimary else StandardOnPrimary

    val alarmBg by animateColorAsState(
        targetValue = if (cdAlarm) Color(0xFF2A0000) else if (isStealth) StealthSurface else StandardSurface,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isStealth) StealthBackground else StandardBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Тактический таймер",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isStealth) StealthPrimary else StandardOnPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, borderCol), RoundedCornerShape(6.dp))
                .background(if (isStealth) StealthSurface else StandardSurface)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Секундомер турникета".uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStealth) StealthPrimary else Color.LightGray
            )

            Text(
                text = formatTime(swTime, true),
                fontFamily = FontFamily.Monospace,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = primaryColor,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (swRunning) {
                            swAccum += System.currentTimeMillis() - swStart
                            swRunning = false
                        } else {
                            swRunning = true
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (swRunning) Color(0xFFC0392B) else if (isStealth) Color(0xFF150000) else StandardSecondary
                    ),
                    border = BorderStroke(1.dp, if (isStealth) StealthPrimary else StandardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (swRunning) "ПАУЗА" else if (swTime > 0) "ПРОДОЛЖИТЬ" else "СТАРТ",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        swRunning = false
                        swAccum = 0L
                        swTime = 0L
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    border = BorderStroke(1.dp, borderCol),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "СБРОС",
                        fontWeight = FontWeight.Bold,
                        color = if (isStealth) StealthPrimary else Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, if (cdAlarm) Color.Red else borderCol), RoundedCornerShape(6.dp))
                .background(alarmBg)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Обратный отсчет".uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStealth) StealthPrimary else Color.LightGray
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { startCdMins(2) },
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        border = BorderStroke(1.dp, borderCol),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("2 МИН (СЛР)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                    Button(
                        onClick = { startCdMins(10) },
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        border = BorderStroke(1.dp, borderCol),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("10 МИН (TXA)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { startCdMins(15) },
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        border = BorderStroke(1.dp, borderCol),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("15 МИН (MARCH)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                    Button(
                        onClick = { startCdMins(120) },
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        border = BorderStroke(1.dp, borderCol),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("120 МИН (ТУРН)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    TacticalTextField(value = manualMin, onValueChange = { manualMin = it }, label = "Мин", isStealth = isStealth)
                }
                Text(":", fontWeight = FontWeight.Black, fontSize = 20.sp, color = borderCol)
                Box(modifier = Modifier.weight(1f)) {
                    TacticalTextField(value = manualSec, onValueChange = { manualSec = it }, label = "Сек", isStealth = isStealth)
                }
                Button(
                    onClick = {
                        val m = manualMin.toIntOrNull() ?: 0
                        val s = manualSec.toIntOrNull() ?: 0
                        if (m > 0 || s > 0) {
                            startCdMs((m * 60 + s) * 1000L)
                            manualMin = ""
                            manualSec = ""
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isStealth) Color(0xFF150000) else StandardSecondary),
                    border = BorderStroke(1.dp, borderCol),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("ЗАПУСК", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                }
            }

            Text(
                text = formatTime(cdTime, cdTime >= 3600000L),
                fontFamily = FontFamily.Monospace,
                fontSize = 54.sp,
                fontWeight = FontWeight.Black,
                color = if (cdAlarm) Color.Red else primaryColor,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (cdRunning || cdAlarm) {
                Button(
                    onClick = {
                        cdRunning = false
                        cdTime = 0
                        cdAlarm = false
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("СТОП / СБРОС ТАЙМЕРА", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

private fun triggerHapticAlarm(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    vibrator?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 500, 200, 500, 200, 1000)
            val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
            it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            it.vibrate(longArrayOf(0, 500, 200, 500, 200, 1000), -1)
        }
    }
}

// ================= TacticalMath Monolithic Block =================

object TacticalMath {

    // Russian declination logic for grammatical precision
    fun declination(n: Double, forms: Array<String>): String {
        val rounded = abs(n.roundToInt()) % 100
        val n1 = rounded % 10
        if (rounded in 5..20) return forms[2]
        if (n1 == 1) return forms[0]
        if (n1 in 2..4) return forms[1]
        return forms[2]
    }

    // Double rounding helper to mimic Javascript decimal boundaries
    fun round10(v: Double, decimals: Int): Double {
        val factor = 10.0.pow(decimals.toDouble())
        return (v * factor).roundToInt() / factor
    }

    // 1. Infusion Drops Calculator
    data class InfusionResult(
        val dropsPerMin: Double,
        val dropsPerMinText: String,
        val dropsPerSec: Double,
        val dropsPerSecText: String
    )

    fun calcInfusion(vol: Double, time: Double, isHours: Boolean): InfusionResult? {
        val v = vol * 20.0 // 20 drops per ml
        val t = if (isHours) time * 60.0 else time
        if (t <= 0.0 || v <= 0.0) return null

        val dropsPerMin = v / t
        val dropsPerSec = dropsPerMin / 60.0

        return InfusionResult(
            dropsPerMin = round10(dropsPerMin, 1),
            dropsPerMinText = declination(dropsPerMin, arrayOf("капля", "капли", "капель")) + " в минуту",
            dropsPerSec = round10(dropsPerSec, 2),
            dropsPerSecText = declination(dropsPerSec, arrayOf("капля", "капли", "капель")) + " в секунду"
        )
    }

    // 2. Percent to Mg Calculator
    data class PercentResult(
        val mg: Double,
        val mgText: String,
        val mcg: Double,
        val mcgText: String
    )

    fun calcPercentToMg(perc: Double, vol: Double): PercentResult? {
        val res1 = perc * 10.0 * vol
        val res2 = res1 * 1000.0
        if (res1.isNaN() || res1 <= 0.0) return null

        return PercentResult(
            mg = round10(res1, 2),
            mgText = declination(res1, arrayOf("миллиграмм", "миллиграмма", "миллиграммов")),
            mcg = round10(res2, 2),
            mcgText = declination(res2, arrayOf("микрограмм", "микрограмма", "микрограммов"))
        )
    }

    // 3. Titration Speed (Syringe Pump) Calculator
    data class TitrationResult(
        val primaryVal: Double,
        val primaryUnit: String,
        val secondaryVal: String,
        val secondaryUnit: String
    )

    fun calcTitration(
        countMg: Double,
        volMl: Double,
        weightKg: Double,
        dosageVal: Double,
        findMlhMode: Boolean
    ): TitrationResult? {
        if (countMg <= 0 || volMl <= 0 || weightKg <= 0 || dosageVal <= 0) return null

        val dose = weightKg * dosageVal // mcg/min
        val conc = (countMg * 1000.0) / volMl // mcg/ml

        if (findMlhMode) {
            val speed = (dose / conc) * 60.0
            val speedRounded = round10(speed, 2)
            val kap = (speedRounded * 20.0) / 60.0
            val kapRounded = round10(kap, 1)

            val secVal = if (kapRounded <= 1.0) "Менее 1 капли/мин" else round10(kap, 2).toString()
            val secUnit = if (kapRounded <= 1.0) "Используйте инфузомат" else declination(kap, arrayOf("капля", "капли", "капель")) + " в минуту"

            return TitrationResult(
                primaryVal = speedRounded,
                primaryUnit = "мл/час",
                secondaryVal = secVal,
                secondaryUnit = secUnit
            )
        } else {
            // find mcg
            val speed = (dosageVal / 60.0) * conc / weightKg
            val speedRounded = round10(speed, 2)
            val kapRev = (dosageVal * 20.0) / 60.0
            val kapRevRounded = round10(kapRev, 1)

            val secVal = if (kapRevRounded <= 1.0) "Менее 1 капли/мин" else round10(kapRev, 2).toString()
            val secUnit = if (kapRevRounded <= 1.0) "Используйте инфузомат" else declination(kapRev, arrayOf("капля", "капли", "капель")) + " в минуту"

            return TitrationResult(
                primaryVal = speedRounded,
                primaryUnit = "мкг×кг/мин",
                secondaryVal = secVal,
                secondaryUnit = secUnit
            )
        }
    }

    // 4. Glomerular Filtration Rate Calculator
    data class GFRResult(
        val gfr: Int,
        val stage: String
    )

    fun calcGFR(
        isFemale: Boolean,
        ageYears: Double,
        heightCm: Double,
        creatUmol: Double,
        isNegroid: Boolean,
        isIDMS: Boolean
    ): GFRResult? {
        if (ageYears <= 0.0 || creatUmol <= 0.0 || (ageYears <= 18.0 && heightCm <= 0.0)) return null

        val idmsVal = if (isIDMS) 1.0 else 0.95
        var gfr = 0.0

        if (ageYears > 18.0) {
            gfr = 175.0 * (creatUmol * idmsVal / 88.4).pow(-1.154) * ageYears.pow(-0.203)
            // Ported EXACTLY as is from JS source to support test specifications perfectly
            if (isFemale && !isNegroid) {
                gfr *= 1.212
            } else if (!isFemale && isNegroid) {
                gfr *= 0.742
            } else if (!isFemale && !isNegroid) {
                gfr *= 0.742 * 1.212
            }
        } else {
            gfr = (36.2 * heightCm) / creatUmol
        }

        if (gfr.isNaN() || gfr.isInfinite() || gfr <= 0.0) return null

        val finalGfr = gfr.roundToInt()
        val stageStr = when {
            finalGfr < 15 -> "V"
            finalGfr < 30 -> "IV"
            finalGfr < 45 -> "IIIb"
            finalGfr < 60 -> "IIIa"
            finalGfr < 90 -> "II"
            else -> "I (Норма)"
        }

        return GFRResult(finalGfr, stageStr)
    }

    // 5. Ideal Body Weight Devine
    fun calcIBW(isMale: Boolean, heightCm: Double): Double? {
        if (heightCm <= 0.0) return null
        val base = if (isMale) 50.0 else 45.5
        val r = base + 2.3 * (heightCm / 2.54 - 60.0)
        return round10(r, 1)
    }

    // 6. Anesthesia Spreadsheet Recalculator Engine
    data class DrugEntry(
        val mg: String,
        val ml: String,
        val mgMaintenance: String = "-",
        val mlMaintenance: String = "-"
    )

    data class AnesthesiaData(
        val ad: String = "-",
        val bpm: String = "-",
        val ipm: String = "-",
        val cvd: String = "-",
        val ett: String = "-",
        val inspVol: String = "-",
        val deadSpace: String = "-",
        val larMask: String = "-",
        val minVol: String = "-",
        val ock: String = "-",
        val hourlyDiuresis: String = "-",
        val dailyDiuresis: String = "-",
        val drugs: Map<String, DrugEntry> = emptyMap()
    )

    fun calcAnesthesia(ageYears: Double?, weightKg: Double?): AnesthesiaData {
        val a = ageYears?.roundToInt() ?: 0
        val m = weightKg?.roundToInt() ?: 0

        var ad = "-"
        var bpm = "-"
        var ipm = "-"
        var cvd = "-"
        var ett = "-"
        var inspVol = "-"
        var deadSpace = "-"
        var larMask = "-"
        var minVol = "-"
        var ock = "-"
        var hourlyDiuresis = "-"
        var dailyDiuresis = "-"

        val drugsMap = mutableMapOf<String, DrugEntry>()

        if (a > 0) {
            ad = when {
                a < 1 -> "85/50"
                a < 3 -> "90/55"
                a <= 6 -> "100/65"
                a <= 9 -> "105/70"
                a <= 12 -> "110/70"
                else -> "до 139/89"
            }

            bpm = when {
                a < 1 -> "95-160"
                a <= 2 -> "90-150"
                a <= 4 -> "85-130"
                a <= 6 -> "75-125"
                a <= 10 -> "65-110"
                else -> "60-90"
            }

            ipm = when {
                a < 5 -> "30-35"
                a < 9 -> "25-27"
                a <= 12 -> "20-22"
                else -> "16-20"
            }

            val cvdVal = when {
                a < 2 -> "2.7 - 6.7"
                a < 4 -> "2.8 - 8.3"
                a < 14 -> "3 - 10"
                else -> "4 - 12"
            }
            cvd = "$cvdVal cm H2O"

            ett = when {
                a < 2 -> "3-4 без манжеты"
                a < 4 -> "4.5"
                a < 6 -> "5"
                a < 8 -> "5.5 с манжетой"
                a < 10 -> "6 с манжетой"
                a < 12 -> "6.5 с манжетой"
                else -> "7-9 с манжетой"
            }
        }

        if (m > 0) {
            inspVol = "${m * 7} мл"
            deadSpace = "${(m * 2.1).roundToInt()} мл"
            larMask = when {
                m < 6.5 -> "№1"
                m < 20 -> "№2"
                m < 30 -> "№2.5"
                m < 70 -> "№3"
                else -> "№4"
            }
        }

        if (a > 0 && m > 0) {
            val mvRaw = if (a < 1) 180 * m else if (a < 18) 100 * m else 80 * m
            minVol = "$mvRaw мл"

            val ockRaw = when {
                a < 1 -> 90 * m
                a < 4 -> 85 * m
                a <= 6 -> 80 * m
                a < 14 -> 75 * m
                else -> 65 * m
            }
            ock = "$ockRaw мл"

            val diurRaw = when {
                a < 1 -> 3.5 * m
                a < 2 -> 2.0 * m
                a < 11 -> 1.7 * m
                a <= 14 -> 1.4 * m
                else -> 0.8 * m
            }
            hourlyDiuresis = "${diurRaw.roundToInt()} мл/ч"
            dailyDiuresis = "${(diurRaw * 24.0).roundToInt()} мл"

            // Drugs
            val d_atr = if (a < 18) m * 0.01 else m * 0.02
            drugsMap["atr"] = DrugEntry(String.format("%.1f", d_atr), String.format("%.1f", d_atr))

            val d_diaz = m * 0.2
            drugsMap["diaz"] = DrugEntry(String.format("%.1f", d_diaz), String.format("%.1f", d_diaz / 5.0))

            val d_dim = m * 0.5
            drugsMap["dimedrol"] = DrugEntry(String.format("%.1f", d_dim), String.format("%.1f", d_dim / 10.0))

            val d_drop = m * 0.1
            drugsMap["drop"] = DrugEntry(String.format("%.1f", d_drop), String.format("%.1f", d_drop / 2.5))

            val d_mid = if (a < 18) m * 0.2 else m * 0.1
            drugsMap["mid"] = DrugEntry(String.format("%.1f", d_mid), String.format("%.1f", d_mid / 5.0))

            val d_prom = if (a < 18) m * 0.1 else m * 0.2
            val promMl = if (a < 18) d_prom / 5.0 else d_prom / 20.0
            drugsMap["prom"] = DrugEntry(String.format("%.1f", d_prom), String.format("%.1f", promMl))

            // Anesthetics
            val d_ket = m * 2.0
            drugsMap["ket"] = DrugEntry(
                mg = String.format("%.1f", d_ket),
                ml = String.format("%.1f", d_ket / 50.0),
                mgMaintenance = "${m} мг или ${m * 4} мг/ч",
                mlMaintenance = String.format("%.1f мл или %.1f мл/ч", m / 50.0, m * 4.0 / 50.0)
            )

            val d_prop = if (a < 8) m * 4.0 else m * 2.5
            val s_prop = if (a < 8) m * 12.0 else m * 8.0
            drugsMap["prop"] = DrugEntry(
                mg = String.format("%.1f", d_prop),
                ml = String.format("%.1f", d_prop / 10.0),
                mgMaintenance = String.format("%.1f мг/ч", s_prop),
                mlMaintenance = String.format("%.1f мл/ч", s_prop / 10.0)
            )

            drugsMap["tiop"] = DrugEntry(
                mg = "${m * 5}",
                ml = "Произв.",
                mgMaintenance = "50-100 взрослым, 25-50 детям",
                mlMaintenance = "Произв."
            )

            val d_ox = if (a < 14) m * 100 else m * 70
            drugsMap["oxib"] = DrugEntry(
                mg = "$d_ox",
                ml = String.format("%.1f", d_ox / 200.0),
                mgMaintenance = "${m * 40}",
                mlMaintenance = String.format("%.1f", m * 40.0 / 200.0)
            )

            // Muscle relaxants
            val ia = if (a < 14) m * 0.4 else m * 0.5
            drugsMap["i_atr"] = DrugEntry(String.format("%.1f", ia), String.format("%.1f", ia / 10.0))

            val ip = if (a < 1) m * 0.04 else if (a < 14) m * 0.057 else m * 0.075
            drugsMap["i_pip"] = DrugEntry(String.format("%.2f", ip), "Произв.")

            val is_val = if (a < 1) m * 3.0 else if (a <= 3) m * 2.0 else m * 1.5
            drugsMap["i_suk"] = DrugEntry(String.format("%.1f", is_val), String.format("%.1f", is_val / 20.0))

            val pa = m * 0.3
            drugsMap["p_atr"] = DrugEntry(String.format("%.1f", pa), String.format("%.1f", pa / 10.0))

            val pp = m * 0.0125
            drugsMap["p_pip"] = DrugEntry(String.format("%.3f", pp), "Произв.")

            val ps = if (a < 1) m * 2.5 else if (a <= 3) m * 1.5 else m * 1.0
            drugsMap["p_suk"] = DrugEntry(String.format("%.1f", ps), String.format("%.1f", ps / 20.0))

            // Locals
            drugsMap["lido_an"] = DrugEntry("до " + String.format("%.1f", m * 5.0), "до " + String.format("%.1f", m * 5.0 / 10.0))
            drugsMap["bupi"] = DrugEntry("до " + String.format("%.1f", m * 2.5), "до " + String.format("%.1f", m * 2.5 / 2.5))

            // Analgesics
            val analginMg = if (a < 14) "${m * 10}" else "500-1000"
            val analginMl = if (a < 14) String.format("%.1f", m * 10.0 / 500.0) else "1-2"
            drugsMap["analgin"] = DrugEntry(analginMg, analginMl)

            val d_ketor = m * 0.5
            drugsMap["ketor"] = DrugEntry(String.format("%.1f", d_ketor), String.format("%.1f", d_ketor / 30.0))

            val d_morph = m * 0.07
            drugsMap["morphin"] = DrugEntry(String.format("%.1f", d_morph), String.format("%.1f", d_morph / 10.0))

            val trm = if (a < 14) m * 0.075 else m * 0.2
            drugsMap["trim"] = DrugEntry(String.format("%.1f", trm), String.format("%.1f", trm / 20.0))

            val tramMg = if (a < 1) "Нет" else if (a < 14) "${m * 1}" else "50"
            val tramMl = if (a < 1) "Нет" else if (a < 14) String.format("%.1f", m / 50.0) else "1"
            drugsMap["tramadol"] = DrugEntry(tramMg, tramMl)

            val fentMg = if (a < 12) String.format("%.3f", m * 0.002) else "0.1"
            val fentMl = if (a < 12) String.format("%.3f", m * 0.002 / 0.05) else "2"
            drugsMap["fent"] = DrugEntry(fentMg, fentMl)

            // Steroids & Diuretics
            val predMg = if (a < 14) (if (a < 1) "${m * 2}" else "${m * 1}") else "до 1200 / сут"
            drugsMap["prednisolon"] = DrugEntry(predMg, "Произв.")

            drugsMap["metpred"] = DrugEntry("${m * 10}", "Произв.")

            val dexaMg = if (a in 1..13) String.format("%.2f", m * 0.097) else "до 80 / сут"
            drugsMap["dexa"] = DrugEntry(dexaMg, "Произв.")

            val furoMg = if (a < 14) "${m * 1}" else "20-40"
            val furoMl = if (a < 14) String.format("%.1f", m / 10.0) else "1-2"
            drugsMap["furo"] = DrugEntry(furoMg, furoMl)

            drugsMap["mannit"] = DrugEntry("${m * 1000}", String.format("%.1f", m * 1000.0 / 150.0))

            // Hemostatics, Cards & Antidotes
            val etm = if (a < 14) m * 20 else m * 15
            drugsMap["etam"] = DrugEntry("$etm / сут", String.format("%.1f / сут", etm / 125.0))

            val amcMg = if (a < 18) "${m * 75}" else "5000"
            val amcMl = if (a < 18) String.format("%.1f", m * 75.0 / 50.0) else "100 мл"
            drugsMap["amc"] = DrugEntry(amcMg, amcMl)

            drugsMap["lido"] = DrugEntry("${m * 1}", String.format("%.1f", m / 20.0))
            drugsMap["amio"] = DrugEntry("${m * 5}", "Произв.")

            val d_nal = if (a < 18) m * 0.01 else 4.0
            val s_nal_ml = if (a < 18) String.format("%.2f", m * 0.01 / 4.0) else "1"
            drugsMap["nalox"] = DrugEntry(String.format("%.2f", d_nal), s_nal_ml)

            var metMg = "10"
            var metMl = "2"
            if (a < 2) {
                metMg = "Нет"
                metMl = "Нет"
            } else if (a < 6) {
                metMg = String.format("%.1f", m * 0.1)
                metMl = String.format("%.1f", m * 0.1 / 5.0)
            } else if (a < 18) {
                metMg = "5"
                metMl = "1"
            }
            drugsMap["metoclop"] = DrugEntry(metMg, metMl)
        }

        return AnesthesiaData(
            ad = ad, bpm = bpm, ipm = ipm, cvd = cvd, ett = ett,
            inspVol = inspVol, deadSpace = deadSpace, larMask = larMask,
            minVol = minVol, ock = ock, hourlyDiuresis = hourlyDiuresis, dailyDiuresis = dailyDiuresis,
            drugs = drugsMap
        )
    }
}
