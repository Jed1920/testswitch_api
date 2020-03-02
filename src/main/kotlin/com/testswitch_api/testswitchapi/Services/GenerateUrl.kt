package com.testswitch_api.testswitchapi.Services

import com.amazonaws.AmazonServiceException
import com.amazonaws.HttpMethod
import com.amazonaws.SdkClientException
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*

@Service
class GenerateURL {
    @Throws(IOException::class)
    fun generateUrl() : String{
        val clientRegion = Regions.EU_WEST_2
        val bucketName = "jedjohnsonwebapp"
        val objectKey = "FileTrial02"
        try {
            val s3Client : AmazonS3 = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
//                    .withCredentials(ProfileCredentialsProvider())
                    .build()
            // Set the presigned URL to expire after one hour.
            val expiration = Date()
            var expTimeMillis = expiration.time
            expTimeMillis += 1000 * 60 * 60.toLong()
            expiration.time = expTimeMillis
            // Generate the presigned URL.
            println("Generating pre-signed URL.")
            val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration)
            println("Generated pre-signed URL.")
            val url = s3Client.generatePresignedUrl(generatePresignedUrlRequest)
            println("Pre-Signed URL: $url")
            return url.toString()
        } catch (e: AmazonServiceException) { // The call was transmitted successfully, but Amazon S3 couldn't process
// it, so it returned an error response.
            e.printStackTrace()
            return ""
        } catch (e: SdkClientException) { // Amazon S3 couldn't be contacted for a response, or the client
// couldn't parse the response from Amazon S3.
            e.printStackTrace()
            return ""
        }
    }
}
