import org.hexalite.network.build.applyPurpurLogic

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(hexalite.plugins.shadow.get().pluginId)
}

applyPurpurLogic()

dependencies {
    compileOnly(rootProject.hexalite.bundles.kotlin.essential)
    compileOnly(rootProject.hexalite.mordant)
    compileOnly(rootProject.hexalite.kotlinx.serialization.json)
    compileOnly(rootProject.hexalite.dotenv)
}