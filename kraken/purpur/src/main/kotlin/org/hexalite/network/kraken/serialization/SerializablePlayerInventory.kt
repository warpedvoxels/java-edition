@file:UseSerializers(ItemStackSerializer::class)

package org.hexalite.network.kraken.serialization

import kotlinx.serialization.UseSerializers
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.valiktor.functions.hasSize
import org.valiktor.validate

@kotlinx.serialization.Serializable
data class SerializablePlayerInventory(
    val head: ItemStack? = null,
    val chest: ItemStack? = null,
    val legs: ItemStack? = null,
    val feet: ItemStack? = null,
    val offHand: ItemStack? = null,
    val contents: Array<ItemStack?> = arrayOf(),
) {
    companion object {
        val Empty = SerializablePlayerInventory()
    }

    init {
        validate(this) {
            validate(SerializablePlayerInventory::contents).hasSize(min = 0, max = 36)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SerializablePlayerInventory
        if (head != other.head) return false
        if (chest != other.chest) return false
        if (legs != other.legs) return false
        if (feet != other.feet) return false
        if (offHand != other.offHand) return false
        if (!contents.contentEquals(other.contents)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = head?.hashCode() ?: 0
        result = 31 * result + (chest?.hashCode() ?: 0)
        result = 31 * result + (legs?.hashCode() ?: 0)
        result = 31 * result + (feet?.hashCode() ?: 0)
        result = 31 * result + (offHand?.hashCode() ?: 0)
        result = 31 * result + contents.contentHashCode()
        return result
    }

    fun applyTo(player: Player) {
        player.inventory.helmet = head
        player.inventory.chestplate = chest
        player.inventory.leggings = legs
        player.inventory.boots = feet
        player.inventory.setItemInOffHand(offHand)
        contents.forEachIndexed { index, itemStack -> player.inventory.setItem(index, itemStack) }
    }
}