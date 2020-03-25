package com.testswitch_api.testswitchapi.controllers;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.testswitch_api.testswitchapi.enums.ApplicationState;
import com.testswitch_api.testswitchapi.models.*;
import com.testswitch_api.testswitchapi.services.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/application")
@CrossOrigin("http://localhost:3000")

public class ApplicantController {

    private ApplicationService applicationService;
    private TestUrlService testUrlService;
    private EmailService emailService;
    private UploadService uploadService;
    private String uiUrl;

    @Autowired
    public ApplicantController(ApplicationService applicationService, TestUrlService testUrlService, EmailService emailService, UploadService uploadService, @Qualifier("uiUrlString") String uiUrl) {

        this.applicationService = applicationService;
        this.testUrlService = testUrlService;
        this.emailService = emailService;
        this.uploadService = uploadService;
        this.uiUrl = uiUrl;

    }

    @PostMapping(value = "/add", consumes = {"multipart/form-data"} )
    public ResponseEntity storeApplication(@Valid @ModelAttribute Application application, @ModelAttribute MultipartFile cvMultiFile) throws IOException {
        Boolean cvAvailable = cvMultiFile != null;
        applicationService.addApplicant(application, cvAvailable);
        if (cvAvailable) {
            int applicationId = applicationService.getLastApplicantEntry();
            PutObjectResult result = uploadService.uploadFile(application.getName(), applicationId, cvMultiFile);
            return ResponseEntity.ok().body(result.getETag());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get_all")
    public List<DatabaseApplication> getAllApplications() {
        return applicationService.getAllApplicants();
    }

    @GetMapping("/get_applicant/{id}")
    public DatabaseApplication getAllApplications(@PathVariable int id) {
        return applicationService.getApplicantById(id);
    }

    @GetMapping("/change_state/{id}/{state}")
    public ResponseEntity updateApplicationstate(@PathVariable int id, @PathVariable ApplicationState state) {
        if (state == ApplicationState.SENT) {
            String testString = randomString();
            String email = applicationService.getApplicantById(id).getEmail();
            String subject = "Testswitch - Application Test";
            String body = String.format("%s/test/%s",uiUrl,testString);
            emailService.sendApplicantTest(email, subject, body);
            applicationService.storeApplicantTestString(id, testString);
        }
        applicationService.updateApplicationState(id, state);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/{testString}")
    public ResponseEntity getApplicantTest(@PathVariable String testString) {
        try {
            int applicationId = applicationService.getApplicationIdByTestString(testString);
            return ResponseEntity.ok().body(applicationService.getApplicantById(applicationId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get_url/{objectKey}")
    public ResponseEntity generateUrl(@PathVariable String objectKey) {
        String urlString = testUrlService.getUrlStringByObjectKey(objectKey);
        if (urlString == null) {
            urlString = testUrlService.generateUrl(objectKey);
            if (urlString.equals("Amazon")) {
                return ResponseEntity.badRequest().body(new ErrorMessage("Error with Amazon Service"));
            } else if (urlString.equals("SDK")) {
                return ResponseEntity.badRequest().body(new ErrorMessage("Error with Server"));
            }
        }
        UrlObject url = new UrlObject();
        url.setUrl(urlString);
        return ResponseEntity.ok().body(url);
    }

    private String randomString() {
        return RandomStringUtils.randomAlphanumeric(32);
    }
}
