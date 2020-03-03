package com.testswitch_api.testswitchapi.Services

import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import java.net.URL

internal class GenerateURLTest{
//    @Test
//    fun successfulGenerateUrlTest(){
//        val s3Client = Mockito.mock(AmazonS3::class.java)
//        Mockito.`when`(s3Client.generatePresignedUrl(any())).thenReturn(URL("http://url_string"))
//
//        var generateUrl = GenerateURL(s3Client)
//
//        val url = generateUrl.generateUrl("objectKey")
//
//        assertThat(url).isEqualTo("http://url_string")
//    }

    @Test
    fun amazonServiceExceptionGenerateUrlTest(){
        val s3Client = Mockito.mock(AmazonS3::class.java)
        Mockito.`when`(s3Client.generatePresignedUrl(any())).thenThrow(AmazonServiceException(""))

        var generateUrl = GenerateURL(s3Client)

        val urlString = generateUrl.generateUrl("objectKey")

        assertThat(urlString).isEqualTo("Error with Amazon Service")
    }

    @Test
    fun sdkClientExceptionGenerateUrlTest(){
        val s3Client = Mockito.mock(AmazonS3::class.java)
        Mockito.`when`(s3Client.generatePresignedUrl(any())).thenThrow(SdkClientException(""))

        var generateUrl = GenerateURL(s3Client)

        val urlString = generateUrl.generateUrl("objectKey")

        assertThat(urlString).isEqualTo("Error with Server")
    }
}