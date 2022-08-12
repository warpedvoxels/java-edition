package org.hexalite.network.kraken.gameplay.feature.item

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.extension.BukkitEventListener
import org.hexalite.network.kraken.gameplay.feature.GameplayFeature
import java.util.*

open class CustomItemFeature @JvmOverloads constructor(
    val textureIndex: Int,
    val slot: EquipmentSlot = EquipmentSlot.HAND,
    val modifiers: MutableSet<CustomItemModifier> = mutableSetOf(),
): GameplayFeature {
    @JvmName("withModifier")
    inline fun <reified T: CustomItemModifier> modifier(value: T) {
        modifiers.removeIf { it is T }
        modifiers.add(value)
    }

    @JvmName("withName")
    fun name(component: Component) = apply { modifier(CustomItemModifier.Name(component)) }

    @JvmName("withLocalizedName")
    fun localizedName(key: String) = apply { modifier(CustomItemModifier.Name(Component.translatable(key))) }

    @JvmName("withAttackSpeed")
    fun attackSpeed(value: Double) = apply { modifier(CustomItemModifier.AttackSpeed(value)) }

    @JvmName("withAttackDamage")
    fun attackDamage(value: Double) = apply { modifier(CustomItemModifier.AttackDamage(value)) }

    @JvmName("withAttackKnockback")
    fun attackKnockback(value: Double) = apply { modifier(CustomItemModifier.AttackKnockback(value)) }

    @JvmName("withArmor")
    fun armor(points: Double) = apply {  modifier(CustomItemModifier.Armor(points)) }

    @JvmName("withKnockbackResistance")
    fun knockbackResistance(value: Double) = apply {  modifier(CustomItemModifier.KnockbackResistance(value)) }

    @JvmName("withAttack")
    fun attack(damage: Double) = apply { attackDamage(damage) }

    @JvmName("withAttack")
    fun attack(speed: Double, damage: Double) = apply  {
        attackSpeed(speed)
        attackDamage(damage)
    }

    @JvmName("withAttack")
    fun attack(speed: Double, damage: Double, knockback: Double) = apply {
        attackSpeed(speed)
        attackDamage(damage)
        attackKnockback(knockback)
    }

    @JvmName("withLore")
    fun lore(vararg components: Component) = apply {
        if (components.isEmpty()) {
            modifiers.removeIf { it is CustomItemModifier.Lore }
            return@apply
        }
        modifier(CustomItemModifier.Lore(components.toList()))
    }

    fun appendLore(vararg components: Component) = apply {
        if (components.isEmpty()) {
            return@apply
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

    @JvmName("asItemStack")
    @JvmOverloads
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

context(BukkitEventListener)
    fun CustomItemFeature.block() = plugin.features.blocks[textureIndex]

context(BukkitEventListener)
    fun ItemStack.custom() = asCustomOrNull(plugin)

fun ItemStack.asCustomOrNull(plugin: KrakenPlugin): CustomItemFeature? {
    val meta = itemMeta ?: return null
    val container = meta.persistentDataContainer
    val textureIndex = container.get(plugin.features.id, PersistentDataType.INTEGER) ?: return null
    return plugin.features.items[textureIndex]
}