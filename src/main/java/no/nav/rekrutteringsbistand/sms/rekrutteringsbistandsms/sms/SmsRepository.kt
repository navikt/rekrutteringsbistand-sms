package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


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

    @Transactional
    fun lagreSms(sms: OpprettSms) {
        // TODO sjekk om ting gikk ok
        sms.fnr.forEach {
            smsInsert.execute(
                    mapOf(
                            OPPRETTET to LocalDateTime.now(),
                            SENDT to null,
                            MELDING to sms.melding,
                            FNR to it,
                            KANDIDATLISTE_ID to sms.kandidatlisteId,
                            NAVIDENT to sms.navident,
                            STATUS to Status.IKKE_SENDT.toString()
                    )
            )
        }
        log.info("Lagret ${sms.fnr.size} SMSer i database")
    }

    fun hentSms(id: Number): Sms? {
        return jdbcTemplate.queryForObject("SELECT * FROM sms WHERE id = ? LIMIT 1", arrayOf<Any>(id), SmsMapper())
    }

}
