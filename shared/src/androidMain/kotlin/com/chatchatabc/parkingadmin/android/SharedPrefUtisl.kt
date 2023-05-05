package com.chatchatabc.parkingadmin.android

import android.content.SharedPreferences

fun SharedPreferences.getAuthToken(): String = getString("authToken", "") ?: ""