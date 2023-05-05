package com.chatchatabc.parkingadmin.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parkingadmin.api.UserAPI
import com.chatchatabc.parkingadmin.model.dto.UpdateUserDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NewUserViewModel(
    val userAPI: UserAPI,
    val preferences: SharedPreferences
): ViewModel() {
    val Json = Json { ignoreUnknownKeys = true }

    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val username = MutableStateFlow("")

    val uiState: MutableStateFlow<NewUserState> = MutableStateFlow(NewUserState.INPUT)
    val isLoading = MutableStateFlow(false)

    val errors: MutableStateFlow<Map<String, String>> = MutableStateFlow(mapOf())

    fun updateUser() {
        val dto = UpdateUserDTO(
            firstName = firstName.value,
            lastName = lastName.value,
            email = email.value.let { it.ifBlank { null } },
            username = null
        )

        isLoading.value = true
        viewModelScope.launch {
            userAPI.updateProfile(dto).let {
                Log.d("ERROR", "ERROR? ${it.error}")
                if (!it.error) {
                    Log.d("ERROR", "Success: ${it.data}")
                    preferences.edit()
                        .putString("user", Json.encodeToString(it.data!!))
                        .apply()
                    uiState.value = NewUserState.SUBMITTED
                } else {
                    Log.d("ERROR", "Failed: ${it.message}")
                    errors.value = mapOf("updateUser" to it.message)
                }
            }
        }
        isLoading.value = false
    }

    fun validateAndSubmit() {
        errors.value = mapOf()
        if (firstName.value.isBlank()) errors.value = errors.value.plus("firstName" to "First name is required.")
        if (lastName.value.isBlank()) errors.value = errors.value.plus("lastName" to "Last name is required.")
        if (errors.value.isEmpty()) {
            updateUser()
        }
    }
}

enum class NewUserState {
    INPUT,
    SUBMITTED
}