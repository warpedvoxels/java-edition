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
    api(rootProject.hexalite.bundles.adventure)
    api(rootProject.hexalite.mordant)
    api(project(":common"))
    api(rootProject.hexalite.bundles.caffeine)
    compileOnly(rootProject.hexalite.bundles.adventure)
}

bukkit {
    apiVersion = Hexalite.vAPI
    authors = Hexalite.Authors
    main = "org.hexalite.network.kraken.KrakenFrameworkPlugin"
    prefix = "Kraken"
    name = prefix
}

tasks {
    getByName<RemapJar>("reobfJar") {
        outputJar.set(layout.buildDirectory.file("libs/${project.name}-reobf.jar"))
    }
}