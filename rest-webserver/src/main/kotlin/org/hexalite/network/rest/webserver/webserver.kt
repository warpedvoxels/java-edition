//    ___          __    _      __    __
//  / _ \___ ___ / /_  | | /| / /__ / /  ___ ___ _____  _____ ____
// / , _/ -_|_-</ __/  | |/ |/ / -_) _ \(_-</ -_) __/ |/ / -_) __/
///_/|_|\__/___/\__/    |__/|__/\__/_.__/___/\__/_/  |___/\__/_/

@file:JvmName("HexaliteRestWebserver")

package org.hexalite.network.rest.webserver

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import org.hexalite.network.common.env.Environment
import org.hexalite.network.rest.webserver.annotations.use
import org.hexalite.network.rest.webserver.api.v1.listAllUsers
import org.hexalite.network.rest.webserver.db.buildDatabaseFromDataSource
import org.hexalite.network.rest.webserver.db.pooling.createPooledDataSource
import org.hexalite.network.rest.webserver.generic.reply

private typealias API = PipelineContext<Unit, ApplicationCall>

// Live reload: Pass `-Dio.ktor.development=true` to VM flags.
fun main() {
    buildDatabaseFromDataSource(createPooledDataSource())

    val server = embeddedServer(Netty, port = Environment.WebServer.port) {
        install(DefaultHeaders)
        install(ContentNegotiation) {
            json()
        }
        install(CallLogging)
        install(CachingHeaders)

        routing {
            get("/api") {
                call.reply("""
                    |Welcome to the root of our public API! Feel free to ask about it in our Discord server.
                    |We are currently in alpha stage, so please be patient, since we are working on it!
                    |To use our services, you need an authorization token and a API version, so you would call `/api/v<version>/<endpoint>` with an 'Authorization' header.
                    |For example, for be authenticated, you would call `/api/v1/auth` and pass your credentials in the JSON payload.
                    """.trimMargin().replace("\n", " ")
                )
            }
            route("/api/v1") {
                this use API::listAllUsers
            }
        }
    }
    server.start(wait = true)
}