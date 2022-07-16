@file:Suppress("UNUSED")
package org.hexalite.discord.common.command.slash.options

import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Entity
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.ResolvedChannel

open class SlashCommandArguments {
    val options: MutableList<SlashCommandOption<Any?>> = mutableListOf()
}

fun SlashCommandArguments.string(
    name: String,
    description: String,
    builder: StringCommandOptionBuilder.() -> (Unit) = {}
): StringCommandOption<String> = StringCommandOption<String>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.optionalString(
    name: String,
    description: String,
    builder: StringCommandOptionBuilder.() -> (Unit) = {}
): StringCommandOption<String?> = StringCommandOption<String?>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.integer(
    name: String,
    description: String,
    builder: IntegerCommandOptionBuilder.() -> (Unit) = {}
): IntegerCommandOption<Long> = IntegerCommandOption<Long>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.optionalInteger(
    name: String,
    description: String,
    builder: IntegerCommandOptionBuilder.() -> (Unit) = {}
): IntegerCommandOption<Long?> = IntegerCommandOption<Long?>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.number(
    name: String,
    description: String,
    builder: NumberCommandOptionBuilder.() -> (Unit) = {}
): NumberCommandOption<Double> = NumberCommandOption<Double>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.optionalNumber(
    name: String,
    description: String,
    builder: NumberCommandOptionBuilder.() -> (Unit) = {}
): NumberCommandOption<Double?> = NumberCommandOption<Double?>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.boolean(
    name: String,
    description: String,
    builder: BooleanCommandOptionBuilder.() -> (Unit) = {}
): BooleanCommandOption<Boolean> = BooleanCommandOption<Boolean>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.optionalBoolean(
    name: String,
    description: String,
    builder: BooleanCommandOptionBuilder.() -> (Unit) = {}
): BooleanCommandOption<Boolean?> = BooleanCommandOption<Boolean?>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.user(
    name: String,
    description: String,
    builder: UserCommandOptionBuilder.() -> (Unit) = {}
): UserCommandOption<User> = UserCommandOption<User>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.optionalUser(
    name: String,
    description: String,
    builder: UserCommandOptionBuilder.() -> (Unit) = {}
): UserCommandOption<User?> =
    UserCommandOption<User?>(name, description).apply(builder).also {
        register(it)
    }

fun SlashCommandArguments.role(
    name: String,
    description: String,
    builder: RoleCommandOptionBuilder.() -> (Unit) = {}
): RoleCommandOption<Role> = RoleCommandOption<Role>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.optionalRole(
    name: String,
    description: String,
    builder: RoleCommandOptionBuilder.() -> (Unit) = {}
): RoleCommandOption<Role?> = RoleCommandOption<Role?>(name, description).apply(builder).also {
    register(it)
}

fun SlashCommandArguments.channel(
    name: String,
    description: String,
    builder: ChannelCommandOptionBuilder.() -> (Unit) = {}
): ChannelCommandOption<ResolvedChannel> =
    ChannelCommandOption<ResolvedChannel>(name, description).apply(builder).also {
        register(it)
    }

fun SlashCommandArguments.optionalChannel(
    name: String,
    description: String,
    builder: ChannelCommandOptionBuilder.() -> (Unit) = {}
): ChannelCommandOption<ResolvedChannel?> =
    ChannelCommandOption<ResolvedChannel?>(name, description).apply(builder).also {
        register(it)
    }

fun SlashCommandArguments.mentionable(
    name: String,
    description: String,
    builder: MentionableCommandOptionBuilder.() -> (Unit) = {}
): MentionableCommandOption<Entity> =
    MentionableCommandOption<Entity>(name, description).apply(builder).also {
        register(it)
    }

fun SlashCommandArguments.optionalMentionable(
    name: String,
    description: String,
    builder: MentionableCommandOptionBuilder.() -> (Unit) = {}
): MentionableCommandOption<Entity?> =
    MentionableCommandOption<Entity?>(name, description).apply(builder).also {
        register(it)
    }

fun SlashCommandArguments.attachment(
    name: String,
    description: String,
    builder: AttachmentCommandOptionBuilder.() -> (Unit) = {}
): AttachmentCommandOption<Attachment> =
    AttachmentCommandOption<Attachment>(name, description).apply(builder).also {
        register(it)
    }

fun SlashCommandArguments.optionalAttachment(
    name: String,
    description: String,
    builder: AttachmentCommandOptionBuilder.() -> (Unit) = {}
): AttachmentCommandOption<Attachment?> =
    AttachmentCommandOption<Attachment?>(name, description).apply(builder).also {
        register(it)
    }

@Suppress("UNCHECKED_CAST")
private inline fun <reified T> SlashCommandArguments.register(option: SlashCommandOption<T>) {
    if (options.any { it.name == option.name })
        error("Duplicate argument \"${option.name}\"!")
    if (options.size > 25)
        error("Too many options! The maximum is 25")

    option.validate()
    option.apply {
        this.required = null !is T
    }

    options.add(option as SlashCommandOption<Any?>)
}