package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Component

@Component
class AuthUtils(val tokenValidationContextHolder: TokenValidationContextHolder) {

    companion object {
        private const val ISSUER_ISSO = "isso"
        private const val NAVIDENT_CLAIM = "NAVident"
    }

    fun hentNavident(): String =
        tokenValidationContextHolder.tokenValidationContext
                .getClaims(ISSUER_ISSO)
                .get(NAVIDENT_CLAIM)
                .toString()
}
