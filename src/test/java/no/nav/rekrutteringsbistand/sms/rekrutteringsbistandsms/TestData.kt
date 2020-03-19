package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.OpprettSms

val enSmsTilOppretting = OpprettSms(
        melding = "Vi trenger deg!",
        fnr = listOf("123", "456", "789"),
        kandidatlisteId = "123456",
        navident = "X123456"
)
