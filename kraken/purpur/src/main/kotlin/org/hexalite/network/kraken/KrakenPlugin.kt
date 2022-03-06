package org.hexalite.network.kraken

import com.github.ajalt.mordant.rendering.TextColors
import kotlinx.coroutines.*
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.hexalite.network.kraken.configuration.KrakenConfig
import org.hexalite.network.kraken.extension.unaryPlus
import org.hexalite.network.kraken.gameplay.feature.GameplayFeatureDsl
import org.hexalite.network.kraken.gameplay.feature.GameplayFeatureView
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockAdapter
import org.hexalite.network.kraken.gameplay.feature.item.CustomItemAdapter
import org.hexalite.network.kraken.logging.BasicLogger
import org.hexalite.network.kraken.logging.info
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class KrakenPlugin(open val namespace: String): JavaPlugin() {
    /**
     * The default [KrakenConfig] for this plugin. It can be (de)serialized using kotlinx.serialization
     * and the KAML plug-in.
     */
    val conf = KrakenConfig()

    inline val log: BasicLogger
        get() = BasicLogger { conf.logging }

    val features by lazy {
        GameplayFeatureView(this)
    }

    @OptIn(ExperimentalContracts::class)
    @GameplayFeatureDsl
    inline fun features(block: GameplayFeatureView.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        if (HandlerList.getRegisteredListeners(this).none { it.listener is CustomItemAdapter }) {
            +features.itemAdapter
        }
        if (HandlerList.getRegisteredListeners(this).none { it.listener is CustomBlockAdapter }) {
            +features.blockAdapter
        }
        features.block()
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

//    lateinit var adventure: BukkitAudiences
//        private set

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
