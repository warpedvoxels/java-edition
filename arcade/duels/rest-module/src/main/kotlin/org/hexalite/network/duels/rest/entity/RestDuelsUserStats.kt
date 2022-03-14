package org.hexalite.network.duels.rest.entity

import kotlinx.serialization.SerialName
import org.hexalite.network.common.rest.entity.RestEntity

@kotlinx.serialization.Serializable
data class RestDuelsUserStats(
    val wins: Int,
    val losses: Int,
    val draws: Int,
    @SerialName("win_streak")
    val winStreak: Int,
): RestEntity
