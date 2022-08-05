package org.hexalite.network.kraken.scoreboard

import net.kyori.adventure.text.Component
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.bukkit.BukkitDslMarker
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class ScoreboardBuilder(val plugin: KrakenPlugin, val id: String) {
    var title: ScoreboardEntry = { emptyList() }
    val entries: MutableList<ScoreboardEntry> = mutableListOf()

    @BukkitDslMarker
    fun title(builder: ScoreboardEntry) {
        title = builder
    }

    @JvmName("stringTitle")
    fun title(list: List<String>) = with(list.map(Component::text)) {
        title {
            this@with
        }
    }

    fun title(list: List<Component>) = title {
        list
    }


    @BukkitDslMarker
    fun entry(builder: ScoreboardEntry) {
        entries.add(builder)
    }

    @JvmName("stringEntry")
    fun entry(list: List<String>) = with(list.map(Component::text)) {
        title {
            this@with
        }
    }
    fun entry(list: List<Component>) = entry {
        list
    }

    fun build(): KrakenScoreboard = KrakenScoreboard(
        id = id,
        title = title,
        entries = entries,
        plugin = plugin
    )
}

@OptIn(ExperimentalContracts::class)
inline fun KrakenPlugin.scoreboard(id: String, scoreboardBuilder: ScoreboardBuilder.() -> Unit): KrakenScoreboard {
    contract {
        callsInPlace(scoreboardBuilder, InvocationKind.EXACTLY_ONCE)
    }
    return ScoreboardBuilder(this, id).apply(scoreboardBuilder).build()
}
