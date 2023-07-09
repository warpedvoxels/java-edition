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
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories

fun Project.voxelsUsePurpur() = withCatalog("libs") {
    apply(plugin = pluginId("paperweight-userdev"))
    apply(plugin = pluginId("shadow"))
    repositories {
        maven(url = "https://repo.purpurmc.org/snapshots") {
            name = "PurpurMC"
        }
    }
    dependencies {
        "paperweightDevelopmentBundle"(library("purpur.dev.bundle"))
    }
    tasks.getByName("build") {
        dependsOn(tasks.getByName("reobfJar"))
        dependsOn(tasks.getByName("shadowJar"))
    }
    tasks.getByName("assemble") {
        dependsOn(tasks.getByName("reobfJar"))
        dependsOn(tasks.getByName("shadowJar"))
    }
}