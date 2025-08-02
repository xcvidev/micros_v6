package com.xcvi.micros.domain.utils
import java.util.Locale

fun Float.roundToInt(): Int =
    if (isNaN()) 0 else Math.round(this)

fun Double.roundToInt(): Int = when {
    isNaN() -> 0
    this > Int.MAX_VALUE -> Int.MAX_VALUE
    this < Int.MIN_VALUE -> Int.MIN_VALUE
    else -> Math.round(this).toInt()
}

fun List<Double?>.normalize(scale: Double = 0.0): List<Double?> {
    if(this.isEmpty()) return this
    val min = this.filterNotNull().minOf { it }
    return this.map {
        if (it == null) {
            null
        }
        else{
            it - min + min*scale
        }
    }
}
fun Double.nextAmount(): Int {
    return try {
        if (this < 0.0) {
            0
        } else {
            val rounded = this.roundToInt()
            ((rounded / 10) + 1) * 10
        }
    } catch (e: Exception) {
        0
    }
}

fun Double.previousAmount(): Int {
    return try {
        if (this <= 10) {
            0
        } else {
            val rounded = this.roundToInt()
            ((rounded - 1) / 10) * 10
        }
    } catch (e: Exception) {
        0
    }
}
fun Int.nextAmount(): Int {
    return try {
        if (this < 0.0) {
            0
        } else {
            val rounded = this
            ((rounded / 10) + 1) * 10
        }
    } catch (e: Exception) {
        0
    }
}

fun Int.previousAmount(): Int {
    return try {
        if (this <= 10) {
            0
        } else {
            val rounded = this
            ((rounded - 1) / 10) * 10
        }
    } catch (e: Exception) {
        0
    }
}

fun Double.nextAmount50(): Double {
    return try{
        if (this < 0.0) {
            0.0
        } else {
            val rounded = this.roundToInt()
            ((rounded / 50) + 1) * 50.0
        }
    } catch (e: Exception) {
        return 0.0
    }
}

fun Double.previousAmount50(): Double {
    return try{
        if (this <= 50) {
            0.0
        } else {
            val rounded = this.roundToInt()
            ((rounded - 1) / 50) * 50.0
        }
    } catch (e: Exception) {
        0.0
    }
}

fun Double.formatClean(): String {
    try{
        if (this == 0.0) return "0"
        val formatted =
            String.format(Locale.getDefault(), "%.1f", this).removeSuffix(".0").removeSuffix(",0")
        return formatted
    } catch (e: Exception) {
        return "0"
    }
}
fun Double.roundDecimals(): Double {
    try{
        val rounded = (this * 10).roundToInt() / 10.0
        return rounded
    } catch (e: Exception) {
        return 0.0
    }
}