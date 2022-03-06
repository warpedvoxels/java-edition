package org.hexalite.network.kraken.gameplay.feature.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.hexalite.network.kraken.gameplay.feature.GameplayFeatureView

open class CustomItemFeature(
    val textureIndex: Int,
    val translatableKey: TranslatableComponent? = null,
) {
    fun stack(namespace: NamespacedKey, amount: Int = 1, showTextureIndexInLore: Boolean = true): ItemStack {
        val stack = ItemStack(Material.PAPER, amount)
        with(stack) {
            val meta = itemMeta ?: return stack
            val container = meta.persistentDataContainer
            container.set(namespace, PersistentDataType.INTEGER, textureIndex)
            meta.setCustomModelData(textureIndex)
            if (showTextureIndexInLore) {
                meta.lore(mutableListOf<Component>(Component.text("§8#$textureIndex")))
            }
            if (translatableKey != null) {
                meta.displayName(translatableKey)
            }
            itemMeta = meta
            return stack
        }
    }
}

fun CustomItemFeature.asCustomBlockOrNull(view: GameplayFeatureView) = view.retrieveCustomBlock(textureIndex)

fun ItemStack.asCustomOrNull(view: GameplayFeatureView): CustomItemFeature? {
    val meta = itemMeta ?: return null
    val container = meta.persistentDataContainer
    val textureIndex = container.get(view.id, PersistentDataType.INTEGER) ?: return null
    return view.retrieveCustomItem(textureIndex)
}