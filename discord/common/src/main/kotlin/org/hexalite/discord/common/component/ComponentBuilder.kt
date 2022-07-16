package org.hexalite.discord.common.component

interface ComponentBuilder {
    var disabled: Boolean?

    fun build(): MessageComponentData
}