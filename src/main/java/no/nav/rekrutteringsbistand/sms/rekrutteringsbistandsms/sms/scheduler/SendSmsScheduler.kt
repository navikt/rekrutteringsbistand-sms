package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SendSmsService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
class SendSmsScheduler(private val sendSmsService: SendSmsService) {

    companion object {
        const val HVERT_MINUTT = "0 */1 * * * *"
    }

    private val skalKjøreSkedulertSmsutsending = AtomicBoolean(true)

    @Scheduled(cron = HVERT_MINUTT)
    fun skedulertSmsutsending() {
        if (skalKjøreSkedulertSmsutsending.get()) {
            sendSmsService.sendSmserAsync()
        }
    }

    fun stopSkedulertSmsutsending() {
        skalKjøreSkedulertSmsutsending.set(false)
    }
}
