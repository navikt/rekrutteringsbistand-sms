package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {

    private static Logger log = LoggerFactory.getLogger(SmsController.class);

    public SmsController() {
    }

    @GetMapping("/sms")
    public String test() {
        log.info("Lagrer i database");
        // TODO: Lagre i database
        return "test";
    }
}
