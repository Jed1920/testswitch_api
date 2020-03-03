package com.testswitch_api.testswitchapi.Services

import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import java.io.File

internal class UploadObjectTest{

    @Test
    fun successfulUploadObjectTest(){
        val s3Client = Mockito.mock(AmazonS3::class.java)

        var uploadObject = UploadObject(s3Client)
        val file = File("")
        val name = "name"
        val id = 1

        uploadObject.uploadFile(name,id,file)

        Mockito.verify(s3Client, Mockito.atLeastOnce()).putObject(any(PutObjectRequest::class.java))
    }

    @Test
    fun amazonServiceExceptionUploadObjectTest(){
        val s3Client = Mockito.mock(AmazonS3::class.java)
        Mockito.`when`(s3Client.putObject(any(PutObjectRequest::class.java))).thenThrow(AmazonServiceException(""))

        var uploadObject = UploadObject(s3Client)
        val file = File("")
        val name = "name"
        val id = 1

        uploadObject.uploadFile(name,id,file)

        Mockito.verify(s3Client, Mockito.atLeastOnce()).putObject(any(PutObjectRequest::class.java))
    }

    @Test
    fun sdkClientExceptionUploadObjectTest(){
        val s3Client = Mockito.mock(AmazonS3::class.java)
        Mockito.`when`(s3Client.putObject(any(PutObjectRequest::class.java))).thenThrow(SdkClientException(""))

        var uploadObject = UploadObject(s3Client)
        val file = File("")
        val name = "name"
        val id = 1

        uploadObject.uploadFile(name,id,file)

        Mockito.verify(s3Client, Mockito.atLeastOnce()).putObject(any(PutObjectRequest::class.java))
    }
}