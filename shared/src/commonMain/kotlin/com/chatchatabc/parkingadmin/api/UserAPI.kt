package com.chatchatabc.parkingadmin.api

import com.chatchatabc.parkingadmin.model.User
import com.chatchatabc.parkingadmin.model.dto.UpdateUserDTO
import com.chatchatabc.parkingadmin.model.response.ApiResponse
import io.ktor.client.HttpClient

class UserAPI(val client: HttpClient, token: Token?): AbstractAPI(client, token) {
    init {
        ENDPOINT = "/api/user"
    }
    suspend fun getUser(token: String): ApiResponse<User> =
        apiGet("$ENDPOINT/me")

    suspend fun updateProfile(payload: UpdateUserDTO): ApiResponse<User> =
        apiPut("$ENDPOINT/update", payload)
}
