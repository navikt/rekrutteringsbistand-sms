package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel.AltinnException
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel.AltinnVarselAdapter
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler.ConcurrencyConfig
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.stereotype.Component
import java.util.HashMap
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class SendSmsService(
        private val altinnVarselAdapter: AltinnVarselAdapter,
        private val smsRepository: SmsRepository,
        private val concurrencyConfig: ConcurrencyConfig
) {

    fun sendSmserAsync() {
        val usendteSmser = smsRepository.hentUsendteSmser()
        log.info("Fant ${usendteSmser.size} usendte SMSer")

        val allFutures = HashMap<String, CompletableFuture<String>>()
        usendteSmser.forEach {
            allFutures[it.id] = CompletableFuture.supplyAsync(
                    Supplier { sendSms(it) },
                    concurrencyConfig.sendSmsExecutor()
            )
        }
        CompletableFuture.allOf(*allFutures.values.toTypedArray())
    }

    fun sendSms(sms: Sms): String {
        smsRepository.settStatus(sms.id, Status.UNDER_UTSENDING)
        try {
            altinnVarselAdapter.sendVarsel(sms.fnr, sms.melding)
            log.info("Sendte SMS, id: ${sms.id}")
            smsRepository.settSendt(sms.id)
        } catch (exception: AltinnException) {
            log.warn("Kunne ikke sende SMS, id: ${sms.id}")
            smsRepository.settStatus(sms.id, Status.FEIL)
        }
        return sms.id
    }
}
