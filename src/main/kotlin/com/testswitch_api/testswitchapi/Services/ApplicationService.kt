package com.testswitch_api.testswitchapi.Services

import com.testswitch_api.testswitchapi.Models.Application
import com.testswitch_api.testswitchapi.Models.DatabaseApplication

class ApplicationService():DatabaseService(){

    fun addApplicant(application: Application):DatabaseApplication{
        val names : DatabaseApplication = jdbi.withHandle<DatabaseApplication, RuntimeException> { handle ->
            handle.createUpdate("INSERT INTO applications(name, email,contact_info,experience)" +
                    "VALUES(:name,email,contactInfo,experience);")
                    .bind("name",application.name)
                    .bind("email",application.email)
                    .bind("contactInfo",application.contactInfo)
                    .bind("experience",application.experience)
                    .execute()
            handle.createQuery("SELECT * from applications WHERE name=:name")
                    .bind("name",application.name)
                    .mapToBean(DatabaseApplication::class.java).one()
        }
        return names
    }
}