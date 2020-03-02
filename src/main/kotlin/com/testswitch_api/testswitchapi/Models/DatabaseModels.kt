package com.testswitch_api.testswitchapi.Models

data class DatabaseApplication(val id:Int, val name: String, val email: String, val contactInfo: String, val experience: ExperienceLevel,val applicationState: ApplicationState,val cv : Boolean)

enum class ApplicationState() {
    NEW,
    SENT,
    COMPLETED,
    EXPIRED,
    ACCEPTED,
    REJECTED
}

enum class ExperienceLevel() {
    NONE,
    BEGINNER,
    INTERMEDIATE,
    EXPERT
}