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
    ":arcade:origins",
    ":arcade:origins:skills",
    ":arcade:duels",
    ":recyclable:chat",
    ":recyclable:combat",
    ":rest-webserver"
)

dependencyResolutionManagement {
    versionCatalogs {
        create("hexalite") {
            from(files("./hexalite.versions.toml"))
        }
    }
}