package org.hexalite.discord.common.autocomplete

import dev.kord.core.behavior.interaction.suggestInt
import dev.kord.core.behavior.interaction.suggestNumber
import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import org.hexalite.discord.common.command.CommandRegistry
import org.hexalite.discord.common.command.slash.CommandWithArguments
import org.hexalite.discord.common.command.slash.RootSlashCommandData
import org.hexalite.discord.common.command.slash.options.*

interface AutoCompleteRegistry : CommandRegistry {
    fun findAutoComplete(interaction: AutoCompleteInteraction): CommandWithArguments<out SlashCommandArguments> {
        // Find the command to then find the autocomplete option
        val rootCommand = commands.find { it.name == interaction.command.rootName }
            ?: error("The ${interaction.command.rootName} command was not found")

        return findCommand(interaction.command, rootCommand as RootSlashCommandData<*>)
    }

    suspend fun executeAutoComplete(
        command: CommandWithArguments<out SlashCommandArguments>,
        interaction: AutoCompleteInteraction
    ) {
        val args = command.arguments?.invoke() ?: error("The ${command.name} command has no options, bug?")
        // The option in focus is the one we need
        val interactionArgument = interaction.command.options.entries.find { it.value.focused }!!
        val argument = (args.options.find { it.name == interactionArgument.key }
            ?: error("The autocomplete ${interactionArgument.key} option was not found, bug?"))
                as ChoiceableCommandOption<*, *>

        val context = AutoCompleteContext(interaction, hexalite)
        val autocompleteResult = argument.autocomplete!!.invoke(context)

        when (argument) {
            is StringCommandOption -> {
                interaction.suggestString {
                    for ((name, value) in autocompleteResult) {
                        choice(name, value as String)
                    }
                }
            }
            is NumberCommandOption -> {
                interaction.suggestNumber {
                    for ((name, value) in autocompleteResult) {
                        choice(name, value as Double)
                    }
                }
            }
            is IntegerCommandOption -> {
                interaction.suggestInt {
                    for ((name, value) in autocompleteResult) {
                        choice(name, value as Long)
                    }
                }
            }
        }
    }
}