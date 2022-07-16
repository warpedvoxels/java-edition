package org.hexalite.discord.common.modal

import org.hexalite.discord.common.modal.options.ModalArguments

class ModalSubmitBuilder<T : ModalArguments>(private val customId: String, private val arguments: () -> T) {
    private lateinit var executor: suspend (ModalSubmitContext<T>).() -> Unit

    fun onSubmit(block: suspend (ModalSubmitContext<T>).() -> Unit) {
        executor = block
    }

    fun validate() {
        if(!::executor.isInitialized)
            error("The $customId ModalSubmit needs an executor")
    }

    fun build(): ModalSubmitData<T> = ModalSubmitData(customId, arguments, executor)
}