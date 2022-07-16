package org.hexalite.discord.common.modal

import org.hexalite.discord.common.InteractionData
import org.hexalite.discord.common.modal.options.ModalArguments

data class ModalSubmitData<T : ModalArguments>(
    val customId: String,
    val arguments: () -> T,
    val executor: suspend (ModalSubmitContext<T>).() -> Unit
) : InteractionData