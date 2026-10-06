import com.shortdrama.ai.buildlogic.libs
import com.shortdrama.ai.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * A feature module = Android library + Hilt + the core modules every screen needs.
 * Features never depend on each other or on :core:data; they talk to the domain layer only.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("shortfilm.android.library")
            pluginManager.apply("shortfilm.android.hilt")

            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:model"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:ui"))

                add("implementation", libs.library("androidx-core-ktx"))
                add("implementation", libs.library("androidx-appcompat"))
                add("implementation", libs.library("androidx-fragment-ktx"))
                add("implementation", libs.library("androidx-constraintlayout"))
                add("implementation", libs.library("material"))
                add("implementation", libs.library("androidx-lifecycle-viewmodel-ktx"))
                add("implementation", libs.library("androidx-lifecycle-runtime-ktx"))
                add("implementation", libs.library("androidx-navigation-fragment-ktx"))
                add("implementation", libs.library("androidx-navigation-ui-ktx"))
                add("implementation", libs.library("kotlinx-coroutines-android"))

                add("testImplementation", libs.library("kotlinx-coroutines-test"))
            }
        }
    }
}
