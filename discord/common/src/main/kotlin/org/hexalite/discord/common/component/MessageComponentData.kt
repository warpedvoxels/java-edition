package org.hexalite.discord.common.component

import org.hexalite.discord.common.InteractionData

interface MessageComponentData : InteractionData {
    val customId: String
    val executor: suspend (ComponentContext).() -> Unit
}