package com.chatchatabc.parking.api

import com.chatchatabc.parking.Config.BASE_URL
import com.chatchatabc.parking.model.ParkingLot
import com.chatchatabc.parking.model.ParkingLotImage
import com.chatchatabc.parking.model.dto.ParkingLotDraftDTO
import com.chatchatabc.parking.model.pagination.Page
import com.chatchatabc.parking.model.pagination.Pagination
import com.chatchatabc.parking.model.response.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

typealias Token = String

class ParkingAPI(val client: HttpClient): AbstractAPI(client) {
    val ENDPOINT = "/api/parking-lot"

    suspend fun setToPending(): ApiResponse<ParkingLot> =
        makeRequest(HttpMethod.Put, "$ENDPOINT/set-pending")

    suspend fun createDraft(payload: ParkingLotDraftDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Post, "$ENDPOINT/register", payload)

    suspend fun saveDraft(payload: ParkingLotDraftDTO): ApiResponse<Unit> =
        makeRequest(HttpMethod.Put, "$ENDPOINT/update", payload)

    suspend fun getParkingLot(): ApiResponse<ParkingLot> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/me")

    suspend fun getParkingLot(parkingLotUuid: String): ApiResponse<ParkingLot> =
        makeRequest(HttpMethod.Get, "$ENDPOINT/get/$parkingLotUuid")

    suspend fun uploadImage(image: ByteArray, onProgress: (Int) -> Unit): ApiResponse<ParkingLotImage> =
        makeUploadRequest( "$ENDPOINT/upload-image", image, onUploadUpdate = { sent, totalSize ->
            onProgress((sent.toDouble() / totalSize.toDouble() * 100).toInt())
        })

    /**
     * Let the image loader library handle the loading of the image.
     * This function will only return the url of the image.
     */
    fun getImage(id: String): String = "$BASE_URL$ENDPOINT/image/$id"

    suspend fun getImages(parkingLotUuid: String, pagination: Pagination? = null): ApiResponse<Page<ParkingLotImage>> =
        makeRequest(
            HttpMethod.Get,
            "$ENDPOINT/images/$parkingLotUuid",
            pagination = pagination,
        )

    suspend fun getAllParkingLots(): ApiResponse<Page<ParkingLot>> =
        makeRequest(
            HttpMethod.Get,
            "$ENDPOINT/get-all",
        )
}