package org.hexalite.network.common.util.exposed

import kotlinx.datetime.LocalDateTime
import org.hexalite.network.common.extension.now
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

abstract class BaseRestWebserverUUIDTable(name: String): UUIDTable(name = name) {
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}

abstract class BaseRestWebserverIntTable(name: String): IntIdTable(name = name) {
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
}

abstract class StringIdTable(name: String = "", columnName: String = "id", columnLength: Int = 32): IdTable<String>(name) {
    override val id: Column<EntityID<String>> = varchar(columnName, columnLength).entityId()

    override val primaryKey by lazy {
        PrimaryKey(id, name = "PK_${this::class.simpleName}_Id")
    }
}