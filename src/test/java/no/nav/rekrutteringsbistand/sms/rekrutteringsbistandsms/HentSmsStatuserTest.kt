package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SendSmsService
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsRepository
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsStatus
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.Status
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
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
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.temporal.ChronoUnit.SECONDS

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HentSmsStatuserTest {

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
    fun `GET mot sms skal returnere SMS-statuser`() {
        restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOppretting, null), String::class.java)
        sendSmsService.sendSmser()

        val respons: ResponseEntity<List<SmsStatus>> = restTemplate.exchange(
            "$baseUrl/sms/${enSmsTilOppretting.kandidatlisteId}",
            HttpMethod.GET,
            HttpEntity(null, null),
            object : ParameterizedTypeReference<List<SmsStatus>>() {}
        )
        assertThat(respons.statusCode).isEqualTo(HttpStatus.OK)
        respons.body!!.forEachIndexed { index, smsStatus ->
            assertThat(smsStatus.id).isGreaterThan(0)
            assertThat(smsStatus.fnr).isEqualTo(enSmsTilOppretting.fnr[index])
            assertThat(smsStatus.opprettet).isCloseTo(now(), within(2, SECONDS))
            assertThat(smsStatus.sendt).isCloseTo(now(), within(2, SECONDS))
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


    @Test
    fun `Kall for å hente smser for en person skal returnere SMS-statuser`() {

        val fnr = enAnnenSmsTilOpprettingSammeFnr.fnr[0]
        restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enSmsTilOppretting, null), String::class.java)
        sendSmsService.sendSmser()
        restTemplate.postForEntity("$baseUrl/sms", HttpEntity(enAnnenSmsTilOpprettingSammeFnr, null), String::class.java)
        sendSmsService.sendSmser()

        val respons: ResponseEntity<List<SmsStatus>> = restTemplate.exchange(
            "$baseUrl/sms/fnr/${fnr}",
            HttpMethod.GET,
            HttpEntity(null, null),
            object : ParameterizedTypeReference<List<SmsStatus>>() {}
        )
        assertThat(respons.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(respons.body).isNotEmpty.hasSize(2)
        val body = respons.body!!

        assertThat(body[0].fnr).isEqualTo(fnr)
        assertThat(body[0].id).isGreaterThan(0)
        assertThat(body[0].opprettet).isCloseTo(now(), within(2, SECONDS))
        assertThat(body[0].sendt).isCloseTo(now(), within(2, SECONDS))
        assertThat(body[0].status).isEqualTo(Status.SENDT)
        assertThat(body[0].navIdent).isEqualTo("X123456")
        assertThat(body[0].kandidatlisteId).isEqualTo("123456")

        assertThat(body[1].fnr).isEqualTo(fnr)
        assertThat(body[1].id).isGreaterThan(0).isNotEqualTo(body[0].id)
        assertThat(body[1].opprettet).isCloseTo(now(), within(2, SECONDS))
        assertThat(body[1].sendt).isCloseTo(now(), within(2, SECONDS))
        assertThat(body[1].status).isEqualTo(Status.SENDT)
        assertThat(body[1].navIdent).isEqualTo("X123456")
        assertThat(body[1].kandidatlisteId).isEqualTo("555555")
    }

    @Test
    fun `Kall for å hente smser for en person skal returnere 200 ok med tom liste hvis ingen SMSer finnes for person`() {
        val respons: ResponseEntity<List<SmsStatus>> = restTemplate.exchange(
            "$baseUrl/sms/fnr${enSmsTilOppretting.fnr[0]}",
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
