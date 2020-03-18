package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.database

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert

@Configuration
class JdbcConfig {

    @Bean
    fun simpleJdbcInsert(jdbcTemplate: JdbcTemplate): SimpleJdbcInsert {
        return SimpleJdbcInsert(jdbcTemplate)
    }

}
