package org.hexalite.network.duels.db.entity

import org.hexalite.network.common.db.table.Users
import org.hexalite.network.duels.db.table.DuelsKits
import org.hexalite.network.duels.rest.entity.RestDuelsKit
import org.hexalite.network.kraken.serialization.SerializableInventory
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import java.util.*

class DuelsKit(id: EntityID<UUID>): UUIDEntity(id) {
    var userId by DuelsKits.userId
    var inventory by DuelsKits.inventory

    companion object: UUIDEntityClass<DuelsKit>(DuelsKits) {
        fun new(userId: UUID, inventory: SerializableInventory) = new {
            this.inventory = inventory
            this.userId = EntityID(userId, Users)
        }
    }
}

inline fun DuelsKit.rest(): RestDuelsKit = RestDuelsKit(
    owner = userId.value,
    inventory = inventory
)

inline fun SizedIterable<DuelsKit>.rest(): List<RestDuelsKit> = map(DuelsKit::rest)
