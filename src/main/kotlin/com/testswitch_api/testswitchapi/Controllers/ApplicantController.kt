package com.testswitch_api.testswitchapi.Controllers

import com.testswitch_api.testswitchapi.Models.Application
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ApplicantController {
    @PostMapping("/application")
    fun storeApplication(@ModelAttribute application: Application) :Application{
        return application
    }
}