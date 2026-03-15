package com.example.srimaps_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class WelcomeScreenState { SPLASH, INTRODUCTION }

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelcomeFlow()
        }
    }

    @Composable
    fun WelcomeFlow() {
        var screenState by remember { mutableStateOf(WelcomeScreenState.SPLASH) }
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp

        val interFont = FontFamily(Font(R.font.gsans_regular))
        val interBold = FontFamily(Font(R.font.gsans_bold))
        val abhayaFont = FontFamily(Font(R.font.abhaya_libre))

        LaunchedEffect(Unit) {
            delay(3000)
            screenState = WelcomeScreenState.INTRODUCTION
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // Balanced and Spread Blobs Layer
            BlobsLayer(screenState, screenWidth, screenHeight)

            val logoYOffset by animateDpAsState(
                targetValue = if (screenState == WelcomeScreenState.SPLASH) 0.dp else (-220).dp,
                animationSpec = tween(1200, easing = FastOutSlowInEasing)
            )
            val logoScale by animateFloatAsState(
                targetValue = if (screenState == WelcomeScreenState.SPLASH) 1f else 0.7f,
                animationSpec = tween(1200, easing = FastOutSlowInEasing)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = logoYOffset),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = screenState == WelcomeScreenState.SPLASH,
                    exit = fadeOut(tween(500))
                ) {
                    Text(
                        text = "Welcome to",
                        color = Color(0xFF444444),
                        fontSize = 18.sp,
                        fontFamily = interFont,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ශ්‍රී",
                        color = Color.Black,
                        fontSize = (100 * logoScale).sp,
                        fontFamily = abhayaFont
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Maps",
                        color = Color.Black,
                        fontSize = (45 * logoScale).sp,
                        fontFamily = interFont
                    )
                }
            }

            AnimatedVisibility(
                visible = screenState == WelcomeScreenState.INTRODUCTION,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(tween(1000)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                WelcomeCard(interBold, interFont, abhayaFont)
            }
        }
    }

    @Composable
    fun BlobsLayer(state: WelcomeScreenState, width: Dp, height: Dp) {
        val blobs = listOf(
            // Top Left area
            BlobData(R.drawable.blob_yellow, 240.dp, (-60).dp, (-60).dp, (-100).dp, (-50).dp, 0.95f),
            // Top Right area
            BlobData(R.drawable.blob_green, 260.dp, width - 140.dp, (-80).dp, width - 80.dp, (-120).dp, 0.9f),
            // Left Middle area
            BlobData(R.drawable.blob_red, 180.dp, (-80).dp, height * 0.35f, (-120).dp, height * 0.2f, 0.9f),
            // Right Middle area
            BlobData(R.drawable.blob_orange, 200.dp, width - 80.dp, height * 0.45f, width + 20.dp, height * 0.6f, 0.95f),
            // Bottom Left area
            BlobData(R.drawable.blob_yellow, 190.dp, (-30).dp, height - 150.dp, (-60).dp, height - 100.dp, 0.85f),
            // Bottom Right area
            BlobData(R.drawable.blob_red, 220.dp, width - 150.dp, height - 180.dp, width - 50.dp, height - 120.dp, 0.85f),
            // Center area - spread them out in introduction
            BlobData(R.drawable.blob_orange, 170.dp, width * 0.2f, height * 0.15f, width * 0.7f, height * 0.1f, 0.8f),
            BlobData(R.drawable.blob_green, 210.dp, width * 0.7f, height * 0.75f, width * 0.1f, height * 0.8f, 0.85f),
            // Fill gaps
            BlobData(R.drawable.blob_yellow, 150.dp, width * 0.45f, height * 0.05f, width * 0.5f, height * 0.3f, 0.75f),
            BlobData(R.drawable.blob_red, 160.dp, width * 0.1f, height * 0.8f, width * 0.6f, height * 0.9f, 0.8f)
        )

        blobs.forEach { blob ->
            val targetX by animateDpAsState(
                targetValue = if (state == WelcomeScreenState.SPLASH) blob.splashX else blob.introX,
                animationSpec = tween(2200, easing = FastOutSlowInEasing)
            )
            val targetY by animateDpAsState(
                targetValue = if (state == WelcomeScreenState.SPLASH) blob.splashY else blob.introY,
                animationSpec = tween(2200, easing = FastOutSlowInEasing)
            )
            
            FloatingBlob(blob.resId, blob.size, targetX, targetY, blob.alpha)
        }
    }

    @Composable
    fun FloatingBlob(resId: Int, size: Dp, x: Dp, y: Dp, baseAlpha: Float) {
        val infiniteTransition = rememberInfiniteTransition()
        val floatX by infiniteTransition.animateFloat(
            initialValue = -50f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(tween(7000, easing = LinearOutSlowInEasing), RepeatMode.Reverse)
        )
        val floatY by infiniteTransition.animateFloat(
            initialValue = -60f,
            targetValue = 60f,
            animationSpec = infiniteRepeatable(tween(8000, easing = LinearOutSlowInEasing), RepeatMode.Reverse)
        )
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart)
        )
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(tween(9000, easing = FastOutSlowInEasing), RepeatMode.Reverse)
        )

        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .offset(x = x + floatX.dp, y = y + floatY.dp)
                .scale(scale)
                .alpha(baseAlpha)
                .graphicsLayer(rotationZ = rotation)
        )
    }

    @Composable
    fun WelcomeCard(bold: FontFamily, regular: FontFamily, abhaya: FontFamily) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Welcome to ", fontSize = 28.sp, fontFamily = bold, fontWeight = FontWeight.Bold)
                    Text("ශ්‍රී", fontSize = 40.sp, fontFamily = abhaya)
                    Text(" Maps !", fontSize = 28.sp, fontFamily = bold, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "We help you track your bus in real-time,\nstay updated and travel with confidence\nto anywhere in Sri Lanka.",
                    fontSize = 14.sp,
                    fontFamily = regular,
                    textAlign = TextAlign.Center,
                    color = Color.Black.copy(alpha = 0.8f),
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = {
                        startActivity(Intent(this@WelcomeActivity, LanguageSelectionActivity::class.java))
                        finish()
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Get Started", color = Color.White, fontSize = 18.sp, fontFamily = regular)
                }
            }
        }
    }

    data class BlobData(val resId: Int, val size: Dp, val splashX: Dp, val splashY: Dp, val introX: Dp, val introY: Dp, val alpha: Float)
}
