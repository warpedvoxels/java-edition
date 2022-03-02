package org.hexalite.network.kraken.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayOutputStream
import kotlin.reflect.KClass

abstract class BaseBukkitSerializer<T: ConfigurationSerializable>(private val kotlinClass: KClass<T>): KSerializer<T> {
    private val serializer = ByteArraySerializer()

    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: T) {
        val bytes = ByteArrayOutputStream()
        BukkitObjectOutputStream(bytes).use { it.writeObject(value) }
        encoder.encodeSerializableValue(serializer, bytes.toByteArray())
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): T {
        val bytes = decoder.decodeSerializableValue(serializer)
        return BukkitObjectInputStream(bytes.inputStream()).use { it.readObject() as T }
    }
}

object ItemStackSerializer: BaseBukkitSerializer<ItemStack>(ItemStack::class)

object ItemMetaSerializer: BaseBukkitSerializer<ItemMeta>(ItemMeta::class)

object LocationSerializer: BaseBukkitSerializer<Location>(Location::class)

object PotionMetaSerializer: BaseBukkitSerializer<PotionMeta>(PotionMeta::class)

object SkullMetaSerializer: BaseBukkitSerializer<SkullMeta>(SkullMeta::class)
