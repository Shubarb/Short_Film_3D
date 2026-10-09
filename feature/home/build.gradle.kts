plugins {
    alias(libs.plugins.shortfilm.android.feature)
}

android {
    namespace = "com.shortdrama.ai.feature.home"
}

dependencies {
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.viewpager2)
}
