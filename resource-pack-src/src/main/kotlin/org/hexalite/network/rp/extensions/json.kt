package org.hexalite.network.rp.extensions

fun Int.instrument() = when (this / 25 % 384) {
    1 -> "basedrum"
    2 -> "snare"
    3 -> "hat"
    4 -> "bass"
    5 -> "flute"
    6 -> "bell"
    7 -> "guitar"
    8 -> "chime"
    9 -> "xylophone"
    10 -> "iron_xylophone"
    11 -> "cow_bell"
    12 -> "didgeridoo"
    13 -> "bit"
    14 -> "banjo"
    15 -> "pling"
    else -> "harp"
}
