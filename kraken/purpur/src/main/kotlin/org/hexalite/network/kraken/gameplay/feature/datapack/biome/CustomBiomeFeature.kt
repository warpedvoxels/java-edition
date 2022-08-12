package org.hexalite.network.kraken.gameplay.feature.datapack.biome

import com.mojang.serialization.Lifecycle
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.WritableRegistry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.Music
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.biome.*
import net.minecraft.world.level.biome.Biome.*
import net.minecraft.world.level.biome.BiomeSpecialEffects.GrassColorModifier
import net.minecraft.world.level.levelgen.GenerationStep
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.entity.Player
import org.hexalite.network.common.math.Color
import org.hexalite.network.kraken.BukkitDslMarker
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.handle
import org.hexalite.network.kraken.logging.debug
import org.hexalite.network.kraken.logging.error
import kotlin.jvm.optionals.getOrNull

typealias ChunkRenderingPredicate = (player: Player, chunk: Chunk) -> Boolean

open class CustomBiomeFeature(path: ResourceLocation, base: ResourceLocation? = null) {
    var settings = Settings(path, base)

    data class Settings(
        val path: ResourceLocation,
        val base: ResourceLocation? = null,
        var fx: Fx = Fx(),
        var geography: Geography = Geography(),
        var generation: (BiomeGenerationSettings.Builder.() -> Unit)? = null,
        var mobs: (MobSpawnSettings.Builder.() -> Unit)? = null,
        var renderPredicate: ChunkRenderingPredicate = { _, _ -> false }
    ) {
        data class Fx(
            var fogColor: FogColor? = null,
            var waterColor: Color? = null,
            var skyColor: Color? = null,
            var foliageColor: Color? = null,
            var grassColor: Color? = null,
            var grassColorModifier: GrassColorModifier? = null,
            var ambientParticleSettings: AmbientParticleSettings? = null,
            var ambientLoopSoundEvent: SoundEvent? = null,
            var ambientMoodSettings: AmbientMoodSettings? = null,
            var ambientAdditionsSettings: AmbientAdditionsSettings? = null,
            var backgroundMusic: Music? = null,
        ) {
            data class FogColor(
                var surface: Color = Color(0, 0, 0),
                var water: Color? = Color(0, 0, 0),
            )

            @BukkitDslMarker
            fun fogColor(builder: FogColor.() -> Unit) {
                fogColor = (fogColor ?: FogColor()).apply(builder)
            }
        }

        data class Geography(
            var precipitation: Precipitation? = null,
            var temperature: Temperature? = null,
            var downfall: Float? = null,
        ) {
            data class Temperature(
                val base: Float = 0.0f,
                val adjustment: TemperatureModifier = TemperatureModifier.NONE
            )

            @BukkitDslMarker
            fun temperature(builder: Temperature.() -> Unit) {
                temperature = (temperature ?: Temperature()).apply(builder)
            }
        }

        @BukkitDslMarker
        fun geography(builder: Geography.() -> Unit) = geography.builder()

        @BukkitDslMarker
        fun fx(builder: Fx.() -> Unit) = fx.builder()

        @BukkitDslMarker
        fun generation(builder: BiomeGenerationSettings.Builder.() -> Unit) {
            generation = builder
        }

        @BukkitDslMarker
        fun mobs(builder: MobSpawnSettings.Builder.() -> Unit) {
            mobs = builder
        }

        @BukkitDslMarker
        fun renderPredicate(predicate: ChunkRenderingPredicate) {
            renderPredicate = predicate
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    val handle: Biome by lazy {
        BiomeBuilder().apply {
            val base = settings.base?.let(::findBiome)
            (settings.geography.precipitation ?: base?.precipitation)?.run(this::precipitation)
            (settings.geography.temperature?.base ?: base?.baseTemperature)?.run(this::temperature)
            temperatureAdjustment(settings.geography.temperature?.adjustment ?: TemperatureModifier.NONE)
            specialEffects(BiomeSpecialEffects.Builder().apply {
                (settings.fx.fogColor?.surface?.rgb ?: base?.fogColor)?.run(this::fogColor)
                (settings.fx.fogColor?.water?.rgb ?: base?.waterFogColor)?.run(this::waterFogColor)
                (settings.fx.waterColor?.rgb ?: base?.waterColor)?.run(this::waterColor)
                (settings.fx.skyColor?.rgb ?: base?.skyColor)?.run(this::skyColor)
                (settings.fx.grassColor?.rgb
                    ?: base?.specialEffects?.grassColorOverride?.getOrNull())?.run(this::grassColorOverride)
                (settings.fx.foliageColor?.rgb
                    ?: base?.specialEffects?.foliageColorOverride?.getOrNull())?.run(this::foliageColorOverride)
                (settings.fx.grassColorModifier
                    ?: base?.specialEffects?.grassColorModifier)?.run(this::grassColorModifier)
                (settings.fx.ambientParticleSettings
                    ?: base?.specialEffects?.ambientParticleSettings?.getOrNull())?.run(
                    this::ambientParticle
                )
                (settings.fx.ambientLoopSoundEvent
                    ?: base?.specialEffects?.ambientLoopSoundEvent?.getOrNull())?.run(this::ambientLoopSound)
                (settings.fx.ambientMoodSettings
                    ?: base?.specialEffects?.ambientMoodSettings?.getOrNull())?.run(this::ambientMoodSound)
                (settings.fx.ambientAdditionsSettings
                    ?: base?.specialEffects?.ambientAdditionsSettings?.getOrNull())?.run(
                    this::ambientAdditionsSound
                )
                (settings.fx.backgroundMusic
                    ?: base?.specialEffects?.backgroundMusic?.getOrNull())?.run(this::backgroundMusic)
            }.build())
            mobSpawnSettings(MobSpawnSettings.Builder().apply {
                if (base != null) {
                    MobCategory.values().forEach { category ->
                        val mobs = base.mobSettings.getMobs(category)
                        mobs.unwrap().forEach {
                            addSpawn(category, it)
                        }
                    }
                    org.bukkit.entity.EntityType.values().forEach { type ->
                        val type = EntityType.getFromBukkitType(type)
                        val cost = base.mobSettings.getMobSpawnCost(type) ?: return@forEach
                        addMobCharge(type, cost.charge, cost.energyBudget)
                    }
                    creatureGenerationProbability(base.mobSettings.creatureProbability)
                }
                settings.mobs?.invoke(this)
            }.build())
            generationSettings(BiomeGenerationSettings.Builder().apply {
                if (base != null) {
                    base.generationSettings.features().forEachIndexed { index, it ->
                        it.forEach { holder ->
                            addFeature(index, holder)
                        }
                    }
                    GenerationStep.Carving.values().forEach { step ->
                        val carver = base.generationSettings.getCarvers(step)
                        carver.forEach { holder ->
                            addCarver(step, holder)
                        }
                    }
                }
                settings.generation?.invoke(this)
            }.build())
        }.build()
    }

    private val frozen = try {
        MappedRegistry::class.java.getDeclaredField("frozen")
    } catch (notFound: NoSuchFieldException) {
        MappedRegistry::class.java.getDeclaredField("ca")
    }.apply {
        isAccessible = true
    }

    fun register(plugin: KrakenPlugin) {
        val registry =
            Bukkit.getServer().handle().registryAccess().ownedRegistryOrThrow(Registry.BIOME_REGISTRY)
                as WritableRegistry<Biome>
        val path = ResourceKey.create(Registry.BIOME_REGISTRY, settings.path)
        try {
            frozen.set(registry, false)
            registry.register(path, handle, Lifecycle.stable())
            plugin.log.debug { "Successfully registered the '$path' biome." }
            frozen.set(registry, true)
        } catch (exception: Exception) {
            plugin.log.error { "Failed to add '$path' to the biome registry." }
            throw exception
        }
    }
}