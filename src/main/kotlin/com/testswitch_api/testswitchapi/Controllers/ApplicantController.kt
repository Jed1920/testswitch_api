package com.testswitch_api.testswitchapi.Controllers

import com.testswitch_api.testswitchapi.Models.*
import com.testswitch_api.testswitchapi.Services.ApplicationService
import com.testswitch_api.testswitchapi.Services.GenerateURL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import java.util.function.Consumer
import javax.validation.Valid

@RestController
@RequestMapping("/application")
@CrossOrigin("http://localhost:3000")
class ApplicantController @Autowired constructor(
        private val applicationService: ApplicationService,
        private val generateUrl: GenerateURL
) {
    @PostMapping("/add",consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun storeApplication(@Valid @RequestBody application: Application, @ModelAttribute cvFile: MultipartFile?): ResponseEntity<Any> {
        applicationService.addApplicant(application, cvFile)
        return ResponseEntity.ok().build()
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

    @GetMapping("/get_url/{objectKey}")
    fun generateUrl(@PathVariable objectKey: String): ResponseEntity<Any> {
        var urlString : String? = generateUrl.getUrlStringByObjectKey(objectKey)

        if(urlString == null) {
            urlString = generateUrl.generateUrl(objectKey)
            if (urlString == "Amazon") {
                return ResponseEntity.badRequest().body(ErrorMessage("Error with Amazon Service"))
            } else if (urlString == "SDK") {
                return ResponseEntity.badRequest().body(ErrorMessage("Error with Server"))
            }
        }
        return ResponseEntity.ok().body(UrlObject(urlString))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): Map<String, String?>? {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            errors[fieldName] = errorMessage
        })
        return errors
    }
}