package org.hexalite.network.kraken.scoreboard

import io.papermc.paper.adventure.PaperAdventure
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.TickerMode
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.translation.GlobalTranslator
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket
import net.minecraft.network.protocol.game.ClientboundSetScorePacket
import net.minecraft.server.ServerScoreboard
import net.minecraft.world.scores.Objective
import net.minecraft.world.scores.Scoreboard
import net.minecraft.world.scores.criteria.ObjectiveCriteria
import org.bukkit.entity.Player
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.collections.onlinePlayersMapOf
import org.hexalite.network.kraken.pipeline.packet.sendPacket
import kotlin.time.Duration

typealias ScoreboardEntry = Entry.(player: Player) -> Unit

@JvmInline
value class Entry(val components: MutableList<Component> = mutableListOf()) {
    operator fun Any.unaryPlus() {
        components.add(Component.text(toString()))
    }

    operator fun Component.unaryPlus() {
        components.add(this)
    }
}

data class KrakenScoreboard(
    val id: String, var title: ScoreboardEntry, var entries: List<ScoreboardEntry>, val plugin: KrakenPlugin
) {
    private data class IndividualMetadata(var cursor: Int, var lastEntriesCount: Int, var maxCursorWidth: Int)

    private val consumers = plugin.onlinePlayersMapOf<IndividualMetadata> { player, _ ->
        val objective = scoreboard.getObjective(player.scoreboardId)
        if (objective != null) {
            scoreboard.removeObjective(objective)
        }
    }
    private val scoreboard = Scoreboard()

    @Suppress("NOTHING_TO_INLINE")
    private inline fun IndividualMetadata.updateWidth(string: String) = also {
        if (string.length > maxCursorWidth) {
            maxCursorWidth = string.length
        }
    }

    private inline val Player.scoreboardId: String
        get() = "$id-" + (uniqueId.toString().substringBefore('-'))

    /**
     * Calls [tickIndividually] to all online consumers of this
     * scoreboard
     *
     * @see tickIndividually to tick/update the scoreboard for a single player
     */
    fun tick() = consumers.keys.forEach(this::tickIndividually)

    /**
     * Updates the scoreboard for a single player. If [player] is not yet a consumer of
     * this scoreboard, they'll turn into one and keep up with the following ticks/updates
     * of this scoreboard.
     *
     * @see tick to tick/update the scoreboard for all consumers at the same time
     */
    fun tickIndividually(player: Player) {
        val metadata = consumers[player] ?: consumers.putIfAbsent(player, IndividualMetadata(0, 0, 0))
        val cursor = metadata?.cursor ?: 0
        val title = Entry().apply { title(player) }.components
        val titleFrame = PaperAdventure.asVanilla(title[cursor.coerceAtMost(title.size - 1)])
        val objective: Objective = scoreboard.getObjective(player.scoreboardId) ?: scoreboard.addObjective(
            player.scoreboardId, ObjectiveCriteria.DUMMY, titleFrame, ObjectiveCriteria.RenderType.INTEGER
        )
        objective.setDisplayName(titleFrame)

        if (metadata == null || metadata.lastEntriesCount != entries.size) {
            player.sendPacket(ClientboundSetObjectivePacket(objective, 1)) // remove objective
            player.sendPacket(ClientboundSetObjectivePacket(objective, 0)) // create objective
            player.sendPacket(ClientboundSetDisplayObjectivePacket(1, objective)) // display objective
        }

        if (entries.isNotEmpty()) {
            for (index in entries.lastIndex downTo 0) {
                val entry = Entry().apply { entries[index](player) }.components
                val frame = GlobalTranslator.render(entry[cursor.coerceAtMost(entry.size - 1)], player.locale())
                val serialized = LegacyComponentSerializer.legacySection().serialize(frame)
                val score = ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, objective.name, serialized, index)
                metadata?.updateWidth(serialized)
                player.sendPacket(score)
            }
        }
        val newCursor = cursor + 1
        if (metadata != null && newCursor < metadata.maxCursorWidth) {
            metadata.cursor = newCursor
        } else {
            metadata?.cursor = 0
        }
    }

    fun close(player: Player) {
        if (consumers.remove(player) != null) {
            val objective = scoreboard.getObjective(player.scoreboardId) ?: return
            player.sendPacket(ClientboundSetObjectivePacket(objective, 1))
        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun enableConstantTicking(
        duration: Duration,
        initialDelay: Duration = duration,
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        mode: TickerMode = TickerMode.FIXED_PERIOD
    ): Job {
        val ticker = ticker(
            delayMillis = duration.inWholeMilliseconds,
            initialDelayMillis = initialDelay.inWholeMilliseconds,
            context = scope.coroutineContext,
            mode = mode
        )
        return scope.launch {
            ticker.consumeEach {
                tick()
            }
        }
    }
}