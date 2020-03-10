package com.testswitch_api.testswitchapi.Models

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class Application(@field: NotBlank(message = "{field.required}")
                       val name: String,

                       @field: NotBlank(message = "{field.required}")
                       @field: Email(message = "{email.invalid}")
                       val email: String,

                       @field: NotBlank(message = "{field.required}")
                       val contactInfo: String,

                       val experience: ExperienceLevel)


