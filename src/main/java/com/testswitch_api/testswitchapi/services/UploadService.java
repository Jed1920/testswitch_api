package com.testswitch_api.testswitchapi.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class UploadService {

    private AmazonS3 s3Client;
    private String bucketName;

    @Autowired
    public UploadService(AmazonS3 s3Client, @Qualifier("s3BucketName") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public PutObjectResult uploadFile(String name, int id, MultipartFile cvMultiFile) throws IOException {
        File cvFile = convertMultipartFileToFile(cvMultiFile);
        String fileObjKeyName = String.format("%s_%s", id, name.replace(" ", "_"));
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, cvFile);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("application/pdf");
            metadata.addUserMetadata("x-amz-meta-title", "TestSwitch Web App CVs");
            request.setMetadata(metadata);
            return s3Client.putObject(request);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        } finally {
            cvFile.delete();
        }
        return null;
    }

    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
