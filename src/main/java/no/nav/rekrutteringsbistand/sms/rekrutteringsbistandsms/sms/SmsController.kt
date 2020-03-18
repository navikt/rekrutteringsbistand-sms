package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.security.token.support.core.api.Protected
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Protected
@RestController
class SmsController(
        private val smsRepository: SmsRepository
) {

    companion object {
        private val log = LoggerFactory.getLogger(SmsController::class.java)
    }

    // TODO: Til post
    @GetMapping("/sms")
    fun test(): String {
        log.info("Lagrer i database")
        // TODO: Lagre i database
        val sms = OpprettSms(
                melding = "test",
                fnr = "blabla",
                kandidatlisteId = "bla",
                navident = "yey"
        )
        val id = smsRepository.lagreSms(sms)

        smsRepository.hentSms(id)

        return "test"
    }
}
