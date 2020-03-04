//package com.testswitch_api.testswitchapi.Services
//
//import com.opentable.db.postgres.embedded.EmbeddedPostgres
//import com.testswitch_api.testswitchapi.Models.Application
//import com.testswitch_api.testswitchapi.Models.ApplicationState
//import com.testswitch_api.testswitchapi.Models.ExperienceLevel
//import org.jdbi.v3.core.Jdbi
//import org.jdbi.v3.core.kotlin.KotlinPlugin
//import org.jdbi.v3.core.kotlin.mapTo
//import org.junit.jupiter.api.BeforeAll
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.TestInstance
//import org.mockito.ArgumentMatchers
//import org.mockito.Mockito
//import org.springframework.mail.SimpleMailMessage
//import org.springframework.mail.javamail.JavaMailSender
//import org.springframework.web.multipart.MultipartFile
//import java.io.File
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//internal class ApplicationServiceTest {
//
//    var application1 = Application("James May", "jimmyM@top.ge", "0284819123", ExperienceLevel.INTERMEDIATE)
//    var application2 = Application("Tom Jones", "tomjones@wales.dr", "48984932", ExperienceLevel.BEGINNER)
//    var testString = "kn3ZRGlinp1fmCctSlXa1QXjP8NaFMwI"
//    val postgres = EmbeddedPostgres.builder().start()
//    val jdbi = Jdbi.create(postgres.postgresDatabase).installPlugin(KotlinPlugin())
//
//    @BeforeAll
//    internal fun populateTable() {
//        addFakeApplicant(application1)
//        addFakeApplicant(application1)
//        addFakeApplicant(application2)
//        sentTestApplicant2()
//    }
//
//    @Test
//    fun checkUpdateApplicationStateSENT() {
//
//        val javaMailSender = Mockito.mock(JavaMailSender::class.java)
//        Mockito.doNothing().`when`(javaMailSender).send(ArgumentMatchers.any(SimpleMailMessage::class.java))
//        val emailService = EmailService(javaMailSender)
//        val uploadObject = Mockito.mock(UploadObject::class.java)
//        val applicationService = ApplicationService(jdbi, emailService,uploadObject)
//        applicationService.updateApplicationState(1, ApplicationState.SENT)
//
////        val testString = jdbi.withHandle<String, RuntimeException> { handle ->
////            (handle.createQuery("SELECT * FROM sent_tests WHERE id =1")
////                    .mapTo<String>()
////                    .one())
////        }
//        Mockito.verify(javaMailSender, Mockito.times(1)).send(ArgumentMatchers.any(SimpleMailMessage::class.java))
////        assertThat(testString)
//    }
//
//    @Test
//    fun addApplicantEndpointWithFile() {
//        val emailService = Mockito.mock(EmailService::class.java)
//        val uploadObject = Mockito.mock(UploadObject::class.java)
//        val jdbi = Mockito.mock(Jdbi::class.java)
//
//        val file = File("")
//        val id = 1
//        Mockito.doNothing().`when`(uploadObject).uploadFile("", id, file)
//        Mockito.`when`(jdbi.withHandle<Int, RuntimeException>{ handle ->
//            (handle.createQuery("SELECT id FROM applications ORDER BY id DESC LIMIT 1;")
//                    .mapTo<Int>()
//                    .one())
//        }).thenReturn(id)
//
//        val applicationService = ApplicationService(jdbi, emailService, uploadObject)
//
//
//        val multiFile = Mockito.mock(MultipartFile::class.java)
//        applicationService.addApplicant(application1, multiFile)
//
////        assertThat(uploadObject)
//        Mockito.verify(uploadObject, Mockito.atLeastOnce())
//    }
//
//    fun addFakeApplicant(applicant : Application) {
//        jdbi.useHandle<RuntimeException> { handle ->
//            (
//                    handle.createUpdate("INSERT INTO applications(name, email,contact_info,experience) " +
//                            "VALUES(:name,:email,:contactInfo,:experience::experience_level)")
//                            .bind("name", applicant.name)
//                            .bind("email", applicant.email)
//                            .bind("contactInfo", applicant.contactInfo)
//                            .bind("experience", applicant.experience)
//                            .execute()
//                    )
//        }
//    }
//    fun sentTestApplicant2() {
//        jdbi.useHandle<RuntimeException> { handle ->
//            (
//                    handle.createUpdate("INSERT INTO sent_tests(id, test_string) VALUES(:id,:testString)")
//                            .bind("id", 3)
//                            .bind("testString", testString)
//                            .execute()
//                    )
//        }
//    }
//}