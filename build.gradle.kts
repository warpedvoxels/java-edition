@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    alias(hexalite.plugins.kotlin.jvm)
    alias(hexalite.plugins.kotlinx.serialization) apply false
    alias(hexalite.plugins.paperweight.userdev) apply false
    alias(hexalite.plugins.kapt) apply false
    id("hexalite-build-logic") apply false
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "org.gradle.java-library")
    apply(plugin = "hexalite-build-logic")

    repositories {
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
            name = "SpigotMC"
        }
        maven("https://papermc.io/repo/repository/maven-public/") {
            name = "PaperMC"
        }
        maven(url = "https://repo.purpurmc.org/snapshots/") {
            name = "PurpurMC"
        }
        maven(url = "https://nexus.velocitypowered.com/repository/maven-public/") {
            name = "Velocity"
        }
        maven(url = "https://maven.fabricmc.net/") {
            name = "FabricMC"
        }
        maven(url = "https://maven.pkg.jetbrains.space/public/p/ktor/eap/") {
            name = "Ktor EAP"
        }
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
            name = "Sonatype"
        }
        maven(url = "https://jitpack.io/") {
            name = "JitPack"
        }
        mavenCentral()
    }

    dependencies {
        testImplementation(kotlin("test"))
        testImplementation(kotlin("test-junit5"))
        testImplementation(rootProject.hexalite.mockk)
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
        test {
            useJUnitPlatform()
        }
    }
}
