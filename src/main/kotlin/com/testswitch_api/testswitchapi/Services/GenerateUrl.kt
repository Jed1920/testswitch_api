package com.testswitch_api.testswitchapi.Services

import com.amazonaws.AmazonServiceException
import com.amazonaws.HttpMethod
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.testswitch_api.testswitchapi.Models.UrlDatabaseObject
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*

@Service
class GenerateURL constructor (var s3Client : AmazonS3, val jdbi: Jdbi){
    @Throws(IOException::class)

    fun addUrlObject(objectKey: String,urlString :String,expiration :Long) {
        return jdbi.useHandle<RuntimeException> { handle ->
            (handle.createUpdate("INSERT INTO cv_urls(object_key, url_string,expiration)" +
                    "VALUES(:objectKey,:urlString,:expiration)")
                    .bind("objectKey", objectKey)
                    .bind("urlString", urlString)
                    .bind("expiration", expiration)
                    .execute())
        }
    }

    fun getUrlStringByObjectKey(objectKey: String): String? {
        try {
            val urlObject: UrlDatabaseObject = jdbi.withHandle<UrlDatabaseObject, RuntimeException> { handle ->
                (handle.createQuery("SELECT * FROM cv_urls WHERE object_key = :objectKey")
                        .bind("objectKey", objectKey)
                        .mapTo<UrlDatabaseObject>()
                        .one())
            }

            if (urlObject.expiration > Date().time) {
                return urlObject.urlString
            }
            jdbi.useHandle<RuntimeException> { handle ->
                (handle.createUpdate("DELETE FROM cv_urls WHERE object_key = :objectKey")
                        .bind("objectKey", objectKey)
                        .execute())}
            return null
        } catch (e : Exception){
            return null
        }
    }

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
            print("generated new url")
            addUrlObject(objectKey,url.toString(),expiration.time)
            return url.toString()
        } catch (e: AmazonServiceException) {
            e.printStackTrace()
            return "Amazon"
        } catch (e: SdkClientException) {
            e.printStackTrace()
            return "SDK"
        }
    }
}
