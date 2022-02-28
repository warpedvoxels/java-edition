package org.hexalite.network.chat.roles

import net.kyori.adventure.text.format.TextColor

sealed class ChatRole(open val unicode: String, open val color: TextColor) {
    companion object {
        val types = listOf(EliteRole::class)
    }
}
