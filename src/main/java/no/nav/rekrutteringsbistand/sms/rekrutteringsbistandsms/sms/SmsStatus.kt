package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import java.time.LocalDateTime

data class SmsStatus (
        val fnr: String,
        val opprettet: LocalDateTime,
        val sendt: LocalDateTime?,
        val status: Status
)
