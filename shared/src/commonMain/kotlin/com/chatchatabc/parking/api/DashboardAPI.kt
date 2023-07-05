package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.DashboardStatistics
import com.chatchatabc.parking.model.Vehicle
import com.chatchatabc.parking.model.pagination.Page
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class DashboardAPI(client: HttpClient): AbstractAPI(client) {
    val ENDPOINT = "/api/dashboard"

    suspend fun getDashboardStatistics(): ApiResponse<DashboardStatistics> =
        makeRequest(HttpMethod.Get, "$ENDPOINT")

    suspend fun searchForVehicle(query: String, parkingLotUuid: String): ApiResponse<Page<Vehicle>> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/search/$parkingLotUuid/$query")

    suspend fun getVehicle(plateNumber: String, parkingLotUuid: String): ApiResponse<Vehicle> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/search/$parkingLotUuid/$plateNumber")
}