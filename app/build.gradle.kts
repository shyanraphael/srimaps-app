import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.srimaps_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.srimaps_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks.register("fixFonts") {
    doLast {
        val fontDir = file("src/main/res/font")
        val files = fontDir.listFiles() ?: return@doLast
        files.forEach { file ->
            val name = file.name
            val lowerName = name.lowercase()
            if (name != lowerName) {
                val tempFile = File(fontDir, name + ".tmp")
                val targetFile = File(fontDir, lowerName)
                if (file.renameTo(tempFile)) {
                    if (targetFile.exists()) targetFile.delete()
                    if (tempFile.renameTo(targetFile)) {
                        println("Renamed $name to $lowerName")
                    } else {
                        println("Failed to rename temp to $lowerName")
                    }
                } else {
                    println("Failed to rename $name to temp")
                }
            }
        }
    }
}
