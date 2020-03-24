package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.OpprettSms

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
