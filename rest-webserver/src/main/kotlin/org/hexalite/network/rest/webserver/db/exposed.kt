package org.hexalite.network.rest.webserver.db

import org.hexalite.network.common.roles.CommonRole
import org.hexalite.network.duels.exposed.table.DuelsKits
import org.hexalite.network.duels.exposed.table.DuelsUserStats
import org.hexalite.network.rest.webserver.db.entity.Role
import org.hexalite.network.rest.webserver.db.table.Roles
import org.hexalite.network.rest.webserver.db.table.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import javax.sql.DataSource

fun buildDatabaseFromDataSource(dataSource: DataSource) = Database.connect(dataSource).also {
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
    transaction {
        SchemaUtils.createMissingTablesAndColumns(
            Roles,
            Users,
            DuelsUserStats,
            DuelsKits
        )

        for (role in CommonRole.types) {
            val r = Role.findById(role.id()) ?: Role.new(role)
            r.tabListIndex = role.tabListIndex
        }
    }
}