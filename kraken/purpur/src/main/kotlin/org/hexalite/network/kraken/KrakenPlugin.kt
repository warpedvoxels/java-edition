package org.hexalite.network.kraken

import com.github.ajalt.mordant.rendering.TextColors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.hexalite.network.kraken.command.dsl.CommandRegisteringScope
import org.hexalite.network.kraken.configuration.KrakenConfig
import org.hexalite.network.kraken.coroutines.BukkitDispatcher
import org.hexalite.network.kraken.extension.unaryPlus
import org.hexalite.network.kraken.gameplay.feature.GameplayFeature
import org.hexalite.network.kraken.gameplay.feature.GameplayFeatureDsl
import org.hexalite.network.kraken.gameplay.feature.GameplayFeatureView
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockAdapter
import org.hexalite.network.kraken.gameplay.feature.datapack.DataPackFeatureAdapter
import org.hexalite.network.kraken.gameplay.feature.item.CustomItemAdapter
import org.hexalite.network.kraken.logging.BasicLogger
import org.hexalite.network.kraken.logging.info
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

abstract class KrakenPlugin(open val namespace: String) : JavaPlugin(), CoroutineScope {
    val commands = CommandRegisteringScope(this)

    // dispatchers
    val sync = BukkitDispatcher(this, false)
    val async = BukkitDispatcher(this, true)

    /**
     * The default [KrakenConfig] for this plugin. It can be (de)serialized using kotlinx.serialization
     * and the KToml add-on.
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
     * Returns a [MutableSharedFlow]-based event consumer and publisher for the Bukkit API.
     */
    val eventFlow = MutableSharedFlow<Event>()

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
        if (HandlerList.getRegisteredListeners(this).none { it.listener is DataPackFeatureAdapter }) {
            + features.dataPackAdapter
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
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default

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
        cancel()
    }
}
