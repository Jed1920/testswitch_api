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
class UploadObject {
    @Throws(IOException::class)
    fun uploadFile() {
        val clientRegion: Regions = Regions.EU_WEST_2
        val bucketName = "jedjohnsonwebapp"
        val stringObjKeyName = "StringTrial01"
        val fileObjKeyName = "FileTrial02"
        val fileName = "C:/Users/jedjo/OneDrive/Documents/CV/Johnson_Jed_CV.pdf"
        try { //This code expects that you have AWS credentials set up per:
// https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html
            val s3Client: AmazonS3 = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build()
            // Upload a text string as a new object.
            s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object")
            // Upload a file as a new object with ContentType and title specified.
            val request = PutObjectRequest(bucketName, fileObjKeyName, File(fileName))
            val metadata = ObjectMetadata()
            metadata.setContentType("application/pdf")
            metadata.addUserMetadata("x-amz-meta-title", "Test 01")
            request.setMetadata(metadata)
            s3Client.putObject(request)
        } catch (e: AmazonServiceException) { // The call was transmitted successfully, but Amazon S3 couldn't process
// it, so it returned an error response.
            e.printStackTrace()
        } catch (e: SdkClientException) { // Amazon S3 couldn't be contacted for a response, or the client
// couldn't parse the response from Amazon S3.
            e.printStackTrace()
        }
    }
}