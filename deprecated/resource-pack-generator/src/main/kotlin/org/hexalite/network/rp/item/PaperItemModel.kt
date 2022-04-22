package org.hexalite.network.rp.item

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaperItemModel(
    val parent: String = "minecraft:item/generated",
    val textures: Textures = Textures(),
    val overrides: MutableList<Override> = mutableListOf(),
) {
    @Serializable
    data class Textures(
        val layer0: String = "minecraft:item/paper",
    )

    @Serializable
    data class Override(
        val predicate: Predicate = Predicate(),
        val model: String = "minecraft:item/generated",
    ) {
        @Serializable
        data class Predicate(
            @SerialName("custom_model_data")
            val customModelData: Int = 1,
        )
    }
}
