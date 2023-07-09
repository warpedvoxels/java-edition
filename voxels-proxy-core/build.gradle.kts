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

import net.warpedvoxels.gradle.voxelsUseKotlin

plugins {
    id("voxels-gradle-plugin") apply false
    kotlin("kapt") apply false
}

allprojects {
    apply(plugin = "voxels-gradle-plugin")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    voxelsUseKotlin()

    dependencies {
        compileOnly(rootProject.libs.velocity.api)
        "kapt"(rootProject.libs.velocity.api)
    }
}
