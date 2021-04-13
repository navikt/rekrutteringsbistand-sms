package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.config

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.ISSUER_ISSO
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.core.api.Unprotected
import no.nav.security.token.support.spring.test.MockOAuth2ServerAutoConfiguration
import org.springframework.context.annotation.Import
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Import(MockOAuth2ServerAutoConfiguration::class)
@RestController
class MockLogin(val mockOauth2Server: MockOAuth2Server) {

    @Unprotected
    @GetMapping("/veileder-token-cookie")
    fun getVeilederTokenCookie(response: HttpServletResponse) {

        val token = mockOauth2Server.issueToken(
                issuerId = ISSUER_ISSO,
                subject = "brukes-ikke",
                claims = mapOf(
                        "unique_name" to "Clark.Kent@nav.no",
                        "NAVident" to "X123456",
                        "name" to "Clark Kent"
                )
        )

        val cookie = Cookie("isso-idtoken", token.serialize())
        response.addCookie(cookie)
    }
}
