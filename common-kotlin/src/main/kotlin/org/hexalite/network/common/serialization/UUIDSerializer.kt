package org.hexalite.network.common.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.nio.ByteBuffer
import java.util.*

object UUIDSerializer : KSerializer<UUID> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("UUID", ByteArraySerializer().descriptor)

    override fun deserialize(decoder: Decoder): UUID = with(decoder.decodeSerializableValue(ByteArraySerializer())) {
        val buffer = ByteBuffer.wrap(this)
        UUID(buffer.long, buffer.long)
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        val buffer = ByteBuffer.wrap(ByteArray(16)).apply {
            putLong(value.mostSignificantBits)
            putLong(value.leastSignificantBits)
        }
        encoder.encodeSerializableValue(ByteArraySerializer(), buffer.array())
    }

}

object StringUUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())
}