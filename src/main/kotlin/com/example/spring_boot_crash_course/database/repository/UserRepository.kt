package com.example.spring_boot_crash_course.database.repository

import com.example.spring_boot_crash_course.database.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Repository interface for managing `User` entities in the database.
 * Inherits basic CRUD and query execution methods from `MongoRepository`.
 * Provides additional methods for querying `User` entities by custom parameters.
 */
interface UserRepository: MongoRepository<User, ObjectId> {
    /**
     * Finds a user by their email address.
     *
     * This method is responsible for retrieving a `User` object associated with the provided email.
     *
     * @param email The email address of the user to look for. Must be a non-null string.
     * @return The `User` object if found, or `null` if no user exists with the given email.
     */
    fun findByEmail(email: String): User?
}