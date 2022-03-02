package org.hexalite.network.rest.webserver.generic

import io.ktor.server.application.*
import io.ktor.server.response.*

@kotlinx.serialization.Serializable
data class GenericReply(val message: String)

suspend inline fun ApplicationCall.reply(message: String) = respond(GenericReply(message))
