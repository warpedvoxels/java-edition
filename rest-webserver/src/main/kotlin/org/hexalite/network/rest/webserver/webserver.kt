//    ___          __    _      __    __
//  / _ \___ ___ / /_  | | /| / /__ / /  ___ ___ _____  _____ ____
// / , _/ -_|_-</ __/  | |/ |/ / -_) _ \(_-</ -_) __/ |/ / -_) __/
///_/|_|\__/___/\__/   |__/|__/\__/_.__/___/\__/_/  |___/\__/_/

@file:JvmName("HexaliteRestWebserver")

package org.hexalite.network.rest.webserver

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.hexalite.network.rest.webserver.db.buildDatabaseFromDataSource
import org.hexalite.network.rest.webserver.db.pooling.createPooledDataSource
import java.io.File

val env = dotenv {
    directory = "${System.getProperty("user.home")}${File.pathSeparator}.hexalite"
}

val pooling = createPooledDataSource()
val database = buildDatabaseFromDataSource(pooling)

fun main() {
    val server = embeddedServer(Netty) {
    }
    server.start(wait = true)
}