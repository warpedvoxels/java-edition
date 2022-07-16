package org.hexalite.network.common.settings

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlInputConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.FileNotFoundException

@Serializable
data class HexaliteSettings(
    val grpc: Grpc,
    val discord: Discord
) {
    @Serializable
    data class Grpc(val root: Root) {
        @Serializable
        data class Root(val ip: String, val port: Long, val ssl: Boolean)
    }

    @Serializable
    data class Discord(
        val token: String,
        val id: Long,
        val publicKey: String,
        val secret: String,
        val guidsToRegisterCommands: List<Long>?
    )

    companion object {
        val Toml = Toml(
            inputConfig = TomlInputConfig(ignoreUnknownNames = true),
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

