package org.hexalite.network.kraken.gameplay.feature.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.hexalite.network.kraken.gameplay.feature.GameplayFeatureView
import java.util.*

open class CustomItemFeature(
    val textureIndex: Int,
    val slot: EquipmentSlot = EquipmentSlot.HAND,
    val modifiers: MutableSet<CustomItemModifier> = mutableSetOf(),
) {
    inline fun <reified T: CustomItemModifier> modifier(value: T) {
        modifiers.removeIf { it is T }
        modifiers.add(value)
    }

    fun name(component: TranslatableComponent) = modifier(CustomItemModifier.Name(component))

    fun name(key: String) = modifier(CustomItemModifier.Name(Component.translatable(key)))

    fun attackSpeed(value: Double) = modifier(CustomItemModifier.AttackSpeed(value))

    fun attackDamage(value: Double) = modifier(CustomItemModifier.AttackDamage(value))

    fun attackKnockback(value: Double) = modifier(CustomItemModifier.AttackKnockback(value))

    fun armor(points: Double) = modifier(CustomItemModifier.Armor(points))

    fun knockbackResistance(value: Double) = modifier(CustomItemModifier.KnockbackResistance(value))

    fun attack(damage: Double) = attackDamage(damage)

    fun attack(speed: Double, damage: Double) {
        attackSpeed(speed)
        attackDamage(damage)
    }

    fun attack(speed: Double, damage: Double, knockback: Double) {
        attackSpeed(speed)
        attackDamage(damage)
        attackKnockback(knockback)
    }

    fun lore(vararg components: Component) {
        if (components.isEmpty()) {
            modifiers.removeIf { it is CustomItemModifier.Lore }
            return
        }
        modifier(CustomItemModifier.Lore(components.toList()))
    }

    fun appendLore(vararg components: Component) {
        if (components.isEmpty()) {
            return
        }
        val modifier = modifiers.firstNotNullOfOrNull { it as? CustomItemModifier.Lore? } ?: return lore(*components)
        modifier(CustomItemModifier.Lore(modifier.components + components))
    }

    inline val name get() = modifiers.firstNotNullOfOrNull { it as? CustomItemModifier.Name? }

    inline val lore get() = modifiers.firstNotNullOfOrNull { it as? CustomItemModifier.Lore? }

    inline val attackSpeed get() = modifiers.firstNotNullOfOrNull { it as? CustomItemModifier.AttackSpeed? }

    inline val attackDamage get() = modifiers.firstNotNullOfOrNull { it as? CustomItemModifier.AttackDamage? }

    inline val attackKnockback get() = modifiers.firstNotNullOfOrNull { it as? CustomItemModifier.AttackKnockback? }

    inline val armor get() = modifiers.firstNotNullOfOrNull { it as? CustomItemModifier.Armor? }

    inline val knockbackResistance get() = modifiers.firstNotNullOfOrNull { it as? CustomItemModifier.KnockbackResistance? }

    inline val customModelData: Int
        get() = textureIndex + 1000

    fun stack(namespace: NamespacedKey, amount: Int = 1, showTextureIndexInLore: Boolean = true): ItemStack {
        fun ItemMeta.setAttribute(key: Attribute, value: Double?) {
            if (value != null) {
                removeAttributeModifier(key)
                val modifier = AttributeModifier(UUID.randomUUID(), key.key.key, value, AttributeModifier.Operation.ADD_NUMBER, slot)
                addAttributeModifier(key, modifier)
            }
        }

        val scope = this
        val stack = ItemStack(Material.PAPER, amount)
        with(stack) {
            val meta = itemMeta ?: return stack
            val container = meta.persistentDataContainer
            container.set(namespace, PersistentDataType.INTEGER, textureIndex)
            meta.setCustomModelData(scope.customModelData)

            val lore = scope.lore
            if (showTextureIndexInLore) {
                val component = Component.text("§8#$textureIndex")
                if (lore == null) {
                    meta.lore(mutableListOf<Component>(component))
                } else {
                    meta.lore(lore.components + component)
                }
            } else if (lore != null) {
                meta.lore(lore.components)
            }
            val name = scope.name
            if (name != null) {
                meta.displayName(name.component)
            }
            meta.setAttribute(Attribute.GENERIC_ATTACK_DAMAGE, scope.attackDamage?.value)
            meta.setAttribute(Attribute.GENERIC_ATTACK_SPEED, scope.attackSpeed?.value)
            meta.setAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK, scope.attackKnockback?.amount)
            meta.setAttribute(Attribute.GENERIC_ARMOR, scope.armor?.points)
            meta.setAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE, scope.knockbackResistance?.amount)
            itemMeta = meta
            return stack
        }
    }
}

fun CustomItemFeature.asCustomBlockOrNull(view: GameplayFeatureView) = view.retrieveCustomBlock(textureIndex)

fun ItemStack.asCustomOrNull(view: GameplayFeatureView): CustomItemFeature? {
    val meta = itemMeta ?: return null
    val container = meta.persistentDataContainer
    val textureIndex = container.get(view.id, PersistentDataType.INTEGER) ?: return null
    return view.retrieveCustomItem(textureIndex)
}