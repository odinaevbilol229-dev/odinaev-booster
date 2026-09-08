package com.odinaev.gamebooster

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BoosterFeature(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val action: (Context) -> Unit
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameBoosterTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun GameBoosterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFFF5A2C),
            secondary = Color(0xFFFF8A00),
            background = Color(0xFF0D0D12),
            surface = Color(0xFF17171F)
        ),
        content = content
    )
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    val features = remember {
        listOf(
            BoosterFeature("FPS Монитор", "Намоиши FPS дар бозӣ", Icons.Filled.Speed) { ctx ->
                requestOverlayPermissionThenStart(ctx)
            },
            BoosterFeature("Тоза кардани RAM", "Бастани апп-ҳои фон", Icons.Filled.Memory) { ctx ->
                clearBackgroundApps(ctx)
            },
            BoosterFeature("Game Mode", "Бастани notification-ҳо", Icons.Filled.SportsEsports) { ctx ->
                toggleDnd(ctx)
            },
            BoosterFeature("Ҳарорати CPU", "Назорати гармшавӣ", Icons.Filled.Thermostat) { },
            BoosterFeature("Суръати интернет", "Назорати network", Icons.Filled.NetworkCheck) { },
            BoosterFeature("Тоза кардани Cache", "Озод кардани ҷой", Icons.Filled.CleaningServices) { },
            BoosterFeature("Қулфи равшанӣ", "Нигоҳ доштани brightness", Icons.Filled.Brightness6) { },
            BoosterFeature("Рӯйхати бозиҳо", "Интихоби бозии мақсаднок", Icons.Filled.Games) { },
            BoosterFeature("GFX Танзимот", "Танзими график дар бозӣ", Icons.Filled.Tune) { },
            BoosterFeature("Мод шаб/рӯз", "Тағйири теми апп", Icons.Filled.DarkMode) { },
            BoosterFeature("Ҳолати батарея", "Назорати энергия", Icons.Filled.BatteryChargingFull) { },
            BoosterFeature("Boost — ҳама якҷоя", "Иҷрои ҳамаи корҳо якбора", Icons.Filled.RocketLaunch) { ctx ->
                clearBackgroundApps(ctx)
                toggleDnd(ctx)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D0D12), Color(0xFF1B1024), Color(0xFF0D0D12))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Text(
                "ＯＤＩＮＡＥＶ Booster",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Барномаи тезонидани бозӣ",
                fontSize = 14.sp,
                color = Color(0xFFAAAAAA),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            BoostButton(onClick = {
                clearBackgroundApps(context)
                toggleDnd(context)
            })

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(features) { feature ->
                    FeatureCard(feature, context)
                }
            }
        }
    }
}

@Composable
fun BoostButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFFFF5A2C))
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(listOf(Color(0xFFFF5A2C), Color(0xFFFF8A00)))
            ),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick) {
            Text("⚡ BOOST", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun FeatureCard(feature: BoosterFeature, context: Context) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .shadow(10.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1E1E28).copy(alpha = 0.75f))
            .clickable { feature.action(context) }
            .padding(14.dp)
    ) {
        Column {
            Icon(feature.icon, contentDescription = null, tint = Color(0xFFFF8A00))
            Spacer(modifier = Modifier.height(8.dp))
            Text(feature.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(feature.subtitle, color = Color(0xFF999999), fontSize = 11.sp)
        }
    }
}

// ---- Real feature logic ----

fun requestOverlayPermissionThenStart(context: Context) {
    if (!Settings.canDrawOverlays(context)) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        context.startActivity(intent)
    } else {
        val serviceIntent = Intent(context, OverlayService::class.java)
        context.startService(serviceIntent)
    }
}

fun clearBackgroundApps(context: Context) {
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val processes = am.runningAppProcesses ?: return
    for (process in processes) {
        if (process.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            try {
                am.killBackgroundProcesses(process.processName)
            } catch (_: Exception) { }
        }
    }
}

fun toggleDnd(context: Context) {
    // Requires notification policy access permission granted once by the user.
    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.startActivity(intent)
    }
}
