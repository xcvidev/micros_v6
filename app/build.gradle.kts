plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.xcvi.micros"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.xcvi.micros"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = org.jetbrains.kotlin.konan.properties.Properties().apply {
            val localFile = rootProject.file("local.properties")
            if (localFile.exists()) {
                load(localFile.inputStream())
            }
        }

        val openAiKey = localProperties.getProperty("OPENAI_API_KEY")
            ?: throw GradleException("OPENAI_API_KEY not found in local.properties")

        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiKey\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    //licence
    implementation(project(":lvl"))

    //scanner
    implementation(libs.google.mlkit)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.view)

    //kotlin
    implementation(libs.kotlin.json)
    implementation(libs.kotlin.dateTime)
    implementation(libs.kotlin.stdlib)

    //room
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // Koin
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.android)

    //ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.utils)

    //compose
    implementation(libs.androidx.compose.viewmodel)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.m3)
    implementation(libs.androidx.compose.icons)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}