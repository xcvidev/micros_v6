package com.xcvi.micros.ui.core.utils

import android.content.Context
import com.xcvi.micros.R

fun getMealName(applicationContext: Context, meal: Int) = when(meal){
        1 -> applicationContext.getString(R.string.meal1)
        2 -> applicationContext.getString(R.string.meal2)
        3 -> applicationContext.getString(R.string.meal3)
        4 -> applicationContext.getString(R.string.meal4)
        5 -> applicationContext.getString(R.string.meal5)
        6 -> applicationContext.getString(R.string.meal6)
        7 -> applicationContext.getString(R.string.meal7)
        8 -> applicationContext.getString(R.string.meal8)
        else -> ""
}

