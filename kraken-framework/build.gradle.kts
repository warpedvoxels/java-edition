@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(hexalite.plugins.plugin.yml.get().pluginId)
    id(hexalite.plugins.paperweight.userdev.get().pluginId)
    id(hexalite.plugins.shadow.get().pluginId)
}

repositories {
    maven(url = "https://repo.minebench.de") {
        name = "Minebench"
    }
}

dependencies {
    api(rootProject.hexalite.bundles.kotlin.essential)
    implementation(rootProject.hexalite.minedown)
    compileOnly(rootProject.hexalite.bundles.adventure)
    paperweightDevBundle(org.hexalite.network.build_logic.PURPUR_GROUP, hexalite.versions.purpur.get())
}

bukkit {
    name = "Kraken"
    apiVersion = "1.18"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("Hexalite Network Development Team")
    main = "org.hexalite.network.kraken.KrakenFrameworkPlugin"
    prefix = "Kraken"
}

tasks {
    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/${project.name}-reobf.jar"))
    }
    assemble {
        dependsOn(shadowJar, reobfJar)
    }
    shadowJar {
        relocate("de.themoep.minedown", "org.hexalite.network.kraken.thirdparty.minedown")
    }
}