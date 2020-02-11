package com.testswitch_api.testswitchapi.config

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
class AppConfiguration {

    @Bean
    @Profile("productionDataSource")
    fun getDataSource(): DataSource {
        val dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.driverClassName("org.postgresql.Driver")
        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/testswitch")
        dataSourceBuilder.username("postgres")
        dataSourceBuilder.password("techswitch")
        return dataSourceBuilder.build()
    }

    @Bean
    fun getJdbi(): Jdbi{
        val jdbi = Jdbi.create(getDataSource()).installPlugin(KotlinPlugin())
        return jdbi
    }
}