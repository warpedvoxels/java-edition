package org.hexalite.network.duels.db.table

import org.hexalite.network.common.db.table.Users
import org.hexalite.network.common.util.exposed.jsonb
import org.hexalite.network.kraken.serialization.SerializableInventory
import org.jetbrains.exposed.dao.id.UUIDTable

object DuelsKits: UUIDTable(name = "duels_kits") {
    val inventory = jsonb<SerializableInventory>(name = "inventory")
    val userId = reference("user_id", Users)
}