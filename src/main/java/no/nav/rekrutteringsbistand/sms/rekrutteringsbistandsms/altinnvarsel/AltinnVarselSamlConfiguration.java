package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel;

import no.altinn.services.serviceengine.notification._2010._10.INotificationAgencyExternalBasic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "prod"})
public class AltinnVarselSamlConfiguration {

    private final AltinnVarselProperties varselProperties;
    private final StsProperties stsProperties;

    public AltinnVarselSamlConfiguration(AltinnVarselProperties varselProperties, StsProperties stsProperties) {
        this.varselProperties = varselProperties;
        this.stsProperties = stsProperties;
    }

    @Bean
    public INotificationAgencyExternalBasic iNotificationAgencyExternalBasic() {
        INotificationAgencyExternalBasic port = WsClient.createPort(varselProperties.getUri().toString(), INotificationAgencyExternalBasic.class, varselProperties.isDebugLog());
        STSClientConfigurer configurer = new STSClientConfigurer(stsProperties.getWsUri(), stsProperties.getUsername(), stsProperties.getPassword(), varselProperties.isDebugLog());
        configurer.configureRequestSamlToken(port);
        return port;
    }
}
