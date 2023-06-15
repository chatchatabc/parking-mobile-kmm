package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.Jeepney
import com.chatchatabc.parking.model.dto.UpdateJeepneyLocationDTO
import com.chatchatabc.parking.model.pagination.Page
import com.chatchatabc.parking.model.pagination.Pagination
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class JeepneyAPI(client: HttpClient) : AbstractAPI(client) {
    val ENDPOINT = "/api/jeepney"

    /**
     * Get a Jeepney using an identifier (uuid, name, or plate number)
     */
    suspend fun getJeepney(jeepneyIdentifier: String): ApiResponse<Jeepney> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/$jeepneyIdentifier")

    /**
     * Get all Jeepneys with pagination
     */
    suspend fun getAllJeepneys(pagination: Pagination? = null): ApiResponse<Page<Jeepney>> =
        makeRequest(
            HttpMethod.Get, "$ENDPOINT/all",
            pagination = pagination
        )

    /**
     * Get all Jeepneys by route
     */
    suspend fun getAllJeepneysByRoute(
        routeUuid: String,
        pagination: Pagination? = null
    ): ApiResponse<Page<Jeepney>> =
        makeRequest(
            HttpMethod.Get, "$ENDPOINT/route/$routeUuid",
            pagination = pagination
        )

    /**
     * Update the location of a Jeepney
     */
    suspend fun updateJeepneyLocation(
        jeepneyIdentifier: String,
        payload: UpdateJeepneyLocationDTO
    ): ApiResponse<Unit> =
        makeRequest(HttpMethod.Put, "$ENDPOINT/update-location", payload)
}