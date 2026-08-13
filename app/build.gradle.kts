import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.secrets)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.aistudio.jarvis.jvsqzn"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath =
                System.getenv("KEYSTORE_PATH")
                    ?: "${rootDir}/my-upload-key.jks"

            storeFile = file(keystorePath)
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = "upload"
            keyPassword = System.getenv("KEY_PASSWORD")
        }

        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isCrunchPngs = false
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = true
    }
}

/*
 * Kotlin 2.x / Gradle 9.x compatible JVM configuration.
 *
 * IMPORTANT:
 * Do NOT use:
 *
 * kotlinOptions {
 *     jvmTarget = "17"
 * }
 *
 * because Kotlin 2.x uses compilerOptions.
 */
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

// Secrets Gradle Plugin
secrets {
    propertiesFileName = ".env"
    defaultPropertiesFileName = ".env.example"
    ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

// Firebase Google Services
// google-services.json is supplied by GitHub Actions during build.
googleServices {
    missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN
}

dependencies {

    // ---------------------------------------------------------
    // Compose
    // ---------------------------------------------------------

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // ---------------------------------------------------------
    // Android Core
    // ---------------------------------------------------------

    implementation(libs.androidx.core.ktx)

    // ---------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // ---------------------------------------------------------
    // Navigation
    // ---------------------------------------------------------

    implementation(libs.androidx.navigation.compose)

    // ---------------------------------------------------------
    // Room
    // ---------------------------------------------------------

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    // ---------------------------------------------------------
    // Image Loading
    // ---------------------------------------------------------

    implementation(libs.coil.compose)

    // ---------------------------------------------------------
    // Networking
    // ---------------------------------------------------------

    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)

    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.retrofit)

    // ---------------------------------------------------------
    // Firebase
    // ---------------------------------------------------------

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.ai)
    implementation(libs.firebase.appcheck.recaptcha)

    // ---------------------------------------------------------
    // Kotlin Coroutines
    // ---------------------------------------------------------

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // ---------------------------------------------------------
    // Unit Tests
    // ---------------------------------------------------------

    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.junit)

    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.robolectric)

    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)

    // ---------------------------------------------------------
    // Android Tests
    // ---------------------------------------------------------

    androidTestImplementation(platform(libs.androidx.compose.bom))

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.runner)

    // ---------------------------------------------------------
    // Debug
    // ---------------------------------------------------------

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // ---------------------------------------------------------
    // KSP / Code Generation
    // ---------------------------------------------------------

    ksp(libs.androidx.room.compiler)
    ksp(libs.moshi.kotlin.codegen)
}
