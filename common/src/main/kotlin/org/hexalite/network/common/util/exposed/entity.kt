package org.hexalite.network.common.util.exposed

import kotlinx.datetime.LocalDateTime
import org.hexalite.network.common.extension.now
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

abstract class BaseRestWebserverUUIDEntity(id: EntityID<UUID>, table: BaseRestWebserverUUIDTable): UUIDEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseRestWebserverUUIDEntityClass<E: BaseRestWebserverUUIDEntity>(table: BaseRestWebserverUUIDTable): UUIDEntityClass<E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                runCatching {
                    action.toEntity(this)?.updatedAt = LocalDateTime.now()
                }
            }
        }
    }
}

abstract class BaseRestWebserverIntEntity(id: EntityID<Int>, table: BaseRestWebserverIntTable): IntEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseRestWebserverIntEntityClass<E: BaseRestWebserverIntEntity>(table: BaseRestWebserverIntTable): IntEntityClass<E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                runCatching {
                    action.toEntity(this)?.updatedAt = LocalDateTime.now()
                }
            }
        }
    }
}

abstract class StringEntity(id: EntityID<String>): Entity<String>(id)

abstract class StringEntityClass<out E: StringEntity>(table: StringIdTable, entityType: Class<E>? = null): EntityClass<String, E>(table, entityType)
