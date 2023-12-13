plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.withpersona.sdk2.demo"
    compileSdk = 34

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.withpersona.sdk2.demo"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(libs.persona.inquiry)
    implementation(libs.persona.nfc)
    implementation(libs.persona.webrtc)
    implementation(libs.appcompat)
    implementation(libs.material)
}
