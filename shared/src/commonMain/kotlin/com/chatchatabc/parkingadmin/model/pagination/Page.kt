package com.chatchatabc.parkingadmin.model.pagination

import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val totalPages: Int,
    val totalElements: Long,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val content: T,
    val number: Int,
    val sort: Sort,
    val numberOfElements: Int,
    val pageable: PageableObject,
    val empty: Boolean
)

