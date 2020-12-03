package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.OpprettSms
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

val enSmsTilOppretting = OpprettSms(
    melding = "Vi trenger deg!",
    fnr = listOf("02080569664", "03012341295", "31129611815"),
    kandidatlisteId = "123456"
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
 * Ensure the precision is no more than milliseconds, since Mac and Windows operating systems differ,
 * plus ensure unique timestamps in case this method is called multiple times during one millisecond.
 */
fun now(): LocalDateTime {
    Thread.sleep(1)
    return now().truncatedTo(ChronoUnit.MILLENNIA)
}
