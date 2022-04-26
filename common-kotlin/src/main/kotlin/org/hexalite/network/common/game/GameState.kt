package org.hexalite.network.common.game

interface GameState

sealed interface GenericGameState : GameState {
    object AwaitingPlayers : GenericGameState

    object Finished : GenericGameState
}