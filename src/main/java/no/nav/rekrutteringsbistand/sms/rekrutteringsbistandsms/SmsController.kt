package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.security.token.support.core.api.Protected
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Protected
@RestController
class SmsController {

    companion object {
        private val log = LoggerFactory.getLogger(SmsController::class.java)
    }

    @GetMapping("/sms")
    fun test(): String {
        log.info("Lagrer i database")
        // TODO: Lagre i database
        return "test"
    }
}
