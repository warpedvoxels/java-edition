package org.hexalite.network.rp.font

@kotlinx.serialization.Serializable
open class FontCustomProvider(
    val file: String,
    val chars: Set<String>,
    val ascent: Int = 7, // Usually the same as height
    val height: Int = 7, // Usually half of the image height
    val type: String = "bitmap",
) {
    constructor(file: String, char: String, ascent: Int = 7, height: Int = 7): this(file, setOf(char), ascent, height)
}


@kotlinx.serialization.Serializable
data class FontCustomProviderCollectionHolder(
    val providers: Set<FontCustomProvider>,
)