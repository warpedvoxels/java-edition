package org.hexalite.network.duels.exposed.entity

import org.hexalite.network.duels.exposed.table.DuelsUserStats
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
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