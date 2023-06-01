package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.User
import com.chatchatabc.parking.model.dto.UpdateUserDTO
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class UserAPI(val client: HttpClient, token: Token?): com.chatchatabc.parking.api.AbstractAPI(client, token) {
    val ENDPOINT = "/api/user"
    suspend fun getUser(): ApiResponse<User> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/me")

    suspend fun updateProfile(payload: UpdateUserDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Put, "$ENDPOINT/update", payload)
}
