package com.chatchatabc.parking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatchatabc.parking.api.AbstractAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

open class BaseViewModel: ViewModel(), KoinComponent {
    val isLoading = MutableStateFlow(false)
    val token: String by inject(named("token"))

    fun setToken(vararg apis: AbstractAPI) {
        apis.forEach {
            it.setToken(token)
        }
    }

    fun load(action: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            action()
            isLoading.value = false
        }
    }
}