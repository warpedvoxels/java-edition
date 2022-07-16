@file:Suppress("UNCHECKED_CAST")
package org.hexalite.discord.common.command.slash.options

import dev.kord.common.Locale
import dev.kord.common.entity.ChannelType
import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import org.hexalite.discord.common.autocomplete.AutoCompleteContext
import kotlin.reflect.KProperty

sealed class SlashCommandOption<T : Any?>(
    override val name: String,
    val description: String
) : SlashCommandOptionBuilder {
    override var nameLocalizations: MutableMap<Locale, String>? = null
    override var descriptionLocalizations: MutableMap<Locale, String>? = null
    override var default: Boolean? = null
    var required: Boolean = true

    var parsedValue: T? = null

    operator fun getValue(thisRef: SlashCommandArguments, property: KProperty<*>): T = parsedValue as T

    open fun parse(interaction: ChatInputCommandInteraction): T {
        return interaction.command.options[name]?.value as T
    }
}

sealed class ChoiceableCommandOption<T, S : Any>(name: String, description: String) :
    SlashCommandOption<T>(name, description), ChoiceableCommandOptionBuilder<S> {
    override var autocomplete: (suspend AutoCompleteContext.() -> Map<String, S>)? = null
    override var choices: MutableList<SlashCommandChoice<S>>? = null
}

sealed class NumericCommandOption<T, S : Any>(name: String, description: String) :
    ChoiceableCommandOption<T, S>(name, description), NumericCommandOptionBuilder<S> {
    override var minValue: S? = null
    override var maxValue: S? = null
}

class StringCommandOption<T : String?>(name: String, description: String) :
    ChoiceableCommandOption<T, String>(name, description), StringCommandOptionBuilder {
    override var minLength: Int? = null
    override var maxLength: Int? = null
}

class IntegerCommandOption<T : Long?>(name: String, description: String) :
    NumericCommandOption<T, Long>(name, description), IntegerCommandOptionBuilder

class NumberCommandOption<T : Double?>(name: String, description: String) :
    NumericCommandOption<T, Double>(name, description), NumberCommandOptionBuilder

class BooleanCommandOption<T : Boolean?>(name: String, description: String) :
    SlashCommandOption<T>(name, description), BooleanCommandOptionBuilder

class UserCommandOption<T : User?>(name: String, description: String) :
    SlashCommandOption<T>(name, description), UserCommandOptionBuilder {
    override fun parse(interaction: ChatInputCommandInteraction): T {
        return interaction.command.users[name] as T
    }
}

class RoleCommandOption<T : Role?>(name: String, description: String) :
    SlashCommandOption<T>(name, description), RoleCommandOptionBuilder {
    override fun parse(interaction: ChatInputCommandInteraction): T {
        return interaction.command.roles[name] as T
    }
}

class ChannelCommandOption<T : ResolvedChannel?>(name: String, description: String) :
    SlashCommandOption<T>(name, description), ChannelCommandOptionBuilder {
    override var channelTypes: List<ChannelType>? = null

    override fun parse(interaction: ChatInputCommandInteraction): T {
        return interaction.command.channels[name] as T
    }
}

class MentionableCommandOption<T : Entity?>(
    name: String,
    description: String
) : SlashCommandOption<T>(name, description), MentionableCommandOptionBuilder {
    override fun parse(interaction: ChatInputCommandInteraction): T {
        return interaction.command.mentionables[name] as T
    }
}

class AttachmentCommandOption<T : Attachment?>(
    name: String,
    description: String
) : SlashCommandOption<T>(name, description), AttachmentCommandOptionBuilder {
    override fun parse(interaction: ChatInputCommandInteraction): T {
        return interaction.command.attachments[name] as T
    }
}