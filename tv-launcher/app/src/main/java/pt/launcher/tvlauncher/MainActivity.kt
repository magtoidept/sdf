package pt.launcher.tvlauncher

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AppEntry(val name: String, val packages: List<String>)

private val YOUTUBE = AppEntry(
    "YouTube",
    listOf("com.google.android.youtube.tv", "com.google.android.youtube")
)
private val PRIME = AppEntry(
    "Amazon Prime Video",
    listOf(
        "com.amazon.amazonvideo.livingroom",
        "com.amazon.avod",
        "com.amazon.avod.thirdpartyclient"
    )
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { LauncherScreen(onOpen = ::openApp) }
    }

    override fun onResume() {
        super.onResume()
        WindowInsetsControllerCompat(window, window.decorView)
            .hide(WindowInsetsCompat.Type.systemBars())
    }

    /** Devolve true se a aplicação foi aberta, false se não estiver instalada. */
    private fun openApp(entry: AppEntry): Boolean {
        for (pkg in entry.packages) {
            val intent = packageManager.getLeanbackLaunchIntentForPackage(pkg)
                ?: packageManager.getLaunchIntentForPackage(pkg)
            if (intent != null) {
                return try {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }
        return false
    }
}

@Composable
fun LauncherScreen(onOpen: (AppEntry) -> Boolean) {
    var message by remember { mutableStateOf<String?>(null) }
    val firstFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) { firstFocus.requestFocus() }
    LaunchedEffect(message) {
        if (message != null) {
            delay(3500)
            message = null
        }
    }

    // O launcher nunca fecha com o botão Voltar
    BackHandler(enabled = true) { }

    fun open(entry: AppEntry) {
        if (!onOpen(entry)) message = "${entry.name} não está instalado nesta televisão."
    }

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF161618), Color(0xFF000000))))
    ) {
        val cardWidth = maxWidth * 0.34f

        Box(Modifier.align(Alignment.TopEnd).padding(horizontal = 56.dp, vertical = 40.dp)) {
            Clock()
        }

        Row(
            Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(56.dp)
        ) {
            AppCard(
                width = cardWidth,
                modifier = Modifier.focusRequester(firstFocus),
                background = Brush.linearGradient(listOf(Color(0xFF2A2A2D), Color(0xFF151517))),
                onClick = { open(YOUTUBE) }
            ) { YoutubeLogo() }

            AppCard(
                width = cardWidth,
                background = Brush.linearGradient(listOf(Color(0xFF1B2A38), Color(0xFF0B141C))),
                onClick = { open(PRIME) }
            ) { PrimeLogo() }
        }

        message?.let {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF2C2C2E))
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                BasicText(it, style = TextStyle(color = Color.White, fontSize = 22.sp))
            }
        }
    }
}

@Composable
private fun AppCard(
    width: androidx.compose.ui.unit.Dp,
    background: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.10f else 1f, tween(250), label = "scale")
    val shape = RoundedCornerShape(28.dp)

    Box(
        modifier
            .width(width)
            .aspectRatio(16f / 10f)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(if (focused) 32.dp else 0.dp, shape, ambientColor = Color.White, spotColor = Color.White)
            .clip(shape)
            .background(background)
            .border(
                BorderStroke(if (focused) 3.dp else 1.dp, if (focused) Color.White else Color(0x22FFFFFF)),
                shape
            )
            .onFocusChanged { focused = it.isFocused }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun Clock() {
    fun now(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    var time by remember { mutableStateOf(now()) }
    LaunchedEffect(Unit) {
        while (true) {
            time = now()
            delay(10_000)
        }
    }
    BasicText(
        time,
        style = TextStyle(color = Color(0xB3FFFFFF), fontSize = 24.sp, fontWeight = FontWeight.Light)
    )
}

/* Representações simplificadas desenhadas em código (não são os ficheiros oficiais). */

@Composable
private fun YoutubeLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(Modifier.size(112.dp, 80.dp)) {
            drawRoundRect(Color(0xFFFF0000), cornerRadius = CornerRadius(size.height * 0.3f))
            val p = Path().apply {
                moveTo(size.width * 0.40f, size.height * 0.28f)
                lineTo(size.width * 0.40f, size.height * 0.72f)
                lineTo(size.width * 0.68f, size.height * 0.50f)
                close()
            }
            drawPath(p, Color.White)
        }
        Box(Modifier.padding(top = 14.dp)) {
            BasicText(
                "YouTube",
                style = TextStyle(color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
private fun PrimeLogo() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BasicText(
            "prime video",
            style = TextStyle(color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold)
        )
        Canvas(Modifier.padding(top = 4.dp).size(170.dp, 28.dp)) {
            drawArc(
                color = Color(0xFF00A8E1),
                startAngle = 20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(size.width * 0.1f, -size.height * 1.2f),
                size = Size(size.width * 0.8f, size.height * 2.2f),
                style = Stroke(width = 7f, cap = StrokeCap.Round)
            )
        }
    }
}
