package com.chatchatabc.parkingadmin.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ParkingUploadResponse(
    val filename: String,
    val filesize: Int,
    val mimetype: String,
    val url: String,
    val createdAt: String,
    val fileOrder: Int
)