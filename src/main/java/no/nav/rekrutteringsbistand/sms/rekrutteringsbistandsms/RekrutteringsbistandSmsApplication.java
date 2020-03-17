package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class RekrutteringsbistandSmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RekrutteringsbistandSmsApplication.class, args);
	}

}
