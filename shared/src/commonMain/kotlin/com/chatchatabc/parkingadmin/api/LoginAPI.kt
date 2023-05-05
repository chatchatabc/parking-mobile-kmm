package com.chatchatabc.parkingadmin.api

import com.chatchatabc.parkingadmin.model.dto.LoginDTO
import com.chatchatabc.parkingadmin.model.dto.OTPLoginDTO
import com.chatchatabc.parkingadmin.model.response.ApiResponse
import com.chatchatabc.parkingadmin.model.response.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LoginAPI(client: HttpClient): AbstractAPI(client) {
    init {
        ENDPOINT = "/api/auth"
    }

    suspend fun login(payload: LoginDTO): ApiResponse<LoginResponse> =
        apiPost("$ENDPOINT/login", payload)

    suspend fun verifyOPT(payload: OTPLoginDTO): HttpResponse =
        httpClient.post("$ENDPOINT/verify/manager") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
}