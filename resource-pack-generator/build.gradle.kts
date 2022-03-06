import org.hexalite.network.build.applyPurpurLogic

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(hexalite.plugins.shadow.get().pluginId)
    application
}

application {
    mainClass.set("org.hexalite.network.rp.ResourcePackGeneration")
}

applyPurpurLogic()

dependencies {
    api(rootProject.hexalite.bundles.kotlin.essential)
    api(rootProject.hexalite.commons.io)
    api(project(":common"))
    api(rootProject.hexalite.mordant)
}

tasks {
    shadowJar {
        archiveFileName.set("rp-shaded.jar")
    }
}