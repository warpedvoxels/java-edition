@file:UseSerializers(ItemStackSerializer::class)

package org.hexalite.network.kraken.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.hexalite.network.kraken.bukkit.server
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@kotlinx.serialization.Serializable
data class SerializableInventory(val contents: Array<ItemStack?>) {
    @kotlinx.serialization.Serializable
    val size
        get() = contents.size

    companion object Serializer: KSerializer<SerializableInventory> {
        override val descriptor = buildClassSerialDescriptor("SerializableInventory") {
            element("contents", String.serializer().descriptor) // 0
        }

        private fun Array<ItemStack?>.base64(): String {
            val bytes = ByteArrayOutputStream()
            BukkitObjectOutputStream(bytes).use {
                it.writeInt(size)
                for (item in this) {
                    it.writeObject(item?.serializeAsBytes())
                }
            }
            return Base64Coder.encodeLines(bytes.toByteArray())
        }

        private fun String.inventory(): Array<ItemStack?> {
            val bytes = ByteArrayInputStream(Base64Coder.decodeLines(this))
            val bukkit = BukkitObjectInputStream(bytes)
            val items = Array(bukkit.readInt()) {
                val stack = bukkit.readObject() as? ByteArray?
                if (stack != null) {
                    ItemStack.deserializeBytes(stack)
                } else {
                    null
                }
            }
            bukkit.close()
            return items
        }

        override fun deserialize(decoder: Decoder): SerializableInventory = decoder.decodeStructure(descriptor) {
            var contents: Array<ItemStack?>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> contents = decodeStringElement(descriptor, index).inventory()
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
            SerializableInventory(contents!!)
        }

        override fun serialize(encoder: Encoder, value: SerializableInventory) = encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.contents.base64())
        }
    }

    fun apply(player: Player) {
        player.inventory.armorContents = contents
    }

    fun bukkit(title: Component = Component.empty(), holder: InventoryHolder? = null): Inventory = server.createInventory(holder, contents.size, title).apply {
        contents = this@SerializableInventory.contents
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SerializableInventory
        if (!contents.contentEquals(other.contents)) return false
        return true
    }

    override fun hashCode(): Int = contents.contentHashCode()
}

fun Inventory.serializable(): SerializableInventory = SerializableInventory(contents)
