package com.testswitch_api.testswitchapi.Controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testswitch_api.testswitchapi.AppTestConfiguration
import com.testswitch_api.testswitchapi.Models.Application
import com.testswitch_api.testswitchapi.Models.DatabaseApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest
@ContextConfiguration(classes = [AppTestConfiguration::class])
@AutoConfigureMockMvc
@ActiveProfiles("testDataSource")
internal class ApplicantControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun checkAddApplicantEndpoint() {
        val objectMapper = jacksonObjectMapper()

        val result = mockMvc.perform(post("/application/add")
                .param("name", "John Cena")
                .param("email", "itsjohn@cena.com")
                .param("contactInfo", "03212553422")
                .param("experience", "Beginner"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val resultString = result.response.getContentAsString()

        var mockResponse = objectMapper.readValue<DatabaseApplication>(resultString)
        assertThat(mockResponse.id).isEqualTo(1)
        assertThat(mockResponse.name).isEqualTo("John Cena")
    }
}
