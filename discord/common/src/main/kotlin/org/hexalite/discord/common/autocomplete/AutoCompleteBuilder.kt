package org.hexalite.discord.common.autocomplete

class AutoCompleteBuilder<T> {
    lateinit var executor: suspend (AutoCompleteContext).() -> Map<String, T>

    // Map<String seen by the user, Argument value>
    fun execute(block: suspend (AutoCompleteContext).() -> Map<String, T>) {
        executor = block
    }

    fun validate() {
        if(!::executor.isInitialized)
            error("AutoComplete needs an executor")
    }
}