package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import java.time.LocalDateTime

data class Sms (
        val id: String,
        val opprettet: LocalDateTime,
        val sendt: LocalDateTime?,
        val melding: String,
        val fnr: String,
        val kandidatlisteId: String,
        val navident: String,
        val status: Status
)

data class OpprettSms (
        val melding: String,
        val fnr: List<String>,
        val kandidatlisteId: String,
        val navident: String
)

enum class Status {
    SENDT, IKKE_SENDT, FEIL
}
