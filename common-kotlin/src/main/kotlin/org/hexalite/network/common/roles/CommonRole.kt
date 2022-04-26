package org.hexalite.network.common.roles

import net.kyori.adventure.text.format.TextColor

sealed class CommonRole(open val unicode: String, open val color: TextColor, open val tabListIndex: Int) {
    companion object {
        val types: Set<CommonRole> = linkedSetOf(EliteRole)

        fun named(name: String): CommonRole? = types.find { it.id() == name }
    }

    fun id() = this::class.simpleName!!.lowercase().substringBeforeLast("role")
}
