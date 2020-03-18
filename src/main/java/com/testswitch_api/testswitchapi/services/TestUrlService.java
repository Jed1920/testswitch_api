package com.testswitch_api.testswitchapi.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.testswitch_api.testswitchapi.models.DatabaseUrl;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.util.*;

@Service
public class TestUrlService {

    private AmazonS3 s3Client;
    private Jdbi jdbi;

    @Autowired
    public TestUrlService(AmazonS3 s3Client, Jdbi jdbi) {
        this.s3Client = s3Client;
        this.jdbi = jdbi;
    }

    public void addUrlObject(String objectKey, String urlString, Long expiration) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO cv_urls(object_key, url_string,expiration)" +
                        "VALUES(:objectKey,:urlString,:expiration)")
                        .bind("objectKey", objectKey)
                        .bind("urlString", urlString)
                        .bind("expiration", expiration)
                        .execute());
    }

    public String getUrlStringByObjectKey(String objectKey) {
        try {
            DatabaseUrl urlObject = jdbi.withHandle(handle ->
                    handle.createQuery("SELECT * FROM cv_urls WHERE object_key = :objectKey")
                            .bind("objectKey", objectKey)
                            .mapToBean(DatabaseUrl.class)
                            .one());
            if (urlObject.getExpiration() > new Date().getTime()) {
                return urlObject.getUrlString();
            }
            jdbi.useHandle(handle ->
                    handle.createUpdate("DELETE FROM cv_urls WHERE object_key = :objectKey")
                            .bind("objectKey", objectKey)
                            .execute());
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public String generateUrl(String objectKey) {
        String bucketName = System.getenv("AWS_BUCKET_NAME");
        try {
            Date expiration = new Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            addUrlObject(objectKey, url.toString(), expiration.getTime());
            return url.toString();
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            return "Amazon";
        } catch (SdkClientException e) {
            e.printStackTrace();
            return "SDK";
        }
    }
}

