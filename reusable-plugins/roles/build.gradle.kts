import org.hexalite.network.build.Hexalite
import org.hexalite.network.build.applyPurpurLogic

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    alias(hexalite.plugins.plugin.yml)
    alias(hexalite.plugins.shadow)
}

applyPurpurLogic()

dependencies {
    compileOnly(project(":kraken:purpur"))
}

bukkit {
    apiVersion = Hexalite.vAPI
    authors = Hexalite.Authors
    depend = Hexalite.Depend
    main = "org.hexalite.network.combat.RolesPlugin"
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