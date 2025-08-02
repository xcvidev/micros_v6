package com.xcvi.micros.domain.model.utils

interface DailySummary : Comparable<DailySummary> {
    val date: Int
    override fun compareTo(other: DailySummary): Int = date.compareTo(other.date)
}

