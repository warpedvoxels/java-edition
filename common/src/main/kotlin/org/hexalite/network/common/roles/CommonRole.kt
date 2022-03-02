package org.hexalite.network.common.roles

import net.kyori.adventure.text.format.TextColor

sealed class CommonRole(open val unicode: String, open val color: TextColor, open val tabListIndex: Int) {
    companion object {
        val types: Set<CommonRole> = linkedSetOf(EliteRole)
    }

    fun id() = this::class.simpleName!!.lowercase().substringBeforeLast("role")
}
