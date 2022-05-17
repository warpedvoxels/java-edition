@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
}

tasks {
    task<Exec>("cbindgen") {
        val config = rootProject.file("grpc-server/bindings/cbindgen.toml")
        val output = rootProject.projectDir.resolve("target/release/client.h")

        inputs.file(config)
        inputs.dir(rootProject.projectDir.resolve("grpc-server/bindings/src"))
        outputs.file(output)

        workingDir(rootProject.projectDir)
        commandLine("cbindgen")
        args(
            "--config", config.absolutePath,
            "--crate", "grpc-server-bindings",
            "--output", output.absolutePath
        )
    }
}
