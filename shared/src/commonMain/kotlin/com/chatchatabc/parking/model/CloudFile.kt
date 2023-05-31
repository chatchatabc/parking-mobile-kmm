package com.chatchatabc.parking.model

data class CloudFile(
    val id: Int,
    val bucket: String?,
    val key: String,
    val uploadedBy: Int,
    val name: String,
    val size: Int,
    val mimeType: String,
    val tags: Any?, // Adjust to the actual type of "tags" based on your needs
    val status: Int,
    val createdAt: String,
    val updatedAt: String
)