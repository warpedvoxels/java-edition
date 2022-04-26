package org.hexalite.network.common.env

import io.github.cdimascio.dotenv.dotenv
import java.io.File

sealed interface Environment {
    companion object {
        private val dotenv by lazy {
            dotenv {
                directory = "${System.getProperty("user.home")}${File.separator}.hexalite"
            }
        }
    }

    sealed class Docker: Environment {
        object Postgres: Docker() {
            val user = dotenv["POSTGRES_USER"]
            val password = dotenv["POSTGRES_PASSWORD"]
            val database = dotenv["POSTGRES_DB"]
        }
    }

    sealed class WebServer: Environment {
        object DB: WebServer() {
            val user = dotenv["WEBSERVER_DB_USER"]
            val password = dotenv["WEBSERVER_DB_PASSWORD"]
            val url = dotenv["WEBSERVER_DB_URL"]
        }

        companion object {
            val port = dotenv["WEBSERVER_PORT"].toShort().toInt() // Make sure that is a real port then convert to int
        }
    }

    object Pooling: Environment {
        val connectionTimeout = dotenv["POOLING_CONNECTION_TIMEOUT"].toLong()
        val idleTimeout = dotenv["POOLING_IDLE_TIMEOUT"].toLong()
        val maximumLifetime = dotenv["POOLING_MAXIMUM_LIFETIME"].toLong()
        val maximumPoolSize = dotenv["POOLING_MAXIMUM_POOL_SIZE"].toInt()
    }
}