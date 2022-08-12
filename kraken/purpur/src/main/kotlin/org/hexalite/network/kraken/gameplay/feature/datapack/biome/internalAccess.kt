@file:JvmName("BiomeInternalAccess")

package org.hexalite.network.kraken.gameplay.feature.datapack.biome

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Biome.BiomeBuilder
import net.minecraft.world.level.biome.BiomeGenerationSettings
import net.minecraft.world.level.biome.BiomeSpecialEffects
import net.minecraft.world.level.biome.MobSpawnSettings
import org.bukkit.Bukkit
import org.bukkit.Location
import org.hexalite.network.kraken.extension.handle
import org.hexalite.network.kraken.handle
import kotlin.jvm.optionals.getOrNull

@OptIn(ExperimentalStdlibApi::class)
fun findBiome(location: ResourceLocation): Biome? {
    val registry = Bukkit.getServer().handle().registryHolder.registry(Registry.BIOME_REGISTRY)
    val key = ResourceKey.create(Registry.BIOME_REGISTRY, location)
    return (registry.getOrNull() ?: error("Failed to get biome registry.")).get(key)
}

val Location.biome: Holder<Biome>?
    get() {
        val position = BlockPos(blockX, 0, blockZ)
        val chunk = world.handle().getChunkAt(position)
        return chunk.getNoiseBiome(position.x, 0, position.y)
    }

data class BiomeCreationBuilder(
    var location: ResourceLocation,
    var generationSettings: BiomeGenerationSettings.Builder.() -> Unit = {},
    val specialEffects: BiomeSpecialEffects.Builder.() -> Unit = {},
    val mobSpawnSettings: MobSpawnSettings.Builder.() -> Unit = {},
    val root: BiomeBuilder.() -> Unit = {},
)
