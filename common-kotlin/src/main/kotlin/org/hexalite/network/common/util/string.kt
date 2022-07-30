@file:JvmName("StringExt")

package org.hexalite.network.common.util

fun String.snakecase(): String = split("(?=[A-Z])".toRegex()).joinToString("_").lowercase()

