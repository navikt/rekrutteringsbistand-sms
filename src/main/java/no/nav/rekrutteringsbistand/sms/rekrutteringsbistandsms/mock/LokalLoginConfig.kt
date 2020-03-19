package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.mock

import no.nav.security.token.support.test.spring.TokenGeneratorConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Profile

@Configuration
@Import(TokenGeneratorConfiguration::class)
@Profile("lokal")
class LokalLoginConfig
