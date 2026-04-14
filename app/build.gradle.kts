plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.murmurbox"
    compileSdk = 36

    flavorDimensions.add("env")

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.murmurbox"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    productFlavors {
        create("dev") {
            dimension = "env"

            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            buildConfigField("Boolean", "ALLOW_DESTRUCTIVE_MIGRATION", "true")
        }

        create("prod") {
            dimension = "env"

            buildConfigField("Boolean", "ALLOW_DESTRUCTIVE_MIGRATION", "false")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.room3.common.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
}