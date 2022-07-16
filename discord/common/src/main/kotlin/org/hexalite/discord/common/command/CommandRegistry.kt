package org.hexalite.discord.common.command

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.*
import dev.kord.rest.builder.interaction.*
import dev.kord.rest.builder.interaction.GroupCommandBuilder
import dev.kord.rest.builder.interaction.SubCommandBuilder
import org.hexalite.discord.common.DiscordCommonData
import org.hexalite.discord.common.command.message.MessageCommandContext
import org.hexalite.discord.common.command.message.MessageCommandData
import org.hexalite.discord.common.command.slash.*
import org.hexalite.discord.common.command.slash.options.*
import org.hexalite.discord.common.command.user.UserCommandContext
import org.hexalite.discord.common.command.user.UserCommandData

interface CommandRegistry {
    val kord: Kord
    val hexalite: DiscordCommonData
    val commands: MutableList<ApplicationCommandData>

    suspend fun registerDiscordCommands() {
        val commandsToRegister = commands.toMutableList()

        if (hexalite.settings.guildIds == null) {
            kord.createGlobalApplicationCommands {
                registerDiscordCommands(commandsToRegister)
            }
        } else {
            hexalite.settings.guildIds!!.forEach {
                kord.createGuildApplicationCommands(Snowflake(it)) {
                    registerDiscordCommands(commandsToRegister)
                }
            }
        }
    }

    fun findCommand(interaction: ApplicationCommandInteraction): ApplicationCommandData {
        val rootCommand = commands.find { it.name == interaction.invokedCommandName }
            ?: error("The ${interaction.invokedCommandName} command was not found")

        return when (interaction) {
            is ChatInputCommandInteraction -> {
                findCommand(interaction.command, rootCommand as RootSlashCommandData<*>)
            }

            is UserCommandInteraction, is MessageCommandInteraction -> {
                // MessageCommand and UserCommand have no groups or subcommands
                rootCommand
            }
        }
    }

