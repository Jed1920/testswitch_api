package com.testswitch_api.testswitchapi.Controllers

import com.testswitch_api.testswitchapi.Models.Application
import com.testswitch_api.testswitchapi.Models.DatabaseApplication
import com.testswitch_api.testswitchapi.Services.ApplicationService
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApplicantController @Autowired constructor(
        private val applicationService : ApplicationService
){

    @PostMapping("/application")
    fun storeApplication(@ModelAttribute application: Application) : DatabaseApplication {
//        var applicationService : ApplicationService
        var databaseApplication : DatabaseApplication = applicationService.addApplicant(application)
        return databaseApplication
    }
}