/*
 * WarpedVoxels, a network of Minecraft: Java Edition servers
 * Copyright (C) 2023  Pedro Henrique
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.warpedvoxels.core.rp.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("EnumEntryName")
@Serializable(with = ResourcePackFormatSerializer::class)
public enum class ResourcePackFormat(public val value: Int) {
    `1_8_9`(1),
    `1_10_2`(2),
    `1_12_2`(3),
    `1_14_4`(4),
    `1_16_1`(5),
    `1_16_5`(6),
    `1_17_1`(7),
    `1_18_2`(8),
    `1_19_2`(9),
    `1_19_3`(12),
    `1_19_4`(13),
    `1_20_1`(15)
}

public object ResourcePackFormatSerializer : KSerializer<ResourcePackFormat> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("pack_format", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): ResourcePackFormat {
        val value = decoder.decodeInt()
        return ResourcePackFormat.entries.find { it.value == value }
            ?: throw NoSuchElementException("There's no resource pack format with ID '$value'.")
    }

    override fun serialize(encoder: Encoder, value: ResourcePackFormat) {
        encoder.encodeInt(value.value)
    }
}