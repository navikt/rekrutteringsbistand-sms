package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.mock

import com.nimbusds.jwt.JWTClaimsSet
import no.nav.security.token.support.core.api.Unprotected
import no.nav.security.token.support.test.JwkGenerator
import no.nav.security.token.support.test.JwtTokenGenerator
import no.nav.security.token.support.test.spring.TokenGeneratorConfiguration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Import(TokenGeneratorConfiguration::class)
@RestController
@Profile("lokal")
class MockLogin {

    /**
     *  Lager egen login controller siden token-validation-test-support ikke legger med NAVident claimet i sin login
     *  https://github.com/navikt/token-support/blob/master/token-validation-test-support/src/main/java/no/nav/security/token/support/test/JwtTokenGenerator.java#L45
     */
    @Unprotected
    @GetMapping("/local/login")
    fun addCookie(response: HttpServletResponse): Cookie? {
        val now = Date()
        val claimSet = JWTClaimsSet.Builder()
                .subject("01234567890")
                .issuer("iss-localhost")
                .audience("aud-localhost")
                .claim("acr", "Level4")
                .claim("NAVident", "X123456")
                .issueTime(now)
                .expirationTime(Date(now.time + 12960000L))
                .build()

        val token = JwtTokenGenerator.createSignedJWT(JwkGenerator.getDefaultRSAKey(), claimSet)
        val cookie = Cookie("localhost-idtoken", token.serialize())
        cookie.domain = "localhost"
        cookie.path = "/"
        response.addCookie(cookie)
        return cookie
    }
}
