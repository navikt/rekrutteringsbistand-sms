package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.IdMapper
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
    private val authUtils: AuthUtils,
    private val idMapper: IdMapper,
) {
    @PostMapping("/sms" )
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

    @GetMapping("/sms/fnr/{fnr}")
    fun hentSmsStatuserForPerson(@PathVariable fnr: String): ResponseEntity<List<SmsStatus>> {
        val smsStatuser = smsRepository.hentSmserForPerson(fnr).map { it.tilSmsStatus() }
        return ResponseEntity.ok(smsStatuser)
    }


    @PostMapping("/varsel/stilling/{stillingId}")
    fun sendSmsV2(@PathVariable stillingId: String, @RequestBody opprettSmsV2: OpprettSmsV2): ResponseEntity<String> {
        val opprettSms = OpprettSms(
            melding = "${opprettSmsV2.mal.tekst} nav.no/arbeid/stilling/${stillingId}",
            fnr = opprettSmsV2.fnr,
            kandidatlisteId = idMapper.hentKandidatlisteId(stillingId)
        )
        return sendSms(opprettSms)
    }

    @GetMapping("/varsel/stilling/{stillingId}")
    fun hentSmsStatuserV2(@PathVariable stillingId: String): ResponseEntity<List<SmsStatusV2>> {
        val kandidatlisteId = idMapper.hentKandidatlisteId(stillingId)
        val smsStatuser = smsRepository.hentSmser(kandidatlisteId).map { tilSmsStatusV2(it) }
        return ResponseEntity.ok(smsStatuser)
    }

    data class Query(val fnr: String)

    @PostMapping("/varsel/query")
    fun hentSmsStatuserForPersonV2(@RequestBody body: Query): ResponseEntity<List<SmsStatusV2>> {
        val smsStatuser = smsRepository.hentSmserForPerson(body.fnr).map { tilSmsStatusV2(it) }
        return ResponseEntity.ok(smsStatuser)
    }

    fun tilSmsStatusV2(sms: Sms): SmsStatusV2 {
        return with(sms) {
            SmsStatusV2(
                id = id.toString(),
                fnr = fnr,
                opprettet = opprettet,
                status = when (status) {
                    Status.SENDT -> StatusV2.SENDT
                    Status.UNDER_UTSENDING -> StatusV2.UNDER_UTSENDING
                    Status.IKKE_SENDT -> StatusV2.UNDER_UTSENDING
                    Status.FEIL -> StatusV2.FEIL
                },
                navIdent = navident,
                stillingId = stillingId ?: idMapper.hentStillingId(kandidatlisteId),
            )
        }
    }
}
