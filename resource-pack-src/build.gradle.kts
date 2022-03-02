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
    implementation(rootProject.hexalite.bundles.kotlin.essential)
    implementation(project(":common"))
}