package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms;

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJwtTokenValidation(ignore = "org.springframework")
public class RekrutteringsbistandSmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RekrutteringsbistandSmsApplication.class, args);
	}

}
