package com.testswitch_api.testswitchapi.Controllers

import com.testswitch_api.testswitchapi.Models.ApplicationState
import com.testswitch_api.testswitchapi.Services.ApplicationService
import com.testswitch_api.testswitchapi.Services.EmailService
import com.testswitch_api.testswitchapi.Services.UploadObject
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

internal class ApplicantControllerTest{

    @Autowired
    private lateinit var jdbi: Jdbi

    @Test
    fun checkUpdateApplicationStateSENT() {
        jdbi
        val javaMailSender = Mockito.mock(JavaMailSender::class.java)
        Mockito.doNothing().`when`(javaMailSender).send(ArgumentMatchers.any(SimpleMailMessage::class.java))
        val emailService = EmailService(javaMailSender)
        val uploadObject = Mockito.mock(UploadObject::class.java)
        val applicationService = ApplicationService(jdbi, emailService,uploadObject)
        applicationService.updateApplicationState(1, ApplicationState.SENT)

//        val testString = jdbi.withHandle<String, RuntimeException> { handle ->
//            (handle.createQuery("SELECT * FROM sent_tests WHERE id =1")
//                    .mapTo<String>()
//                    .one())
//        }
        Mockito.verify(javaMailSender, Mockito.times(1)).send(ArgumentMatchers.any(SimpleMailMessage::class.java))
//        assertThat(testString)
    }
}