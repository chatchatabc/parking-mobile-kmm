package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.ProfileAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    val profileAPI: ProfileAPI,
    val sharedPreferences: SharedPreferences
): BaseViewModel() {

    val logoutPopupOpened = MutableStateFlow(false)

    init {
        setToken(profileAPI)
    }

    fun clearAuthToken() {
        viewModelScope.launch {
            // Change activity to loginActivity
            sharedPreferences.edit().remove("authToken").apply()
            profileAPI.logout()
        }
    }
}