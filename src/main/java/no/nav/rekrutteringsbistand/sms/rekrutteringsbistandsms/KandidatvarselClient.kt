package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.Sms
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType.*
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMapAdapter
import org.springframework.web.client.postForEntity
import java.time.LocalDateTime

@Service
class KandidatvarselClient(
    @Value("\${azure.openid.config.token.endpoint}") private val tokenEndpoint: String,
    @Value("\${azure.app.client.id}") private val clientId: String,
    @Value("\${azure.app.client.secret}") private val clientSecret: String,
    restTemplateBuilder: RestTemplateBuilder,
) {
    init {
        log.info("KandidatvarselClient: tokenEndpoint=$tokenEndpoint, clientId=$clientId")
    }

    private val backfillUrl =
        if (System.getenv("NAIS_CLUSTER_NAME") == "prod-fss")
            "https://rekrutteringsbistand-kandidatvarsel-api.intern.nav.no/api/backfill"
        else
            "https://rekrutteringsbistand-kandidatvarsel-api.intern.dev.nav.no/api/backfill"

    private val restTemplate = restTemplateBuilder
        .additionalMessageConverters(
            MappingJackson2HttpMessageConverter().apply {
                supportedMediaTypes = listOf(APPLICATION_JSON, APPLICATION_OCTET_STREAM)
            }
        )
        .build()

    data class BackfillRequest(
        val frontendId: String,
        val opprettet: LocalDateTime,
        val stillingId: String,
        val melding: String,
        val fnr: String,
        val status: String,
        val navIdent: String,
        val sendt: LocalDateTime?
    )

    fun backfill(smser: List<Sms>): Boolean {
        val headers = HttpHeaders().apply {
            setBearerAuth(authToken())
            accept = listOf(APPLICATION_JSON)
        }
        val backfillSmser = smser.map {
            BackfillRequest(
                frontendId = it.id.toString(),
                opprettet = it.opprettet,
                stillingId = it.stillingId!!,
                melding = it.melding,
                fnr = it.fnr,
                status = it.status.toString(),
                navIdent = it.navident,
                sendt = it.sendt
            )
        }
        val request = HttpEntity<List<BackfillRequest>>(backfillSmser, headers)
        return try {
            val response = restTemplate.postForEntity<String>(
                backfillUrl,
                request,
            )
            return response.statusCode.is2xxSuccessful
        } catch (e: Exception) {
            log.info("kallet '{}' feilet med exception {}", backfillUrl, e::class.qualifiedName, e)
            false
        }
    }

    private fun authToken(): String {
        val response = restTemplate.exchange(
            tokenEndpoint,
            HttpMethod.POST,
            HttpEntity(
                MultiValueMapAdapter(mapOf(
                    "client_id" to clientId,
                    "client_secret" to clientSecret,
                    "grant_type" to "client_credentials",
                    "scope" to
                            if (System.getenv("NAIS_CLUSTER_NAME") == "prod-fss")
                                "api://prod-gcp.toi.rekrutteringsbistand-kandidatvarsel-api/.default"
                            else
                                "api://dev-gcp.toi.rekrutteringsbistand-kandidatvarsel-api/.default",
                ).mapValues { (_, v) -> listOf(v) }),
                HttpHeaders().apply {
                    contentType = APPLICATION_FORM_URLENCODED
                }
            ),
            TokenResponse::class.java
        )
        return response.body!!.access_token
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class TokenResponse(
        @Suppress("PropertyName") val access_token: String,
    )
}