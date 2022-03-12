rootProject.name = "hexalite-java-edition"

enableFeaturePreview("VERSION_CATALOGS")
includeBuild("build-logic")

pluginManagement {
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/") {
            name = "PaperMC"
        }
        maven(url = "https://maven.fabricmc.net/") {
            name = "FabricMC"
        }
        gradlePluginPortal()
    }
}

include(
    ":common",
    ":kraken:purpur",
    ":kraken:velocity",
    ":arcade:origins",
    ":arcade:origins:rest-module",
    ":arcade:origins:skills",
    ":arcade:duels",
    ":arcade:duels:rest-module",
    ":recyclable:chat",
    ":recyclable:combat",
    ":rest-webserver",
    ":rest-webclient",
    ":resource-pack-generator"
)

dependencyResolutionManagement {
    versionCatalogs {
        create("hexalite") {
            from(files("./hexalite.versions.toml"))
        }
    }
}