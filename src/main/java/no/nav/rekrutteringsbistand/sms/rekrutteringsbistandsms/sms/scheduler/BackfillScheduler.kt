package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.KandidatlisteClient
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.KandidatvarselClient
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsRepository
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BackfillScheduler(
    private val smsRepository: SmsRepository,
    private val kandidatvarselClient: KandidatvarselClient,
    private val kandidatlisteClient: KandidatlisteClient,
) {
    companion object {
        const val HVERT_ANDRE_MINUTT = "0 */2 * * * *"
        const val HVERT_TIENDE_SEKUND = "*/10 * * * * *"
    }

    @Scheduled(cron = HVERT_TIENDE_SEKUND)
    fun berikStillingId() {
        val sms = smsRepository.hentSmsUtenStillingId()
        if (sms != null) {
            log.info("BakcfillScheduler.berikStillingId: fant sms uten stillingId")
            smsRepository.settStillingId(
                kandidatlisteId = sms.kandidatlisteId,
                stillingId = kandidatlisteClient.hentStillingId(sms.kandidatlisteId)
            )
            log.info("BakcfillScheduler.berikStillingId: beriket sms")
        }
    }

//    @Scheduled(cron = HVERT_ANDRE_MINUTT)
//    fun backfillDirtySmser() {
//        val smser = smsRepository.hentDirtySmser(1000)
//        log.info("BackfillScheduler.backfillDirtySmser: hentet ${smser.size} dirty smser")
//        if (kandidatvarselClient.backfill(smser)) {
//            log.info("BackfillScheduler.backfillDirtySmser: back-fillet ${smser.size} dirty smser")
//            smsRepository.markClean(smser)
//            log.info("BackfillScheduler.backfillDirtySmser: markerte ${smser.size} smser som clean")
//        } else {
//              log.info("BACKFILL: backfillDirtySmser: backfill feilet")
//        }
//    }
}