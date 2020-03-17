package com.testswitch_api.testswitchapi;

import com.amazonaws.services.s3.AmazonS3;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@TestConfiguration
public class AppTestConfiguration {

    @Bean
    @Profile("testDataSource")
    public DataSource getDataSource() throws IOException {
        EmbeddedPostgres pg = EmbeddedPostgres.builder()
                .start();
        return pg.getPostgresDatabase();
    }

//    @Bean
//    public Jdbi getJdbi() throws IOException {
//        return Jdbi.create(getDataSource());
//    }

    @Bean
    @Profile("testAmazonS3")
    public AmazonS3 getS3Client() throws MalformedURLException {
        AmazonS3 s3Client = Mockito.mock(AmazonS3.class);
        Mockito.when(s3Client.generatePresignedUrl(ArgumentMatchers.any())).thenReturn(new URL("http://url_string"));
        return s3Client;
    }

//    @Bean
//    public JavaMailSender getJavaMailSender() {
//
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//
//        mailSender.setUsername("webapp1920@gmail.com");
//        mailSender.setPassword(System.getenv("GOOGLE_APP_PASSWORD"));
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "true");
//
//        return mailSender;
//    }

    @Bean
    @Profile("testUIUrl")
    public String getUIUrl() {
        return "test_UI_Url";
    }
}
