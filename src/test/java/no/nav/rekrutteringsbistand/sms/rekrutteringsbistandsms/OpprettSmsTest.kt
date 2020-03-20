package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.Sms
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsRepository
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.Status
import org.assertj.core.api.Assertions.assertThat
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

    @BeforeEach
    fun login() {
        restTemplate.getForObject("$baseUrl/local/login", String::class.java)
    }

    @Test
    fun `POST til sms skal lagre i database og sende SMSer`() {
        val respons = restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOppretting, null), String::class.java)
        assertThat(respons.statusCode).isEqualTo(HttpStatus.CREATED)
        Thread.sleep(500)

        enSmsTilOppretting.fnr.forEachIndexed { index, fnr ->
            val sms: Sms = repository.hentSms(index + 1)!!
            assertThat(sms.opprettet).isEqualToIgnoringSeconds(LocalDateTime.now())
            assertThat(sms.sendt).isEqualToIgnoringSeconds(LocalDateTime.now())
            assertThat(sms.melding).isEqualTo(enSmsTilOppretting.melding)
            assertThat(sms.fnr).isEqualTo(fnr)
            assertThat(sms.kandidatlisteId).isEqualTo(enSmsTilOppretting.kandidatlisteId)
            assertThat(sms.navident).isEqualTo("X123456")
            assertThat(sms.status).isEqualTo(Status.SENDT)
        }
    }

    @Test
    fun `POST til sms skal returne 409 conflict hvis SMS med samme fnr og kandidatlisteId allerede er lagret`() {
        repository.lagreSms(enSmsTilOppretting, "X123456")
        val respons = restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOppretting, null), String::class.java)
        assertThat(respons.statusCode).isEqualTo(HttpStatus.CONFLICT)
    }
}
