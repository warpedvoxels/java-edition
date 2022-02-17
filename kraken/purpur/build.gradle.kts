import io.papermc.paperweight.tasks.RemapJar
import org.hexalite.network.build.Hexalite
import org.hexalite.network.build.applyPurpurLogic

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(hexalite.plugins.plugin.yml.get().pluginId)
    id(hexalite.plugins.shadow.get().pluginId)
}

applyPurpurLogic()

dependencies {
    api(rootProject.hexalite.bundles.kotlin.essential)
    api(rootProject.hexalite.minedown)
    api(project(":common"))
    compileOnly(rootProject.hexalite.bundles.adventure)
}

bukkit {
    name = "Kraken"
    apiVersion = "1.18"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = Hexalite.Authors
    main = "org.hexalite.network.kraken.KrakenFrameworkPlugin"
    prefix = "Kraken"
}

tasks {
    getByName<RemapJar>("reobfJar") {
        outputJar.set(layout.buildDirectory.file("libs/${project.name}-reobf.jar"))
    }
    shadowJar {
        relocate("de.themoep.minedown", "org.hexalite.network.kraken.thirdparty.minedown")
    }
}