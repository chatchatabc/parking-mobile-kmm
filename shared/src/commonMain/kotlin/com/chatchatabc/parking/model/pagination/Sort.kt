package com.chatchatabc.parking.model.pagination

import kotlinx.serialization.Serializable

@Serializable
data class Sort(
    val empty: Boolean,
    val unsorted: Boolean,
    val sorted: Boolean
)