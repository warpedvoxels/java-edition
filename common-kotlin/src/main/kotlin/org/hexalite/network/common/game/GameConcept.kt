package org.hexalite.network.common.game

interface GameConcept {
    val name: String
}

abstract class AbstractGameConcept(override val name: String): GameConcept
