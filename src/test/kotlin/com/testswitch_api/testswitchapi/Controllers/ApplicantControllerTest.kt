package com.testswitch_api.testswitchapi.Controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testswitch_api.testswitchapi.AppTestConfiguration
import com.testswitch_api.testswitchapi.Models.Application
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
        addFakeApplicant()
        addFakeApplicant()
        addFakeApplicant()
    }


    @Test
    fun addApplicantEndpointReturnsStatusOk() {
        mockMvc.perform(post("/application/add")
                .param("name", "John Cena")
                .param("email", "itsjohn@cena.com")
                .param("contactInfo", "03212553422")
                .param("experience", "Beginner"))
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


    fun addFakeApplicant(){
        jdbi.useHandle<RuntimeException> {handle ->(
            handle.createUpdate("INSERT INTO applications(name, email,contact_info,experience) " +
                    "VALUES(:name,:email,:contactInfo,:experience)")
                    .bind("name","James May")
                    .bind("email","jimmyM@top.ge")
                    .bind("contactInfo","0284819123")
                    .bind("experience","No Experience")
                    .execute()
                )
        }
    }
}
