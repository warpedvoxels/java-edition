@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("fabric-loom")
}

dependencies {
    mappings(loom.officialMojangMappings())
    modImplementation(origins.bundles.fabric)
    minecraft(origins.minecraft)
}
