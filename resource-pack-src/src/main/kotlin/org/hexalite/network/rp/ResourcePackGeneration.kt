package org.hexalite.network.rp

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.putJsonObject
import org.hexalite.network.rp.block.base.field
import org.hexalite.network.rp.block.base.state
import org.hexalite.network.rp.block.model.CustomBlocks
import org.hexalite.network.rp.font.CustomFontProviders
import org.hexalite.network.rp.font.FontCustomProviderCollectionHolder
import org.hexalite.network.rp.item.PaperItemModel
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

    val paper = PaperItemModel()

    /**
     * Copy the MCMeta to the destination resource pack.
     */
    private fun copyMeta() {
        println("=> Copying resource pack meta to the root folder...")
        val mcmetaFile = base.resolve("pack.mcmeta").toFile()
        mcmetaFile.writeText(json.encodeToString(mcmeta))
    }

    /**
     * Generate the block state for the note block
     */
    private fun generateBlockState() {
        println("=> Generating custom block states...")
        val obj = buildJsonObject {
            putJsonObject("variants") {
                CustomBlocks.map {
                    put(it.field(), json.encodeToJsonElement(it.state()))
                }
            }
        }
        val dir = base.resolve("assets/minecraft/blockstates")
        runCatching {
            dir.createDirectories()
        }
        val noteBlock = dir.resolve("note_block.json").toFile()
        noteBlock.writeText(json.encodeToString(obj))
    }

    /**
     * Generate the models for all custom blocks
     */
    private fun generateBlockModels() {
        println("=> Generating custom block models...")
        val blockDir = base.resolve("assets/minecraft/models/block")
        runCatching {
            blockDir.createDirectories()
        }

        CustomBlocks.forEach {
            val blockJson = json.encodeToString(it)
            val name = it.textures.all.substringAfterLast('/')
            val blockFile = blockDir.resolve("$name.json").toFile()
            blockFile.writeText(blockJson)
            paper.overrides.add(PaperItemModel.Override(PaperItemModel.Override.Predicate(customModelData = it.index), "block/$name"))
        }
    }

    /**
     * Generate the custom font
     */
    private fun generateCustomFont() {
        println("=> Generating custom font...")
        val dir = base.resolve("assets/minecraft/font")
        runCatching {
            dir.createDirectories()
        }
        val file = dir.resolve("default.json").toFile()
        val json = json.encodeToString(FontCustomProviderCollectionHolder(CustomFontProviders))
        file.writeText(json)
    }

    /**
     * Generate the custom item models
     */
    private fun generateItemModels() {
        println("=> Generating custom item models...")
        val itemDir = base.resolve("assets/minecraft/models/item")
        runCatching {
            itemDir.createDirectories()
        }
        itemDir.resolve("paper.json").toFile().writeText(json.encodeToString(paper))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        copyMeta()
        generateBlockState()
        generateBlockModels()
        generateCustomFont()
        generateItemModels()
    }
}
