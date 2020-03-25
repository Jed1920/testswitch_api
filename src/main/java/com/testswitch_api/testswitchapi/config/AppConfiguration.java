package com.testswitch_api.testswitchapi.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.testswitch_api.testswitchapi.models.LoginCredentials;
import io.github.cdimascio.dotenv.Dotenv;
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

    private Dotenv dotenv = Dotenv.load();

    @Bean
    @Profile("productionDataSource")
    public DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url(dotenv.get("DATABASE_URL"))
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
        mailSender.setPassword(dotenv.get("GOOGLE_APP_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean("uiUrlString")
    @Profile("productionUIUrl")
    public String getUIUrl() {
        return dotenv.get("UI_URL");
    }

    @Bean("s3BucketName")
    @Profile("productionBucketName")
    public String getS3BucketName() {
        return dotenv.get("AWS_BUCKET_NAME");
    }

    @Bean
    @Profile("productionLoginCredentials")
    public LoginCredentials serverCredentials(){
        LoginCredentials loginCredentials = new LoginCredentials();
        loginCredentials.setUsername(dotenv.get("SERVER_USERNAME"));
        loginCredentials.setPassword(dotenv.get("SERVER_PASSWORD"));
        return loginCredentials;
    }
}
