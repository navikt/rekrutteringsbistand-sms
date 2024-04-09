package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@RestController
class PreStopController(
    private val sendSmsScheduler: SendSmsScheduler,
    private val backfillScheduler: BackfillScheduler,
) {

    @ExperimentalTime
    @Unprotected
    @GetMapping("/internal/preStopHook")
    fun preStophook(): ResponseEntity<Unit> {
        log.info("Applikasjonen har fått beskjed om å stoppe, stopper skedulert SMS-utsending")
        sendSmsScheduler.stopSkedulertSmsutsending()
        backfillScheduler.stop()
        runBlocking { delay(30.seconds) }
        return ResponseEntity.ok().build()
    }
}
