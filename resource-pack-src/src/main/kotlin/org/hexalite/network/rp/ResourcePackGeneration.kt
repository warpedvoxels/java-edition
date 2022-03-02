package org.hexalite.network.rp

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.putJsonObject
import org.hexalite.network.rp.block.PaperItemModel
import org.hexalite.network.rp.block.base.field
import org.hexalite.network.rp.block.base.state
import org.hexalite.network.rp.block.model.CustomBlocks
import org.hexalite.network.rp.font.CustomFontProviders
import org.hexalite.network.rp.font.FontCustomProviderCollectionHolder
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

object ResourcePackGeneration {
    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
        encodeDefaults = true
    }

    val mcmeta = PackMCMetaHolder(PackMCMeta(description = "A resource pack for distributing custom content in Hexalite Network."))
    val base = Path("${System.getProperty("user.home")}${File.separator}.hexalite${File.separator}dev${File.separator}").toRealPath().resolve("resource-pack")
        .createDirectories()

    fun resource(name: String) = File(this::class.java.classLoader.getResource(name)!!.toURI())

    /**
     * Copy the MCMeta to the destination resource pack.
     */
    private fun copyMeta() {
        val mcmetaFile = base.resolve("pack.mcmeta").toFile()
        mcmetaFile.writeText(json.encodeToString(mcmeta))
    }

    /**
     * Copy all assets to the destination pack.
     */
    private fun copyAssets() {
        val dir = base.resolve("assets").createDirectories()
        val textures = resource("assets")
        textures.copyRecursively(dir.toFile()) { _, _ -> OnErrorAction.SKIP }
    }

    /**
     * Generate the block state for the note block
     */
    private fun generateBlockState() {
        val obj = buildJsonObject {
            putJsonObject("variants") {
                CustomBlocks.map {
                    put(it.field(), json.encodeToJsonElement(it.state()))
                }
            }
        }
        val dir = base.resolve("assets/minecraft/blockstates").createDirectories()
        val noteBlock = dir.resolve("note_block.json").toFile()
        noteBlock.writeText(json.encodeToString(obj))
    }

    /**
     * Generate the models for all custom blocks
     */
    private fun generateBlockModels() {
        val paper = PaperItemModel()
        val blockDir = base.resolve("assets/minecraft/models/block").createDirectories()
        val itemDir = base.resolve("assets/minecraft/models/item").createDirectories()

        CustomBlocks.forEach {
            val blockJson = json.encodeToString(it)
            val name = it.textures.all.substringAfterLast('/')
            val blockFile = blockDir.resolve("$name.json").toFile()
            blockFile.writeText(blockJson)
            paper.overrides.add(PaperItemModel.Override(PaperItemModel.Override.Predicate(customModelData = it.index), "block/$name"))
        }
        itemDir.resolve("paper.json").toFile().writeText(json.encodeToString(paper))
    }

    /**
     * Generate the custom font
     */
    private fun generateCustomFont() {
        val dir = base.resolve("assets/minecraft/font").createDirectories()
        val file = dir.resolve("default.json").toFile()
        val json = json.encodeToString(FontCustomProviderCollectionHolder(CustomFontProviders))
        file.writeText(json)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        copyMeta()
        copyAssets()
        generateBlockState()
        generateBlockModels()
        generateCustomFont()
    }
}