package com.testswitch_api.testswitchapi.Models

import java.util.*

data class UrlObject(var url: String)

data class ErrorMessage(var error: String)

data class UrlDatabaseObject(var objectKey: String, var urlString : String, var expiration : Long)