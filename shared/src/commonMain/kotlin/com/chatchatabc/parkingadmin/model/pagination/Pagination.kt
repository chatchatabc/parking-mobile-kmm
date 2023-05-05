package com.chatchatabc.parkingadmin.model.pagination

import kotlinx.serialization.Serializable

@Serializable
data class Pagination(
    val page: Int = 0,
    val size: Int = 10,
    val sort: List<String> = listOf()
)
