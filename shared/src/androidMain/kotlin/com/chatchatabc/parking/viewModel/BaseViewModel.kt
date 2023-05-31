package com.chatchatabc.parking.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel: ViewModel() {
    val isLoading = MutableStateFlow(false)

    fun load(action: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            action()
            isLoading.value = false
        }
    }
}