package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler

import net.javacrumbs.shedlock.core.LockAssert
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel.AltinnVarselAdapter
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsRepository
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.Status
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SendSmsScheduler(
        private val altinnVarselAdapter: AltinnVarselAdapter,
        private val smsRepository: SmsRepository
) {

    // TODO finn tidsinterval
    @Scheduled(cron = "0 */1 * * * *")
    // TODO lock at most, lock at least
    @SchedulerLock(name = "sendSmsScheduler")
    fun sendSmser() {
        LockAssert.assertLocked()
        val usendteSmser = smsRepository.hentUsendteSmser()
        log.info("Fant ${usendteSmser.size} usendte SMSer")
        usendteSmser.forEach {
            altinnVarselAdapter.sendVarsel(it.fnr, it.melding)
            // TODO gjør faktisk sjekk
            val smsSendt = true
            if (smsSendt) {
                log.info("Sendte SMS, id: ${it.id}")
                smsRepository.endreSendtStatus(it, Status.SENDT)
            } else {
                // TODO: feil
            }
        }



    }

}
