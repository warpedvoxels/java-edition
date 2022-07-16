package org.hexalite.discord.common.command.slash.options

import dev.kord.common.Locale

data class SlashCommandChoice<T>(
    override val name: String,
    val value: T
) : CommandChoiceBuilder {
    override var nameLocalizations: MutableMap<Locale, String>? = null
}