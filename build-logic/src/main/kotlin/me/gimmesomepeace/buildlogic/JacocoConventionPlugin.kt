package me.gimmesomepeace.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport


@Suppress("unused")
class JacocoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("jacoco")

            extensions.configure<JacocoPluginExtension> {
                toolVersion = "0.8.12"
            }

            tasks.withType(Test::class.java).configureEach {
                finalizedBy(tasks.named("jacocoTestReport"))
            }
            tasks.withType(JacocoReport::class.java).configureEach {
                dependsOn(tasks.named("test"))

                reports {
                    html.required.set(true)
                    xml.required.set(true)
                }
            }
        }
    }
}
