package com.chatchatabc.parking.api

import com.chatchatabc.parking.model.Invoice
import com.chatchatabc.parking.model.dto.CreateInvoiceDTO
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class InvoiceAPI(client: HttpClient): AbstractAPI(client) {
    val ENDPOINT: String = "/api/invoice"

    suspend fun getActiveInvoice(vehicleUuid: String): ApiResponse<Invoice?> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/get/active/$vehicleUuid")

    suspend fun startInvoice(vehicleUuid: String, payload: CreateInvoiceDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/create/$vehicleUuid")

    suspend fun endInvoice(vehicleUuid: String): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/end/$vehicleUuid")

    suspend fun payInvoice(invoiceUuid: String): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/pay/$invoiceUuid")
}