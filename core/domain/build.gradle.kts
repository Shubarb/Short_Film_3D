plugins {
    alias(libs.plugins.shortfilm.jvm.library)
}

dependencies {
    api(projects.core.model)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(libs.kotlinx.coroutines.test)
}
