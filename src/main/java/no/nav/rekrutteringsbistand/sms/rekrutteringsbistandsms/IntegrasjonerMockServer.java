package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class IntegrasjonerMockServer implements InitializingBean, DisposableBean {
    private final WireMockServer server;

    public IntegrasjonerMockServer() {
        // TODO: Log
//        log.info("Starter mockserver for eksterne integrasjoner.");
        server = new WireMockServer(WireMockConfiguration.options().usingFilesUnderClasspath(".").port(8091));
    }

    @Override
    public void destroy() {
        // TODO: Log
//        log.info("Stopper mockserver.");
        server.shutdown();
    }

    @Override
    public void afterPropertiesSet() {
        server.start();
    }
}
