import com.shortdrama.ai.buildlogic.configureKotlinJvm
import com.shortdrama.ai.buildlogic.libs
import com.shortdrama.ai.buildlogic.library
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            configureKotlinJvm()

            dependencies {
                add("testImplementation", libs.library("junit"))
            }
        }
    }
}
