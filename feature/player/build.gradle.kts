plugins {
    alias(libs.plugins.shortfilm.android.feature)
}

android {
    namespace = "com.shortdrama.ai.feature.player"
}

dependencies {
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.ui)
}
