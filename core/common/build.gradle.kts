plugins {
    alias(libs.plugins.shortfilm.android.library)
    alias(libs.plugins.shortfilm.android.hilt)
}

android {
    namespace = "com.shortdrama.ai.core.common"
}

dependencies {
    api(libs.kotlinx.coroutines.android)
}
