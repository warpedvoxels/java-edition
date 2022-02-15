@file:Suppress("UnstableApiUsage")

package org.hexalite.network.build

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

// Access the version catalog and execute code.
inline fun Project.withCatalog(name: String, crossinline block: VersionCatalog.() -> Unit) {
    val libs = extensions.getByType<VersionCatalogsExtension>()
    pluginManager.withPlugin("java") {
        val catalog = libs.named(name)
        catalog.block()
    }
}

fun VersionCatalog.version(name: String) = findVersion(name).get().displayName

fun VersionCatalog.dependency(id: String) = findDependency(id).get().get().run { "$module:${versionConstraint.displayName}" }

fun VersionCatalog.pluginId(id: String) = findPlugin(id).get().get().pluginId
