package com.example.roleora.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local User profile entity for offline authentication caching and offline state persistence.
 * Note: Never stores passwords, tokens or credentials.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val lastLoginAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Session tracking entity for active user session management and offline login support.
 */
@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val sessionId: String,
    val userId: String,
    val deviceId: String = "",
    val deviceName: String = "Android Device",
    val loginProvider: String = "google", // google, email, demo
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = 0L
)
