package com.testswitch_api.testswitchapi.Controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testswitch_api.testswitchapi.AppTestConfiguration
import com.testswitch_api.testswitchapi.Models.Application
import com.testswitch_api.testswitchapi.Models.ApplicationState
import com.testswitch_api.testswitchapi.Models.DatabaseApplication
import com.testswitch_api.testswitchapi.Models.ExperienceLevel
import com.testswitch_api.testswitchapi.Services.ApplicationService
import com.testswitch_api.testswitchapi.Services.EmailService
import org.assertj.core.api.Assertions.assertThat
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest
@ContextConfiguration(classes = [AppTestConfiguration::class])
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("testDataSource")
internal class ApplicantControllerTest {

    var application1 = Application("James May", "jimmyM@top.ge", "0284819123", ExperienceLevel.INTERMEDIATE)
    var application2 = Application("Tom Jones", "tomjones@wales.dr", "48984932", ExperienceLevel.BEGINNER)
    var testString = "kn3ZRGlinp1fmCctSlXa1QXjP8NaFMwI"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jdbi: Jdbi

    @BeforeAll
    internal fun populateTable() {
        addFakeApplicant(application1)
        addFakeApplicant(application1)
        addFakeApplicant(application2)
        sentTestApplicant2()
    }

    @Test
    fun getAllApplicantsEndpointReturnsListOfApplicants() {
        val objectMapper = jacksonObjectMapper()
        val endpointResponse = mockMvc.perform(get("/application/get_all"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.getContentAsString()
        var mockResponse = objectMapper.readValue<List<DatabaseApplication>>(response)
        print(mockResponse)
        assertThat(mockResponse.size).isGreaterThanOrEqualTo(3)
        assertThat(mockResponse[0].name).isEqualTo(application1.name)
    }

    @Test
    fun addApplicantEndpointReturnsStatusOk() {
        mockMvc.perform(post("/application/add")
                .param("name", "John Cena")
                .param("email", "itsjohn@cena.com")
                .param("contactInfo", "03212553422")
                .param("experience", "BEGINNER"))
                .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun getIndividualApplicantEndpointReturnsApplicant() {
        val objectMapper = jacksonObjectMapper()
        val endpointResponse = mockMvc.perform(get("/application/get_applicant/3"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.getContentAsString()
        var mockResponse = objectMapper.readValue<DatabaseApplication>(response)
        assertThat(mockResponse.name).isEqualTo(application2.name)
    }

    @Test
    fun updateStateEndpointReturnsUpdatedApplicant() {
        val objectMapper = jacksonObjectMapper()
        val endpointResponse = mockMvc.perform(get("/application/change_state/3/ACCEPTED"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.getContentAsString()
        var mockResponse = objectMapper.readValue<DatabaseApplication>(response)
        assertThat(mockResponse.applicationState).isEqualTo(ApplicationState.ACCEPTED)
    }

    @Test
    fun checkupdateApplicationStateSENT() {
        val javaMailSender = Mockito.mock(JavaMailSender::class.java)
        var message = SimpleMailMessage()
        Mockito.doNothing().`when`(javaMailSender).send(message)
        var emailService = EmailService(javaMailSender)
        var applicationService = ApplicationService(jdbi, emailService)
        applicationService.updateApplicationState(1, ApplicationState.SENT)

        var testString = jdbi.withHandle<String, RuntimeException> { handle ->
            (handle.createQuery("SELECT * FROM sent_tests WHERE id =1")
                    .mapTo<String>()
                    .one())
        }
        assertThat(testString)
    }

    @Test
    fun getApplicantTestEndpointReturnsApplicant() {
        val objectMapper = jacksonObjectMapper()
        val endpointResponse = mockMvc.perform(get("/application/test/${testString}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.getContentAsString()
        var mockResponse = objectMapper.readValue<DatabaseApplication>(response)
        assertThat(mockResponse.name).isEqualTo(application2.name)
    }

    @Test
    fun getApplicantTestEndpointReturns404NotFound() {
        mockMvc.perform(get("/application/test/wrong_string"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    fun addFakeApplicant(applicant : Application) {
        jdbi.useHandle<RuntimeException> { handle ->
            (
                    handle.createUpdate("INSERT INTO applications(name, email,contact_info,experience) " +
                            "VALUES(:name,:email,:contactInfo,:experience::experience_level)")
                            .bind("name", applicant.name)
                            .bind("email", applicant.email)
                            .bind("contactInfo", applicant.contactInfo)
                            .bind("experience", applicant.experience)
                            .execute()
                    )
        }
    }
    fun sentTestApplicant2() {
        jdbi.useHandle<RuntimeException> { handle ->
            (
                    handle.createUpdate("INSERT INTO sent_tests(id, test_string) VALUES(:id,:testString)")
                            .bind("id", 3)
                            .bind("testString", testString)
                            .execute()
                    )
        }
    }
}
