dependencies {
    api(project(":common-kotlin"))
}
sourceSets.main {
    java {
        srcDirs(
            "build/generated/source/definitions/main/kotlin"
        )
    }
}