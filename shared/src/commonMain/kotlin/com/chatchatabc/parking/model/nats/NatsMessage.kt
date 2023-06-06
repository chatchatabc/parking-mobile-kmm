package com.chatchatabc.parking.model.nats

import kotlinx.serialization.Serializable

@Serializable
data class NatsMessageWithPayload<T>(
    val type: NatsEnums,
    val payload: T?
)

@Serializable
data class NatsMessage(
    val type: NatsEnums
)