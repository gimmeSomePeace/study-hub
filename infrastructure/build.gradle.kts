plugins {
    alias(libs.plugins.kotlin.jvm)

    id("conventional.detekt")
    id("conventional.jacoco")

}

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":features:core"))
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.security.web)
    implementation(libs.spring.security.crypto)

    implementation(libs.jjwt.api)
    implementation(libs.uuid.creator)
    implementation(libs.jakarta.servlet.api)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
