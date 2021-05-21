package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import net.javacrumbs.shedlock.core.LockAssert
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel.AltinnException
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel.AltinnVarselAdapter
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import kotlin.system.measureTimeMillis

@Service
class SendSmsService(
        private val altinnVarselAdapter: AltinnVarselAdapter,
        private val smsRepository: SmsRepository,
        private val meterRegistry: MeterRegistry
) {

    companion object {
        const val MAKS_ANTALL_FORSØK = 10
        const val PRØV_IGJEN_ETTER_MINUTTER = 15L
        const val SMS_SENDT = "rekrutteringsbistand.sms-sendt"
    }

    val smsSendtMetrikk = meterRegistry.counter(SMS_SENDT)

    @SchedulerLock(name = "sendSmsScheduler")
    fun sendSmserSynkront() {
        LockAssert.assertLocked()

        log.info("Skal sende SMSer");


        // Skal ikke komme forbi denne linjen

        val usendteSmser = smsRepository.hentUsendteSmser()
                .filter { it.gjenværendeForsøk > 0 }
                .filter { it.sistFeilet?.plusMinutes(PRØV_IGJEN_ETTER_MINUTTER)?.isBefore(now()) ?: true }

        log.info("Fant ${usendteSmser.size} usendte SMSer")

        runBlocking {
            val lås = Semaphore(5)
            val utsendinger = usendteSmser.map {
                async { // TODO: "Using async or launch on the instance of GlobalScope is highly discouraged." i følge https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-global-scope/index.html
                    lås.acquire()
                    val tid = measureTimeMillis {
                        sendSms(it)
                    }
                    log.info("Kall til Altinn tok ${tid}ms, id: ${it.id}")
                    lås.release()
                }
            }
            utsendinger.awaitAll()
        }


        /*
        val lås = Semaphore(5)
        val utsendinger = usendteSmser.map {
            async {
                lås.withPermit {
                    // Send SMS
                }
            }
        }
        val responses = utsendinger.awaitAll()
         */

        log.info("Ferdig med all sending")
    }

    fun sendSms(sms: Sms) {
        smsRepository.settStatus(sms.id, Status.UNDER_UTSENDING)
        try {
            altinnVarselAdapter.sendVarsel(sms.fnr, sms.melding)
            smsRepository.settSendt(sms.id)

            log.info("Sendte SMS, id: ${sms.id}")
            smsSendtMetrikk.increment()

        } catch (exception: AltinnException) {
            val gjenværendeForsøk = if (sms.gjenværendeForsøk > 0) sms.gjenværendeForsøk - 1 else 0
            smsRepository.settFeil(
                    id = sms.id,
                    status = Status.FEIL,
                    gjenværendeForsøk = gjenværendeForsøk,
                    sistFeilet = now()
            )

            if (sms.gjenværendeForsøk == 0) {
                log.warn("Kunne ikke sende SMS, id: ${sms.id}, har feilet $MAKS_ANTALL_FORSØK ganger, prøver ikke igjen", exception)
            }
        }
    }
}
