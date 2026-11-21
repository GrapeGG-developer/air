package gpr.greentune.air.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gpr.greentune.air.Air
import gpr.greentune.air.AirBackground
import gpr.greentune.air.AirButton
import gpr.greentune.air.AirDialog
import gpr.greentune.air.AirProgressBar
import gpr.greentune.air.AirSlider
import gpr.greentune.air.AirTheme
import gpr.greentune.air.Badge
import gpr.greentune.air.GlassPanel
import gpr.greentune.air.Orb
import gpr.greentune.air.OrbState
import gpr.greentune.air.ProvideBackdrop
import gpr.greentune.air.SectionTitle
import gpr.greentune.air.Segmented
import gpr.greentune.air.TiltSensor
import gpr.greentune.air.ToggleRow
import gpr.greentune.air.backdropSource
import gpr.greentune.air.staggerIn
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { Showcase() }
    }
}

@Composable
private fun Showcase() {
    var theme by remember { mutableStateOf("system") }
    var tint by remember { mutableStateOf(Color(0xFF19A7CE)) }
    AirTheme(theme, tint, 0.6f) {
        ProvideBackdrop {
            var state by remember { mutableStateOf(OrbState.Off) }
            var level by remember { mutableFloatStateOf(0f) }
            var tilt by remember { mutableStateOf(true) }
            var dialog by remember { mutableStateOf(false) }
            TiltSensor(tilt)
            LaunchedEffect(state) {
                if (state == OrbState.Starting) {
                    delay(1400)
                    state = OrbState.On
                }
            }
            Box(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize().backdropSource()) {
                    AirBackground(true, if (state == OrbState.On) 1f else 0f, pulse = level)
                }
                Column(
                    Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Air", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Air.colors.text)
                        Spacer(Modifier.width(10.dp))
                        Badge("1.0")
                    }
                    Spacer(Modifier.height(24.dp))
                    Orb(
                        state = state,
                        level = level,
                        description = "Orb",
                        size = 200.dp,
                        onClick = { state = if (state == OrbState.Off || state == OrbState.Error) OrbState.Starting else OrbState.Off },
                    )
                    Spacer(Modifier.height(24.dp))
                    GlassPanel(Modifier.fillMaxWidth().staggerIn(0)) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SectionTitle("Live background")
                            Text("Pulse", color = Air.colors.subText)
                            AirSlider(level, { level = it })
                            AirProgressBar(if (state == OrbState.Starting) null else level)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    GlassPanel(Modifier.fillMaxWidth().staggerIn(1)) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SectionTitle("Theme")
                            Segmented(listOf("system" to "System", "light" to "Light", "dark" to "Dark"), theme, { theme = it })
                            Segmented(listOf("aqua" to "Aqua", "jade" to "Jade", "ember" to "Ember"),
                                when (tint) { Color(0xFF2EB872) -> "jade"; Color(0xFFE8743B) -> "ember"; else -> "aqua" },
                                { tint = when (it) { "jade" -> Color(0xFF2EB872); "ember" -> Color(0xFFE8743B); else -> Color(0xFF19A7CE) } })
                            ToggleRow("Tilt parallax", "Glare and background follow the phone", tilt, { tilt = it })
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.fillMaxWidth().staggerIn(2), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AirButton("Dialog", { dialog = true }, Modifier.weight(1f), primary = true)
                        AirButton("Error", { state = OrbState.Error }, Modifier.weight(1f), danger = true)
                    }
                }
                if (dialog) {
                    AirDialog(
                        title = "Air",
                        onDismiss = { dialog = false },
                        buttons = { AirButton("OK", { dialog = false }, primary = true) },
                    ) {
                        Text("Glass, light and motion for Jetpack Compose.", color = Air.colors.text)
                    }
                }
            }
        }
    }
}
