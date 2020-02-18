package com.testswitch_api.testswitchapi.Controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testswitch_api.testswitchapi.AppTestConfiguration
import com.testswitch_api.testswitchapi.Models.Application
import com.testswitch_api.testswitchapi.Models.ApplicationState
import com.testswitch_api.testswitchapi.Models.DatabaseApplication
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
import org.springframework.test.context.event.annotation.BeforeTestMethod
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.lang.RuntimeException


@SpringBootTest
@ContextConfiguration(classes = [AppTestConfiguration::class])
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("testDataSource")
internal class ApplicantControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jdbi: Jdbi

    @BeforeAll
    internal fun populateTable(){
        addFakeApplicant1()
        addFakeApplicant1()
        addFakeApplicant2()
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
    fun getAllApplicantsEndpointReturnsListOfApplicants(){
        val objectMapper = jacksonObjectMapper()
        val endpointResponse = mockMvc.perform(get("/application/get_all"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.getContentAsString()
        var mockResponse = objectMapper.readValue<List<DatabaseApplication>>(response)
        assertThat(mockResponse.size).isGreaterThanOrEqualTo(3)
        assertThat(mockResponse[0].name).isEqualTo("James May")
    }

    @Test
    fun getIndividualApplicantEndpointReturnsApplicant(){
        val objectMapper = jacksonObjectMapper()
        val endpointResponse = mockMvc.perform(get("/application/get_applicant/3"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.getContentAsString()
        var mockResponse = objectMapper.readValue<DatabaseApplication>(response)
        assertThat(mockResponse.name).isEqualTo("Tom Jones")
    }

    @Test
    fun updateStateEndpointReturnsUpdatedApplicant(){
        val objectMapper = jacksonObjectMapper()
        val endpointResponse = mockMvc.perform(get("/application/change_state/3/ACCEPTED"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val response = endpointResponse.response.getContentAsString()
        var mockResponse = objectMapper.readValue<DatabaseApplication>(response)
        assertThat(mockResponse.applicationState).isEqualTo(ApplicationState.ACCEPTED)
    }

    fun addFakeApplicant1() {
        jdbi.useHandle<RuntimeException> { handle ->
            (
                    handle.createUpdate("INSERT INTO applications(name, email,contact_info,experience) " +
                            "VALUES(:name,:email,:contactInfo,:experience::experience_level)")
                            .bind("name", "James May")
                            .bind("email", "jimmyM@top.ge")
                            .bind("contactInfo", "0284819123")
                            .bind("experience", "INTERMEDIATE")
                            .execute()
                    )
        }
    }
        fun addFakeApplicant2(){
        jdbi.useHandle<RuntimeException> {handle ->(
            handle.createUpdate("INSERT INTO applications(name, email,contact_info,experience) " +
                    "VALUES(:name,:email,:contactInfo,:experience::experience_level)")
                    .bind("name","Tom Jones")
                    .bind("email","tomjones@wales.dr")
                    .bind("contactInfo","48984932")
                    .bind("experience","BEGINNER")
                    .execute()
                )
        }
    }
}
