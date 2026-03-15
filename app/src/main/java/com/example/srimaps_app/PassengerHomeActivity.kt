package com.example.srimaps_app

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.*

class PassengerHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        
        setContent {
             PassengerHomeScreen()
        }
    }

    @Composable
    fun PassengerHomeScreen() {
        var currentTab by remember { mutableStateOf("home") }
        var isJourneyPlanned by remember { mutableStateOf(false) }
        var selectedArticle by remember { mutableStateOf<NewsArticleData?>(null) }
        
        val isDarkMode = AppThemeConfig.isDarkMode
        val accentColor = Color(0xFFC6FF00) // Vibrant Lime Green

        val appBg = if (isDarkMode) Color.Black else Color.White
        val onAppBg = if (isDarkMode) Color.White else Color.Black
        val mapContainerBg = if (isDarkMode) Color.White else Color.Black

        // Back button handling
        BackHandler(enabled = selectedArticle != null || (currentTab == "home" && isJourneyPlanned)) {
            if (selectedArticle != null) {
                selectedArticle = null
            } else if (currentTab == "home" && isJourneyPlanned) {
                isJourneyPlanned = false
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(appBg)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Top Bar - Hide when viewing article detail to match image
                if (selectedArticle == null) {
                    TopBar(
                        currentTab = currentTab,
                        isDarkMode = isDarkMode,
                        onToggleTheme = { AppThemeConfig.isDarkMode = !AppThemeConfig.isDarkMode },
                        textColor = onAppBg,
                        accentColor = Color(0xFF005EFF),
                        onBack = {
                            if (currentTab == "home" && isJourneyPlanned) {
                                isJourneyPlanned = false
                            } else {
                                finish()
                            }
                        }
                    )
                }
                
                // 2. Content Area
                val contentModifier = if ((currentTab == "news" && selectedArticle == null) || currentTab == "schedule") {
                    Modifier.weight(1f).fillMaxWidth()
                } else if (selectedArticle != null) {
                    Modifier.weight(1f).fillMaxWidth()
                } else {
                    Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp).clip(RoundedCornerShape(45.dp))
                }

                Box(modifier = contentModifier.background(if (currentTab == "news" || currentTab == "schedule") appBg else mapContainerBg)) {
                    if (selectedArticle != null) {
                        NewsDetailView(selectedArticle!!, isDarkMode) { selectedArticle = null }
                    } else {
                        Crossfade(targetState = currentTab, animationSpec = tween(500, easing = EaseInOutQuart)) { tab ->
                            when (tab) {
                                "home" -> HomeTabView(isJourneyPlanned, isDarkMode) { isJourneyPlanned = true }
                                "news" -> NewsTabView(isDarkMode) { article -> selectedArticle = article }
                                "schedule" -> ScheduleTabView(isDarkMode)
                                "profile" -> PlaceholderView("Items", isDarkMode)
                            }
                        }
                    }
                }
                
                // 3. Navigation Bar
                CircleNavigationBar(
                    selectedTab = currentTab,
                    accentColor = accentColor,
                    isDarkMode = isDarkMode,
                    onTabSelected = { 
                        currentTab = it
                        selectedArticle = null // Close detail if tab changes
                    }
                )
            }
        }
    }

    @Composable
    fun TopBar(currentTab: String, isDarkMode: Boolean, onToggleTheme: () -> Unit, textColor: Color, accentColor: Color, onBack: () -> Unit) {
        val gsansFont = FontFamily(Font(R.font.gsans_regular))
        val gsansBold = FontFamily(Font(R.font.gsans_bold))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 48.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = textColor
                )
            }
            
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            ) {
                val title = if (currentTab == "news") "News" else if (currentTab == "schedule") "Schedule" else "Greetings, Passenger"
                val subTitle = if (currentTab == "news") "News from all around the country" else if (currentTab == "schedule") "Find your bus timings" else "Welcome to SriMaps!"
                
                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontFamily = gsansBold,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subTitle,
                    fontSize = 14.sp,
                    fontFamily = gsansFont,
                    color = textColor.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (currentTab != "news" && currentTab != "schedule") {
                    Box(
                        modifier = Modifier.size(42.dp).clip(CircleShape).background(textColor).clickable { onToggleTheme() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = if (isDarkMode) R.drawable.ic_light_mode else R.drawable.ic_dark_mode),
                            contentDescription = "Theme Toggle",
                            tint = if (isDarkMode) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier.size(42.dp).clip(CircleShape).background(textColor).clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "Notifications",
                            modifier = Modifier.size(22.dp),
                            tint = if (isDarkMode) Color.Black else Color.White
                        )
                    }
                } else if (currentTab == "news") {
                    Box(
                        modifier = Modifier.size(42.dp).clip(CircleShape).background(accentColor).clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pen),
                            contentDescription = "Post News",
                            modifier = Modifier.size(22.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun HomeTabView(isJourneyPlanned: Boolean, isDarkMode: Boolean, onProceed: () -> Unit) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(6.9271, 79.8612))
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Journey Card Overlay
            AnimatedVisibility(
                visible = !isJourneyPlanned,
                enter = fadeIn() + scaleIn(initialScale = 0.9f),
                exit = fadeOut() + scaleOut(targetScale = 0.9f)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f))) {
                    ProfessionalJourneyCard(isDarkMode, onProceed)
                }
            }
        }
    }

    @Composable
    fun ProfessionalJourneyCard(isDarkMode: Boolean, onProceed: () -> Unit) {
        val gsansBold = FontFamily(Font(R.font.gsans_bold))
        var startLoc by remember { mutableStateOf("") }
        var endLoc by remember { mutableStateOf("") }
        var busNo by remember { mutableStateOf("") }

        val cardBg = if (isDarkMode) Color.Black else Color.White
        val onCardBg = if (isDarkMode) Color.White else Color.Black

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.fillMaxWidth(0.88f).wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
            ) {
                Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Plan Your Journey", fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = gsansBold, color = onCardBg, modifier = Modifier.align(Alignment.Start).padding(bottom = 24.dp))
                    JourneyInputField(value = startLoc, onValueChange = { startLoc = it }, label = "Starting Point", isDarkMode = isDarkMode)
                    Spacer(modifier = Modifier.height(12.dp))
                    JourneyInputField(value = endLoc, onValueChange = { endLoc = it }, label = "Ending Point", isDarkMode = isDarkMode)
                    Text(text = "OR", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = gsansBold, color = onCardBg, modifier = Modifier.padding(vertical = 16.dp))
                    JourneyInputField(value = busNo, onValueChange = { busNo = it }, label = "Bus Number", isDarkMode = isDarkMode)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { if (startLoc.isNotEmpty() && endLoc.isNotEmpty() || busNo.isNotEmpty()) onProceed() },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = onCardBg),
                        shape = RoundedCornerShape(29.dp)
                    ) {
                        Text(text = "Proceed", color = cardBg, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = gsansBold)
                    }
                }
            }
        }
    }

    @Composable
    fun JourneyInputField(value: String, onValueChange: (String) -> Unit, label: String, isDarkMode: Boolean) {
        val gsansFont = FontFamily(Font(R.font.gsans_regular))
        val fieldBg = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F5F5)
        val textColor = if (isDarkMode) Color.White else Color.Black
        OutlinedTextField(
            value = value, onValueChange = onValueChange,
            placeholder = { Text(label, fontSize = 14.sp, color = Color.Gray, fontFamily = gsansFont) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedContainerColor = fieldBg, unfocusedContainerColor = fieldBg, cursorColor = textColor, focusedTextColor = textColor, unfocusedTextColor = textColor)
        )
    }

    @Composable
    fun CircleNavigationBar(selectedTab: String, accentColor: Color, isDarkMode: Boolean, onTabSelected: (String) -> Unit) {
        val navBg = if (isDarkMode) Color.Black else Color.White
        Box(modifier = Modifier.fillMaxWidth().height(95.dp).background(navBg)) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
                val tabs = listOf(TabItem("home", "Home", R.drawable.ic_home), TabItem("news", "News", R.drawable.ic_bus_news), TabItem("schedule", "Schedule", R.drawable.ic_bus_schedule), TabItem("profile", "Items", R.drawable.ic_lost_found))
                tabs.forEach { tab -> CircleNavItem(item = tab, isSelected = selectedTab == tab.id, activeColor = accentColor, onNavBg = if(isDarkMode) Color.White else Color.Black, onClick = { onTabSelected(tab.id) }) }
            }
        }
    }

    @Composable
    fun RowScope.CircleNavItem(item: TabItem, isSelected: Boolean, activeColor: Color, onNavBg: Color, onClick: () -> Unit) {
        val gsansThin = FontFamily(Font(R.font.gsans_thin))
        val gsansBold = FontFamily(Font(R.font.gsans_bold))
        val tintColor by animateColorAsState(targetValue = if (isSelected) activeColor else onNavBg.copy(alpha = 0.6f))
        val iconScale by animateFloatAsState(targetValue = if (isSelected) 1.15f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        val circleScale by animateFloatAsState(targetValue = if (isSelected) 1f else 0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        Column(modifier = Modifier.weight(1f).fillMaxHeight().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(48.dp).scale(circleScale).background(activeColor.copy(alpha = 0.15f), CircleShape))
                Icon(painter = painterResource(id = item.icon), contentDescription = null, modifier = Modifier.size(26.dp).scale(iconScale), tint = tintColor)
            }
            Text(text = item.label, fontSize = 12.sp, fontFamily = if (isSelected) gsansBold else gsansThin, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.6f), modifier = Modifier.padding(top = 4.dp))
        }
    }

    @Composable
    fun NewsTabView(isDarkMode: Boolean, onArticleClick: (NewsArticleData) -> Unit) {
        val textColor = if (isDarkMode) Color.White else Color.Black
        val gsansBold = FontFamily(Font(R.font.gsans_bold))
        val gsansRegular = FontFamily(Font(R.font.gsans_regular))
        val articles = remember {
            listOf(
                NewsArticleData("Bus Overturns on Highway, Several Passengers Injured", "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=800", "A passenger bus traveling on a national highway overturned early in the morning after reportedly losing control while speeding. Authorities confirmed that several passengers were injured and taken to nearby hospitals for treatment. Initial investigations suggest driver fatigue and reckless driving may have contributed to the accident."),
                NewsArticleData("Severe Traffic Congestion Reported in Colombo Center", "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?w=800", "Commuters in Colombo faced significant delays this morning due to a major breakdown of multiple heavy vehicles at key intersections. Traffic police are on the scene directing vehicles, but the congestion is expected to persist until midday."),
                NewsArticleData("New Intercity Express Bus Service Launched from Kandy", "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=800", "The Ministry of Transport has officially inaugurated a new fleet of high-end intercity express buses connecting Kandy to Colombo. These buses are equipped with modern amenities including air conditioning."),
                NewsArticleData("Public Transport Fares to be Revised Next Month", "https://images.unsplash.com/photo-1557223562-6c77ef16210f?w=800", "The National Transport Commission has announced a scheduled revision of bus and train fares starting from the 1st of next month. The adjustment follows a period of fluctuating fuel prices."),
                NewsArticleData("Electric Bus Pilot Program Expands to More Routes", "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?w=800", "The successful electric bus pilot program is being expanded to several more urban routes. This move is part of the government's commitment to reducing carbon emissions in public transport.")
            )
        }
        LazyColumn(modifier = Modifier.fillMaxSize().background(if (isDarkMode) Color.Black else Color.White), contentPadding = PaddingValues(16.dp)) {
            items(articles) { article ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable { onArticleClick(article) }) {
                    Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color.LightGray)) {
                        AsyncImage(model = article.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f).height(100.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Text(text = article.title, fontSize = 16.sp, fontFamily = gsansBold, fontWeight = FontWeight.Bold, color = textColor, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(text = "14 March 2026\n07:30 AM", fontSize = 12.sp, fontFamily = gsansRegular, color = textColor.copy(alpha = 0.6f), textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                    }
                }
                HorizontalDivider(color = textColor.copy(alpha = 0.1f))
            }
        }
    }

    @Composable
    fun NewsDetailView(article: NewsArticleData, isDarkMode: Boolean, onBack: () -> Unit) {
        val textColor = if (isDarkMode) Color.White else Color.Black
        val gsansBold = FontFamily(Font(R.font.gsans_bold))
        val gsansRegular = FontFamily(Font(R.font.gsans_regular))
        Column(modifier = Modifier.fillMaxSize().background(if (isDarkMode) Color.Black else Color.White).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().height(450.dp)) {
                AsyncImage(model = article.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                IconButton(onClick = onBack, modifier = Modifier.padding(top = 48.dp, start = 16.dp).size(40.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)) {
                    Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "14 March 2026  •  07:30 AM", fontSize = 14.sp, fontFamily = gsansRegular, color = textColor.copy(alpha = 0.6f), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = article.title, fontSize = 24.sp, fontFamily = gsansBold, fontWeight = FontWeight.Bold, color = textColor, lineHeight = 32.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = article.description, fontSize = 16.sp, fontFamily = gsansRegular, color = textColor.copy(alpha = 0.8f), lineHeight = 26.sp)
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }

    @Composable
    fun ScheduleTabView(isDarkMode: Boolean) {
        var hasSearched by remember { mutableStateOf(false) }
        LazyColumn(modifier = Modifier.fillMaxSize().background(if (isDarkMode) Color.Black else Color.White), contentPadding = PaddingValues(16.dp)) {
            item { ScheduleSearchSection(isDarkMode) { hasSearched = true } }
            if (hasSearched) {
                item { Spacer(modifier = Modifier.height(24.dp)); RouteHeaderCard(isDarkMode); Spacer(modifier = Modifier.height(24.dp)) }
                item { Text(text = "Daily Schedule", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.White else Color.Black, fontFamily = FontFamily(Font(R.font.gsans_bold)), modifier = Modifier.padding(bottom = 12.dp)) }
                item { DailyScheduleList(isDarkMode) }
            }
        }
    }

    @Composable
    fun ScheduleSearchSection(isDarkMode: Boolean, onSearch: () -> Unit) {
        val context = LocalContext.current
        var from by remember { mutableStateOf("") }
        var to by remember { mutableStateOf("") }
        var busNo by remember { mutableStateOf("") }
        var selectedDate by remember { mutableStateOf("Today") }
        val datePickerDialog = DatePickerDialog(context, { _, y, m, d -> selectedDate = "$d/${m + 1}/$y" }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF5F5F5))) {
            Column(modifier = Modifier.padding(20.dp)) {
                ModernScheduleInputField(value = from, onValueChange = { from = it }, label = "From", isDarkMode = isDarkMode)
                Spacer(modifier = Modifier.height(12.dp))
                ModernScheduleInputField(value = to, onValueChange = { to = it }, label = "To", isDarkMode = isDarkMode)
                Spacer(modifier = Modifier.height(12.dp))
                ModernScheduleInputField(value = busNo, onValueChange = { busNo = it }, label = "Bus Number", isDarkMode = isDarkMode)
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(12.dp)).background(if (isDarkMode) Color.Black else Color.White).clickable { datePickerDialog.show() }.padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                    Text(text = "Date: $selectedDate", color = (if (isDarkMode) Color.White else Color.Black).copy(alpha = if (selectedDate == "Today") 0.6f else 1f), fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.gsans_regular)))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onSearch, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = if(isDarkMode) Color.White else Color.Black), shape = RoundedCornerShape(25.dp)) {
                    Text("Search Schedule", color = if(isDarkMode) Color.Black else Color.White, fontFamily = FontFamily(Font(R.font.gsans_regular)))
                }
            }
        }
    }

    @Composable
    fun ModernScheduleInputField(value: String, onValueChange: (String) -> Unit, label: String, isDarkMode: Boolean) {
        OutlinedTextField(value = value, onValueChange = onValueChange, placeholder = { Text(label, fontSize = 14.sp, color = Color.Gray, fontFamily = FontFamily(Font(R.font.gsans_regular))) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent, focusedContainerColor = if (isDarkMode) Color.Black else Color.White, unfocusedContainerColor = if (isDarkMode) Color.Black else Color.White, cursorColor = if (isDarkMode) Color.White else Color.Black, focusedTextColor = if (isDarkMode) Color.White else Color.Black, unfocusedTextColor = if (isDarkMode) Color.White else Color.Black))
    }

    @Composable
    fun RouteHeaderCard(isDarkMode: Boolean) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF005EFF) else Color(0xFFF0F4FF))) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Route 120", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color.White else Color.Black, fontFamily = FontFamily(Font(R.font.gsans_bold)))
                Text("Colombo → Horana", fontSize = 16.sp, color = (if (isDarkMode) Color.White else Color.Black).copy(alpha = 0.8f), fontFamily = FontFamily(Font(R.font.gsans_regular)))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Distance: 32 km", fontSize = 14.sp, color = (if (isDarkMode) Color.White else Color.Black).copy(alpha = 0.6f), fontFamily = FontFamily(Font(R.font.gsans_regular)))
            }
        }
    }

    @Composable
    fun DailyScheduleList(isDarkMode: Boolean) {
        val times = listOf("06:00 AM", "06:20 AM", "06:45 AM", "07:10 AM", "07:40 AM", "08:00 AM")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            times.forEach { time ->
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF9F9F9)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🚌", modifier = Modifier.padding(end = 12.dp))
                    Text("120 Colombo → Horana", modifier = Modifier.weight(1f), color = if (isDarkMode) Color.White else Color.Black, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.gsans_regular)))
                    Text(time, color = if (isDarkMode) Color.White else Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily(Font(R.font.gsans_regular)))
                }
            }
        }
    }

    @Composable
    fun PlaceholderView(title: String, isDarkMode: Boolean) {
        Box(modifier = Modifier.fillMaxSize().background(if (isDarkMode) Color.Black else Color.White), contentAlignment = Alignment.Center) {
            Text(title, fontSize = 20.sp, color = if (isDarkMode) Color.White else Color.Black)
        }
    }

    data class TabItem(val id: String, val label: String, val icon: Int)
    data class NewsArticleData(val title: String, val imageUrl: String, val description: String)
}
