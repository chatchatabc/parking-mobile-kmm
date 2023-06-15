package com.chatchatabc.parking.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.UserAPI
import com.chatchatabc.parking.model.dto.UpdateUserDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class NewUserViewModel(
    val userAPI: UserAPI,
    val preferences: SharedPreferences
): BaseViewModel(userAPI) {
    val Json = Json { ignoreUnknownKeys = true }

    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val email = MutableStateFlow("")

    val uiState: MutableStateFlow<NewUserState> = MutableStateFlow(NewUserState.INPUT)
    val loadState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val errors: MutableStateFlow<Map<String, String>> = MutableStateFlow(mapOf())

    fun updateUser() = flow {
        emit(true)
        val dto = UpdateUserDTO(
            firstName = firstName.value,
            lastName = lastName.value,
            email = email.value.let { it.ifBlank { null } }
        )

        val updateProfileResult = userAPI.updateProfile(dto)
        if (updateProfileResult.errors.isNotEmpty()) {
            Timber.e("Failed: ${updateProfileResult.errors[0].message ?: "Unknown error"}")
            errors.value = mapOf("updateUser" to (updateProfileResult.errors[0].message ?: "Unknown error"))
            return@flow
        }

        val getUserResult = userAPI.getUser()
        if (getUserResult.errors.isNotEmpty()) {
            Timber.e("Failed: ${getUserResult.errors[0].message ?: "Unknown error"}")
            errors.value = mapOf("updateUser" to (getUserResult.errors[0].message ?: "Unknown error"))
            return@flow
        }

        preferences.edit()
            .putString("user", Json.encodeToString(getUserResult.data!!))
            .apply()

        emit(false)
        uiState.value = NewUserState.SUBMITTED
    }.onEach {
        loadState.value = it
    }.launchIn(viewModelScope)

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