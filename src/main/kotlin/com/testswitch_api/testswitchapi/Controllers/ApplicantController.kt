package com.testswitch_api.testswitchapi.Controllers

import com.testswitch_api.testswitchapi.Models.*
import com.testswitch_api.testswitchapi.Services.ApplicationService
import com.testswitch_api.testswitchapi.Services.EmailService
import com.testswitch_api.testswitchapi.Services.GenerateURL
import com.testswitch_api.testswitchapi.Services.UploadObject
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.function.Consumer
import javax.validation.Valid

@RestController
@RequestMapping("/application")
@CrossOrigin("http://localhost:3000")
class ApplicantController @Autowired constructor(
        private val applicationService: ApplicationService,
        private val generateUrl: GenerateURL,
        private val emailService: EmailService,
        private val uploadObject: UploadObject
) {
    @PostMapping("/add", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun storeApplication(@Valid @RequestBody application: Application, @ModelAttribute cvMultiFile: MultipartFile?): ResponseEntity<Any> {
        val cvAvailable: Boolean = cvMultiFile != null
        applicationService.addApplicant(application, cvAvailable)
        if (cvAvailable) {
            val applicationId: Int = applicationService.getLastApplicantEntry()
            uploadObject.uploadFile(application.name, applicationId, cvMultiFile!!)
        }
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
    fun updateApplicationstate(@PathVariable id: Int, @PathVariable state: ApplicationState): ResponseEntity<Any>{
        if (state == ApplicationState.SENT) {
            val testString: String = randomString()
            val email: String = applicationService.getApplicantById(id).email
            val subject = "Testswitch - Application Test"
            val body = "${System.getenv("UI_URL")}/test/${testString}"
            emailService.sendSimpleMessage(email, subject, body)
            applicationService.sendApplicantTest(id, testString)
        }
        applicationService.updateApplicationState(id, state)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/test/{testString}")
    fun getApplicantTest(@PathVariable testString: String): ResponseEntity<Any> {
        return try {
            val applicationId = applicationService.getApplicationIdByTestString(testString)
            ResponseEntity.ok().body(applicationService.getApplicantById(applicationId))
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/get_url/{objectKey}")
    fun generateUrl(@PathVariable objectKey: String): ResponseEntity<Any> {
        var urlString: String? = generateUrl.getUrlStringByObjectKey(objectKey)
        if (urlString == null) {
            urlString = generateUrl.generateUrl(objectKey)
            if (urlString == "Amazon") {
                return ResponseEntity.badRequest().body(ErrorMessage("Error with Amazon Service"))
            } else if (urlString == "SDK") {
                return ResponseEntity.badRequest().body(ErrorMessage("Error with Server"))
            }
        }
        return ResponseEntity.ok().body(UrlObject(urlString))
    }

    fun randomString(): String {
        return RandomStringUtils.randomAlphanumeric(32)
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