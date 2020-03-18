package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class SmsRepository(
        val jdbcTemplate: JdbcTemplate,
        simpleJdbcInsert: SimpleJdbcInsert
) {

     companion object {
        const val TABELL = "sms"
        const val ID = "id"
        const val OPPRETTET = "opprettet"
        const val SENDT = "sendt"
        const val MELDING = "melding"
        const val FNR = "fnr"
        const val KANDIDATLISTE_ID = "kandidatliste_id"
        const val NAVIDENT = "navident"
        const val STATUS = "status"
    }

    private val smsInsert = simpleJdbcInsert
            .withTableName(TABELL)
            .usingGeneratedKeyColumns(ID)

    fun lagreSms(sms: OpprettSms): Number {
        return smsInsert.executeAndReturnKey(
                mapOf(
                        OPPRETTET to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        SENDT to null,
                        MELDING to sms.melding,
                        FNR to sms.fnr,
                        KANDIDATLISTE_ID to sms.kandidatlisteId,
                        NAVIDENT to sms.navident,
                        STATUS to Status.IKKE_SENDT.toString()
                )
        )

    }

    fun hentSms(id: Number): Sms {
        return jdbcTemplate.query("SELECT * FROM sms LIMIT 1", SmsMapper())[0]
    }

}