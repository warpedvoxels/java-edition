package org.hexalite.network.common.db.entity

import org.hexalite.network.common.db.table.UserRoles
import org.hexalite.network.common.rest.entity.RestUserRole
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import java.util.*
import org.hexalite.network.common.roles.CommonRole as Role

class UserRole(id: EntityID<Int>): IntEntity(id) {
    var user by User referencedOn UserRoles.user
    var roleId by UserRoles.roleId

    companion object: IntEntityClass<UserRole>(UserRoles) {
        fun new(id: UUID, role: Role) = new {
            user = User.findById(id) ?: error("User '$id' not found. Cannot insert a UserRole into the database.")
            roleId = role.id()
        }

        fun new(user: User, role: Role) = new {
            this.user = user
            roleId = role.id()
        }
    }
}

inline fun UserRole.rest(): RestUserRole = RestUserRole(
    id = roleId,
    tabListIndex = Role.named(roleId)?.tabListIndex ?: -1
)

inline fun SizedIterable<UserRole>.rest(): List<RestUserRole> = map(UserRole::rest)