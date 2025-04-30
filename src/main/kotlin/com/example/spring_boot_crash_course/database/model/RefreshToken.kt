package com.example.spring_boot_crash_course.database.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * Represents a refresh token associated with a user for authentication purposes.
 *
 * This class is used to manage and store the refresh tokens issued during the authentication process.
 * Each refresh token is associated with a specific user and has an expiration time.
 *
 * @property userId The unique identifier of the user to whom this refresh token belongs.
 * @property expireAt The expiration time of the refresh token. It indicates when the token will no longer be valid.
 * @property hashedToken The securely hashed version of the refresh token value to ensure secure storage.
 * @property createdDate The timestamp of when the refresh token was created. Defaults to the current time.
 */
@Document("refresh_tokens")
data class RefreshToken(
    val userId: ObjectId,
    @Indexed(expireAfter = "0s")
    val expireAt: Instant,
    val hashedToken: String,
    val createdDate: Instant = Instant.now()
)
