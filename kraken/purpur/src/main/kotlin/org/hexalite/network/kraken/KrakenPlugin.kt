package org.hexalite.network.kraken

import com.github.ajalt.mordant.rendering.TextColors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.hexalite.network.kraken.command.dsl.CommandRegisteringScope
import org.hexalite.network.kraken.configuration.KrakenConfig
import org.hexalite.network.kraken.coroutines.EventFlowDescription
import org.hexalite.network.kraken.coroutines.createEventFlow
import org.hexalite.network.kraken.extension.unaryPlus
import org.hexalite.network.kraken.gameplay.feature.GameplayFeature
import org.hexalite.network.kraken.gameplay.feature.GameplayFeatureDsl
import org.hexalite.network.kraken.gameplay.feature.GameplayFeatureView
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockAdapter
import org.hexalite.network.kraken.gameplay.feature.item.CustomItemAdapter
import org.hexalite.network.kraken.logging.BasicLogger
import org.hexalite.network.kraken.logging.info
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

abstract class KrakenPlugin(open val namespace: String) : JavaPlugin() {
    val commands = CommandRegisteringScope(this)

    /**
     * The default [KrakenConfig] for this plugin. It can be (de)serialized using kotlinx.serialization
     * and the KAML plug-in.
     */
    val conf = KrakenConfig()

    inline val log: BasicLogger
        get() = BasicLogger(
            namespace.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
            conf.logging
        )

    /**
     * Returns a view for all custom gameplay features.
     */
    val features by lazy {
        GameplayFeatureView(this)
    }

    /**
     * Returns a lazy concurrent set of all event flow descriptions.
     */
    val descriptions by lazy { ConcurrentHashMap.newKeySet<EventFlowDescription<*>>() }

    /**
     * Register a new event flow description.
     * @param type The type of the events to be listened to.
     * @param priority The priority of the event listening.
     * @param ignoreCancelled Whether to ignore cancelled events.
     * @param for For which player this event flow will be assigned.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Event> events(
        type: KClass<T>,
        `for`: UUID? = null,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true
    ): Flow<T> {
        val flow =
            descriptions.firstOrNull { it.type == type && it.`for` == `for` && it.priority == priority && it.ignoreCancelled == ignoreCancelled }
        if (flow != null) {
            return (flow as EventFlowDescription<T>).flow
        }
        val description = createEventFlow(type, `for`, priority, ignoreCancelled, onClose = descriptions::remove)
        descriptions.add(description)
        return description.flow
    }

    /**
     * Register a new event flow description.
     * @param T The type of the events to be listened to.
     * @param priority The priority of the event listening.
     * @param ignoreCancelled Whether to ignore cancelled events.
     * @param for For which player this event flow will be assigned.
     */
    inline fun <reified T : Event> events(
        `for`: UUID? = null,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true
    ): Flow<T> =
        events(T::class, `for`, priority, ignoreCancelled)

    /**
     * Register custom gameplay features for this [KrakenPlugin].
     * @param block The block of code to be executed in the [GameplayFeatureView] scope.
     */
    @OptIn(ExperimentalContracts::class)
    @GameplayFeatureDsl
    inline fun features(block: GameplayFeatureView.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        if (HandlerList.getRegisteredListeners(this).none { it.listener is CustomItemAdapter }) {
            + features.itemAdapter
        }
        if (HandlerList.getRegisteredListeners(this).none { it.listener is CustomBlockAdapter }) {
            + features.blockAdapter
        }
        features.block()
    }

    /**
     * A Java-friendly version of the other features function.
     *
     * Register custom gameplay features for this [KrakenPlugin].
     * @param features The list of features to be registered.
     */
    @JvmName("withFeatures")
    fun features(vararg features: GameplayFeature) {
        features {
            features.forEach {
                + it
            }
        }
    }

    /**
     * A list of jobs that should be cancelled when the plugin is disabled.
     * Mainly used for coroutines launched in this framework.
     */
    val activeJobs = ConcurrentLinkedQueue<Job>()

    /**
     * A coroutine scope mainly used for coroutines launched in this framework.
     */
    val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Executed when the plugin is enabled.
    protected open fun up() {
        log.info { "All systems in this module have been ${TextColors.brightGreen("enabled")}." }
    }

    // Executed when the plugin is disabled.
    protected open fun down() {
        log.info { "All systems in this module have been ${TextColors.brightRed("disabled")}." }
    }

    /**
     * Make sure that everything is fine before enabling the plugin, then run the [up]
     * function.
     */
    final override fun onEnable() {
        //adventure = BukkitAudiences.create(this)
        up()
    }

    /**
     * Make sure that everything is fine before disabling the plugin, then run the [down]
     * function.
     */
    final override fun onDisable() {
        for (job in activeJobs) {
            job.cancel()
        }
        down()
        // Cancel just after the [down] function is called to prevent any unexpected behaviour.
        coroutineScope.cancel()
        //adventure.close()
    }
}
