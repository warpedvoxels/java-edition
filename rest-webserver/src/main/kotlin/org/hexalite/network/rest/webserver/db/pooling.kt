package org.hexalite.network.rest.webserver.db.pooling

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.hexalite.network.rest.webserver.env

fun createPooledDataSource() = HikariDataSource(HikariConfig().apply {
    dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
    username = env["WEBSERVER_DB_USER"]
    password = env["WEBSERVER_DB_PASSWORD"]
    jdbcUrl = env["WEBSERVER_DB_URL"]
    maximumPoolSize = env["POOLING_MAXIMUM_POOL_SIZE"].toInt()
    connectionTimeout = env["POOLING_CONNECTION_TIMEOUT"].toLong()
    idleTimeout = env["POOLING_IDLE_TIMEOUT"].toLong()
    maxLifetime = env["POOLING_MAXIMUM_LIFETIME"].toLong()
    addDataSourceProperty("cachePrepStmts", "true")
    addDataSourceProperty("prepStmtCacheSize", "250")
    addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    addDataSourceProperty("useServerPrepStmts", "true")
    addDataSourceProperty("useLocalSessionState", "true")
    addDataSourceProperty("rewriteBatchedStatements", "true")
    addDataSourceProperty("cacheResultSetMetadata", "true")
    addDataSourceProperty("cacheServerConfiguration", "true")

    // Disable configurations not supported by Jetbrains' Exposed.
    isAutoCommit = false
    transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    leakDetectionThreshold = 7500

    validate()
})