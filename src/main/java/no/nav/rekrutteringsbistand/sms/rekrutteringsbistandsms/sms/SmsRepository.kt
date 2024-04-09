package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
class SmsRepository(
        private val jdbcTemplate: JdbcTemplate,
        private val simpleJdbcInsert: SimpleJdbcInsert
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
        const val GJENVÆRENDE_FORSØK = "gjenvarende_forsok"
        const val SIST_FEILET = "sist_feilet"
        const val STILLING_ID = "stilling_id"
        const val STILLING_ID_MISSING = "stilling_id_missing"
        const val DIRTY = "dirty"
    }

    private val smsInsert = simpleJdbcInsert
            .withTableName(TABELL)
            .usingGeneratedKeyColumns(ID)

    fun lagreSms(sms: OpprettSms, navident: String) {
        val smsRaderTilLagring: List<Map<String, Any?>> = sms.fnr.map {
            mapOf(
                    OPPRETTET to LocalDateTime.now(),
                    SENDT to null,
                    MELDING to sms.melding,
                    FNR to it,
                    KANDIDATLISTE_ID to sms.kandidatlisteId,
                    NAVIDENT to navident,
                    STATUS to Status.IKKE_SENDT.toString(),
                    GJENVÆRENDE_FORSØK to SendSmsService.MAKS_ANTALL_FORSØK
            )
        }
        val oppdaterteRader: IntArray = smsInsert.executeBatch(*smsRaderTilLagring.toTypedArray())
        log.info("Lagret ${oppdaterteRader.sum()} SMSer i database")
    }

    fun hentUsendteSmser(): List<Sms> {
        return jdbcTemplate.query("SELECT * FROM $TABELL WHERE $STATUS = 'IKKE_SENDT' OR $STATUS = 'FEIL'", SmsMapper())
    }

    fun hentDirtySmser(limit: Int): List<Sms> {
        return jdbcTemplate.query("SELECT * FROM $TABELL WHERE $DIRTY <> false AND $STILLING_ID is not null LIMIT $limit", SmsMapper())
    }

    fun smsForFnrPåKandidatlisteAlleredeLagret(fnr: String, kandidatlisteId: String): Boolean {
        return jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM $TABELL WHERE $FNR = ? AND $KANDIDATLISTE_ID = ?)",
                Boolean::class.java,
                fnr,
                kandidatlisteId
        )
    }

    fun settSendt(id: Int) {
        jdbcTemplate.update("UPDATE $TABELL SET $STATUS = ?, $SENDT = ?, dirty = true WHERE $ID = ?", Status.SENDT.name, LocalDateTime.now(), id)
    }

    fun settStatus(id: Int, status: Status) {
        jdbcTemplate.update("UPDATE $TABELL SET $STATUS = ?, dirty = true WHERE $ID = ?", status.name, id)
    }

    fun settStillingId(kandidatlisteId: String, stillingId: String) {
        jdbcTemplate.update("UPDATE $TABELL SET $STILLING_ID = ? WHERE $KANDIDATLISTE_ID = ?", stillingId, kandidatlisteId)
    }

    // TODO Legg til test som bruker denne metoden
    fun settFeil(id: Int, status: Status, gjenværendeForsøk: Int, sistFeilet: LocalDateTime) {
        jdbcTemplate.update(
                "UPDATE $TABELL SET $STATUS = ?, $GJENVÆRENDE_FORSØK = ?, $SIST_FEILET = ?, dirty = true WHERE $ID = ?",
                status.name, gjenværendeForsøk, sistFeilet, id
        )
    }

    fun hentSmser(kandidatlisteId: String): List<Sms> {
        return jdbcTemplate.query("SELECT * FROM $TABELL WHERE $KANDIDATLISTE_ID = ?", arrayOf(kandidatlisteId), SmsMapper())
    }

    fun slettSms(fnr: String) {
        jdbcTemplate.update("DELETE FROM $TABELL WHERE $FNR = ?", fnr)
    }

    fun hentSmserForPerson(fnr: String): List<Sms> {
        return jdbcTemplate.query("SELECT * FROM $TABELL WHERE $FNR = ?", SmsMapper(), fnr)
    }

    fun finnKandidatlisteId(stillingId: String): String? {
        return try {
            jdbcTemplate.queryForObject("SELECT kandidatliste_id FROM $TABELL WHERE $STILLING_ID = ? LIMIT 1",  String::class.java, stillingId)
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun finnStillingId(kandidatlisteId: String): String? {
        return try {
            jdbcTemplate.queryForObject("SELECT stilling_id FROM $TABELL WHERE $KANDIDATLISTE_ID = ? LIMIT 1",  String::class.java, kandidatlisteId)
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun markClean(smser: List<Sms>) {
        jdbcTemplate.update("""
            UPDATE $TABELL
            SET $DIRTY = false
            WHERE $ID IN (:ids)
        """, MapSqlParameterSource().apply {
            addValue("ids", smser.map { it.id })
        })
    }

    fun hentSmsUtenStillingId(): Sms? {
        return try {
            jdbcTemplate.queryForObject("SELECT * FROM $TABELL WHERE $STILLING_ID IS NULL AND $STILLING_ID_MISSING <> true LIMIT 1", SmsMapper())
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun markerStillingsIdSomBorte(kandidatlisteId: String) {
        jdbcTemplate.update("""
            UPDATE $TABELL
            SET $STILLING_ID_MISSING = true
            WHERE $KANDIDATLISTE_ID = ?
        """)
    }
}
