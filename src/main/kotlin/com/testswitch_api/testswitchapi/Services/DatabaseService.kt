package com.testswitch_api.testswitchapi.Services

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin

abstract class DatabaseService {
    val jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/testswitch?user=postgres&password=techswitch").installPlugin(KotlinPlugin())
}

