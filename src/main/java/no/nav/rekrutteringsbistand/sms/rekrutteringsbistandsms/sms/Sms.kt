package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import java.time.LocalDateTime

data class Sms (
        val id: Int,
        val opprettet: LocalDateTime,
        val sendt: LocalDateTime?,
        val melding: String,
        val fnr: String,
        val kandidatlisteId: String,
        val navident: String,
        val status: Status,
        val gjenværendeForsøk: Int,
        val sistFeilet: LocalDateTime?,
        val stillingId: String?,
) {
    fun tilSmsStatus(): SmsStatus {
        return SmsStatus(
                id = this.id,
                fnr = this.fnr,
                opprettet = this.opprettet,
                sendt = this.sendt,
                status = this.status,
                navIdent = this.navident,
                kandidatlisteId = this.kandidatlisteId
        )
    }
}

data class OpprettSms (
        val melding: String,
        val fnr: List<String>,
        val kandidatlisteId: String
)

enum class Status {
    SENDT, UNDER_UTSENDING, IKKE_SENDT, FEIL
}


enum class Mal(val tekst: String) {
    @Suppress("unused") // deserialisert
    VURDERT_SOM_AKTUELL("Hei, vi har vurdert at kompetansen din kan passe til denne stillingen, hilsen NAV"),
    @Suppress("unused") // deserialisert
    PASSENDE_STILLING("Hei! Vi har funnet en stilling som kan passe deg. Interessert? Søk via lenka i annonsen. Hilsen NAV"),
    @Suppress("unused") // deserialisert
    PASSENDE_JOBBARRANGEMENT("Hei, vi har et jobbarrangement som kan passe for deg, hilsen NAV. Se mer info:"),
}

data class OpprettSmsV2 (
    val mal: Mal,
    val fnr: List<String>,
)

enum class StatusV2 {
    UNDER_UTSENDING, SENDT, FEIL
}
