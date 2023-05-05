package com.chatchatabc.parkingadmin.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: String,
    val email: String?,
    val username: String?,
    val phone: String,
    val firstName: String?,
    val lastName: String?,
    val createdAt: String?,
    val updatedAt: String,
    val emailVerifiedAt: String?,
    val phoneVerifiedAt: String?,
    val enabled: Boolean = false,
    val accountNonExpired: Boolean = false,
    val credentialsNonExpired: Boolean = false,
    val accountNonLocked: Boolean = false,
    val authorities: List<GrantedAuthority> = listOf()
)

@Serializable
data class GrantedAuthority(
    val authority: String
)
