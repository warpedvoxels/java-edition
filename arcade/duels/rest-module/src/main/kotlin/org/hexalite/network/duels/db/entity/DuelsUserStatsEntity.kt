package org.hexalite.network.duels.db.entity

import org.hexalite.network.duels.db.table.DuelsUserStats
import org.hexalite.network.duels.rest.entity.RestDuelsUserStats
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import java.util.*

class DuelsUserStatsEntity(id: EntityID<UUID>): UUIDEntity(id) {
    var wins by DuelsUserStats.wins
    var losses by DuelsUserStats.losses
    var draws by DuelsUserStats.draws
    var winStreak by DuelsUserStats.winStreak

    companion object: UUIDEntityClass<DuelsUserStatsEntity>(DuelsUserStats) {
        fun new(userId: UUID) = new(userId) {}
    }
}

inline fun DuelsUserStatsEntity.rest(): RestDuelsUserStats = RestDuelsUserStats(
    wins = wins,
    losses = losses,
    draws = draws,
    winStreak = winStreak
)

inline fun SizedIterable<DuelsUserStatsEntity>.rest(): List<RestDuelsUserStats> = map(DuelsUserStatsEntity::rest)
