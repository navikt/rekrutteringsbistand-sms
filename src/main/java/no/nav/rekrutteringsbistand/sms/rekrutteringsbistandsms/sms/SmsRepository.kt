package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
class SmsRepository(
        private val jdbcTemplate: JdbcTemplate,
        private val simpleJdbcInsert: SimpleJdbcInsert,
        private val authUtils: AuthUtils
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
        val oppdaterteRader: IntArray = smsInsert.executeBatch(*smsRaderTilLagring.toTypedArray())
        log.info("Lagret ${oppdaterteRader.sum()} SMSer i database")
    }

    // TODO: Skal man kun sende de som er IKKE_SENDT? Hva med de som feiler?
    fun hentUsendteSmser(): List<Sms> {
        return jdbcTemplate.query("SELECT * FROM sms WHERE status = 'IKKE_SENDT'", SmsMapper())
    }

    fun settSendt(id: String) {
        jdbcTemplate.update("UPDATE sms SET status = ?, sendt = ? WHERE id = ?", Status.SENDT.name, LocalDateTime.now(), id)
    }

    fun settStatus(id: String, status: Status) {
        jdbcTemplate.update("UPDATE sms SET status = ? WHERE id = ?", status.name, id)
    }

    fun hentSms(id: Number): Sms? {
        return jdbcTemplate.queryForObject("SELECT * FROM sms WHERE id = ? LIMIT 1", arrayOf<Any>(id), SmsMapper())
    }
}
