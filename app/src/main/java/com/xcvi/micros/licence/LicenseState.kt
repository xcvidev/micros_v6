package com.xcvi.micros.licence

sealed interface LicenseState {
    data object Licensed : LicenseState
    data class NotLicensed(val reason: Int) : LicenseState
    data class Error(val errorCode: Int) : LicenseState
}