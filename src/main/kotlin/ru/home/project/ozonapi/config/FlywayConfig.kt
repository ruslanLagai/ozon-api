package ru.home.project.ozonapi.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import ru.home.project.ozonapi.event.DbMigratedEvent
import javax.sql.DataSource

/**
 * @author rlagay
 */
@Configuration
class FlywayConfig(
    val context: ApplicationContext,
    val dataSource: DataSource,
    val publisher: ApplicationEventPublisher
) {

    @Value("\${spring.flyway.schemas}")
    private val schema: String? = null

    @Value("\${spring.flyway.locations}")
    private val locations: String? = null

    @EventListener
    fun migrateDB(event: ContextRefreshedEvent) {
        if (event.applicationContext == context) {
            Flyway.configure().dataSource(dataSource)
                .baselineOnMigrate(true)
                .schemas(schema)
                .locations(locations)
                .load().migrate()
            publisher.publishEvent(DbMigratedEvent(true))
        }
    }
}