plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.spring)

    id("conventional.detekt")
    id("conventional.jacoco")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":features:common"))

    implementation(platform(libs.spring.boot.dependencies))

    implementation(libs.spring.web)
    implementation(libs.spring.boot.starter.validation)

    implementation(libs.spring.data.jpa)
    implementation(libs.jakarta.persistence.api)
    implementation(libs.jackson.module.kotlin)

    implementation(libs.jjwt.api)
    implementation(libs.spring.security.core)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.mockk)
    testImplementation(libs.mockk)

    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
