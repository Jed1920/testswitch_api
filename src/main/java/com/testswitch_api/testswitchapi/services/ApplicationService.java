package com.testswitch_api.testswitchapi.services;

import com.testswitch_api.testswitchapi.models.Application;
import com.testswitch_api.testswitchapi.enums.ApplicationState;
import com.testswitch_api.testswitchapi.models.DatabaseApplication;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApplicationService {

    private Jdbi jdbi;

    @Autowired
    public ApplicationService(Jdbi jdbi) {
        this.jdbi = jdbi;
    }


    public void addApplicant(Application application, Boolean cvAvailable) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO applications(name, email,contact_info,experience,cv)" +
                        "VALUES(:name,:email,:contactInfo,:experience::experience_level,:cv);")
                        .bind("name", application.getName())
                        .bind("email", application.getEmail())
                        .bind("contactInfo", application.getContactInfo())
                        .bind("experience", application.getExperience())
                        .bind("cv", cvAvailable)
                        .execute());
    }

    public int getLastApplicantEntry() {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT id FROM applications ORDER BY id DESC LIMIT 1;")
                        .mapTo(int.class)
                        .one());
    }

    public List<DatabaseApplication> getAllApplicants() {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM applications ORDER BY application_state;")
                        .mapToBean(DatabaseApplication.class)
                        .list());
    }

    public void updateApplicationState(int id, ApplicationState state) {
        jdbi.useHandle(handle ->
                handle.createUpdate("UPDATE applications SET application_state = :state::application_state WHERE id = :id;")
                        .bind("state", state)
                        .bind("id", id)
                        .execute());
    }

    public void storeApplicantTestString(int id, String testString) {
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO sent_tests(id, test_string) VALUES(:id,:testString);")
                        .bind("id", id)
                        .bind("testString", testString)
                        .execute());
    }

    public DatabaseApplication getApplicantById(int id) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM applications WHERE id = :id")
                        .bind("id", id)
                        .mapToBean(DatabaseApplication.class)
                        .one());
    }

    public int getApplicationIdByTestString(String testString) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM sent_tests WHERE test_string = :testString")
                        .bind("testString", testString)
                        .mapTo(int.class)
                        .one());
    }
}
