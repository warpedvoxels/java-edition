package org.hexalite.network.rest.webserver.db.table

import org.hexalite.network.common.util.exposed.StringIdTable

object Roles: StringIdTable(name = "roles") {
    val tabListIndex = integer("tab_list_index").default(0)
}
