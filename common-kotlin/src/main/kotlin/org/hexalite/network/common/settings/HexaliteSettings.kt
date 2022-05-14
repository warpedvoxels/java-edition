package org.hexalite.network.common.settings

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.FileNotFoundException

@Serializable
data class HexaliteSettings(
    val grpc: Grpc
) {
    @Serializable
    data class Grpc(val root: Root) {
        @Serializable
        data class Root(val host: String, val port: Short)
    }

    companion object {
        val Toml = Toml(
            config = TomlConfig(ignoreUnknownNames = true)
        )

        fun read(): HexaliteSettings {
            val home = System.getProperty("user.home")
            val file = File("$home/.hexalite/settings.toml")
            if (!file.exists()) {
                throw FileNotFoundException(
                    "Settings file not found. Please make sure to run the gRPC server at least once."
                )
            }
            return Toml.decodeFromString(file.readText())
        }
    }
}

