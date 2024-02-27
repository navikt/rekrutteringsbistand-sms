package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SendSmsService
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsRepository
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.Status
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SendSmsServiceTest {

    @LocalServerPort
    private var port = 0
    val baseUrl by lazy { "http://localhost:$port/rekrutteringsbistand-sms-pg15" }
    val restTemplate = TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES)

    @Autowired
    lateinit var repository: SmsRepository

    @Autowired
    lateinit var sendSmsService: SendSmsService

    @BeforeEach
    fun login() {
        restTemplate.getForObject("$baseUrl/veileder-token-cookie", String::class.java)
    }

    @Test
    fun `sendSms skal sende SMS-er og oppdatere status i database`() {
        repository.lagreSms(enSmsTilOppretting, enNavIdent)
        sendSmsService.sendSmser()

        val smser = repository.hentSmser(enSmsTilOppretting.kandidatlisteId)
        smser.forEachIndexed { index, sms ->
            assertThat(sms.opprettet).isEqualToIgnoringSeconds(now())
            assertThat(sms.melding).isEqualTo(enSmsTilOppretting.melding)
            assertThat(sms.fnr).isEqualTo(enSmsTilOppretting.fnr[index])
            assertThat(sms.kandidatlisteId).isEqualTo(enSmsTilOppretting.kandidatlisteId)
            assertThat(sms.navident).isEqualTo(enNavIdent)
            assertThat(sms.status).isEqualTo(Status.SENDT)
            assertThat(sms.sendt).isEqualToIgnoringSeconds(now())
        }
    }

    @Test
    fun `Hvis en sending til Altinn feiler så skal man ikke prøve igjen innen 15 min etter sist feilet tidspunkt`() {
        val nå = now()
        repository.lagreSms(enSmsTilOppretting, enNavIdent)
        repository.hentSmser(enSmsTilOppretting.kandidatlisteId).forEach {
            repository.settFeil(it.id, Status.FEIL, 10, nå)
        }
        sendSmsService.sendSmser()

        val smser = repository.hentSmser(enSmsTilOppretting.kandidatlisteId)
        smser.forEach {
            assertThat(it.status).isEqualTo(Status.FEIL)
            assertThat(it.gjenværendeForsøk).isEqualTo(10)
            assertThat(it.sistFeilet).isEqualTo(nå)
        }
    }

    @AfterEach
    fun tearDown() {
        enSmsTilOppretting.fnr.forEach {
            repository.slettSms(it)
        }
    }
}
