package org.hexalite.discord.common.command.slash.options

import dev.kord.common.Locale
import dev.kord.common.entity.ChannelType
import org.hexalite.discord.common.autocomplete.AutoCompleteBuilder
import org.hexalite.discord.common.autocomplete.AutoCompleteContext
import org.hexalite.discord.common.utils.validateLocales

interface SlashCommandOptionBuilder {
    // The value is passed as a class parameter, but is here to improve the validation system
    val name: String

    var nameLocalizations: MutableMap<Locale, String>?
    var descriptionLocalizations: MutableMap<Locale, String>?
    var default: Boolean?

    fun validate() {
        validateLocales(nameLocalizations?.plus(descriptionLocalizations ?: emptyMap())?.toMutableMap(), name)
    }
}

interface ChoiceableCommandOptionBuilder<T> : SlashCommandOptionBuilder {
    var choices: MutableList<SlashCommandChoice<T>>?
    var autocomplete: (suspend (AutoCompleteContext).() -> Map<String, T>)?

    fun choice(name: String, value: T, block: CommandChoiceBuilder.() -> (Unit) = {}) {
        val choice = SlashCommandChoice(name, value).apply(block)
        choice.validate()

        if (choices == null)
            choices = mutableListOf()
        choices?.add(choice)
    }

    fun autoComplete(block: AutoCompleteBuilder<T>.() -> Unit) {
        val builder = AutoCompleteBuilder<T>().apply(block)
        builder.validate()
        autocomplete = builder.executor
    }

    override fun validate() {
        super.validate()

        if (choices != null && autocomplete != null)
            error("You cannot register an executor because the $name option has choices")

        if (choices != null && choices!!.size > 25)
            error("Too many choices in $name option")
    }
}

interface NumericCommandOptionBuilder<T : Any> : ChoiceableCommandOptionBuilder<T> {
    var minValue: T?
    var maxValue: T?
}

interface IntegerCommandOptionBuilder : NumericCommandOptionBuilder<Long>

interface NumberCommandOptionBuilder : NumericCommandOptionBuilder<Double>

interface CommandChoiceBuilder {
    val name: String

    var nameLocalizations: MutableMap<Locale, String>?

    fun validate() {
        validateLocales(nameLocalizations, name)
    }
}

interface StringCommandOptionBuilder : ChoiceableCommandOptionBuilder<String> {
    var minLength: Int?
    var maxLength: Int?

    override fun validate() {
        super.validate()

        if (minLength != null && minLength!! < 0)
            error("The minimum string length in $name option must be greater than 0")

        if (maxLength != null && maxLength!! < 1)
            error("The minimum string length in $name option must be greater than 1")
    }
}

interface BooleanCommandOptionBuilder : SlashCommandOptionBuilder

interface UserCommandOptionBuilder : SlashCommandOptionBuilder

interface RoleCommandOptionBuilder : SlashCommandOptionBuilder

interface ChannelCommandOptionBuilder : SlashCommandOptionBuilder {
    var channelTypes: List<ChannelType>?
}

interface MentionableCommandOptionBuilder : SlashCommandOptionBuilder

interface AttachmentCommandOptionBuilder : SlashCommandOptionBuilder