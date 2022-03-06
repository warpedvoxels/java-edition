package org.hexalite.network.rest.webserver.api.v1

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.hexalite.network.common.api.RestUser
import org.hexalite.network.common.db.entity.User
import org.hexalite.network.rest.webserver.annotations.Get
import org.hexalite.network.rest.webserver.util.api

@Get("/users")
suspend fun PipelineContext<Unit, ApplicationCall>.listAllUsers() {
    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1

    val users = api<List<RestUser>> {
        User.all().limit(n = 5, offset = ((page - 1) * 5).toLong())
    }
    call.respond(users)
}