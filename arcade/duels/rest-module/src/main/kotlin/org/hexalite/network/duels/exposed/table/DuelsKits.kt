package org.hexalite.network.duels.exposed.table

import org.hexalite.network.common.db.table.Users
import org.hexalite.network.common.util.exposed.jsonb
import org.hexalite.network.kraken.serialization.SerializablePlayerInventory
import org.jetbrains.exposed.dao.id.UUIDTable

object DuelsKits: UUIDTable(name = "duels_kits") {
    val inventory = jsonb<SerializablePlayerInventory>(name = "inventory")
    val userId = reference("user_id", Users)
}