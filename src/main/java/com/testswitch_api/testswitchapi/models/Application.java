package com.testswitch_api.testswitchapi.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class Application {

    @NotEmpty
    private String name;
    @Email
    private String email;
    private String contactInfo;
    @NotNull
    private String experience;

    public Application(String name, String email, String contactInfo, String experience){
        this.name = name;
        this.email = email;
        this.contactInfo = contactInfo;
        this.experience = experience;
    }


    //    @field: NotBlank(message = "{field.required}")
//    val name: String,
//
//    @field: NotBlank(message = "{field.required}")
//    @field: Email(message = "{email.invalid}")
//    val email: String,
//
//    @field: NotBlank(message = "{field.required}")
//    val contactInfo: String,
//
//    val experience: ExperienceLevel)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }


}
