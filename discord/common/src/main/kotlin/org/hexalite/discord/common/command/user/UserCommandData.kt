package org.hexalite.discord.common.command.user

import dev.kord.common.Locale
import dev.kord.common.entity.Permissions
import org.hexalite.discord.common.command.ApplicationCommandData

data class UserCommandData(
    override val name: String,
    val nameLocalizations: MutableMap<Locale, String>?,
    val dmPermission: Boolean?,
    val defaultMemberPermissions: Permissions?,
    val executor: suspend (UserCommandContext).() -> Unit
) : ApplicationCommandData