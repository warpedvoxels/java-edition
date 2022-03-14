@file:UseSerializers(UUIDSerializer::class)

package org.hexalite.network.duels.rest.entity

import kotlinx.serialization.UseSerializers
import org.hexalite.network.common.serialization.UUIDSerializer
import org.hexalite.network.kraken.serialization.SerializableInventory
import java.util.*

@kotlinx.serialization.Serializable
data class RestDuelsKit(
    val owner: UUID,
    val inventory: SerializableInventory,
)
