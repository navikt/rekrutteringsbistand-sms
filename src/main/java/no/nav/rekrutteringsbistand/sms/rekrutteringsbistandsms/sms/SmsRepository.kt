package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
class SmsRepository(
        val jdbcTemplate: JdbcTemplate,
        simpleJdbcInsert: SimpleJdbcInsert,
        val authUtils: AuthUtils
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

    fun lagreSms(sms: OpprettSms) {
        // TODO sjekk om ting gikk ok
        val smsRaderTilLagring: List<Map<String, Any?>> = sms.fnr.map {
            mapOf(
                    OPPRETTET to LocalDateTime.now(),
                    SENDT to null,
                    MELDING to sms.melding,
                    FNR to it,
                    KANDIDATLISTE_ID to sms.kandidatlisteId,
                    NAVIDENT to authUtils.hentNavident(),
                    STATUS to Status.IKKE_SENDT.toString()
            )
        }
        val antallLagret = smsInsert.executeBatch(*smsRaderTilLagring.toTypedArray())
        log.info("Lagret $antallLagret SMSer i database")
    }

    fun hentSms(id: Number): Sms? {
        return jdbcTemplate.queryForObject("SELECT * FROM sms WHERE id = ? LIMIT 1", arrayOf<Any>(id), SmsMapper())
    }
}
