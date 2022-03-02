package org.hexalite.network.rest.webserver.api.v1

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.hexalite.network.common.api.ApiRole
import org.hexalite.network.common.api.api
import org.hexalite.network.common.db.entity.Role
import org.hexalite.network.rest.webserver.annotations.Get

@Get("/roles/all")
suspend fun PipelineContext<Unit, ApplicationCall>.listAllRoles() {
    val all = api<List<ApiRole>> {
        Role.all()
    }
    call.respond(all)
}