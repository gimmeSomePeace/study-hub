val detektPluginId = "conventional.detekt"
val jacocoPluginId = "conventional.jacoco"

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("DetektConventionPlugin") {
            id = detektPluginId
            implementationClass = "me.gimmesomepeace.buildlogic.DetektConventionPlugin"
        }
        register("JacocoReportPlugin") {
            id = jacocoPluginId
            implementationClass = "me.gimmesomepeace.buildlogic.JacocoConventionPlugin"
        }
    }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findLibrary("kotlin-gradle-plugin").get())
    implementation(libs.findLibrary("detekt-gradle-plugin").get())
}

kotlin {
    jvmToolchain(21)
}
