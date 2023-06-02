package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.model.dto.NewVehicleDTO
import com.chatchatabc.parking.model.pagination.Page
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class VehicleAPI(val client: HttpClient): AbstractAPI(client) {
    val ENDPOINT = "/api/vehicle"

    suspend fun register(payload: NewVehicleDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/register", payload)

    suspend fun getAllVehicles(): ApiResponse<Page<Vehicle>> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/get-my-vehicles?limit=1000")
}