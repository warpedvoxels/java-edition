package org.hexalite.network.definitions.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class DefinitionsGradlePlugin: Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("definitions", DefinitionsPluginSettings::class.java)
        target.task("generateDefinitions").doLast {
            generateDefinitions(target, extension)
        }
    }
}