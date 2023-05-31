package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class VehicleAPI(val client: HttpClient, token: Token?): AbstractAPI(client, token) {
    val ENDPOINT = "/api/vehicle"

    suspend fun register(payload: NewVehicleDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/register", payload)

    suspend fun getAllVehicles(): ApiResponse<List<Vehicle>> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/get-my-vehicles")
}