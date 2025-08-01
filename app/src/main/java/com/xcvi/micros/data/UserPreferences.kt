package com.xcvi.micros.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf

object UserPreferences {
    private const val PREFS_NAME = "MyPrefs"
    private const val MEAL_PREFIX = "meal_"

    private lateinit var sharedPref: SharedPreferences

    fun init(context: Context) {
        sharedPref = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun favoritesFlow(slots: Int = 8): Flow<Set<Int>> {
        if (!this::sharedPref.isInitialized) {
            // Safe default: no favorites yet
            return flowOf(emptySet())
        }

        fun snapshot(): Set<Int> =
            (1..slots).filter { isMealFavorite(it) }.toSet()

        return callbackFlow {
            val sendSnapshot = { trySend(snapshot()).isSuccess }

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key?.startsWith(MEAL_PREFIX) == true) {
                    sendSnapshot()
                }
            }

            // initial emit
            sendSnapshot()

            sharedPref.registerOnSharedPreferenceChangeListener(listener)
            awaitClose { sharedPref.unregisterOnSharedPreferenceChangeListener(listener) }
        }
            .conflate()
            .distinctUntilChanged()
    }

    private fun mealKey(mealNumber: Int) = "meal_$mealNumber"

    fun setMealFavorite(mealNumber: Int, favorite: Boolean) {
        saveBoolean(mealKey(mealNumber), favorite)
    }

    fun isMealFavorite(mealNumber: Int): Boolean {
        return getBoolean(mealKey(mealNumber), false)
    }

    fun saveString(key: String, value: String) {
        if (!this::sharedPref.isInitialized) {
            return
        }
        sharedPref.edit { putString(key, value) }
    }

    fun getString( key: String, defaultValue: String? = null): String? {
        return if (this::sharedPref.isInitialized) {
            sharedPref.getString(key, defaultValue)
        } else {
            null
        }
    }

    private fun saveBoolean(key: String, value: Boolean) {
        if (!this::sharedPref.isInitialized) {
            return
        }
        sharedPref.edit { putBoolean(key, value) }
    }

    private fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        if (!this::sharedPref.isInitialized) {
            return defaultValue
        }
        return sharedPref.getBoolean(key, defaultValue)
    }
}