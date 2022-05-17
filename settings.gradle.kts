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
        google()
    }
}

include(
    ":common-kotlin",
    ":kotlin-grpc-client",
    ":kraken:purpur",
    ":kraken:velocity",
    ":arcade:origins",
    ":arcade:origins:skills",
    ":arcade:duels",
    ":reusable-plugins:chat",
    ":reusable-plugins:combat",
    ":native"
)

dependencyResolutionManagement {
    versionCatalogs {
        create("hexalite") {
            from(files("./hexalite.versions.toml"))
        }
    }
}
