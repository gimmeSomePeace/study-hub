plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)

    alias(libs.plugins.spring.boot)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":features:core"))
    implementation(project(":features:user"))
    implementation(project(":features:semester"))
    implementation(project(":features:subject"))
    implementation(project(":features:deadline"))
    implementation(project(":infrastructure"))

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.springdoc.openapi.starter.webmvc)

    implementation(libs.jjwt.api)
    implementation(libs.spring.boot.starter.flyway)

    runtimeOnly(libs.flyway.core)
    runtimeOnly(libs.flyway.database.postgresql)
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.jjwt.impl)

    testImplementation(libs.spring.boot.starter.test)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
