plugins {
    alias(libs.plugins.spotless)
    base
    jacoco
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

// Агрегированный отчёт по всем модулям
tasks.register<JacocoReport>("jacocoRootReport") {
    description = "Generates an aggregate report from all subprojects"
    group = "verification"

    // Зависит от всех тестов в подмодулях
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("test") })

    // Исходники из всех подмодулей
    sourceDirectories.setFrom(
        files(subprojects.map { it.layout.projectDirectory.dir("src/main/kotlin") }),
    )

    // Классы из всех подмодулей
    classDirectories.setFrom(
        files(
            subprojects.mapNotNull { subproject ->
                subproject.extensions
                    .findByType(SourceSetContainer::class.java)
                    ?.findByName("main")
                    ?.output
            },
        ),
    )

    // Данные выполнения (.exec файлы) из всех подмодулей
    executionData.setFrom(
        files(
            subprojects.mapNotNull { subproject ->
                val testTask = subproject.tasks.findByName("test") as? Test
                testTask?.extensions?.findByType(JacocoTaskExtension::class.java)?.destinationFile
            },
        ),
    )

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}
