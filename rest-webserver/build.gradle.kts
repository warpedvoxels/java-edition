@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(hexalite.plugins.shadow.get().pluginId)
    application
}

dependencies {
    api(project(":common"))
    api(rootProject.hexalite.bundles.kotlin.essential)
    api(rootProject.hexalite.bundles.ktor.server)
    api(rootProject.hexalite.bundles.database)
    api(rootProject.hexalite.mordant)
    api(rootProject.hexalite.kotlinx.serialization.json)
    api(rootProject.hexalite.dotenv)
    api(rootProject.hexalite.logback.classic)
    testImplementation(rootProject.hexalite.ktor.server.test.host)

    // Database modules
    api(project(":arcade:duels:rest-module"))
}

application {
    mainClass.set("org.hexalite.network.rest.webserver.HexaliteRestWebserver")
}

tasks {
    shadowJar {
        archiveFileName.set("rest-webserver-shaded.jar")
    }
}