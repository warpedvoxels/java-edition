package org.hexalite.network.rest.webserver.v1

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

class HexaliteWebClientV1Services {
    private val client = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.INFO
        }
        install(ContentNegotiation) {
            json()
        }
        install(HttpCache)
    }
}