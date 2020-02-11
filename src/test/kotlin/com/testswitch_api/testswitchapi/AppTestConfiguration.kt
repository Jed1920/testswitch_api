package com.testswitch_api.testswitchapi

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

import javax.sql.DataSource

@TestConfiguration
class AppTestConfiguration {

    @Bean
    @Profile("testDataSource")
    fun getDataSource(): DataSource {
        val postgres = EmbeddedPostgres.builder().start()
        return postgres.postgresDatabase
    }

}