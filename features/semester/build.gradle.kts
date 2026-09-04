plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)

    id("conventional.detekt")
    id("conventional.jacoco")
}

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":features:core"))
    implementation(platform(libs.spring.boot.dependencies))

    implementation(libs.spring.web)
    implementation(libs.spring.security.core)
    implementation(libs.spring.boot.starter.validation)

    implementation(libs.spring.data.jpa)
    implementation(libs.jakarta.persistence.api)
    implementation(libs.jackson.module.kotlin)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.mockk)
    testImplementation(libs.mockk)

    testImplementation(libs.spring.security.test)
    testImplementation(libs.spring.security.config)

    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
