package org.hexalite.network.common.math

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// https://github.com/LorittaBot/Loritta/blob/cinnamon/common/src/commonMain/kotlin/net/perfectdreams/loritta/cinnamon/common/utils/Color.kt
@Serializable(with = Color.Serializer::class)
class Color(rgb: Int) {
    constructor(red: Int, green: Int, blue: Int): this(rgb(red, green, blue))

    val rgb = rgb and 0xFFFFFF

    val red: Int get() = (rgb shr 16) and 0xFF
    val green: Int get() = (rgb shr 8) and 0xFF
    val blue: Int get() = (rgb shr 0) and 0xFF

    init {
        require(this.rgb in MIN_COLOR..MAX_COLOR) { "RGB should be in range of $MIN_COLOR..$MAX_COLOR but was ${this.rgb}" }
    }

    override fun toString(): String = "Color(red=$red,green=$green,blue=$blue)"

    override fun hashCode(): Int = rgb.hashCode()

    override fun equals(other: Any?): Boolean {
        val color = other as? Color ?: return false

        return color.rgb == rgb
    }

    companion object {
        private const val MIN_COLOR = 0
        private const val MAX_COLOR = 0xFFFFFF

        fun fromString(input: String): Color {
            if (input.indexOf(',') == 3) {
                // r,g,b color
                val split = input.split(',')
                val r = requireNotNull(split[0].toIntOrNull()) { "Red part of the RGB code is not a valid integer!" }
                val g = requireNotNull(split[1].toIntOrNull()) { "Green part of the RGB code is not a valid integer!" }
                val b = requireNotNull(split[2].toIntOrNull()) { "Blue part of the RGB code is not a valid integer!" }

                return Color(r, g, b)
            } else if (input.startsWith("#")) {
                // Parse it as a hex color
                return fromHex(input)
            } else {
                val inputAsAnInteger = requireNotNull(input.toIntOrNull()) { "The string is not a valid color code!" }
                return Color(inputAsAnInteger)
            }
        }

        // https://stackoverflow.com/a/41654372/7271796
        fun fromHex(input: String) = Color(input.removePrefix("#").toInt(16))
    }

    internal object Serializer: KSerializer<Color> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.color", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: Color) {
            encoder.encodeInt(value.rgb)
        }
    }
}

private fun rgb(red: Int, green: Int, blue: Int): Int {
    require(red in 0..255) { "Red should be in range of 0..255 but was $red" }
    require(green in 0..255) { "Green should be in range of 0..255 but was $green" }
    require(blue in 0..255) { "Blue should be in range of 0..255 but was $blue" }

    return red and 0xFF shl 16 or
        (green and 0xFF shl 8) or
        (blue and 0xFF) shl 0
}

inline val Color.bukkit get() = org.bukkit.Color.fromRGB(red, green, blue)

inline val java.awt.Color.kotlin: Color get() = Color(rgb)
