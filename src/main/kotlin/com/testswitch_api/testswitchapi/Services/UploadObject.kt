package com.testswitch_api.testswitchapi.Services

import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Service
class UploadObject constructor (var s3Client : AmazonS3) {
    @Throws(IOException::class)
    fun uploadFile(name : String,id : Int,cvMultiFile : MultipartFile) {
        val cvFile = convertMultiFileToFile(cvMultiFile)
        val bucketName = System.getenv("AWS_BUCKET_NAME")
        val fileObjKeyName = "${id}_${name.replace(" ","_")}"
        try {
            val request = PutObjectRequest(bucketName, fileObjKeyName, cvFile)
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
        finally {
            cvFile.delete()
        }
    }

    @Throws(IOException::class)
    fun convertMultiFileToFile(file: MultipartFile): File {
        val convFile = File(file.originalFilename!!)
        convFile.createNewFile()
        val fos = FileOutputStream(convFile)
        fos.write(file.bytes)
        fos.close()
        return convFile
    }
}