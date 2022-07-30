package org.hexalite.network.kraken.scoreboard

import org.hexalite.network.kraken.KrakenPlugin
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class ScoreboardBuilder(val plugin: KrakenPlugin, val id: String) {
    var title: ScoreboardEntry = {}
    val entries: MutableList<ScoreboardEntry> = mutableListOf()

    fun title(builder: ScoreboardEntry) {
        title = builder
    }

    fun entry(builder: ScoreboardEntry) {
        entries.add(builder)
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
