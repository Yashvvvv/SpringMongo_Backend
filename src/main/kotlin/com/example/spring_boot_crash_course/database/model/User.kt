package com.example.spring_boot_crash_course.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

// hash("Hello") -> gfd8s435gd48sg38sd

/**
 * Represents a user in the system.
 *
 * This class is used to store user details, such as the email address and hashed password.
 * The `id` property is an automatically generated unique identifier for each user.
 *
 * @property email The email address of the user.
 * @property hashedPassword The hashed version of the user's password.
 * @property id The unique identifier of the user, automatically generated if not provided.
 */
@Document("users")
data class User(
    val email: String,
    val hashedPassword: String,
    @Id val id: ObjectId = ObjectId()
)
