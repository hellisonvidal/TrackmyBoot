package com.trackmyboot.app.util

import java.text.Normalizer
import java.text.NumberFormat
import java.util.Locale

private val DIACRITICS = "\\p{InCombiningDiacriticalMarks}+".toRegex()
private val PT_BR: Locale = Locale.forLanguageTag("pt-BR")

/** Minúsculas e sem acentos, para comparar textos ("Meião" == "meiao"). */
fun String.normalized(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(DIACRITICS, "").lowercase(Locale.ROOT).trim()

fun Double.toBrl(): String = NumberFormat.getCurrencyInstance(PT_BR).format(this)
