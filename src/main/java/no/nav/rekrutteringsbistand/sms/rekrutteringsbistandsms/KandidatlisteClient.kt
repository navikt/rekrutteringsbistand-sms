package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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

@Service
class KandidatlisteClient(
    @Value("\${azure.openid.config.token.endpoint}") private val tokenEndpoint: String,
    @Value("\${azure.app.client.id}") private val clientId: String,
    @Value("\${azure.app.client.secret}") private val clientSecret: String,
    @Value("\${kandidat.api.scope}") private val kandidatapiScope: String,
    restTemplateBuilder: RestTemplateBuilder,
) {
    init {
        log.info("KandidatlisteClient: tokenEndpoint=$tokenEndpoint, clientId=$clientId, kandidatapiScope=$kandidatapiScope")
    }

    private val restTemplate = restTemplateBuilder
        .additionalMessageConverters(
            MappingJackson2HttpMessageConverter().apply {
                supportedMediaTypes = listOf(APPLICATION_JSON, APPLICATION_OCTET_STREAM)
            }
        )
        .build()

    private data class KandidatlisteIdDto(val kandidatlisteId: String)

    fun hentKandidatlisteId(stillingId: String): String {
        return get<KandidatlisteIdDto>(
            "http://rekrutteringsbistand-kandidat-api.toi.svc.nais.local/rekrutteringsbistand-kandidat-api/rest/maskin/stilling/{stillingId}/kandidatlisteid",
            mapOf("stillingId" to stillingId)
        ).kandidatlisteId
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class KandidatlisteDto(val stillingId: String)

    fun hentStillingId(kandidatlisteId: String): String =
        get<KandidatlisteDto>(
            "http://rekrutteringsbistand-kandidat-api.toi.svc.nais.local/rekrutteringsbistand-kandidat-api/rest/maskin/kandidatliste/{kandidatlisteId}/stillingid",
            mapOf("kandidatlisteId" to kandidatlisteId)
        ).stillingId

    private inline fun <reified T> get(urlTemplate: String, templateValues: Map<String, String>): T {
        val httpEntity = HttpEntity<String>(HttpHeaders().apply {
            setBearerAuth(authToken())
            accept = listOf(APPLICATION_JSON)
        })

        val response = restTemplate.exchange(
            urlTemplate,
            HttpMethod.GET,
            httpEntity,
            T::class.java,
            templateValues,
        )

        return response.body!!
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
                    "scope" to kandidatapiScope,
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