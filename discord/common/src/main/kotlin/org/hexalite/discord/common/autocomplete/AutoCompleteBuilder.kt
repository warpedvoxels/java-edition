package org.hexalite.discord.common.autocomplete

class AutoCompleteBuilder<T>(var executor: (suspend (AutoCompleteContext).() -> Map<String, T>)? = null) {

    // Map<String seen by the user, Argument value>
    fun execute(block: suspend (AutoCompleteContext).() -> Map<String, T>) {
        executor = block
    }

    fun validate() {
        if(executor == null)
            error("AutoComplete needs an executor")
    }
}