package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.model.dto.UpdateUserDTO
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

    val uiState: MutableStateFlow<NewUserState> = MutableStateFlow(NewUserState.INPUT)
    val isLoading = MutableStateFlow(false)

    val errors: MutableStateFlow<Map<String, String>> = MutableStateFlow(mapOf())

    fun updateUser() {
        val dto = UpdateUserDTO(
            firstName = firstName.value,
            lastName = lastName.value,
            email = email.value.let { it.ifBlank { null } }
        )

        isLoading.value = true
        viewModelScope.launch {
            val updateProfileResult = userAPI.updateProfile(dto)
            if (updateProfileResult.errors.isNotEmpty()) {
                Log.d("ERROR", "Failed: ${updateProfileResult.errors[0].message ?: "Unknown error"}")
                errors.value = mapOf("updateUser" to (updateProfileResult.errors[0].message ?: "Unknown error"))
                return@launch
            }

            val getUserResult = userAPI.getUser()
            if (getUserResult.errors.isNotEmpty()) {
                Log.d("ERROR", "Failed: ${getUserResult.errors[0].message ?: "Unknown error"}")
                errors.value = mapOf("updateUser" to (getUserResult.errors[0].message ?: "Unknown error"))
                return@launch
            }

            preferences.edit()
                .putString("user", Json.encodeToString(getUserResult.data!!))
                .apply()

            uiState.value = NewUserState.SUBMITTED
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