package org.hexalite.network.build

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

//     ___                        __  _
//   / _ \_______  ___  ___ ____/ /_(_)__ ___
//  / ___/ __/ _ \/ _ \/ -_) __/ __/ / -_|_-<
// /_/  /_/  \___/ .__/\__/_/  \__/_/\__/___/
//              /_/

object Purpur {
    const val Group = "org.purpurmc.purpur"
}


//    ___  __          _        __             _
//   / _ \/ /_ _____ _(_)__    / /  ___  ___ _(_)___
//  / ___/ / // / _ `/ / _ \  / /__/ _ \/ _ `/ / __/
// /_/  /_/\_,_/\_, /_/_//_/ /____/\___/\_, /_/\__/
//             /___/                   /___/

fun Project.applyPurpurLogic() = withCatalog("hexalite") {
    apply(plugin = pluginId("paperweight.userdev"))

    dependencies {
        "paperweightDevelopmentBundle"(dependency("purpur.dev.bundle"))
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