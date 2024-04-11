package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.KandidatlisteClient
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.KandidatvarselClient
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsRepository
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
class BackfillScheduler(
    private val smsRepository: SmsRepository,
    private val kandidatvarselClient: KandidatvarselClient,
    private val kandidatlisteClient: KandidatlisteClient,
) {
    private val shutdownInitiated = AtomicBoolean(false)

    companion object {
        const val HVERT_ANDRE_MINUTT = "0 */2 * * * *"
        const val HVERT_TIENDE_SEKUND = "*/10 * * * * *"
        const val HVERT_ANDRE_SEKUND = "*/2 * * * * *"
    }

    @Scheduled(cron = HVERT_ANDRE_SEKUND)
    fun berikStillingId() {
        if (shutdownInitiated.get()) return

        val sms = smsRepository.hentSmsUtenStillingId()
        if (sms != null) {
            log.info("BakcfillScheduler.berikStillingId: fant sms uten stillingId (kandidatlisteid=${sms.kandidatlisteId})")
            val stillingId = try {
                kandidatlisteClient.hentStillingId(sms.kandidatlisteId)
            } catch (e: Exception) {
                log.info("hentStillingId feilet med exception {}", e::class.qualifiedName, e)
                smsRepository.markerStillingsIdSomBorte(sms.kandidatlisteId)
                return
            }
            smsRepository.settStillingId(
                kandidatlisteId = sms.kandidatlisteId,
                stillingId = stillingId
            )
            log.info("BakcfillScheduler.berikStillingId: vellykket for kandidatlisteid=${sms.kandidatlisteId}")
        } else {
            log.info("BakcfillScheduler.berikStillingId: fant ingen sms uten stillingId")
        }
    }

    @Scheduled(cron = HVERT_TIENDE_SEKUND)
    fun backfillDirtySmser() {
        if (shutdownInitiated.get()) return

        val smser = smsRepository.hentDirtySmser(1000)
        log.info("BackfillScheduler.backfillDirtySmser: hentet ${smser.size} dirty smser")
        if (kandidatvarselClient.backfill(smser)) {
            log.info("BackfillScheduler.backfillDirtySmser: back-fillet ${smser.size} dirty smser")
            smsRepository.markClean(smser)
            log.info("BackfillScheduler.backfillDirtySmser: markerte ${smser.size} smser som clean")
        } else {
            log.info("backfillDirtySmser: backfill feilet")
        }
    }

    private val hex = "[a-f0-9]"
    private val uuidRegex = Regex("""/stilling/($hex{8}-$hex{4}-$hex{4}-$hex{4}-$hex{12})""")
    @Scheduled(cron = HVERT_TIENDE_SEKUND)
    fun backfillDirtySmser2() {
        if (shutdownInitiated.get()) return

        val smser = try {
            smsRepository.hentDirtySmser2(1000)
                .map {
                    it.copy(stillingId = uuidRegex.find(it.melding)!!.groupValues[1])
                }
        } catch (e: Exception) {
            log.info("Extraction av stilling-uuid feilet")
            return
        }

        log.info("BackfillScheduler.backfillDirtySmser2: hentet ${smser.size} dirty smser")

        if (kandidatvarselClient.backfill(smser)) {
            log.info("BackfillScheduler.backfillDirtySmser2: back-fillet ${smser.size} dirty smser")
            smsRepository.markClean(smser)
            log.info("BackfillScheduler.backfillDirtySmser2: markerte ${smser.size} smser som clean")
        } else {
            log.info("backfillDirtySmser2: backfill feilet")
        }
    }

    fun stop() {
        shutdownInitiated.set(true)
    }
}