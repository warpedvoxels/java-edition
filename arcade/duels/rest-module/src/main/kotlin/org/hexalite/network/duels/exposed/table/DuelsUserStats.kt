package org.hexalite.network.duels.exposed.table

import org.jetbrains.exposed.dao.id.UUIDTable

object DuelsUserStats: UUIDTable(name = "duels_user_stats") {
    val wins = integer("wins").default(0)
    val losses = integer("losses").default(0)
    val draws = integer("draws").default(0)
    val winStreak = integer("win_streak").default(0)
}