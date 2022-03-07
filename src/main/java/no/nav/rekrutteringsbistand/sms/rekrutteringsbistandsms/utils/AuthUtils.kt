package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils

import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.springframework.stereotype.Component

const val ISSUER_ISSO = "isso"
const val ISSUER_AZUREAD = "azuread"
const val NAVIDENT_CLAIM = "NAVident"

@Component
class AuthUtils(val tokenValidationContextHolder: TokenValidationContextHolder) {
    fun hentNavident(): String {
        val claimsFraIsso = tokenValidationContextHolder.tokenValidationContext.getClaims(ISSUER_ISSO)
        val claimsFraAzureAd = tokenValidationContextHolder.tokenValidationContext.getClaims(ISSUER_AZUREAD)

        return (claimsFraIsso ?: claimsFraAzureAd).get(NAVIDENT_CLAIM).toString()
    }
}
