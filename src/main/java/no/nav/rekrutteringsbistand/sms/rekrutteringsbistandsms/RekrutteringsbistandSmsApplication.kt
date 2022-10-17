package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
@EnableJwtTokenValidation(ignore = ["org.springframework"])
class RekrutteringsbistandSmsApplication(cluster: String? = System.getenv("NAIS_CLUSTER_NAME")) {

    init {
        val erProd = "prod-fss" == cluster
        FodselsnummerValidator.ALLOW_SYNTHETIC_NUMBERS = !erProd
    }

    fun main(args: Array<String>) {
        runApplication<RekrutteringsbistandSmsApplication>(*args)
    }
}
