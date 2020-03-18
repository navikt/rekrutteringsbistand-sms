package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.LocalDateTime

class SmsMapper : RowMapper<Sms> {

    override fun mapRow(rs: ResultSet, i: Int): Sms {
        // TODO
        val opprettet = LocalDateTime.now()
        val sendt = LocalDateTime.now()
        return Sms(
                id = rs.getString(SmsRepository.ID),
                opprettet = opprettet,
                sendt = sendt,
                melding = rs.getString(SmsRepository.MELDING),
                fnr = rs.getString(SmsRepository.FNR),
                kandidatlisteId = rs.getString(SmsRepository.KANDIDATLISTE_ID),
                navident = rs.getString(SmsRepository.NAVIDENT),
                status = Status.valueOf(rs.getString(SmsRepository.STATUS))
        )
    }


}
