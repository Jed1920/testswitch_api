package com.testswitch_api.testswitchapi.config

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*
import javax.sql.DataSource


@Configuration
class AppConfiguration {

    @Bean
    @Profile("productionDataSource")
    fun getDataSource(): DataSource {
        val dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.driverClassName("org.postgresql.Driver")
        dataSourceBuilder.url(System.getenv("DATABASE_URL"))
        dataSourceBuilder.username("postgres")
        dataSourceBuilder.password("techswitch")
        return dataSourceBuilder.build()
    }

    @Bean
    fun getJdbi(): Jdbi{
        val jdbi = Jdbi.create(getDataSource()).installPlugin(KotlinPlugin())
        return jdbi
    }

    @Bean
    fun getS3Client() : AmazonS3 {
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_WEST_2)
                .build()
    }


    @Bean
    fun getJavaMailSender(): JavaMailSender? {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "smtp.gmail.com"
        mailSender.port = 587
        mailSender.username = "webapp1920@gmail.com"
        mailSender.password = System.getenv("GOOGLE_APP_PASSWORD")
        val props: Properties = mailSender.javaMailProperties
        props.put("mail.transport.protocol", "smtp")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.debug", "true")
        return mailSender
    }
}