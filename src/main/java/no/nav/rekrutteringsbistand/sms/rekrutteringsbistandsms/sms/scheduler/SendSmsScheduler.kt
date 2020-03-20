package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler

import net.javacrumbs.shedlock.core.LockAssert
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SendSmsService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class SendSmsScheduler(private val sendSmsService: SendSmsService) {

    companion object {
        const val HVERT_MINUTT = "0 */1 * * * *"
    }

    @Scheduled(cron = HVERT_MINUTT)
    @SchedulerLock(name = "sendSmsScheduler")
    fun schedulertJobb() {
        LockAssert.assertLocked()
        sendSmsService.sendSmserAsync()
    }

}
