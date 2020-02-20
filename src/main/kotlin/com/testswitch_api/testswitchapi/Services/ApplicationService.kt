package com.testswitch_api.testswitchapi.Services

import com.testswitch_api.testswitchapi.Models.Application
import com.testswitch_api.testswitchapi.Models.ApplicationState
import com.testswitch_api.testswitchapi.Models.DatabaseApplication
import org.apache.commons.lang3.RandomStringUtils
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class ApplicationService @Autowired constructor(
        private val jdbi: Jdbi
) {

    fun addApplicant(application: Application) {
        jdbi.useHandle<RuntimeException> { handle ->
            handle.createUpdate("INSERT INTO applications(name, email,contact_info,experience)" +
                    "VALUES(:name,:email,:contactInfo,:experience::experience_level);")
                    .bind("name", application.name)
                    .bind("email", application.email)
                    .bind("contactInfo", application.contactInfo)
                    .bind("experience", application.experience)
                    .execute()
        }
    }

    fun getAllApplicants(): List<DatabaseApplication> {
        return jdbi.withHandle<List<DatabaseApplication>, RuntimeException> { handle ->
            (handle.createQuery("SELECT * FROM applications ORDER BY application_state;")
                    .mapTo<DatabaseApplication>()
                    .list())
        }
    }

    fun updateApplicationState(id: Integer, state: ApplicationState): DatabaseApplication{
        if(state == ApplicationState.SENT){
            jdbi.useHandle<RuntimeException> { handle ->
                handle.createUpdate("INSERT INTO sent_tests(id, test_string) VALUES(:id,:testString);")
                        .bind("id", id)
                        .bind("testString", randomString())
                        .execute()
            }
        }
        jdbi.useHandle<RuntimeException> { handle ->
            handle.createUpdate("UPDATE applications SET application_state = :state::application_state WHERE id = :id;")
                    .bind("state", state)
                    .bind("id", id)
                    .execute()
        }
        return getApplicantById(id)
    }

    fun getApplicantById(id: Integer): DatabaseApplication {
        return jdbi.withHandle<DatabaseApplication,RuntimeException> {handle ->
            (handle.createQuery("SELECT * FROM applications WHERE id = :id")
                    .bind("id",id)
                    .mapTo<DatabaseApplication>()
                    .one())
        }
    }

    fun randomString() : String{
        return RandomStringUtils.randomAlphanumeric(32);
    }
}