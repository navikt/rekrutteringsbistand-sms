package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<RekrutteringsbistandSmsApplication>(*args) {
        setAdditionalProfiles("default")
    }
}
