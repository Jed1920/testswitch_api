package com.testswitch_api.testswitchapi.Services

import org.jdbi.v3.core.Jdbi

abstract class DatabaseService {
    var jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/testswitch?user=postgres&password=techswitch")

}