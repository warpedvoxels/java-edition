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

import net.warpedvoxels.gradle.JVM_TARGET
import net.warpedvoxels.gradle.KOTLIN_ARGS

plugins {
    id("voxels-gradle-plugin")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.paperweight.userdev) apply false
    alias(libs.plugins.run.paper) apply false
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        maven(url = "https://libraries.minecraft.net/") {
            name = "Minecraft Libraries"
        }
        maven(url = "https://repo.papermc.io/repository/maven-public/") {
            name = "PaperMC"
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(JVM_TARGET))
        }
    }
    kotlin {
        explicitApi()
    }
    tasks {
        compileKotlin {
            kotlinOptions.freeCompilerArgs = KOTLIN_ARGS
            kotlinOptions.jvmTarget = "$JVM_TARGET"
        }
        compileTestKotlin {
            kotlinOptions.freeCompilerArgs = KOTLIN_ARGS
            kotlinOptions.jvmTarget = "$JVM_TARGET"
        }
        jar {
            from(rootProject.file("LICENSE"))
        }
        test {
            useJUnitPlatform()
        }
    }
}

