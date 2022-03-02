package org.hexalite.network.common.db.entity

import org.hexalite.network.common.api.ApiRole
import org.hexalite.network.common.api.fromDatabaseEntity
import org.hexalite.network.common.db.table.Roles
import org.hexalite.network.common.roles.CommonRole
import org.hexalite.network.common.util.exposed.StringEntity
import org.hexalite.network.common.util.exposed.StringEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable

class Role(id: EntityID<String>): StringEntity(id) {
    var tabListIndex by Roles.tabListIndex

    companion object: StringEntityClass<Role>(Roles) {
        fun new(id: String) = new(id) {}

        fun new(role: CommonRole) = new(role.id()) {
            tabListIndex = role.tabListIndex
        }
    }
}

inline fun Role.api() = ApiRole.fromDatabaseEntity(this)

inline fun SizedIterable<Role>.api() = map(Role::api)