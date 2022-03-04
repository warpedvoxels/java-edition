package org.hexalite.network.rest.webserver.db.table

import kotlinx.datetime.LocalDateTime
import org.hexalite.network.common.extension.now
import org.hexalite.network.common.util.exposed.BaseRestWebserverUUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * Table that include global statistics about users in the network.
 */
object Users: BaseRestWebserverUUIDTable(name = "users") {
    val lastUsername = varchar("last_username", 16).default("")
    val lastSeen = datetime("last_seen").clientDefault { LocalDateTime.now() }
    val hexes = long("hexes").default(0)
}