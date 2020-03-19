package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Protected
@RestController
class SmsController(
        private val smsRepository: SmsRepository
) {

    @PostMapping("/sms")
    fun sendSms(@RequestBody sms: OpprettSms): ResponseEntity<Unit> {
        // TODO valider data
        smsRepository.lagreSms(sms)
        return ResponseEntity.ok().build()
    }
}
