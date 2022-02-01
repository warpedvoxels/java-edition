rootProject.name = "hexalite-origins"

enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        maven(url = "https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}

include(
    ":skills",
    ":common",
)

dependencyResolutionManagement {
    versionCatalogs {
        create("origins") {
            from(files("./origins.versions.toml"))
        }
    }
}