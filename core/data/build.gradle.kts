plugins {
    alias(libs.plugins.shortfilm.android.library)
    alias(libs.plugins.shortfilm.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.shortdrama.ai.core.data"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.domain)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.serialization.json)
}
