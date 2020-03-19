package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
@EnableJwtTokenValidation(ignore = ["org.springframework"])
class RekrutteringsbistandSmsApplication

fun main(args: Array<String>) {
    runApplication<RekrutteringsbistandSmsApplication>(*args)
}
