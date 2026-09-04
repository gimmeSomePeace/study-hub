package me.gimmesomepeace.studyhub.app

import me.gimmesomepeace.studyhub.app.property.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["me.gimmesomepeace.studyhub"])
@EntityScan(basePackages = ["me.gimmesomepeace.studyhub"])
@EnableJpaRepositories("me.gimmesomepeace.studyhub")
@EnableConfigurationProperties(JwtProperties::class)
class StudyHubApplication

fun main(args: Array<String>) {
    runApplication<StudyHubApplication>(*args)
}
