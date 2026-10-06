import com.android.build.gradle.LibraryExtension
import com.shortdrama.ai.buildlogic.configureKotlinAndroid
import com.shortdrama.ai.buildlogic.libs
import com.shortdrama.ai.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.consumerProguardFiles("consumer-rules.pro")
            }

            dependencies {
                add("testImplementation", libs.library("junit"))
            }
        }
    }
}
