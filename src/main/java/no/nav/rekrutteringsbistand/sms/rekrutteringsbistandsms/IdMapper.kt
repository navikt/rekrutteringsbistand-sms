package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsRepository
import org.springframework.stereotype.Service

@Service
class IdMapper(
    private val kandidatlisteClient: KandidatlisteClient,
    private val smsRepository: SmsRepository,
) {
    fun hentKandidatlisteId(stillingId: String): String {
        var kandidatlisteId = smsRepository.finnKandidatlisteId(stillingId)
        if (kandidatlisteId == null) {
            kandidatlisteId = kandidatlisteClient.hentKandidatlisteId(stillingId)
            smsRepository.settStillingId(
                kandidatlisteId = kandidatlisteId,
                stillingId = stillingId
            )
        }
        return kandidatlisteId
    }

    fun hentStillingId(kandidatlisteId: String): String {
        var stillingId = smsRepository.finnStillingId(kandidatlisteId)
        if (stillingId == null) {
            stillingId = kandidatlisteClient.hentStillingId(kandidatlisteId)
            smsRepository.settStillingId(
                kandidatlisteId = kandidatlisteId,
                stillingId = stillingId
            )
        }
        return stillingId
    }
}