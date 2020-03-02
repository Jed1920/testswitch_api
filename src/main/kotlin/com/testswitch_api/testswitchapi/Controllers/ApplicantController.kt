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


@RestController
@RequestMapping("/application")
@CrossOrigin("http://localhost:3000")
class ApplicantController @Autowired constructor(
        private val applicationService: ApplicationService,
        private val uploadFile: UploadObject,
        private val generateUrl: GenerateURL
) {
    @PostMapping("/add")
    fun storeApplication(@ModelAttribute application: Application, @ModelAttribute file: MultipartFile?): ResponseEntity<Any> {
        try {
            applicationService.addApplicant(application, file)
            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            return ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/get_all")
    fun getAllApplications(): List<DatabaseApplication> {
        return applicationService.getAllApplicants()
    }

    @GetMapping("/get_applicant/{id}")
    fun getAllApplications(@PathVariable id: Int): DatabaseApplication {
        return applicationService.getApplicantById(id)
    }

    @GetMapping("/change_state/{id}/{state}")
    fun updateApplicationstate(@PathVariable id: Int, @PathVariable state: ApplicationState): DatabaseApplication {
        applicationService.updateApplicationState(id, state)
        return applicationService.getApplicantById(id)
    }

    @GetMapping("/test/{testString}")
    fun getApplicantTest(@PathVariable testString: String): ResponseEntity<Any> {
        try {
            var applicationId = applicationService.getApplicationIdByTestString(testString)
            return ResponseEntity.ok().body(applicationService.getApplicantById(applicationId))
        } catch (e: Exception) {
            return ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("get_url/{objectKey}")
    fun generateUrl(@PathVariable objectKey: String): ResponseEntity<Any> {
        data class urlObject(var url: String)

        return ResponseEntity.ok().body(urlObject(generateUrl.generateUrl(objectKey)))
    }
}