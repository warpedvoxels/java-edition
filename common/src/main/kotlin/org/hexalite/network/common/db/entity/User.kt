package org.hexalite.network.common.db.entity

import org.hexalite.network.common.api.ApiUser
import org.hexalite.network.common.api.fromDatabaseEntity
import org.hexalite.network.common.db.table.Users
import org.hexalite.network.common.util.exposed.BaseRestWebserverUUIDEntity
import org.hexalite.network.common.util.exposed.BaseRestWebserverUUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import java.util.*

class User(id: EntityID<UUID>): BaseRestWebserverUUIDEntity(id, Users) {
    val lastUsername by Users.lastUsername
    val lastSeen by Users.lastSeen
    val hexes by Users.hexes

    companion object: BaseRestWebserverUUIDEntityClass<User>(Users) {
        fun new(uuid: UUID) = new(uuid) {}
    }
}

inline fun User.api() = ApiUser.fromDatabaseEntity(this)

inline fun SizedIterable<User>.api() = map(User::api)