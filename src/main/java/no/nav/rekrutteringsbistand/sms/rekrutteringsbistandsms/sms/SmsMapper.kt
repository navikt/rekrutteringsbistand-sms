package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class SmsMapper : RowMapper<Sms> {
    override fun mapRow(rs: ResultSet, i: Int) =
            Sms(
                    id = rs.getString(SmsRepository.ID),
                    opprettet = rs.getTimestamp(SmsRepository.OPPRETTET).toLocalDateTime(),
                    sendt = rs.getTimestamp(SmsRepository.SENDT)?.toLocalDateTime(),
                    melding = rs.getString(SmsRepository.MELDING),
                    fnr = rs.getString(SmsRepository.FNR),
                    kandidatlisteId = rs.getString(SmsRepository.KANDIDATLISTE_ID),
                    navident = rs.getString(SmsRepository.NAVIDENT),
                    status = Status.valueOf(rs.getString(SmsRepository.STATUS))
            )
}
