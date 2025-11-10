// Đảm bảo 2 dòng import này ở đầu file
import java.util.Properties
import java.io.FileInputStream
// ĐOẠN CODE LOGIC MỚI
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
// Đọc 3 giá trị mới
val cloudName = localProperties.getProperty("CLOUDINARY_CLOUD_NAME") ?: ""
val apiKey = localProperties.getProperty("CLOUDINARY_API_KEY") ?: ""
val apiSecret = localProperties.getProperty("CLOUDINARY_API_SECRET") ?: ""

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.android.secrets.gradle.plugin)
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.antamvieclam"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.antamvieclam"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        // Chỉ giữ lại dòng buildConfigField ở đây
        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"$cloudName\"")
        buildConfigField("String", "CLOUDINARY_API_KEY", "\"$apiKey\"")
        buildConfigField("String", "CLOUDINARY_API_SECRET", "\"$apiSecret\"")    }

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
//    kotlin {
//        jvmToolchain(17)
//    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coil.compose)


    // Jetpack Compose BoM (Bill of Materials) - Giúp quản lý phiên bản các thư viện Compose
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00") // Kiểm tra phiên bản mới nhất
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Các thư viện Compose cần thiết
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Thư viện cần thiết cho setContent
    implementation("androidx.activity:activity-compose:1.9.0") // Kiểm tra phiên bản mới nhất

// TrackAsia Core SDK
    implementation("io.github.track-asia:android-sdk:2.0.1")
// TrackAsia Data Models
    implementation("io.github.track-asia:android-sdk-geojson:2.0.1")
    implementation("io.github.track-asia:android-sdk-turf:2.0.1")
// TrackAsia Plugins
    implementation("io.github.track-asia:android-plugin-annotation-v9:2.0.1")
// TrackAsia Navigation
    implementation("io.github.track-asia:libandroid-navigation:2.0.0")
    implementation("io.github.track-asia:libandroid-navigation-ui:2.0.0")
// Location Services
    implementation("com.google.android.gms:play-services-location:21.0.1")

// Phần này của bạn đã đúng, giữ nguyên
    implementation(libs.cloudinary.android)
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
}

ksp {
    arg("hilt.CorrectErrorTypes", "true")
}