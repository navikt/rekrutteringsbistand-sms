package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import java.time.LocalDateTime

data class SmsStatus(
    val id: Int,
    val fnr: String,
    val opprettet: LocalDateTime,
    val sendt: LocalDateTime?,
    val status: Status,
    val navIdent: String,
    val kandidatlisteId: String
)

data class SmsStatusV2(
    val id: String,
    val fnr: String,
    val opprettet: LocalDateTime,
    val status: StatusV2,
    val navIdent: String,
    val stillingId: String
)
