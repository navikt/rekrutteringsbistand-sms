package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

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
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption.ENABLE_COOKIES
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpprettSmsTest {

    init {
        RekrutteringsbistandSmsApplication("lokaltest")
    }

    @LocalServerPort
    private var port = 0
    val baseUrl by lazy { "http://localhost:$port/rekrutteringsbistand-sms" }
    val restTemplate = TestRestTemplate(ENABLE_COOKIES)

    @Autowired
    lateinit var repository: SmsRepository

    @BeforeEach
    fun login() {
        restTemplate.getForObject("$baseUrl/veileder-token-cookie", String::class.java)
    }

    @Test
    fun `POST til sms skal lagre i database`() {
        val respons =
            restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOppretting, null), String::class.java)
        assertThat(respons.statusCode).isEqualTo(HttpStatus.CREATED)

        val smser = repository.hentSmser(enSmsTilOppretting.kandidatlisteId)
        smser.forEachIndexed { index, sms ->
            assertThat(sms.opprettet).isEqualToIgnoringSeconds(now())
            assertThat(sms.melding).isEqualTo(enSmsTilOppretting.melding)
            assertThat(sms.fnr).isEqualTo(enSmsTilOppretting.fnr[index])
            assertThat(sms.kandidatlisteId).isEqualTo(enSmsTilOppretting.kandidatlisteId)
            assertThat(sms.navident).isEqualTo(enNavIdent)
            assertThat(sms.status).isEqualTo(Status.IKKE_SENDT)
            assertThat(sms.sendt).isNull()
        }

            enSmsTilOppretting.fnr.forEach { repository.slettSms(it) }
    }

    @Test
    fun `POST til sms skal returnere 409 conflict hvis SMS med samme fnr og kandidatlisteId allerede er lagret`() {
        repository.lagreSms(enSmsTilOppretting, enNavIdent)
        val respons =
            restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOppretting, null), String::class.java)
        assertThat(respons.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }

    @Test
    fun `POST til sms skal returnere 201 Created om det er et gyldig fødselsnummer`() {
        val respons = restTemplate.postForEntity(
            "$baseUrl/sms", HttpEntity(enSmsTilOppretting, null), String::class.java
        )
        assertThat(respons.statusCode).isEqualTo(HttpStatus.CREATED)
        enSmsTilOppretting.fnr.forEach { repository.slettSms(it) }
    }

    @Test
    fun `POST til sms skal returnere 201 Created om det er et syntetisk fødselsnummer med test env`() {
        RekrutteringsbistandSmsApplication("dev-gcp")
        val respons = restTemplate.postForEntity(
            "$baseUrl/sms", HttpEntity(enSmsTilOpprettingSyntetiskFnr, null), String::class.java
        )
        assertThat(respons.statusCode).isEqualTo(HttpStatus.CREATED)
        enSmsTilOpprettingSyntetiskFnr.fnr.forEach { repository.slettSms(it) }
    }

    @Test
    fun `POST til sms skal returnere 400 bad request  om det er et syntetisk fødselsnummer med prod env`() {
        RekrutteringsbistandSmsApplication("prod-fss")
        val respons = restTemplate.postForEntity(
            "$baseUrl/sms", HttpEntity(enSmsTilOpprettingSyntetiskFnr, null), String::class.java
        )
        assertThat(respons.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        enSmsTilOpprettingSyntetiskFnr.fnr.forEach { repository.slettSms(it) }
    }


    @Test
    fun `POST til sms skal returnere 400 bad request hvis ugyldig fnr`() {
        val respons = restTemplate.postForEntity(
            "$baseUrl/sms", HttpEntity(enSmsTilOpprettingMedUgyldigFnr, null), String::class.java
        )
        assertThat(respons.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `POST til sms skal returnere 400 bad request hvis meldingen er for lang`() {
        val respons = restTemplate.postForEntity(
            "$baseUrl/sms", HttpEntity(enSmsTilOpprettingMedForLangMelding, null), String::class.java
        )
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
