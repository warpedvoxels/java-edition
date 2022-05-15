plugins {
    `java-gradle-plugin`
}

dependencies {
    implementation(rootProject.hexalite.kotlin.poet)
}

gradlePlugin {
    plugins.register("definitions-gradle-plugin") {
        id = "definitions-gradle-plugin"
        implementationClass = "org.hexalite.network.definitions.gradle.DefinitionsGradlePlugin"
    }
}