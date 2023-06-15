package com.chatchatabc.parking.model.response

/**
 * # ApiCall
 *
 * A generic class that holds the state of an asynchronous call. This allows us to implement
 * loading behavior in the UI. The state of the call is represented by the [State] enum.
 * The response of the call is represented by the [response] property.
 *
 * @param state The state of the API call.
 * @param response The response of the API call.
 *
 * // Loading
 *
 */
data class FlowCall<T>(
    val state: FlowCall.State,
    val response: T? = null,
    val readableError: String? = null
) {
    companion object {
        fun <T> nothing() = FlowCall<T>(State.NOTHING)
        fun <T> loading() = FlowCall<T>(State.LOADING)
        fun <T> success(response: T? = null) = FlowCall<T>(State.SUCCESS, response)
        fun <T> error(response: T? = null, message: String = "") = FlowCall<T>(State.ERROR, response, message)
    }

    enum class State {
        LOADING,
        SUCCESS,
        ERROR,
        NOTHING
    }

    val isLoading: Boolean
        get() = state == State.LOADING

    val isError: Boolean
        get() = state == State.ERROR

    val isSuccess: Boolean
        get() = state == State.SUCCESS
}

/**
 * Transforms an [ApiResponse] into an [FlowCall].
 */
val <T> ApiResponse<T>.flowCall: FlowCall<ApiResponse<T>>
    get() = when {
            this.errors.isEmpty() -> FlowCall.success(this)
            else -> FlowCall.error(this)
        }

val <T> T.flowCall: FlowCall<T>
    get() = FlowCall.success(this)

val <T> FlowCall<T>.error: FlowCall<T>
    get() = FlowCall.error(this.response, this.readableError ?: "")