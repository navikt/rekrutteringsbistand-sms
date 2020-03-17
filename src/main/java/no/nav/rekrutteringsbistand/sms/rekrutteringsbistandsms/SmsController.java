package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {

    public SmsController() {
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
