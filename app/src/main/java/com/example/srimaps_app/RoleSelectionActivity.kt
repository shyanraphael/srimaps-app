package com.example.srimaps_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class RoleSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedLanguage = intent.getStringExtra("selected_language") ?: "en"
        
        setContent {
            RoleSelectionScreen(selectedLanguage)
        }
    }

    @Composable
    fun RoleSelectionScreen(lang: String) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
        val interFont = FontFamily(Font(R.font.gsans_regular))
        
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(100)
            visible = true
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            BackgroundBlobs(screenWidth, screenHeight)

            // Top Bar with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .clickable { finish() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(1000)) + slideInVertically(initialOffsetY = { -40 })
                ) {
                    Text(
                        text = when(lang) {
                            "si" -> "ඔබේ භූමිකාව තෝරන්න."
                            "ta" -> "உங்கள் பாத்திரத்தைத் தேர்ந்தெடுக்கவும்."
                            else -> "Choose your role."
                        },
                        fontSize = 32.sp,
                        fontFamily = interFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = scaleIn(tween(800, delayMillis = 200)) + fadeIn(tween(800))
                ) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val tintColor by infiniteTransition.animateColor(
                        initialValue = Color(0xFFFFC107),
                        targetValue = Color(0xFFE65100),
                        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Reverse)
                    )
                    
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bus),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = tintColor
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))

                RoleButtons(visible, lang)
            }
        }
    }

    @Composable
    fun RoleButtons(visible: Boolean, lang: String) {
        val interFont = FontFamily(Font(R.font.gsans_regular))
        val roles = listOf(
            (if(lang == "si") "රියදුරු" else if(lang == "ta") "ஓட்டுநர்" else "Driver") to "driver",
            (if(lang == "si") "මගියා" else if(lang == "ta") "பயணிகள்" else "Passenger") to "passenger"
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            roles.forEachIndexed { index, (name, role) ->
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(800, delayMillis = 400 + (index * 200))) + 
                            slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(800, delayMillis = 400 + (index * 200)))
                ) {
                    Button(
                        onClick = {
                            if (role == "passenger") {
                                val intent = Intent(this@RoleSelectionActivity, PassengerHomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(text = name, color = Color.White, fontSize = 18.sp, fontFamily = interFont)
                    }
                }
            }
        }
    }

    @Composable
    fun BackgroundBlobs(width: Dp, height: Dp) {
        val blobs = listOf(
            BlobInfo(R.drawable.blob_yellow, 200.dp, (-50).dp, 50.dp, 0.7f),
            BlobInfo(R.drawable.blob_green, 220.dp, width - 120.dp, 150.dp, 0.65f),
            BlobInfo(R.drawable.blob_red, 180.dp, (-30).dp, height - 250.dp, 0.65f),
            BlobInfo(R.drawable.blob_orange, 210.dp, width - 150.dp, height - 100.dp, 0.7f),
            BlobInfo(R.drawable.blob_yellow, 150.dp, width / 2, height / 2, 0.5f)
        )

        blobs.forEach { blob ->
            val infiniteTransition = rememberInfiniteTransition()
            val floatX by infiniteTransition.animateFloat(
                initialValue = -30f, targetValue = 30f,
                animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse)
            )
            val floatY by infiniteTransition.animateFloat(
                initialValue = -35f, targetValue = 35f,
                animationSpec = infiniteRepeatable(tween(4500, easing = FastOutSlowInEasing), RepeatMode.Reverse)
            )

            Image(
                painter = painterResource(id = blob.resId),
                contentDescription = null,
                modifier = Modifier
                    .size(blob.size)
                    .offset(x = blob.x + floatX.dp, y = blob.y + floatY.dp)
                    .alpha(blob.alpha)
            )
        }
    }

    data class BlobInfo(val resId: Int, val size: Dp, val x: Dp, val y: Dp, val alpha: Float)
}
