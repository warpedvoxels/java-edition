package org.hexalite.network.kraken.coroutines

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Convert an integer to a [Duration] in ticks. For example, `20.ticks`.
 */
inline val Int.ticks get() = (this * 50).milliseconds

/**
 * Convert a long to a [Duration] in ticks. For example, `20.ticks`.
 */
inline val Long.ticks get() = (this * 50).milliseconds

/**
 * Convert a [Duration] to a tick-based long. For example, `duration.inWholeTicks`.
 */
inline val Duration.inWholeTicks get() = this.inWholeMilliseconds / 50