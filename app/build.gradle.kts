plugins {
    alias(libs.plugins.shortfilm.android.application)
    alias(libs.plugins.shortfilm.android.hilt)
    alias(libs.plugins.google.services)
}

android {
    // Kotlin/Java code lives in com.shortdrama.ai because "short" is a reserved Java keyword
    // and generated Java sources (Hilt, BuildConfig) cannot use it as a package segment.
    namespace = "com.shortdrama.ai"

    defaultConfig {
        // Must match package_name in google-services.json.
        applicationId = "com.short.drama.ai"
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.data)

    implementation(projects.feature.home)
    implementation(projects.feature.player)
    implementation(projects.feature.shorts)
    implementation(projects.feature.library)
    implementation(projects.feature.settings)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
