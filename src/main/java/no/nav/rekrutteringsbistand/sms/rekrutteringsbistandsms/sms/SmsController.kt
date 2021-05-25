package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.AuthUtils
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import no.nav.security.token.support.core.api.Protected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Protected
@RestController
class SmsController(
    private val smsRepository: SmsRepository,
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
        } else {
            log.warn("Kunne ikke sende SMS tıl kandidater på kandidatliste med id ${sms.kandidatlisteId}, årsak: $melding")
        }

        return ResponseEntity
            .status(httpStatus)
            .body(melding)
    }

    @GetMapping("/sms/{kandidatlisteId}")
    fun hentSmsStatuser(@PathVariable kandidatlisteId: String): ResponseEntity<List<SmsStatus>> {
        val smsStatuser = smsRepository.hentSmser(kandidatlisteId).map { it.tilSmsStatus() }
        return ResponseEntity.ok(smsStatuser)
    }
}
