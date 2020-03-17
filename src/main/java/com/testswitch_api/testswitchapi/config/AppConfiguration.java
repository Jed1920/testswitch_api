package com.testswitch_api.testswitchapi.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.jdbi.v3.core.Jdbi;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.*;
import javax.sql.DataSource;

@Configuration
public class AppConfiguration {

    @Bean
    @Profile("productionDataSource")
    public DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(System.getenv("DATABASE_URL"))
                .username("postgres")
                .password("techswitch")
                .build();
    }

    @Bean
    public Jdbi getJdbi(DataSource dataSource) {
        return Jdbi.create(dataSource);
    }

    @Bean
    @Profile("productionAmazonS3")
    public AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_WEST_2)
                .build();
    }

    @Bean
    public JavaMailSender getJavaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("webapp1920@gmail.com");
        mailSender.setPassword(System.getenv("GOOGLE_APP_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    @Profile("productionUIUrl")
    public String getUIUrl() {
        return System.getenv("UI_URL");
    }
}
