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
import org.gradle.kotlin.dsl.kotlin

val KOTLIN_ARGS = listOf("-opt-in=kotlin.RequiresOptIn", "-Xcontext-receivers")
val JVM_TARGET = 17

fun Project.voxelsUseKotlin() = withCatalog("libs") {
    apply(plugin = pluginId("kotlin-jvm"))
    apply(plugin = pluginId("kotlin-serialization"))
    dependencies {
        "implementation"(library("kotlin-serialization"))
        "implementation"(library("kotlin-coroutines"))
        "implementation"(kotlin("reflect", version = version("kotlin")))
        "testImplementation"(kotlin("test", version = version("kotlin")))
        "testImplementation"(kotlin("test-junit5", version = version("kotlin")))
    }
}