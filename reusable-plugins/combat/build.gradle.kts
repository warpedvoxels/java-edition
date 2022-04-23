import org.hexalite.network.build.Hexalite
import org.hexalite.network.build.applyPurpurLogic

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(hexalite.plugins.plugin.yml.get().pluginId)
    id(hexalite.plugins.shadow.get().pluginId)
}

applyPurpurLogic()

dependencies {
    compileOnly(project(":kraken:purpur"))
}

bukkit {
    apiVersion = Hexalite.vAPI
    authors = Hexalite.Authors
    depend = Hexalite.Depend
    main = "org.hexalite.network.combat.CombatPlugin"
    prefix = "Combat"
    name = prefix
}

tasks {
    getByName<io.papermc.paperweight.tasks.RemapJar>("reobfJar") {
        outputJar.set(layout.buildDirectory.file("libs/${project.name}-reobf.jar"))
    }
    shadowJar {
        exclude {
            @Suppress("UNNECESSARY_SAFE_CALL")
            it.file?.name?.startsWith("kotlin") == true || it.file?.name?.startsWith("patched_") == true
        }
    }
}