package com.chatchatabc.parking.model

import kotlinx.serialization.Serializable

@Serializable
data class Member(
    val memberUuid: String,
    val email: String?,
    val username: String?,
//    val password: String?,
    val phone: String,
    val firstName: String?,
    val lastName: String?,
    val status: Int,
    val emailVerifiedAt: String?,
    val phoneVerifiedAt: String?,
    val createdAt: String?,
    val updatedAt: String,
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
