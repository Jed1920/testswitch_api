package com.testswitch_api.testswitchapi.Services

import com.testswitch_api.testswitchapi.Models.Application
import com.testswitch_api.testswitchapi.Models.ApplicationState
import com.testswitch_api.testswitchapi.Models.DatabaseApplication
import org.apache.commons.lang3.RandomStringUtils
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@Service
class ApplicationService constructor(
        val jdbi: Jdbi,
        val emailService: EmailService,
        val uploadFile : UploadObject
) {

    fun addApplicant(application: Application, multiFile: MultipartFile?) {
        print(multiFile != null)
        jdbi.useHandle<RuntimeException> { handle ->
            handle.createUpdate("INSERT INTO applications(name, email,contact_info,experience,cv)" +
                    "VALUES(:name,:email,:contactInfo,:experience::experience_level,:cv);")
                    .bind("name", application.name)
                    .bind("email", application.email)
                    .bind("contactInfo", application.contactInfo)
                    .bind("experience", application.experience)
                    .bind("cv", multiFile != null)
                    .execute()
        }
        if(multiFile != null) {
            val applicationId : Int = jdbi.withHandle<Int, RuntimeException> { handle ->
                (handle.createQuery("SELECT id FROM applications ORDER BY id DESC LIMIT 1;")
                        .mapTo<Int>()
                        .one())
            }
            var file = convertMultiFileToFile(multiFile)
            uploadFile.uploadFile(application.name,applicationId, file)
            file.delete()
        }
    }

    fun getAllApplicants(): List<DatabaseApplication> {
        return jdbi.withHandle<List<DatabaseApplication>, RuntimeException> { handle ->
            (handle.createQuery("SELECT * FROM applications ORDER BY application_state;")
                    .mapTo<DatabaseApplication>()
                    .list())
        }
    }

    fun updateApplicationState(id: Int, state: ApplicationState) {
        if (state == ApplicationState.SENT) {
            var testString: String = randomString();
            emailService.sendSimpleMessage(getApplicantById(id).email, "Testswitch - Application Test", "${System.getenv("UI_URL")}/test/${testString}")
            jdbi.useHandle<RuntimeException> { handle ->
                handle.createUpdate("INSERT INTO sent_tests(id, test_string) VALUES(:id,:testString);")
                        .bind("id", id)
                        .bind("testString", testString)
                        .execute()
            }
        }
        jdbi.useHandle<RuntimeException> { handle ->
            handle.createUpdate("UPDATE applications SET application_state = :state::application_state WHERE id = :id;")
                    .bind("state", state)
                    .bind("id", id)
                    .execute()
        }
    }

    fun getApplicantById(id: Int): DatabaseApplication {
        return jdbi.withHandle<DatabaseApplication, RuntimeException> { handle ->
            (handle.createQuery("SELECT * FROM applications WHERE id = :id")
                    .bind("id", id)
                    .mapTo<DatabaseApplication>()
                    .one())
        }
    }

    fun getApplicationIdByTestString(testString: String): Int {
        return jdbi.withHandle<Int, RuntimeException> { handle ->
            (handle.createQuery("SELECT * FROM sent_tests WHERE test_string = :testString")
                    .bind("testString", testString)
                    .mapTo<Int>()
                    .one())
        }
    }
    @Throws(IOException::class)
    fun convertMultiFileToFile(file: MultipartFile): File {
        val convFile = File(file.originalFilename)
        convFile.createNewFile()
        val fos = FileOutputStream(convFile)
        fos.write(file.bytes)
        fos.close()
        return convFile
    }

    fun randomString(): String {
        return RandomStringUtils.randomAlphanumeric(32);
    }
}