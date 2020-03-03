package com.testswitch_api.testswitchapi.Controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testswitch_api.testswitchapi.AppTestConfiguration
import com.testswitch_api.testswitchapi.Models.*
import org.assertj.core.api.Assertions.assertThat
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
@ActiveProfiles("testDataSource","testAmazonS3")
internal class ApplicantControllerIntegrationTest {

    var application1 = Application("James May", "jimmyM@top.ge", "0284819123", ExperienceLevel.INTERMEDIATE)
    var application2 = Application("Tom Jones", "tomjones@wales.dr", "48984932", ExperienceLevel.BEGINNER)
    var testString = "kn3ZRGlinp1fmCctSlXa1QXjP8NaFMwI"
    val objectMapper = jacksonObjectMapper()

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
        val endpointResponse = mockMvc.perform(get("/application/get_all"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.contentAsString
        val mockResponse = objectMapper.readValue<List<DatabaseApplication>>(response)
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
        val endpointResponse = mockMvc.perform(get("/application/get_applicant/3"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.contentAsString
        val mockResponse = objectMapper.readValue<DatabaseApplication>(response)
        assertThat(mockResponse.name).isEqualTo(application2.name)
    }

    @Test
    fun updateStateEndpointReturnsUpdatedApplicant() {
        val endpointResponse = mockMvc.perform(get("/application/change_state/3/ACCEPTED"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.contentAsString
        val mockResponse = objectMapper.readValue<DatabaseApplication>(response)
        assertThat(mockResponse.applicationState).isEqualTo(ApplicationState.ACCEPTED)
    }

    @Test
    fun getApplicantTestEndpointReturnsApplicant() {
        val endpointResponse = mockMvc.perform(get("/application/test/${testString}"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.contentAsString
        val mockResponse = objectMapper.readValue<DatabaseApplication>(response)
        assertThat(mockResponse.name).isEqualTo(application2.name)
    }

    @Test
    fun getApplicantTestEndpointReturns404NotFound() {
        mockMvc.perform(get("/application/test/wrong_string"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun generateUrlEndpoint(){
        val endpointResponse = mockMvc.perform(get("/application/get_url/test_object_key"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.contentAsString
        val urlString = objectMapper.readValue<UrlObject>(response)

        assertThat(urlString.url).isEqualTo("http://url_string")
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
