package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SendSmsService
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsRepository
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsStatus
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.Status
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption.ENABLE_COOKIES
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("lokal")
class OpprettSmsTest {

    @LocalServerPort
    private var port = 0
    val baseUrl by lazy { "http://localhost:$port/rekrutteringsbistand-sms" }
    val restTemplate = TestRestTemplate(ENABLE_COOKIES)

    @Autowired
    lateinit var repository: SmsRepository

    @Autowired
    lateinit var sendSmsService: SendSmsService

    @BeforeEach
    fun login() {
        restTemplate.getForObject("$baseUrl/local/login", String::class.java)
    }

    @Test
    fun `POST til sms skal lagre i database og sende SMSer`() {
        val respons = restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOppretting, null), String::class.java)
        assertThat(respons.statusCode).isEqualTo(HttpStatus.CREATED)
        Thread.sleep(500)

        val smser = repository.hentSmser(enSmsTilOppretting.kandidatlisteId)
        smser.forEachIndexed { index, sms ->
            assertThat(sms.opprettet).isEqualToIgnoringSeconds(LocalDateTime.now())
            assertThat(sms.sendt).isEqualToIgnoringSeconds(LocalDateTime.now())
            assertThat(sms.melding).isEqualTo(enSmsTilOppretting.melding)
            assertThat(sms.fnr).isEqualTo(enSmsTilOppretting.fnr[index])
            assertThat(sms.kandidatlisteId).isEqualTo(enSmsTilOppretting.kandidatlisteId)
            assertThat(sms.navident).isEqualTo(enNavIdent)
            assertThat(sms.status).isEqualTo(Status.SENDT)
        }
    }

    @Test
    fun `Hvis en sending til Altinn feiler så skal man ikke prøve igjen innen 15 min etter sist feilet tidspunkt`() {
        val nå = LocalDateTime.now()
        repository.lagreSms(enSmsTilOppretting, enNavIdent)
        repository.hentSmser(enSmsTilOppretting.kandidatlisteId).forEach {
            repository.settFeil(it.id, Status.FEIL, 10, nå)
        }
        sendSmsService.sendSmserAsync()
        Thread.sleep(500)

        val smser = repository.hentSmser(enSmsTilOppretting.kandidatlisteId)
        smser.forEach {
            assertThat(it.status).isEqualTo(Status.FEIL)
            assertThat(it.gjenværendeForsøk).isEqualTo(10)
            assertThat(it.sistFeilet).isEqualTo(nå)
        }
    }

    @Test
    fun `POST til sms skal returnere 409 conflict hvis SMS med samme fnr og kandidatlisteId allerede er lagret`() {
        repository.lagreSms(enSmsTilOppretting, enNavIdent)
        val respons = restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOppretting, null), String::class.java)
        assertThat(respons.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun `POST til sms skal returnere 400 bad request hvis ugyldig fnr`() {
        val respons = restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOpprettingMedUgyldigFnr, null), String::class.java)
        assertThat(respons.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `POST til sms skal returnere 400 bad request hvis meldingen er for lang`() {
        val respons = restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOpprettingMedForLangMelding, null), String::class.java)
        assertThat(respons.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `POST til sms skal returnere 400 bad request hvis listen med fnr er tom`() {
        val respons = restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsUtenFnr, null), String::class.java)
        assertThat(respons.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @AfterEach
    fun tearDown() {
        enSmsTilOppretting.fnr.forEach {
            repository.slettSms(it)
        }
    }
}
