package com.xcvi.micros.licence

import java.util.UUID
import com.xcvi.micros.data.UserPreferences

object InstallIdProvider {
    private const val KEY = "install_id"
    fun get(): String {
        val res = UserPreferences.getString(KEY, null)
        return if (res == null) {
            val uid = UUID.randomUUID().toString()
            UserPreferences.saveString(KEY, uid)
            uid
        } else {
            res
        }
    }
}
