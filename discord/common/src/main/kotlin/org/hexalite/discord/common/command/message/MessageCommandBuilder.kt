package org.hexalite.discord.common.command.message

import dev.kord.common.Locale
import dev.kord.common.entity.Permissions
import org.hexalite.discord.common.utils.validateLocales

class MessageCommandBuilder(private val name: String, private var executor: (suspend (MessageCommandContext).() -> Unit)? = null) {
    var nameLocalizations: MutableMap<Locale, String>? = null
    var dmPermission: Boolean? = null
    var defaultMemberPermissions: Permissions? = null

    fun execute(block: suspend (MessageCommandContext).() -> Unit) {
        executor = block
    }

    fun validate() {
        if (executor == null)
            error("The $name MessageCommand needs an executor")
        if (name.length !in 1..32)
            error("The $name MessageCommand has a name that exceeds the ranger")

        validateLocales(nameLocalizations, name)
    }

    fun build() = MessageCommandData(
        name,
        nameLocalizations,
        dmPermission,
        defaultMemberPermissions,
        executor ?: error("MessageCommandBuilder: Executor function not initialized.")
    )
}