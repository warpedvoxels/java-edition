package org.hexalite.discord.common.utils

import dev.kord.common.Locale

val allowedLocales = Locale.ALL.map { it.mapLocale() }

fun Locale.mapLocale() {
    language + if (country != null) "-$country" else ""
}

fun validateLocales(locales: MutableMap<Locale, String>?, name: String) {
    val invalidLocales = locales?.keys?.map { it.mapLocale() }?.filter { it !in allowedLocales } ?: return

    error("Locales ($invalidLocales) in the $name interaction are not supported by discord")
}