package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.nav.vault.jdbc.hikaricp.HikariCPVaultUtil
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("dev", "prod")
class DatabaseConfig {

    companion object {
        private const val DATABASE_NAVN = "rekrutteringsbistand-sms-pg15"
    }

    @Value("\${rekrutteringsbistand.database.url}")
    private val databaseUrl: String? = null

    @Value("\${rekrutteringsbistand.database.vault-sti}")
    private val mountPath: String? = null

    @Bean
    fun userDataSource(): DataSource {
        return dataSource("user")
    }

    private fun dataSource(user: String): HikariDataSource {
        val config = HikariConfig()
        config.jdbcUrl = databaseUrl
        config.maximumPoolSize = 2
        config.minimumIdle = 1
        return HikariCPVaultUtil.createHikariDataSourceWithVaultIntegration(config, mountPath, dbRole(user))
    }

    @Bean
    fun flywayMigrationStrategy(): FlywayMigrationStrategy {
        return FlywayMigrationStrategy {
            Flyway.configure()
                    .dataSource(dataSource("admin"))
                    .initSql(String.format("SET ROLE \"%s\"", dbRole("admin")))
                    .load()
                    .migrate()
        }
    }

    private fun dbRole(role: String): String {
        return java.lang.String.join("-", DATABASE_NAVN, role)
    }
}
