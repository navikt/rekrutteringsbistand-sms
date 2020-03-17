package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms;

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel.AltinnVarselAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsController {

    private static Logger log = LoggerFactory.getLogger(SmsController.class);
    private final AltinnVarselAdapter altinnVarselAdapter;

    public SmsController(AltinnVarselAdapter altinnVarselAdapter) {
        this.altinnVarselAdapter = altinnVarselAdapter;
    }

    @GetMapping("/sms")
    public String test() {
        log.info("Sender sms til via Altinn");

        // altinnVarselAdapter.sendVarsel("", "");
        return "test";
    }
}
