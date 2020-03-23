package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PreStopController(private val sendSmsScheduler: SendSmsScheduler) {

    @Unprotected
    @GetMapping("/internal/preStopHook")
    fun preStophook(): ResponseEntity<Unit> {
        log.info("Applikasjonen har fått beskjed om å stoppe, stopper skedulert SMS-utsending")
        sendSmsScheduler.stopSkedulertSmsutsending()
        return ResponseEntity.ok().build()
    }
}
