package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.Route
import com.chatchatabc.parking.model.pagination.Page
import com.chatchatabc.parking.model.pagination.Pagination
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class RouteAPI(client: HttpClient) : AbstractAPI(client) {
    val ENDPOINT = "/api/route"

    /**
     * Get all routes
     */
    suspend fun getAllRoutes(pagination: Pagination? = null): ApiResponse<Page<Route>> =
        makeRequest(HttpMethod.Get, ENDPOINT, pagination = pagination)
}