package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.ProfileAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    val profileAPI: ProfileAPI,
    val sharedPreferences: SharedPreferences
): BaseViewModel(profileAPI) {

    val logoutPopupOpened = MutableStateFlow(false)

    fun clearAuthToken() {
        viewModelScope.launch {
            // Change activity to loginActivity
            sharedPreferences.edit().remove("authToken").apply()
            profileAPI.logout()
        }
    }
}