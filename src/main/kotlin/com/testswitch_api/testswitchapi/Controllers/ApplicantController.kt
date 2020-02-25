package com.testswitch_api.testswitchapi.Controllers

import com.testswitch_api.testswitchapi.Models.Application
import com.testswitch_api.testswitchapi.Models.ApplicationState
import com.testswitch_api.testswitchapi.Models.DatabaseApplication
import com.testswitch_api.testswitchapi.Services.ApplicationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/application")
@CrossOrigin("http://localhost:3000")
class ApplicantController @Autowired constructor(
        private val applicationService : ApplicationService
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
    fun getApplicantTest(@PathVariable testString:String) : DatabaseApplication{
        var applicationId = applicationService.getApplicationIdByIdString(testString)
        return applicationService.getApplicantById(applicationId)
    }
}