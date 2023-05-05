package com.chatchatabc.parkingadmin.api

import com.chatchatabc.parkingadmin.model.ParkingLot
import com.chatchatabc.parkingadmin.model.dto.ParkingLotDraftDTO
import com.chatchatabc.parkingadmin.model.pagination.Page
import com.chatchatabc.parkingadmin.model.pagination.Pagination
import com.chatchatabc.parkingadmin.model.response.ApiResponse
import com.chatchatabc.parkingadmin.model.response.ParkingUploadResponse
import io.ktor.client.HttpClient

typealias Token = String

class ParkingAPI(val client: HttpClient, token: Token): AbstractAPI(client, token) {
    init {
        ENDPOINT = "/api/parking-lot"
    }

    suspend fun createDraft(payload: ParkingLotDraftDTO): ApiResponse<ParkingLot> =
        apiPost("$ENDPOINT/register", payload)

    suspend fun saveDraft(id: String, payload: ParkingLotDraftDTO): ApiResponse<ParkingLot> =
        apiPut("$ENDPOINT/update/${id}", payload)

    suspend fun setToPending(id: String): ApiResponse<ParkingLot> =
        apiPut("$ENDPOINT/set-pending/${id}")

    suspend fun getParkingLot(id: String): ApiResponse<ParkingLot> =
        apiGet("$ENDPOINT/get/${id}")
    suspend fun getParkingLots(pagination: Pagination = Pagination()): ApiResponse<Page<List<ParkingLot>>> =
        apiGet("$ENDPOINT/get-managing", pagination)

    suspend fun uploadImage(id: String, image: ByteArray): ApiResponse<ParkingUploadResponse> =
        apiUpload("$ENDPOINT/upload-image/${id}", image)


}