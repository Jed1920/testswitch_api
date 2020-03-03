package com.testswitch_api.testswitchapi

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import java.net.URL

import javax.sql.DataSource

@TestConfiguration
class AppTestConfiguration {

    @Bean
    @Profile("testDataSource")
    fun getDataSource(): DataSource {
        val postgres = EmbeddedPostgres.builder().start()
        return postgres.postgresDatabase
    }

    @Bean
    @Profile("testAmazonS3")
    fun getS3Client() : AmazonS3 {
        val s3Client = Mockito.mock(AmazonS3::class.java)
        Mockito.`when`(s3Client.generatePresignedUrl(ArgumentMatchers.any())).thenReturn(URL("http://url_string"))
        return s3Client
    }
}