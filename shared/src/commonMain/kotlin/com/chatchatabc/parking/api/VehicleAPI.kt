package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.model.dto.NewVehicleDTO
import com.chatchatabc.parking.model.dto.UpdateVehicleDTO
import com.chatchatabc.parking.model.pagination.Page
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class VehicleAPI(val client: HttpClient) : AbstractAPI(client) {
    val ENDPOINT = "/api/vehicle"

    suspend fun register(payload: NewVehicleDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/register", payload)

    // TODO: Replace with pagination?
    suspend fun getAllVehicles(): ApiResponse<Page<Vehicle>> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/my-vehicles?limit=1000")

    suspend fun getVehicle(vehicleUuid: String): ApiResponse<Vehicle> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/$vehicleUuid")

    /**
     * Update vehicle
     */
    suspend fun updateVehicle(vehicleUuid: String, payload: UpdateVehicleDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Put, "$ENDPOINT/update/$vehicleUuid", payload)

    // TODO: Add the add user and remove user endpoints (not yet needed)
}