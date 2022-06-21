package org.hexalite.network.kraken.examples.java;

import org.hexalite.network.kraken.extension.ToolLevel;
import org.hexalite.network.kraken.extension.ToolType;
import org.hexalite.network.kraken.gameplay.feature.block.CustomBlockFeature;
import org.hexalite.network.kraken.gameplay.feature.item.CustomItemFeature;

public class PlaceholderBlockFeature extends CustomBlockFeature {
    public static int ID = 1;

    static PlaceholderBlockFeature BLOCK = new PlaceholderBlockFeature();
    static PlaceholderBlockFeature.Item ITEM = new PlaceholderBlockFeature.Item();

    PlaceholderBlockFeature() {
        super(ID);
        withHardness(20, ToolLevel.Iron, ToolType.Pickaxe);
    }

    public static class Item extends CustomItemFeature {
        Item() {
            super(ID);
            withLocalizedName("block.hexalite.placeholder");
            withAttack(1.0, 7.0);
        }
    }

    public static PlaceholderBlockFeature block() {
        return BLOCK;
    }

    public static PlaceholderBlockFeature.Item item() {
        return ITEM;
    }
}