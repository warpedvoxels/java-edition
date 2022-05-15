package org.hexalite.network.definitions.gradle

data class DefinitionsPluginSettings(
    val basePackage: String = "org.hexalite.network.definition",
    val inputDir: String = "./src/main/definitions",
    val outputDir: String = "./build/generated/source/definitions/main/kotlin"
)
