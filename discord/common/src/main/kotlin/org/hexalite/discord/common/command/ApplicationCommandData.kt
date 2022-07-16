package org.hexalite.discord.common.command

import org.hexalite.discord.common.InteractionData

interface ApplicationCommandData : InteractionData {
    val name: String
}