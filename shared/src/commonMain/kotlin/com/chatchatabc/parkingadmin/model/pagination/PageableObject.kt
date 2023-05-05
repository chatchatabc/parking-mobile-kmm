package com.chatchatabc.parkingadmin.model.pagination

import kotlinx.serialization.Serializable

@Serializable
data class PageableObject(
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean
)