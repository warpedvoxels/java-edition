package org.hexalite.network.kraken.gameplay.feature.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent

sealed class CustomItemModifier {
    data class Name(val component: TranslatableComponent): CustomItemModifier()

    data class Lore(val components: List<Component>): CustomItemModifier()

    data class AttackSpeed(val value: Double): CustomItemModifier()

    data class AttackDamage(val value: Double): CustomItemModifier()

    data class AttackKnockback(val amount: Double): CustomItemModifier()

    data class Armor(val points: Double): CustomItemModifier()

    data class KnockbackResistance(val amount: Double): CustomItemModifier()
}