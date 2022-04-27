import com.google.protobuf.gradle.*
import org.hexalite.network.build.applyPurpurLogic

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    alias(hexalite.plugins.shadow)
    alias(hexalite.plugins.protobuf)
    java
}

applyPurpurLogic()

dependencies {
    implementation(rootProject.hexalite.bundles.kotlin.essential)
    implementation(rootProject.hexalite.mordant)
    implementation(rootProject.hexalite.kotlinx.serialization.json)
    implementation(rootProject.hexalite.kotlinx.serialization.protobuf)
    implementation(rootProject.hexalite.pbandk.runtime)
}

val protocVersion = rootProject.hexalite.versions.protoc.get()
val pbandkVersion = rootProject.hexalite.versions.pbandk.get()

protobuf {
    generatedFilesBaseDir = "$projectDir/src"
    protoc {
        artifact = "com.google.protobuf:protoc:$protocVersion"
    }
    plugins {
        id("pbandk") {
            artifact = "pro.streem.pbandk:protoc-gen-pbandk-jvm:$pbandkVersion:jvm8@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach { task ->
            task.builtins {
                remove("java")
            }
            task.plugins {
                id("pbandk") {
                    option("kotlin_package=org.hexalite.network.protobuf")
                }
            }
        }
    }
}

sourceSets {
    main {
        proto @ExperimentalStdlibApi {
            // walk all directories and subdirectories recursively and add them to a list
            fun File.retrievePaths(): List<String> {
                if (!isDirectory) return emptyList()
                return listOf(absolutePath) + (listFiles()?.flatMap { it.retrievePaths() } ?: emptyList())
            }
            srcDirs(*File(rootProject.projectDir.absolutePath + File.separator + "definitions").retrievePaths().toTypedArray())
        }
        java {
            srcDirs("${buildDir}/generated/source/proto/main/java")
        }
    }
}

tasks.compileJava {
    onlyIf { false }
}