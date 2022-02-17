enableFeaturePreview("VERSION_CATALOGS")

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

dependencyResolutionManagement {
    versionCatalogs {
        create("hexalite") {
            from(files("../hexalite.versions.toml"))
        }
    }
}