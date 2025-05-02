import java.util.Properties
import java.io.InputStream

plugins {
    alias(libs.plugins.android.application)
}

// Load local.properties to access the BASE_URL value
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream: InputStream ->
        localProperties.load(stream)
    }
}

android {
    namespace = "com.huy.QuizMe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.huy.QuizMe"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Add BASE_URL from local.properties to BuildConfig
        val baseUrl = localProperties.getProperty("BASE_URL")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Fragment
    implementation(libs.fragment)

    // Navigation Component
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Lifecycle Components
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // RecyclerView
    implementation(libs.recyclerview)
    implementation(libs.circleimageview)

    // Room Database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Retrofit & Gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation (libs.logging.interceptor)

    // Glide for image loading
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    
    // SVG support for Glide
    implementation(libs.glidetovectoryou)
    implementation (libs.okhttp3.integration)

    // SwipeRefreshLayout
    implementation(libs.swiperefreshlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}