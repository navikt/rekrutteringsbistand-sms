package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel;

import no.altinn.services.serviceengine.notification._2010._10.INotificationAgencyExternalBasic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("lokal")
public class AltinnVarselConfiguration {
    private final AltinnVarselProperties varselProperties;

    public AltinnVarselConfiguration(AltinnVarselProperties varselProperties) {
        this.varselProperties = varselProperties;
    }

    @Bean
    public INotificationAgencyExternalBasic iNotificationAgencyExternalBasic() {
        return WsClient.createPort(
                varselProperties.getUri().toString(),
                INotificationAgencyExternalBasic.class,
                varselProperties.isDebugLog()
        );
    }
}
