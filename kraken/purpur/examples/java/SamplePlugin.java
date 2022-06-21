package org.hexalite.network.kraken.examples.java;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.hexalite.network.kraken.KrakenPlugin;
import org.hexalite.network.kraken.pipeline.packet.PacketPipeline;

public class SamplePlugin extends KrakenPlugin implements Listener {
    public SamplePlugin() {
        super("sample");
    }

    @Override
    protected void up() {
        PacketPipeline.setup(this);
        withFeatures(PlaceholderBlockFeature.block(), PlaceholderBlockFeature.item());
    }

    @EventHandler
    void readJoinEvent(PlayerJoinEvent event) {
        event.getPlayer().getInventory().addItem(PlaceholderBlockFeature.item().asItemStack(getFeatures().getId()));
    }
}
