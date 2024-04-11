package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.OpprettSms
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.SmsRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RepositoryTest {


    @Autowired
    lateinit var repository: SmsRepository

    @Test
    fun test() {
        repository.lagreSms(OpprettSms(
            melding = "Vi trenger deg!",
            fnr = listOf("1234", "1234", "1234"),
            kandidatlisteId = "k",
        ), "1234567")

        repository.hentDirtySmser(3).also {
            repository.markClean(it)
        }
        repository.hentDirtySmser(3).also {
            assertTrue(it.isEmpty())
        }
    }
}