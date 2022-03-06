package org.hexalite.network.rest.webserver.util

import org.hexalite.network.common.db.entity.User
import org.hexalite.network.common.db.entity.api
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalContracts::class)
suspend fun <T> api(rcv: () -> Any): T {
    contract {
        callsInPlace(rcv, InvocationKind.EXACTLY_ONCE)
    }
    return newSuspendedTransaction {
        when (val entity = rcv()) {
            is User -> entity.api() as? T? ?: error("Provided incompatible type (T). Expected ApiUSer, but ${entity::class.simpleName} was given")
            is SizedIterable<*> -> {
                entity.map {
                    when (it) {
                        is User -> it.api()
                        else -> error("[List] Unsupported API type: ${entity::class.simpleName}")
                    }
                } as T
            }
            else -> error("Unsupported API type: ${entity::class.simpleName}")
        }
    }
}
