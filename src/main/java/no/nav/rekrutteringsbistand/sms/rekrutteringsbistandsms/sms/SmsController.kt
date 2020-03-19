package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.bekk.bekkopen.person.FodselsnummerValidator.isValid
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.HttpStatus
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
    fun sendSms(@RequestBody sms: OpprettSms): ResponseEntity<String> {
        val fnrOk = sms.fnr.all { isValid(it) }
        val lengdeOk = sms.melding.length <= 160

        return if (fnrOk && lengdeOk) {
            smsRepository.lagreSms(sms)
            ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build()
        } else if (!fnrOk) {
            ResponseEntity
                    .badRequest()
                    .body("Ett eller flere av fødselsnummerene er ikke gyldige")
        } else {
            ResponseEntity
                    .badRequest()
                    .body("Meldingen kan ikke være lengre enn 160 tegn")
        }
    }
}
