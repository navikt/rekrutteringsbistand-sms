package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Component

const val ISSUER_ISSO = "isso"
const val NAVIDENT_CLAIM = "NAVident"

@Component
class AuthUtils(val tokenValidationContextHolder: TokenValidationContextHolder) {

    companion object {
    }

    fun hentNavident(): String =
        tokenValidationContextHolder.tokenValidationContext
                .getClaims(ISSUER_ISSO)
                .get(NAVIDENT_CLAIM)
                .toString()
}
