package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun swagger(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(withClassAnnotation(RestController::class.java))
                .apis(RequestHandlerSelectors.basePackage("no.nav.rekrutteringsbistand.sms"))
                .paths(PathSelectors.any())
                .build()
    }
}

