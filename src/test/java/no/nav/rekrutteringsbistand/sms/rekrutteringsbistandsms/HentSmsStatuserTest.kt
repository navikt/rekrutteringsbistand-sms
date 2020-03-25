package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

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
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("lokal")
class HentSmsStatuserTest {

    @LocalServerPort
    private var port = 0
    val baseUrl by lazy { "http://localhost:$port/rekrutteringsbistand-sms" }
    val restTemplate = TestRestTemplate(TestRestTemplate.HttpClientOption.ENABLE_COOKIES)

    @Autowired
    lateinit var repository: SmsRepository

    @BeforeEach
    fun login() {
        restTemplate.getForObject("$baseUrl/local/login", String::class.java)
    }

    @Test
    fun `GET mot sms skal returnere SMS-statuser`() {
        restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOppretting, null), String::class.java)
        Thread.sleep(500)

        val respons: ResponseEntity<List<SmsStatus>> = restTemplate.exchange(
                "$baseUrl/sms/${enSmsTilOppretting.kandidatlisteId}",
                HttpMethod.GET,
                HttpEntity(null, null),
                object : ParameterizedTypeReference<List<SmsStatus>>() {}
        )
        assertThat(respons.statusCode).isEqualTo(HttpStatus.OK)
        respons.body!!.forEachIndexed { index, smsStatus ->
            assertThat(smsStatus.fnr).isEqualTo(enSmsTilOppretting.fnr[index])
            assertThat(smsStatus.opprettet).isEqualToIgnoringSeconds(LocalDateTime.now())
            assertThat(smsStatus.sendt).isEqualToIgnoringSeconds(LocalDateTime.now())
            assertThat(smsStatus.status).isEqualTo(Status.SENDT)
        }
    }

    @Test
    fun `GET mot sms skal returnere 200 ok med tom liste hvis ingen SMSer finnes på kandidatlista`() {
        val respons: ResponseEntity<List<SmsStatus>> = restTemplate.exchange(
                "$baseUrl/sms/${enSmsTilOppretting.kandidatlisteId}",
                HttpMethod.GET,
                HttpEntity(null, null),
                object : ParameterizedTypeReference<List<SmsStatus>>() {}
        )
        assertThat(respons.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(respons.body!!).isEmpty()
    }

    @AfterEach
    fun tearDown() {
        enSmsTilOppretting.fnr.forEach {
            repository.slettSms(it)
        }
    }
}