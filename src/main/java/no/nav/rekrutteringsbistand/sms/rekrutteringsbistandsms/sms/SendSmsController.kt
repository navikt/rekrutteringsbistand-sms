package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Protected
@RestController
class SendSmsController(
        private val smsRepository: SmsRepository,
        private val sendSmsService: SendSmsService,
        private val smsValidator: SmsValidator,
        private val authUtils: AuthUtils
) {

    @PostMapping("/sms")
    fun sendSms(@RequestBody sms: OpprettSms): ResponseEntity<String> {
        val (ok, httpStatus, melding) = smsValidator.valider(sms)
        if (ok) {
            smsRepository.lagreSms(
                    sms = sms,
                    navident = authUtils.hentNavident()
            )
            sendSmsService.sendSmserAsync()
        }

        return ResponseEntity
                .status(httpStatus)
                .body(melding)
    }

}
