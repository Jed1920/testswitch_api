package com.testswitch_api.testswitchapi.Services

import com.amazonaws.AmazonServiceException
import com.amazonaws.HttpMethod
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*

@Service
class GenerateURL constructor (var s3Client : AmazonS3){
    @Throws(IOException::class)
    fun generateUrl(objectKey : String) : String{
        val bucketName = System.getenv("AWS_BUCKET_NAME")
        try {
            val expiration = Date()
            var expTimeMillis = expiration.time
            expTimeMillis += 1000 * 60 * 60.toLong()
            expiration.time = expTimeMillis
            val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration)
            val url = s3Client.generatePresignedUrl(generatePresignedUrlRequest)
            return url.toString()
        } catch (e: AmazonServiceException) {
            e.printStackTrace()
            return ""
        } catch (e: SdkClientException) {
            e.printStackTrace()
            return ""
        }
    }
}
