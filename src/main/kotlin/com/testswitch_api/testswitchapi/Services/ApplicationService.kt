package com.testswitch_api.testswitchapi.Services

import com.testswitch_api.testswitchapi.Models.Application
import com.testswitch_api.testswitchapi.Models.DatabaseApplication
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
            (handle.createQuery("SELECT * FROM applications")
                    .mapTo<DatabaseApplication>()
                    .list())
        }
    }

    fun getApplicantById(id: Integer): DatabaseApplication {
        return jdbi.withHandle<DatabaseApplication,RuntimeException> {handle ->
            (handle.createQuery("SELECT * FROM applications WHERE id = :id")
                    .bind("id",id)
                    .mapTo<DatabaseApplication>()
                    .one())
        }
    }
}