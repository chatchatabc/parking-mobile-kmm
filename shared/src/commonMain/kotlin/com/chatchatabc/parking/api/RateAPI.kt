package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.Rate
import com.chatchatabc.parking.model.dto.UpdateRateDTO
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class RateAPI(client: HttpClient): AbstractAPI(client) {
    val ENDPOINT = "/api/rate"

    suspend fun updateRate(parkingLotUuid: String, payload: UpdateRateDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/update/$parkingLotUuid", payload)

    suspend fun getParkingLotRate(parkingLotUuid: String): ApiResponse<Rate> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/parkingLot/$parkingLotUuid")

    suspend fun getRate(rateUuid: String): ApiResponse<Unit> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/$rateUuid")
}