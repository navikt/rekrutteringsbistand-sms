package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import net.javacrumbs.shedlock.core.LockAssert
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel.AltinnException
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel.AltinnVarselAdapter
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.stereotype.Component
import java.time.LocalDateTime.now
import kotlin.system.measureTimeMillis

@Component
class SendSmsService(
        private val altinnVarselAdapter: AltinnVarselAdapter,
        private val smsRepository: SmsRepository
) {

    companion object {
        const val MAKS_ANTALL_FORSØK = 10
        const val PRØV_IGJEN_ETTER_MINUTTER = 15L
    }

    @SchedulerLock(name = "sendSmsScheduler")
    fun sendSmserAsync() {
        LockAssert.assertLocked()

        val usendteSmser = smsRepository.hentUsendteSmser()
                .filter { it.gjenværendeForsøk > 0 }
                .filter { it.sistFeilet?.plusMinutes(PRØV_IGJEN_ETTER_MINUTTER)?.isBefore(now()) ?: true }

        log.info("Fant ${usendteSmser.size} usendte SMSer")

        val lås = Semaphore(5)
        usendteSmser.forEach {
            GlobalScope.launch {
                lås.acquire()
                val tid = measureTimeMillis {
                    sendSms(it)
                }
                log.info("Kall til Altinn tok ${tid}ms, id: ${it.id}")
                lås.release()
            }
        }
    }

    fun sendSms(sms: Sms) {
        smsRepository.settStatus(sms.id, Status.UNDER_UTSENDING)
        try {
            altinnVarselAdapter.sendVarsel(sms.fnr, sms.melding)
            log.info("Sendte SMS, id: ${sms.id}")
            smsRepository.settSendt(sms.id)

        } catch (exception: AltinnException) {
            val gjenværendeForsøk = if (sms.gjenværendeForsøk > 0) sms.gjenværendeForsøk - 1 else 0
            log.warn("Kunne ikke sende SMS, id: ${sms.id}, gjenværende forøk: $gjenværendeForsøk")
            smsRepository.settFeil(
                    id = sms.id,
                    status = Status.FEIL,
                    gjenværendeForsøk = gjenværendeForsøk,
                    sistFeilet = now()
            )
        }
    }
}
