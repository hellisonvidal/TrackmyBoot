plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlinx-serialization")
    id("org.owasp.dependencycheck")
}

android {
    namespace = "com.trackmyboot.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.trackmyboot.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        viewBinding = true
        dataBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE,NOTICE}"
        }
    }

    // Force WorkManager version to 2.9.0 to avoid AGP/compileSdk conflicts
    resolutionsStrategy {
        force("androidx.work:work-runtime:2.9.0")
        force("androidx.work:work-runtime-ktx:2.9.0")
    }
}

dependencies {
    // Core Android
    val coreKtxVersion = "1.12.0"
    val activityComposeVersion = "1.9.0"
    val lifecycleVersion = "2.7.0"
    val roomVersion = "2.6.1"
    val navigationVersion = "2.7.6"
    val hiltVersion = "2.48"
    val retrofitVersion = "2.11.0"
    val okhttpVersion = "4.12.0"
    val kotlinxCoroutinesVersion = "1.7.3"
    val kotlinxSerializationVersion = "1.6.3"
    val coilVersion = "2.6.0"
    val material3Version = "1.3.1"
    val materialVersion = "1.12.0"
    val workManagerVersion = "2.9.0"

    // AndroidX Core
    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:$activityComposeVersion")
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.material3:material3-android:$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.navigation:navigation-compose:$navigationVersion")

    // Room Database
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    // Coroutines & Flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    // Image Loading
    implementation("io.coil-kt:coil-compose:$coilVersion")

    // WorkManager for background price checking
    implementation("androidx.work:work-runtime-ktx:$workManagerVersion")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
}