package com.testswitch_api.testswitchapi.services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

class UploadServiceTest {

    AmazonS3 s3Client = Mockito.mock(AmazonS3.class);

    @Test
    public void uploadFileSuccessful() throws IOException {
        UploadService uploadService = new UploadService(s3Client,"test s3 bucket name");
        MockMultipartFile multiFile = new MockMultipartFile("Test_CV","Test_CV","application/pdf","hello".getBytes());
        uploadService.uploadFile("James Ashton",1, multiFile);

        Mockito.verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void uploadFileUnsuccessfulAmazonException() throws IOException {
        Mockito.when(s3Client.putObject(any(PutObjectRequest.class))).thenThrow(new AmazonServiceException("Amazon Error"));
        UploadService uploadService = new UploadService(s3Client,"test s3 bucket name");
        MockMultipartFile multiFile = new MockMultipartFile("Test_CV","Test_CV","application/pdf","hello".getBytes());
        PutObjectResult result = uploadService.uploadFile("James Ashton",1, multiFile);

        assertThat(result).isNull();
        Mockito.verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void uploadFileUnsuccessfulSDKException() throws IOException {
        Mockito.when(s3Client.putObject(any(PutObjectRequest.class))).thenThrow(new SdkClientException("SDK Error"));
        UploadService uploadService = new UploadService(s3Client,"test s3 bucket name");
        MockMultipartFile multiFile = new MockMultipartFile("Test_CV","Test_CV","application/pdf","hello".getBytes());
        PutObjectResult result = uploadService.uploadFile("James Ashton",1, multiFile);

        assertThat(result).isNull();
        Mockito.verify(s3Client).putObject(any(PutObjectRequest.class));
    }

}