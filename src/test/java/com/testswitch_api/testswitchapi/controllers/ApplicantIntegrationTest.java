package com.testswitch_api.testswitchapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.testswitch_api.testswitchapi.AppTestConfiguration;
import com.testswitch_api.testswitchapi.models.*;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@ContextConfiguration(classes = AppTestConfiguration.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles({"testDataSource", "testAmazonS3","testUIUrl","testBucketName"})
public class ApplicantIntegrationTest {

    Application application1 = new Application("James May", "jimmyM@top.ge", "0284819123", "INTERMEDIATE");
    Application application2 = new Application("Tom Jones", "tomjones@wales.dr", "48984932", "BEGINNER");
    String testString = "kn3ZRGlinp1fmCctSlXa1QXjP8NaFMwI";
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Jdbi jdbi;

    @BeforeAll
    public void populateTable() {
        addFakeApplicant(application1);
        addFakeApplicant(application1);
        addFakeApplicant(application2);
        sentTestApplicant2();
    }

    @Test
    public void getAllApplicantsEndpointReturnsListOfApplicants() throws Exception {
        MvcResult endpointResponse = mockMvc.perform(get("/application/get_all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String response = endpointResponse.getResponse().getContentAsString();
        DatabaseApplication[] mockResponse = objectMapper.readValue(response, DatabaseApplication[].class);

        assertThat(mockResponse.length).isGreaterThanOrEqualTo(3);
        assertThat(mockResponse[0].getName()).isEqualTo(application1.getName());
    }

    @Test
    public void addApplicantEndpointReturnsStatusOk() throws Exception {
        mockMvc.perform(post("/application/add")
                .contentType("multipart/form-data")
                .param("name", "John Cena")
                .param("email", "itsjohn@cena.com")
                .param("contactInfo", "03212553422")
                .param("experience", "BEGINNER"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addInvalidApplicantReturnsStatusBad() throws Exception {
        mockMvc.perform(post("/application/add")
                .contentType("multipart/form-data")
                .param("name", "")
                .param("email", "itsjohn@cena.com")
                .param("contactInfo", "03212553422")
                .param("experience", "BEGINNER"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getIndividualApplicantEndpointReturnsApplicant() throws Exception {
        MvcResult endpointResponse = mockMvc.perform(get("/application/get_applicant/3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String response = endpointResponse.getResponse().getContentAsString();
        DatabaseApplication mockResponse = objectMapper.readValue(response, DatabaseApplication.class);

        assertThat(mockResponse.getName()).isEqualTo(application2.getName());
    }

    @Test
    public void updateStateEndpointReturnsUpdatedApplicant() throws Exception {
        mockMvc.perform(get("/application/change_state/3/ACCEPTED"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getApplicantTestEndpointReturnsApplicant() throws Exception {
        MvcResult endpointResponse = mockMvc.perform(get(String.format("/application/test/%s",testString)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String response = endpointResponse.getResponse().getContentAsString();
        DatabaseApplication mockResponse = objectMapper.readValue(response, DatabaseApplication.class);

        assertThat(mockResponse.getName()).isEqualTo(application2.getName());
    }

    @Test
    public void getApplicantTestEndpointReturns404NotFound() throws Exception {
        mockMvc.perform(get("/application/test/wrong_string"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void generateUrlEndpoint() throws Exception {
        MvcResult endpointResponse = mockMvc.perform(get("/application/get_url/test_object_key"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String response = endpointResponse.getResponse().getContentAsString();
        UrlObject urlString = objectMapper.readValue(response, UrlObject.class);

        assertThat(urlString.getUrl()).isEqualTo("http://url_string");
    }

    public void addFakeApplicant(Application applicant) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO applications(name, email,contact_info,experience) " +
                        "VALUES(:name,:email,:contactInfo,:experience::experience_level)")
                        .bind("name", applicant.getName())
                        .bind("email", applicant.getEmail())
                        .bind("contactInfo", applicant.getContactInfo())
                        .bind("experience", applicant.getExperience())
                        .execute());
    }

    public void sentTestApplicant2() {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO sent_tests(id, test_string) VALUES(:id,:testString)")
                        .bind("id", 3)
                        .bind("testString", testString)
                        .execute()
        );
    }
}

