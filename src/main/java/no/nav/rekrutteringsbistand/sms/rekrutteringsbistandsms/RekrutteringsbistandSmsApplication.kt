package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.bekk.bekkopen.person.FodselsnummerValidator
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
@EnableJwtTokenValidation(ignore = ["org.springframework"])
class RekrutteringsbistandSmsApplication {

    fun main(args: Array<String>) {
        clusterconfig()
        runApplication<RekrutteringsbistandSmsApplication>(*args)
    }

}

fun clusterconfig(cluster: String? = System.getenv("NAIS_CLUSTER_NAME")) {
    val erProd = "prod-fss" == cluster
    FodselsnummerValidator.ALLOW_SYNTHETIC_NUMBERS = !erProd
}
