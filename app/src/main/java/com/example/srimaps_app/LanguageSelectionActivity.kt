package com.example.srimaps_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
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

class LanguageSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LanguageSelectionScreen()
        }
    }

    @Composable
    fun LanguageSelectionScreen() {
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
            // Animated Background
            BackgroundBlobs(screenWidth, screenHeight)

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
                        text = "Select language",
                        fontSize = 32.sp,
                        fontFamily = interFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = scaleIn(tween(800, delayMillis = 200)) + fadeIn(tween(800))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_bus),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))

                LanguageButtons(visible)
            }
        }
    }

    @Composable
    fun LanguageButtons(visible: Boolean) {
        val interFont = FontFamily(Font(R.font.gsans_regular))
        val languages = listOf("English" to "en", "සිංහල" to "si", "தமிழ்" to "ta")

        Column(modifier = Modifier.fillMaxWidth()) {
            languages.forEachIndexed { index, (name, code) ->
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(800, delayMillis = 400 + (index * 150))) + 
                            slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(800, delayMillis = 400 + (index * 150)))
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(this@LanguageSelectionActivity, RoleSelectionActivity::class.java)
                            intent.putExtra("selected_language", code)
                            startActivity(intent)
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
            BlobInfo(R.drawable.blob_yellow, 150.dp, 20.dp, 20.dp, 0.6f),
            BlobInfo(R.drawable.blob_green, 180.dp, width - 100.dp, 100.dp, 0.5f),
            BlobInfo(R.drawable.blob_red, 140.dp, (-50).dp, height - 200.dp, 0.5f),
            BlobInfo(R.drawable.blob_orange, 160.dp, width - 120.dp, height - 150.dp, 0.6f)
        )

        blobs.forEach { blob ->
            val infiniteTransition = rememberInfiniteTransition()
            val floatX by infiniteTransition.animateFloat(
                initialValue = -20f, targetValue = 20f,
                animationSpec = infiniteRepeatable(tween(3000 + (blob.size.value.toInt() * 5), easing = LinearOutSlowInEasing), RepeatMode.Reverse)
            )
            val floatY by infiniteTransition.animateFloat(
                initialValue = -20f, targetValue = 20f,
                animationSpec = infiniteRepeatable(tween(3500 + (blob.size.value.toInt() * 5), easing = LinearOutSlowInEasing), RepeatMode.Reverse)
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
