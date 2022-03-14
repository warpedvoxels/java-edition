package org.hexalite.network.rest.webserver.api.v1

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.hexalite.network.common.db.entity.User
import org.hexalite.network.common.db.entity.rest
import org.hexalite.network.rest.webserver.annotations.Get

@Get("/users")
suspend fun PipelineContext<Unit, ApplicationCall>.listAllUsers() {
    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
    call.respond(User.all().limit(n = 5, offset = (page - 1) * 5L).rest())
}