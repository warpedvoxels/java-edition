/*
 * WarpedVoxels, a network of Minecraft: Java Edition servers
 * Copyright (C) 2023  Pedro Henrique
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.warpedvoxels.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

inline fun Project.withCatalog(
    name: String,
    crossinline block: VersionCatalog.() -> Unit
) {
    val libs = rootProject.extensions.getByType<VersionCatalogsExtension>()
    pluginManager.withPlugin("java") {
        val catalog = libs.named(name)
        catalog.block()
    }
}

fun VersionCatalog.version(name: String) =
    findVersion(name).get().displayName

fun VersionCatalog.library(id: String) =
    findLibrary(id).get().get().run {
        "$module:${versionConstraint.displayName}"
    }

fun VersionCatalog.pluginId(id: String) =
    findPlugin(id).get().get().pluginId