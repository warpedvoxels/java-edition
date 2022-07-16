package org.hexalite.discord.common.component.selectmenu

import dev.kord.rest.builder.component.SelectOptionBuilder
import org.hexalite.discord.common.component.ComponentBuilder
import org.hexalite.discord.common.component.ComponentContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class SelectMenuComponentBuilder(private val customId: String) : ComponentBuilder {
    var allowedValues: ClosedRange<Int> = 1..1
    var placeholder: String? = null
    val options: MutableList<SelectOptionBuilder> = mutableListOf()
    override var disabled: Boolean? = null

    private lateinit var executor: suspend (SelectMenuContext).() -> Unit

    @OptIn(ExperimentalContracts::class)
    inline fun option(label: String, value: String, builder: SelectOptionBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        options.add(SelectOptionBuilder(label = label, value = value).apply(builder))
    }

    fun onSelect(block: suspend (SelectMenuContext).() -> Unit) {
        executor = block
    }

    fun validate() {
        if (options.isEmpty())
            error("The $customId SelectMenu needs registered options to be sent ")
    }

    @Suppress("UNCHECKED_CAST")
    override fun build(): SelectMenuComponentData = SelectMenuComponentData(
        customId,
        executor as suspend (ComponentContext).() -> Unit
    )
}