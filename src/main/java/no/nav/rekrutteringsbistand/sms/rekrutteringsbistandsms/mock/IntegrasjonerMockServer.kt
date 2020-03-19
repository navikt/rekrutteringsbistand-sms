package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.mock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("lokal")
@Component
class IntegrasjonerMockServer : InitializingBean, DisposableBean {

    private val server: WireMockServer

    init {
        log.info("Starter mockserver for eksterne integrasjoner.")
        server = WireMockServer(WireMockConfiguration.options().usingFilesUnderClasspath(".").port(8091))
    }

    override fun destroy() {
        log.info("Stopper mockserver.")
        server.shutdown()
    }

    override fun afterPropertiesSet() {
        server.start()
    }
}
