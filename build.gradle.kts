import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(origins.plugins.kotlin.jvm)
    alias(origins.plugins.fabric.loom) apply false
    java
}

allprojects {
    repositories {
        maven(url = "https://maven.fabricmc.net/") {
            name = "FabricMC"
        }
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    tasks {
        processResources {
            filesMatching("fabric.mod.json") {
                expand(mutableMapOf("version" to project.version))
            }
        }
        jar {
            from("LICENSE.txt") {
                rename { "${it}_origins" }
            }
        }
        java {
            withSourcesJar()
        }
        withType<JavaCompile> {
            // Minecraft 1.18 (1.18-pre2) upwards uses Java 17.
            options.release.set(17)
        }
    }
}
