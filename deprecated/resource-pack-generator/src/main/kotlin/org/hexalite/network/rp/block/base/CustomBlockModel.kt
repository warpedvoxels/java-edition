package org.hexalite.network.rp.block.base

import org.hexalite.network.rp.extensions.instrument

@kotlinx.serialization.Serializable
open class CustomBlockModel(
    val textures: Textures,
    val parent: String = "block/cube_all",
    @kotlinx.serialization.Transient val index: Int = 0,
) {
    constructor(texture: String, parent: String = "block/cube_all", index: Int): this(Textures(texture), parent, index)

    @kotlinx.serialization.Serializable
    data class Textures(val all: String)
}

inline fun CustomBlockModel.field(): String {
    val index = index + 26
    return "instrument=${index.instrument()},note=${index % 25},powered=${index >= 400}"
}

inline fun CustomBlockModel.state() = BlockState("block/${textures.all.substringAfterLast('/')}")
