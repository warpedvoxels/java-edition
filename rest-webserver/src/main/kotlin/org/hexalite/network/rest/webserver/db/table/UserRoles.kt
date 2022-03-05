package org.hexalite.network.rest.webserver.db.table

import org.jetbrains.exposed.dao.id.IntIdTable

object UserRoles: IntIdTable(name = "user_roles") {
    val user = reference("user", Users)
    val roleId = varchar("role_id", 64)
}
