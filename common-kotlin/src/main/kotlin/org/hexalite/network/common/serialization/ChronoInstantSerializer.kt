package org.hexalite.network.common.serialization

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ChronoInstantSerializer : KSerializer<Instant> {
    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        val string = decoder.decodeString()
        return if (string.endsWith('Z')) {
            Instant.parse(string)
        } else {
            Instant.parse(string + 'Z')
        }
    }

    override fun serialize(encoder: Encoder, value: Instant) =
        encoder.encodeString(value.toString())
}