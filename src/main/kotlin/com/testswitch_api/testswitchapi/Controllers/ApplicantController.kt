package com.testswitch_api.testswitchapi.Controllers

import com.testswitch_api.testswitchapi.Models.Application
import com.testswitch_api.testswitchapi.Models.ApplicationState
import com.testswitch_api.testswitchapi.Models.DatabaseApplication
import com.testswitch_api.testswitchapi.Services.ApplicationService
import com.testswitch_api.testswitchapi.Services.GenerateURL
import com.testswitch_api.testswitchapi.Services.UploadObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@RestController
@RequestMapping("/application")
@CrossOrigin("http://localhost:3000")
class ApplicantController @Autowired constructor(
        private val applicationService : ApplicationService,
        private val uploadFile : UploadObject,
        private val generateUrl : GenerateURL
){

    @PostMapping("/add")
    fun storeApplication(@ModelAttribute application: Application) : ResponseEntity<Any> {
        applicationService.addApplicant(application)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/get_all")
    fun getAllApplications() : List<DatabaseApplication>{
        return applicationService.getAllApplicants()
    }

    @GetMapping("/get_applicant/{id}")
    fun getAllApplications(@PathVariable id: Int) : DatabaseApplication{
        return applicationService.getApplicantById(id)
    }

    @GetMapping("/change_state/{id}/{state}")
    fun updateApplicationstate(@PathVariable id:Int, @PathVariable state: ApplicationState) : DatabaseApplication{
        applicationService.updateApplicationState(id,state)
        return applicationService.getApplicantById(id)
    }

    @GetMapping("/test/{testString}")
    fun getApplicantTest(@PathVariable testString:String) : ResponseEntity<Any>{
        try {
            var applicationId = applicationService.getApplicationIdByTestString(testString)
            return ResponseEntity.ok().body(applicationService.getApplicantById(applicationId))
        } catch(e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }

    @PostMapping("fileUpload")
    fun uploadFiletoS3(@ModelAttribute multiFile: MultipartFile){
        var file = convert(multiFile)
        uploadFile.uploadFile(file!!)
        file.delete()
    }
    @GetMapping("getUrl")
    fun generateUrl() : String{
        return generateUrl.generateUrl()
    }
//    @PostMapping("/uploadFile")
//    fun uploadFile(@RequestParam("file") file: MultipartFile): UploadFileResponse? {
//        val fileName: String = fileStorageService?.storeFile(file) ?:"Something went wrong"
//        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/downloadFile/")
//                .path(fileName)
//                .toUriString()
//        return file.contentType?.let { UploadFileResponse(fileName, fileDownloadUri, it, file.size) }
//    }

    @Throws(IOException::class)
    fun convert(file: MultipartFile): File? {
        val convFile = File(file.originalFilename)
        convFile.createNewFile()
        val fos = FileOutputStream(convFile)
        fos.write(file.bytes)
        fos.close()
        return convFile
    }
}