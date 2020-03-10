package com.testswitch_api.testswitchapi.Models

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class Application(@get: NotBlank(message = "{field.required}")
                       val name: String,

                       @get: NotBlank(message = "{field.required}")
                       @get: Email(message = "{email.invalid}")
                       val email: String,

                       @get: NotBlank(message = "{field.required}")
                       val contactInfo: String,

//                       @get: NotBlank(message = "{field.required}")
                       val experience: ExperienceLevel)


