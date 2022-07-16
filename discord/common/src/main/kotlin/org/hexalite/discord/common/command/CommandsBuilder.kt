package org.hexalite.discord.common.command

import org.hexalite.discord.common.command.message.MessageCommandBuilder
import org.hexalite.discord.common.command.message.MessageCommandData
import org.hexalite.discord.common.command.slash.RootSlashCommandBuilder
import org.hexalite.discord.common.command.slash.RootSlashCommandData
import org.hexalite.discord.common.command.slash.options.SlashCommandArguments
import org.hexalite.discord.common.command.user.UserCommandBuilder
import org.hexalite.discord.common.command.user.UserCommandData

inline fun <T : SlashCommandArguments> slashCommand(
    name: String,
    description: String,
    noinline arguments: () -> T,
    block: RootSlashCommandBuilder<T>.() -> Unit
): RootSlashCommandData<T> {
    val builder = RootSlashCommandBuilder(name, description, arguments).apply(block)
    builder.validate()

    return builder.build()
}

inline fun slashCommand(
    name: String,
    description: String,
    block: RootSlashCommandBuilder<*>.() -> Unit
): RootSlashCommandData<SlashCommandArguments> {
    val builder = RootSlashCommandBuilder<SlashCommandArguments>(name, description, null).apply(block)
    builder.validate()

    return builder.build()
}

inline fun messageCommand(name: String, block: MessageCommandBuilder.() -> Unit): MessageCommandData {
    val builder = MessageCommandBuilder(name).apply(block)
    builder.validate()

    return builder.build()
}

inline fun userCommand(name: String, block: UserCommandBuilder.() -> Unit): UserCommandData {
    val builder = UserCommandBuilder(name).apply(block)
    builder.validate()

    return builder.build()
}