package org.hexalite.network.kraken.extension

fun Class<*>.kotlinName(): String {
    val name = name
    return when {
        name.contains("Kt$") -> name.substringBefore("Kt$")
        name.contains('$') -> name.substringBefore('$')
        else -> name
    }
}

inline fun <R> (() -> R).callerName(): String = javaClass.kotlinName()

inline fun <T, R> ((T) -> R).callerName(): String = javaClass.kotlinName()

inline fun <T1, T2, R> ((T1, T2) -> R).callerName(): String = javaClass.kotlinName()

inline fun <T1, T2, T3, R> ((T1, T2, T3) -> R).callerName(): String = javaClass.kotlinName()
