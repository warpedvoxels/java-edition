package org.hexalite.network.rest.webserver.db.pooling

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.hexalite.network.common.env.Environment

fun createPooledDataSource() = HikariDataSource(HikariConfig().apply {
    poolName = "rest-webserver-database-pooling"
    driverClassName = "org.postgresql.Driver"
    username = Environment.WebServer.DB.user
    password = Environment.WebServer.DB.password
    jdbcUrl = Environment.WebServer.DB.url
    maximumPoolSize = Environment.Pooling.maximumPoolSize
    connectionTimeout = Environment.Pooling.connectionTimeout
    idleTimeout = Environment.Pooling.idleTimeout
    maxLifetime = Environment.Pooling.maximumLifetime
    addDataSourceProperty("databaseMetadataCacheFields", "65535")
    addDataSourceProperty("preparedStatementCacheQueries", "256")
    addDataSourceProperty("reWriteBatchedInserts", "true")
    isAutoCommit = false // Exposed does not support auto-commiting
    transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    leakDetectionThreshold = 7500
    validate()
})