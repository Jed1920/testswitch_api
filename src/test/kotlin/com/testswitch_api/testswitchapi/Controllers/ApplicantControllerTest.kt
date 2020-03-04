package com.testswitch_api.testswitchapi.Controllers

import com.testswitch_api.testswitchapi.Services.ApplicationService
import com.testswitch_api.testswitchapi.Services.GenerateURL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class ApplicantControllerTest {

    @Test
    fun testStoreApplicationAmazonException(){
        val applicationService = Mockito.mock(ApplicationService::class.java)
        val generateUrl = Mockito.mock(GenerateURL::class.java)
        val objectString : String = "object test string"
        Mockito.`when`(generateUrl.generateUrl(objectString)).thenReturn("Amazon")
        val applicantController = ApplicantController(applicationService,generateUrl)
        val response = applicantController.generateUrl(objectString)
        assertThat(response.statusCodeValue).isEqualTo(400)
        assertThat(response.body.toString()).contains("Error with Amazon Service")
    }

    @Test
    fun testStoreApplicationSDKException(){
        val applicationService = Mockito.mock(ApplicationService::class.java)
        val generateUrl = Mockito.mock(GenerateURL::class.java)
        val objectString : String = "object test string"
        Mockito.`when`(generateUrl.generateUrl(objectString)).thenReturn("SDK")

        val applicantController = ApplicantController(applicationService,generateUrl)
        val response = applicantController.generateUrl(objectString)

        assertThat(response.statusCodeValue).isEqualTo(400)
        assertThat(response.body.toString()).contains("Error with Server")
    }


}