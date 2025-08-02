package com.xcvi.micros.domain.utils


fun String.normalizeToWordSet(): Set<String> {
    return this
        .lowercase()
        .replace("[()\\[\\]*\"/:;,.+_\\-&%]".toRegex(), " ") // Replace punctuation with space
        .split("\\s+".toRegex()) // Split by whitespace
        .filter { it.isNotBlank() }
        .toSet()
}

fun String.toAscii(): String {
    val text = this
    if (text.isEmpty()) return ""

    val accentMap = mapOf(
        'á' to "a", 'à' to "a", 'ä' to "a", 'â' to "a", 'ã' to "a", 'å' to "a",
        'é' to "e", 'è' to "e", 'ë' to "e", 'ê' to "e",
        'í' to "i", 'ì' to "i", 'ï' to "i", 'î' to "i",
        'ó' to "o", 'ò' to "o", 'ö' to "o", 'ô' to "o", 'õ' to "o",
        'ú' to "u", 'ù' to "u", 'ü' to "u", 'û' to "u",
        'ç' to "c", 'ñ' to "n", 'ß' to "ss",
        'Á' to "A", 'À' to "A", 'Ä' to "A", 'Â' to "A", 'Ã' to "A", 'Å' to "A",
        'É' to "E", 'È' to "E", 'Ë' to "E", 'Ê' to "E",
        'Í' to "I", 'Ì' to "I", 'Ï' to "I", 'Î' to "I",
        'Ó' to "O", 'Ò' to "O", 'Ö' to "O", 'Ô' to "O", 'Õ' to "O",
        'Ú' to "U", 'Ù' to "U", 'Ü' to "U", 'Û' to "U",
        'Ç' to "C", 'Ñ' to "N"
    )
    val asciiText = buildString {
        for (c in text) {
            append(accentMap[c] ?: c)
        }
    }
    return asciiText
        .replace(Regex("[^a-zA-Z0-9]+"), " ")
        .lowercase()
        .trim()
}