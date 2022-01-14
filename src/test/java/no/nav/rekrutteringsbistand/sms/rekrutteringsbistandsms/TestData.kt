package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.OpprettSms
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

val enSmsTilOppretting = OpprettSms(
    melding = "Vi trenger deg!",
    fnr = listOf("02080569664", "03012341295", "31129611815"),
    kandidatlisteId = "123456"
)

val enAnnenSmsTilOpprettingSammeFnr = OpprettSms(
    melding = "Til del 2!",
    fnr = listOf("02080569664", "03012341295", "31129611815"),
    kandidatlisteId = "555555"
)

val enSmsTilOpprettingMedUgyldigFnr = enSmsTilOppretting.copy(
    fnr = listOf("123", "feil")
)

val enSmsTilOpprettingMedForLangMelding = enSmsTilOppretting.copy(
    melding = "Hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei " +
            "Hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei hei "
)

val enSmsUtenFnr = enSmsTilOppretting.copy(
    fnr = listOf()
)

const val enNavIdent = "X123456"

/**
 * Unngå feilende asserts pga. ulik presisjon på timestamps mellom Mac og Windows
 */
fun now(): LocalDateTime {
    Thread.sleep(1) // Sikre unike timestamps når innneværende metode kalles flere ganger i samme millisekund
    return LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
}