    fun findCommand(
        interaction: InteractionCommand,
        rootCommand: RootSlashCommandData<*>
    ): CommandWithArguments<out SlashCommandArguments> {
        return when (interaction) {
            is RootCommand -> rootCommand
            is GroupCommand -> {
                (rootCommand.groups?.find { it.name == interaction.groupName }
                    ?: error("The ${interaction.groupName} command group was not found"))
                    .subCommands.find { it.name == interaction.name }
                    ?: error("The ${interaction.name} subcommand was not found")
            }
            is SubCommand -> {
                rootCommand.subCommands?.find { it.name == interaction.name }
                    ?: error("The ${interaction.name} subcommand was not found")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun executeCommand(command: ApplicationCommandData, interaction: ApplicationCommandInteraction) {
        when (interaction) {
            is ChatInputCommandInteraction -> {
                command as CommandWithArguments<SlashCommandArguments>

                val context = SlashCommandContext<SlashCommandArguments>(interaction, hexalite)
                val args = command.arguments?.invoke()

                if (args != null)
                    context.populateArguments(args)

                command.executor?.invoke(context)
            }
            is MessageCommandInteraction -> {
                command as MessageCommandData

                val context = MessageCommandContext(interaction, hexalite)

                command.executor.invoke(context)
            }
            is UserCommandInteraction -> {
                command as UserCommandData

                val context = UserCommandContext(interaction, hexalite)

                command.executor.invoke(context)
            }
        }
    }

    fun register(command: ApplicationCommandData) {
        if (commands.any { it.name == command.name })
            error("Duplicate command: ${command.name}")

        commands.add(command)
    }

    private tailrec fun MultiApplicationCommandBuilder.registerDiscordCommands(
        toRegister: MutableList<ApplicationCommandData>,
        options: MutableList<OptionsBuilder>? = null
    ) {
        val command = toRegister.firstOrNull() ?: return
        toRegister.remove(command)

        when (command) {
            is RootSlashCommandData<*> -> {
                lateinit var builder: ChatInputCreateBuilder
                input(command.name, command.description) {
                    nameLocalizations = command.nameLocalizations?.toMutableMap()
                    descriptionLocalizations = command.descriptionLocalizations?.toMutableMap()
                    defaultMemberPermissions = command.defaultMemberPermissions

                    if (this@registerDiscordCommands is GlobalMultiApplicationCommandBuilder)
                        (this as GlobalChatInputCreateBuilder).dmPermission = command.dmPermission

                    this.options = mutableListOf()

                    builder = this
                }
                val args = command.arguments?.invoke()?.options

                if (args?.isNotEmpty() == true)
                    builder.registerDiscordOptions(args)

                if (command.subCommands != null || command.groups != null) {
                    return this@registerDiscordCommands.registerDiscordCommands(
                        ((command.subCommands ?: emptyList()).plus(command.groups ?: emptyList())
                            .plus(toRegister)).toMutableList(),
                        builder.options
                    )
                }
            }
            is GroupCommandData -> {
                val groupData = GroupCommandBuilder(command.name, command.description).apply {
                    nameLocalizations = command.nameLocalizations
                    descriptionLocalizations = command.descriptionLocalizations
                    this.options = mutableListOf()
                }

                options?.add(groupData)
                return registerDiscordCommands(
                    (command.subCommands + toRegister).toMutableList(),
                    groupData.options
                )
            }
            is SubCommandData<*> -> {
                val subCommandData = SubCommandBuilder(command.name, command.description).apply {
                    nameLocalizations = command.nameLocalizations
                    descriptionLocalizations = command.descriptionLocalizations
                    this.options = mutableListOf()
                }
                val args = command.arguments?.invoke()?.options

                if (args?.isNotEmpty() == true)
                    subCommandData.registerDiscordOptions(args)

                options?.add(subCommandData)
            }
            is MessageCommandData -> {
                message(command.name) {
                    nameLocalizations = command.nameLocalizations
                    defaultMemberPermissions = command.defaultMemberPermissions
                    if (this@registerDiscordCommands is GlobalMultiApplicationCommandBuilder)
                        (this as GlobalMessageCommandCreateBuilder).dmPermission = command.dmPermission
                }
            }
            is UserCommandData -> {
                user(command.name) {
                    nameLocalizations = command.nameLocalizations
                    defaultMemberPermissions = command.defaultMemberPermissions
                    if (this@registerDiscordCommands is GlobalMultiApplicationCommandBuilder)
                        (this as GlobalUserCommandCreateBuilder).dmPermission = command.dmPermission
                }
            }
        }

        return registerDiscordCommands(toRegister, options)
    }

    private fun BaseInputChatBuilder.registerDiscordOptions(options: List<SlashCommandOption<*>>) {
        options.forEach {
            val name = it.name
            val description = it.description

            when (it) {
                is StringCommandOption -> {
                    string(name, description) {
                        baseBuilder(it)

                        autocomplete = it.autocomplete != null
                        minLength = it.minLength
                        maxLength = it.maxLength

                        it.choices?.forEach { choice ->
                            choice(choice.name, choice.value) {
                                nameLocalizations = choice.nameLocalizations
                            }
                        }
                    }
                }
                is IntegerCommandOption -> {
                    int(name, description) {
                        baseBuilder(it)

                        autocomplete = it.autocomplete != null
                        minValue = it.minValue
                        maxValue = it.maxValue

                        it.choices?.forEach { choice ->
                            choice(choice.name, choice.value) {
                                nameLocalizations = choice.nameLocalizations
                            }
                        }
                    }
                }
                is NumberCommandOption -> {
                    number(name, description) {
                        baseBuilder(it)

                        autocomplete = it.autocomplete != null
                        minValue = it.minValue
                        maxValue = it.maxValue

                        it.choices?.forEach { choice ->
                            choice(choice.name, choice.value) {
                                nameLocalizations = choice.nameLocalizations
                            }
                        }
                    }
                }
                is BooleanCommandOption -> {
                    boolean(name, description) {
                        baseBuilder(it)
                    }
                }
                is UserCommandOption -> {
                    user(name, description) {
                        baseBuilder(it)
                    }
                }
                is RoleCommandOption -> {
                    role(name, description) {
                        baseBuilder(it)
                    }
                }
                is ChannelCommandOption -> {
                    channel(name, description) {
                        baseBuilder(it)

                        channelTypes = it.channelTypes
                    }
                }
                is MentionableCommandOption -> {
                    mentionable(name, description) {
                        baseBuilder(it)
                    }
                }
                is AttachmentCommandOption -> {
                    attachment(name, description) {
                        baseBuilder(it)
                    }
                }
            }
        }
    }

    private fun OptionsBuilder.baseBuilder(option: SlashCommandOption<*>) {
        required = option.required
        default = option.default
        nameLocalizations = option.nameLocalizations
        descriptionLocalizations = option.descriptionLocalizations
    }
}