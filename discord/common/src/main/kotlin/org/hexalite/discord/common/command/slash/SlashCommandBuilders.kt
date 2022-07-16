package org.hexalite.discord.common.command.slash

import dev.kord.common.Locale
import dev.kord.common.entity.Permissions
import org.hexalite.discord.common.command.slash.options.SlashCommandArguments
import org.hexalite.discord.common.utils.validateLocales

class RootSlashCommandBuilder<T : SlashCommandArguments>(
    private val name: String,
    private val description: String,
    private val arguments: (() -> T)?
) {
    var nameLocalizations: MutableMap<Locale, String>? = null
    var descriptionLocalizations: MutableMap<Locale, String>? = null
    var defaultMemberPermissions: Permissions? = null
    var dmPermission: Boolean? = null

    var subCommands: MutableList<SubCommandData<out SlashCommandArguments>>? = null
    var groups: MutableList<GroupCommandData>? = null
    private var executor: (suspend (SlashCommandContext<T>).() -> Unit)? = null

    fun execute(block: suspend (SlashCommandContext<T>).() -> Unit) {
        executor = block
    }

    inline fun subCommand(name: String, description: String, block: SubCommandBuilder<*>.() -> Unit) {
        val builder = SubCommandBuilder<SlashCommandArguments>(name, description, null).apply(block)
        builder.validate()

        if (subCommands == null)
            subCommands = mutableListOf()

        subCommands?.add(builder.build())
    }

    inline fun <R : SlashCommandArguments> subCommand(
        name: String,
        description: String,
        noinline arguments: () -> R,
        block: SubCommandBuilder<R>.() -> Unit
    ) {
        val builder = SubCommandBuilder(name, description, arguments).apply(block)
        builder.validate()

        if (subCommands == null)
            subCommands = mutableListOf()

        subCommands?.add(builder.build())
    }

    inline fun group(name: String, description: String, block: GroupCommandBuilder.() -> Unit) {
        val builder = GroupCommandBuilder(name, description).apply(block)
        builder.validate()

        if (groups == null)
            groups = mutableListOf()

        groups?.add(builder.build())
    }

    fun validate() {
        if (executor != null && (subCommands != null || groups != null))
            error("The $name command cannot have an executor because its subcommands or groups are not empty")

        if (arguments != null && (subCommands != null || groups != null))
            error("The $name command cannot have an argument because its subcommands or groups are not empty")

        if (executor == null && subCommands == null && groups == null)
            error("The $name command must have an executor or subcommands and groups")

        if (name.length !in 1..32)
            error("The $name SlashCommand has a name that exceeds the ranger")

        if (description.length !in 1..100)
            error("The $name SlashCommand has a description that exceeds the ranger")

        validateLocales(nameLocalizations?.plus(descriptionLocalizations ?: emptyMap())?.toMutableMap(), name)
    }

    fun build(): RootSlashCommandData<T> = RootSlashCommandData(
        name,
        nameLocalizations,
        description,
        descriptionLocalizations,
        defaultMemberPermissions,
        dmPermission,
        arguments,
        executor,
        subCommands,
        groups
    )
}

class GroupCommandBuilder(private val name: String, private val description: String) {
    var nameLocalizations: MutableMap<Locale, String>? = null
    var descriptionLocalizations: MutableMap<Locale, String>? = null

    var subCommands: MutableList<SubCommandData<out SlashCommandArguments>> = mutableListOf()

    inline fun subcommand(name: String, description: String, block: SubCommandBuilder<*>.() -> Unit) {
        val builder = SubCommandBuilder<SlashCommandArguments>(name, description, null).apply(block)
        builder.validate()

        subCommands.add(builder.build())
    }

    inline fun <R : SlashCommandArguments> subcommand(
        name: String,
        description: String,
        noinline arguments: () -> R,
        block: SubCommandBuilder<R>.() -> Unit
    ) {
        val builder = SubCommandBuilder(name, description, arguments).apply(block)
        builder.validate()

        subCommands.add(builder.build())
    }

    fun validate() {
        if (subCommands.isEmpty())
            error("The $name command group needs at least one subcommand")

        if (name.length !in 1..32)
            error("The $name GroupCommand has a name that exceeds the ranger")

        if (description.length !in 1..100)
            error("The $name GroupCommand has a description that exceeds the ranger")

        validateLocales(nameLocalizations?.plus(descriptionLocalizations ?: emptyMap())?.toMutableMap(), name)
    }

    fun build(): GroupCommandData = GroupCommandData(
        name,
        nameLocalizations,
        description,
        descriptionLocalizations,
        subCommands
    )
}

class SubCommandBuilder<R : SlashCommandArguments>(
    private val name: String,
    private val description: String,
    private val arguments: (() -> R)?
) {
    var nameLocalizations: MutableMap<Locale, String>? = null
    var descriptionLocalizations: MutableMap<Locale, String>? = null

    private lateinit var executor: suspend (SlashCommandContext<R>).() -> Unit

    fun execute(block: suspend (SlashCommandContext<R>).() -> Unit) {
        executor = block
    }

    fun validate() {
        if (!::executor.isInitialized)
            error("The $name SubCommand needs an executor")

        if (name.length !in 1..32)
            error("The $name SubCommand has a name that exceeds the ranger")

        if (description.length !in 1..100)
            error("The $name SubCommand has a description that exceeds the ranger")

        validateLocales(nameLocalizations?.plus(descriptionLocalizations ?: emptyMap())?.toMutableMap(), name)
    }

    fun build(): SubCommandData<R> = SubCommandData(
        name,
        nameLocalizations,
        description,
        descriptionLocalizations,
        arguments,
        executor
    )
}