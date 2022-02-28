@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(hexalite.plugins.shadow.get().pluginId)
    application
}

dependencies {
    api(rootProject.hexalite.bundles.kotlin.essential)
    api(rootProject.hexalite.bundles.ktor)
    api(rootProject.hexalite.bundles.database)
    api(rootProject.hexalite.mordant)
    api(rootProject.hexalite.kotlinx.serialization.json)
    api(rootProject.hexalite.dotenv)
    testImplementation(rootProject.hexalite.ktor.server.test.host)
}

application {
    mainClass.set("org.hexalite.network.rest.webserver.HexaliteRestWebserver")
}