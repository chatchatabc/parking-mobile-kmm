package com.chatchatabc.parking.api

import com.chatchatabc.parking.Config.BASE_URL
import com.chatchatabc.parking.model.dto.LoginDTO
import com.chatchatabc.parking.model.dto.OTPLoginDTO
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

class LoginAPI(client: HttpClient): com.chatchatabc.parking.api.AbstractAPI(client) {
    private val ENDPOINT = "/api/auth"

    suspend fun login(payload: LoginDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/login", payload)

    suspend fun verifyOTP(payload: OTPLoginDTO): HttpResponse =
        httpClient.post("$BASE_URL$ENDPOINT/verify/manager") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
}