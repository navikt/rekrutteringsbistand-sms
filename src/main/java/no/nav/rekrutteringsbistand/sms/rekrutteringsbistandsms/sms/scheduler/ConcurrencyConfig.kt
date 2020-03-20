package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.sms.scheduler

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class ConcurrencyConfig {

    @Bean
    fun sendSmsExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5
        executor.maxPoolSize = 5
        executor.setQueueCapacity(10000)
        executor.initialize()
        executor.setThreadNamePrefix("SendSms-Thread-")
        return executor
    }
}
