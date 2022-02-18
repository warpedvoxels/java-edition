@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    alias(hexalite.plugins.kotlin.jvm)
    alias(hexalite.plugins.paperweight.userdev) apply false
    alias(hexalite.plugins.plugin.yml) apply false
    alias(hexalite.plugins.shadow) apply false
    id("hexalite-build-logic") apply false
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.gradle.java-library")
    apply(plugin = "hexalite-build-logic")

    repositories {
        maven("https://papermc.io/repo/repository/maven-public/") {
            name = "PaperMC"
        }
        maven(url = "https://repo.purpurmc.org/snapshots/") {
            name = "PurpurMC"
        }
        maven(url = "https://maven.fabricmc.net/") {
            name = "FabricMC"
        }
        maven(url = "https://repo.minebench.de") {
            name = "Minebench"
        }
        mavenCentral()
    }

    tasks {
        compileKotlin {
            kotlinOptions.freeCompilerArgs = org.hexalite.network.build.BuildSystemFlags
            kotlinOptions.jvmTarget = "17"
        }
        compileTestKotlin {
            kotlinOptions.freeCompilerArgs = org.hexalite.network.build.BuildSystemFlags
            kotlinOptions.jvmTarget = "17"
        }
    }
}
