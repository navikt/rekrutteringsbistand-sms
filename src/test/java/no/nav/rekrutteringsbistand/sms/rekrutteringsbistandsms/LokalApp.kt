package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.bekk.bekkopen.person.FodselsnummerValidator
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    FodselsnummerValidator.ALLOW_SYNTHETIC_NUMBERS = true
    runApplication<RekrutteringsbistandSmsApplication>(*args) {
        setAdditionalProfiles("default")
    }
}
