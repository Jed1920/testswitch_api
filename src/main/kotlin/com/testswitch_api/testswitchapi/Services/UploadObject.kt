package com.testswitch_api.testswitchapi.Services

import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class UploadObject constructor (var s3Client : AmazonS3) {
    @Throws(IOException::class)
    fun uploadFile() {
        val bucketName = System.getenv("AWS_BUCKET_NAME")
        val fileObjKeyName = "FileTrial02"
        val fileName = "C:/Users/jedjo/OneDrive/Documents/CV/Johnson_Jed_CV.pdf"
        try {
            val request = PutObjectRequest(bucketName, fileObjKeyName, File(fileName))
            val metadata = ObjectMetadata()
            metadata.setContentType("application/pdf")
            metadata.addUserMetadata("x-amz-meta-title", "TestSwitch Web App CVs")
            request.setMetadata(metadata)
            s3Client.putObject(request)
        } catch (e: AmazonServiceException) {
            e.printStackTrace()
        } catch (e: SdkClientException) {
            e.printStackTrace()
        }
    }
}