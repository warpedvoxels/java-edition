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
    ":kraken",
    ":origins",
    ":origins:skills",
    ":duels",
    ":testing",
    ":testing:commands"
)

dependencyResolutionManagement {
    versionCatalogs {
        create("hexalite") {
            from(files("./hexalite.versions.toml"))
        }
    }
}