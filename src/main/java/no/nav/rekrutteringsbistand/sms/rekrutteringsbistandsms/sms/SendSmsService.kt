package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.*
import net.javacrumbs.shedlock.core.LockAssert
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel.AltinnException
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel.AltinnVarselAdapter
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

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
    fun sendSmser() {
        LockAssert.assertLocked()

        val usendteSmser = smsRepository.hentUsendteSmser()
            .filter { it.gjenværendeForsøk > 0 }
            .filter { it.sistFeilet?.plusMinutes(PRØV_IGJEN_ETTER_MINUTTER)?.isBefore(now()) ?: true }

        log.info("Kjører SMS-scheduler, fant ${usendteSmser.size} usendte SMSer")

        val myPool = Executors.newFixedThreadPool(5).asCoroutineDispatcher()
        val totalTidsbrukMs = measureTimeMillis {
            runBlocking {
                usendteSmser.forEach {
                    launch(myPool) {
                        val tid = measureTimeMillis {
                            sendSms(it)
                        }
                        log.info("Kall til Altinn tok ${tid}ms, id: ${it.id}")
                    }
                }
            }
        }

        log.info("SMS-scheduler ferdig, brukte $totalTidsbrukMs ms")
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
