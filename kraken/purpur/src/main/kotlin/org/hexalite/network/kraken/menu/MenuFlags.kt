package org.hexalite.network.kraken.menu

data class MenuFlags(
    var cancelClick: Boolean = true,
    var cancelItemMoving: Boolean = true,
    val cancelItemDragging: Boolean = true,
)