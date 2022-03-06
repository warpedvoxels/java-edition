package org.hexalite.network.common.db.entity

import org.hexalite.network.common.db.table.UserRoles
import org.hexalite.network.common.roles.CommonRole
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserRole(id: EntityID<Int>): IntEntity(id) {
    var user by User referencedOn UserRoles.user
    var roleId by UserRoles.roleId

    companion object: IntEntityClass<UserRole>(UserRoles) {
        fun new(id: UUID, role: CommonRole) = new {
            user = User.findById(id) ?: error("User '$id' not found. Cannot insert a UserRole into the database.")
            roleId = role.id()
        }

        fun new(user: User, role: CommonRole) = new {
            this.user = user
            roleId = role.id()
        }
    }
}