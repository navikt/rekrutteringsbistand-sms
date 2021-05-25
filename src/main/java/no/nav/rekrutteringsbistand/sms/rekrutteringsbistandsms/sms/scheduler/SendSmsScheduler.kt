package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SendSmsService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
class SendSmsScheduler(private val sendSmsService: SendSmsService) {

    companion object {
        const val HVERT_ANDRE_MINUTT = "0 */2 * * * *"
    }

    private val skalKjøreSkedulertSmsutsending = AtomicBoolean(true)

    @Scheduled(cron = HVERT_ANDRE_MINUTT)
    fun skedulertSmsutsending() {
        if (skalKjøreSkedulertSmsutsending.get()) {
            sendSmsService.sendSmser()
        }
    }

    fun stopSkedulertSmsutsending() {
        skalKjøreSkedulertSmsutsending.set(false)
    }
}
