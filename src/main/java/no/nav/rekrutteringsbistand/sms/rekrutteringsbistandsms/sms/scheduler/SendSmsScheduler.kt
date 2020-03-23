package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SendSmsService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class SendSmsScheduler(private val sendSmsService: SendSmsService) {

    companion object {
        const val HVERT_MINUTT = "0 */1 * * * *"
    }

    @Scheduled(cron = HVERT_MINUTT)
    fun schedulertJobb() {
        sendSmsService.sendSmserAsync()
    }

}
