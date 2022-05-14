import com.google.protobuf.gradle.*

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    alias(hexalite.plugins.protobuf)
}

dependencies {
    implementation(rootProject.hexalite.bundles.proto)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${rootProject.hexalite.versions.protobuf}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${rootProject.hexalite.versions.grpc.protobuf}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${rootProject.hexalite.versions.grpc.kotlin}:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

sourceSets.main {
    proto {

    }
}
