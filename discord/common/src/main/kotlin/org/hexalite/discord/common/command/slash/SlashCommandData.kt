package org.hexalite.discord.common.command.slash

import dev.kord.common.Locale
import dev.kord.common.entity.Permissions
import org.hexalite.discord.common.command.ApplicationCommandData
import org.hexalite.discord.common.command.slash.options.SlashCommandArguments

sealed interface CommandWithArguments<T : SlashCommandArguments> : ApplicationCommandData {
    val arguments: (() -> T)?
    val executor: (suspend (SlashCommandContext<T>).() -> Unit)?
}

data class RootSlashCommandData<T : SlashCommandArguments>(
    override val name: String,
    val nameLocalizations: MutableMap<Locale, String>?,
    val description: String,
    val descriptionLocalizations: MutableMap<Locale, String>?,
    val defaultMemberPermissions: Permissions?,
    val dmPermission: Boolean?,
    override val arguments: (() -> T)?,
    override val executor: (suspend (SlashCommandContext<T>).() -> Unit)?,
    val subCommands: List<SubCommandData<out SlashCommandArguments>>?,
    val groups: List<GroupCommandData>?
) : CommandWithArguments<T>

data class GroupCommandData(
    override val name: String,
    val nameLocalizations: MutableMap<Locale, String>?,
    val description: String,
    val descriptionLocalizations: MutableMap<Locale, String>?,
    val subCommands: List<SubCommandData<out SlashCommandArguments>>
)  : ApplicationCommandData

data class SubCommandData<T : SlashCommandArguments>(
    override val name: String,
    val nameLocalizations: MutableMap<Locale, String>?,
    val description: String,
    val descriptionLocalizations: MutableMap<Locale, String>?,
    override val arguments: (() -> T)?,
    override val executor: suspend (SlashCommandContext<T>).() -> Unit
)  : CommandWithArguments<T>