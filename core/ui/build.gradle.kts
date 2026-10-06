plugins {
    alias(libs.plugins.shortfilm.android.library)
}

android {
    namespace = "com.shortdrama.ai.core.ui"
}

dependencies {
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.androidx.fragment.ktx)
    api(libs.material)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.navigation.fragment.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil)
}
