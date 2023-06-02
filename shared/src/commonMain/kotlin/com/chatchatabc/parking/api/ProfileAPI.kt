package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class ProfileAPI(val client: HttpClient): AbstractAPI(client) {
    val ENDPOINT = "/api/profile"

    suspend fun logout(): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/logout")
}