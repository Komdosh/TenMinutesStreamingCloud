package pro.komdosh.tmsc.auth

import mu.KotlinLogging
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.reactive.config.EnableWebFlux
import pro.komdosh.tmsc.auth.uaa.config.AppProperties
import pro.komdosh.tmsc.auth.uaa.user.AuthProvider
import pro.komdosh.tmsc.auth.uaa.user.UserRepository
import pro.komdosh.tmsc.auth.uaa.user.model.User
import pro.komdosh.tmsc.auth.uaa.user.model.UserRole


val log = KotlinLogging.logger {}

@EnableWebFlux
@EnableDiscoveryClient
@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties(AppProperties::class)
class TmscAuthApplication {
    @Bean
    fun init(userRepository: UserRepository, passwordEncoder: PasswordEncoder) = ApplicationRunner {
        dataSetup(userRepository, passwordEncoder)
    }
}

fun main(args: Array<String>) {
    runApplication<TmscAuthApplication>(*args)
}

fun dataSetup(userRepository: UserRepository, passwordEncoder: PasswordEncoder) {
    if (userRepository.findByEmail("admin").isEmpty) {
        userRepository.save(
            User(
                null,
                "admin",
                passwordEncoder.encode("admin"),
                "Admin",
                "",
                setOf(UserRole.ROLE_ADMIN),
                AuthProvider.LOCAL
            )
        )
    }
}
