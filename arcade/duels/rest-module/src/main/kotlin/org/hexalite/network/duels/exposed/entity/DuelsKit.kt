package org.hexalite.network.duels.exposed.entity

import org.hexalite.network.common.db.table.Users
import org.hexalite.network.duels.exposed.table.DuelsKits
import org.hexalite.network.kraken.serialization.SerializablePlayerInventory
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class DuelsKit(id: EntityID<UUID>): UUIDEntity(id) {
    var userId by DuelsKits.userId
    var inventory by DuelsKits.inventory

    companion object: UUIDEntityClass<DuelsKit>(DuelsKits) {
        fun new(userId: UUID, inventory: SerializablePlayerInventory = SerializablePlayerInventory.Empty) = new {
            this.inventory = inventory
            this.userId = EntityID(userId, Users)
        }
    }
}