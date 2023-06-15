package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.Invoice
import com.chatchatabc.parking.model.dto.CreateInvoiceDTO
import com.chatchatabc.parking.model.pagination.Page
import com.chatchatabc.parking.model.pagination.Pagination
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class InvoiceAPI(client: HttpClient) : AbstractAPI(client) {
    val ENDPOINT: String = "/api/invoice"

    suspend fun getActiveInvoice(vehicleUuid: String): ApiResponse<Invoice?> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/active/$vehicleUuid")

    suspend fun getInvoice(invoiceUuid: String): ApiResponse<Invoice?> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/$invoiceUuid")

    suspend fun startInvoice(vehicleUuid: String, payload: CreateInvoiceDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/create/$vehicleUuid", payload)

    suspend fun endInvoice(invoiceUuid: String): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/end/$invoiceUuid")

    suspend fun payInvoice(invoiceUuid: String): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/pay/$invoiceUuid")

    suspend fun getEstimate(invoiceUuid: String): ApiResponse<Double> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/estimate/$invoiceUuid")

    /**
     * Get invoices by Vehicle
     */
    suspend fun getInvoicesByVehicle(
        vehicleUuid: String,
        pagination: Pagination? = null
    ): ApiResponse<Page<Invoice>> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/vehicle/$vehicleUuid", pagination = pagination)

    /**
     * Get invoices by Parking Lot
     */
    suspend fun getInvoicesByParkingLot(
        parkingLotUuid: String,
        pagination: Pagination? = null
    ): ApiResponse<Page<Invoice>> =
        makeRequest(
            HttpMethod.Get,
            "$ENDPOINT/parking-lot/$parkingLotUuid",
            pagination = pagination
        )
}