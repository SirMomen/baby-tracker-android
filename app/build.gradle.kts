plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.babytracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.babytracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 6
        versionName = "2.4"
    }

    signingConfigs {
        create("release") {
            // Replace with your keystore path and credentials
            storeFile = file("keystore/release.jks")
            storePassword = "YOUR_KEYSTORE_PASSWORD"
            keyAlias = "YOUR_KEY_ALIAS"
            keyPassword = "YOUR_KEY_PASSWORD"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.webkit:webkit:1.11.0")
}
