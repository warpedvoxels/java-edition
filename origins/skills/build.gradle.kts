import org.hexalite.network.build.Hexalite
import org.hexalite.network.build.applyPurpurLogic

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(hexalite.plugins.plugin.yml.get().pluginId)
    id(hexalite.plugins.shadow.get().pluginId)
}

applyPurpurLogic()

dependencies {
    compileOnly(project(":kraken"))
}

bukkit {
    name = "Skills"
    apiVersion = "1.18"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = Hexalite.Authors
    depend = listOf("Kraken")
    main = "org.hexalite.network.duels.HexaliteOriginsSkillsPlugin"
    prefix = "Skills"
}

tasks {
    getByName<io.papermc.paperweight.tasks.RemapJar>("reobfJar") {
        outputJar.set(layout.buildDirectory.file("libs/${project.name}-reobf.jar"))
    }
    shadowJar {
        exclude {
            it.file?.name?.startsWith("kotlin") == true || it.file?.name?.startsWith("patched_") == true
        }
    }
}