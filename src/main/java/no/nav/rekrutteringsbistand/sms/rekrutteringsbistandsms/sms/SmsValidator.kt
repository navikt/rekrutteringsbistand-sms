package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.bekk.bekkopen.person.FodselsnummerValidator.isValid
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

const val MAKS_LENGDE = 160

data class ValideringsResultat(
        val ok: Boolean,
        val httpStatus: HttpStatus,
        val melding: String
)

@Component
class SmsValidator(private val smsRepository: SmsRepository) {

    fun valider(sms: OpprettSms): ValideringsResultat {
        if (sms.fnr.isEmpty()) {
            return ValideringsResultat(
                    false,
                    HttpStatus.BAD_REQUEST,
                    "Minst ett fødselsnummer må sendes med"
            )
        }

        val fnrOk = sms.fnr.all { isValid(it) }
        if (!fnrOk) {
            return ValideringsResultat(
                    false,
                    HttpStatus.BAD_REQUEST,
                    "Ett eller flere av fødselsnummerene er ikke gyldige"
            )
        }

        val lengdeOk = sms.melding.length <= MAKS_LENGDE
        if (!lengdeOk) {
            return ValideringsResultat(
                    false,
                    HttpStatus.BAD_REQUEST,
                    "Meldingen kan ikke være lengre enn $MAKS_LENGDE tegn"
            )
        }

        val smsMedFnrPåKandidatlisteFinnesIkke = sms.fnr.none {
            smsRepository.smsForFnrPåKandidatlisteAlleredeLagret(it, sms.kandidatlisteId)
        }
        if (!smsMedFnrPåKandidatlisteFinnesIkke) {
            return ValideringsResultat(
                    false,
                    HttpStatus.CONFLICT,
                    "SMS til kandidat på denne kandidatlisten er allerede lagret"
            )
        }

        return ValideringsResultat(
                true,
                HttpStatus.CREATED,
                "SMS lagret"
        )
    }

}

