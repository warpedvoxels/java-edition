@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(hexalite.plugins.plugin.yml.get().pluginId)
    id(hexalite.plugins.paperweight.userdev.get().pluginId)
    id(hexalite.plugins.shadow.get().pluginId)
}

dependencies {
    compileOnly(project(":kraken-framework"))
    paperweightDevBundle(org.hexalite.network.build_logic.PURPUR_GROUP, hexalite.versions.purpur.get())
}

bukkit {
    name = "Duels"
    apiVersion = "1.18"
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("Hexalite Network Development Team")
    depend = listOf("Kraken")
    main = "org.hexalite.network.duels.HexaliteDuelsPlugin"
    prefix = "Duels"
}

tasks {
    reobfJar {
        outputJar.set(layout.buildDirectory.file("libs/${project.name}-reobf.jar"))
    }
    build {
        dependsOn(reobfJar)
        dependsOn(shadowJar)
    }
    shadowJar {
        exclude {
            it.file?.name?.startsWith("kotlin") == true || it.file?.name?.startsWith("patched_") == true
        }
    }
}