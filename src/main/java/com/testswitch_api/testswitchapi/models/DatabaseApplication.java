package com.testswitch_api.testswitchapi.models;

import com.testswitch_api.testswitchapi.enums.ApplicationState;
import com.testswitch_api.testswitchapi.enums.ExperienceLevel;

public class DatabaseApplication {

    private int id;
    private String name;
    private String email;
    private String contactInfo;
    private ExperienceLevel experience;
    private ApplicationState applicationState;
    private boolean cv;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public ExperienceLevel getExperience() {
        return experience;
    }

    public void setExperience(ExperienceLevel experience) {
        this.experience = experience;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    public boolean isCv() {
        return cv;
    }

    public void setCv(boolean cv) {
        this.cv = cv;
    }
}
