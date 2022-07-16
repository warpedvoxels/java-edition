package org.hexalite.discord.common.command.user

import dev.kord.common.Locale
import dev.kord.common.entity.Permissions
import org.hexalite.discord.common.utils.validateLocales

class UserCommandBuilder(val name: String, private var executor: (suspend (UserCommandContext).() -> Unit)? = null) {
    var nameLocalizations: MutableMap<Locale, String>? = null
    var dmPermission: Boolean? = null
    var defaultMemberPermissions: Permissions? = null

    fun execute(block: suspend (UserCommandContext).() -> Unit) {
        executor = block
    }

    fun validate() {
        if (executor == null)
            error("The $name UserCommand needs an executor")
        if (name.length !in 1..32)
            error("The $name UserCommand has a name that exceeds the ranger")

        validateLocales(nameLocalizations, name)
    }

    fun build() = UserCommandData(
        name,
        nameLocalizations,
        dmPermission,
        defaultMemberPermissions,
        executor ?: error("UserCommandBuilder: Executor function not initialized.")
    )
}