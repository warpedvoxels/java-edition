package org.hexalite.network.kraken.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.plugin.Plugin
import org.slf4j.Logger

@Plugin(
    id = "kraken",
    name = "Kraken",
    authors = ["Hexalite Network Development Team"],
)
class KrakenVelocity @Inject constructor(val logger: Logger, val events: EventManager) {
    init {
    }
}