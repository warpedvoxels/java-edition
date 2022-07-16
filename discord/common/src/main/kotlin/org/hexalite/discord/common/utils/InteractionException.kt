package org.hexalite.discord.common.utils

import dev.kord.rest.builder.message.create.MessageCreateBuilder

class InteractionException(
    val ephemeral: Boolean,
    val isOriginalInteractionResponse: Boolean,
    val builder: MessageCreateBuilder.() -> Unit
) : Throwable()