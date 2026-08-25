plugins {
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint("1.8.0")
            .setEditorConfigPath("${rootProject.projectDir}/.editorconfig")
    }

    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint("1.8.0")
            .setEditorConfigPath(rootProject.file(".editorconfig").absolutePath)
    }
}
