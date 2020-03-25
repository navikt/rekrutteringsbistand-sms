package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.mock

import no.altinn.services.serviceengine.notification._2010._10.INotificationAgencyExternalBasic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("dev")
class AltinnVarselMockConfig  {

    @Bean
    fun iNotificationAgencyExternalBasic(): INotificationAgencyExternalBasic {
        return AltinnVarselMock()
    }
}
