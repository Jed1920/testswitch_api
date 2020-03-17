package com.testswitch_api.testswitchapi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testswitch_api.testswitchapi.enums.ApplicationState;
import com.testswitch_api.testswitchapi.enums.ExperienceLevel;
import com.testswitch_api.testswitchapi.models.DatabaseApplication;
import com.testswitch_api.testswitchapi.models.ErrorMessage;
import com.testswitch_api.testswitchapi.models.UrlObject;
import com.testswitch_api.testswitchapi.services.ApplicationService;
import com.testswitch_api.testswitchapi.services.EmailService;
import com.testswitch_api.testswitchapi.services.TestUrlService;
import com.testswitch_api.testswitchapi.services.UploadService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class ApplicantControllerTest {

    ApplicationService applicationService = Mockito.mock(ApplicationService.class);
    TestUrlService testUrlService = Mockito.mock(TestUrlService.class);
    EmailService emailService = Mockito.mock(EmailService.class);
    UploadService uploadObject = Mockito.mock(UploadService.class);
    DatabaseApplication application = mockDatabaseApplication(1,"James May", "jimmyM@top.ge", "0284819123", ExperienceLevel.INTERMEDIATE, ApplicationState.NEW,false);

    @Test
    public void updateApplicationStateToSent(){
        Mockito.when(applicationService.getApplicantById(1)).thenReturn(application);
        ApplicantController applicantController = new ApplicantController(applicationService, testUrlService, emailService, uploadObject,"test_Ui_Url");
        applicantController.updateApplicationstate(1, ApplicationState.SENT);
        verify(emailService).sendApplicantTest(any(String.class),any(String.class),any(String.class));
    }


    @Test
    public void testStoreApplicationAmazonException() throws JsonProcessingException {
        String objectString = "object test string";
        Mockito.when(testUrlService.generateUrl(objectString)).thenReturn("Amazon");

        ApplicantController applicantController = new ApplicantController(applicationService, testUrlService, emailService, uploadObject,"test_Ui_Url");
        ResponseEntity response = applicantController.generateUrl(objectString);
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().toString()).contains("Error with Amazon Service");
    }

    @Test
    public void testStoreApplicationSDKException() {
        String objectString = "object test string";
        Mockito.when(testUrlService.generateUrl(objectString)).thenReturn("SDK");

        ApplicantController applicantController = new ApplicantController(applicationService, testUrlService, emailService, uploadObject,"test_Ui_Url");
        ResponseEntity response = applicantController.generateUrl(objectString);
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().toString()).contains("Error with Server");
    }

    public DatabaseApplication mockDatabaseApplication(int id, String name,String email,String contactInfo, ExperienceLevel experience, ApplicationState applicationState,boolean cv){
        DatabaseApplication application = new DatabaseApplication();
        application.setName(name);
        application.setEmail(email);
        application.setContactInfo(contactInfo);
        application.setExperience(experience);
        application.setApplicationState(applicationState);
        application.setCv(cv);
        return application;
    }

}

