import org.hexalite.network.build.Hexalite

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    id(hexalite.plugins.shadow.get().pluginId)
    id(hexalite.plugins.kapt.get().pluginId)
}

// for some reason, gradle expect to have something before a import, so we add a dummy import
// just for be able to suppress the warnings
Hexalite

dependencies {
    compileOnly(hexalite.velocity.api)
    kapt(hexalite.velocity.annotation.processor)
}