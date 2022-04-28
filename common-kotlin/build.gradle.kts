import org.hexalite.network.build.applyPurpurLogic

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    alias(hexalite.plugins.shadow)
    java
}

applyPurpurLogic()

dependencies {
    implementation(rootProject.hexalite.bundles.kotlin.essential)
    implementation(rootProject.hexalite.mordant)
    implementation(rootProject.hexalite.kotlinx.serialization.json)
    implementation(rootProject.hexalite.kotlinx.serialization.protobuf)
}

tasks.compileJava {
    enabled = false
}
