package org.hexalite.network.kraken.extension

import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import java.util.*

inline val Entity.uuid: UUID
    get() = uniqueId

inline fun UUID.asIdentity() = Identity.identity(this)

inline fun CommandSender.send(component: Component) =
    sendMessage(component)

inline fun CommandSender.send(text: List<String>) =
    sendMessage(*text.toTypedArray())

inline fun CommandSender.send(text: String) =
    sendMessage(text)

inline fun CommandSender.send(uuid: Identity, text: Component) =
    sendMessage(uuid, text)

inline fun CommandSender.send(uuid: UUID, text: List<String>) =
    sendMessage(uuid, *text.toTypedArray())

inline fun CommandSender.send(uuid: UUID, text: String) =
    sendMessage(uuid, text)
