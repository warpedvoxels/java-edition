package org.hexalite.network.kraken

import kotlinx.coroutines.*
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentLinkedQueue

abstract class KrakenPlugin(open val namespace: String) : JavaPlugin() {
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
    protected open fun up() {}

    // Executed when the plugin is disabled.
    protected open fun down() {}

    lateinit var adventure: BukkitAudiences
        private set

    /**
     * Make sure that everything is fine before enabling the plugin, then run the [up]
     * function.
     */
    final override fun onEnable() {
        adventure = BukkitAudiences.create(this)
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
        adventure.close()
    }
}
