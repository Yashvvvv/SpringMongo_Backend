package com.example.spring_boot_crash_course.database.repository

import com.example.spring_boot_crash_course.database.model.RefreshToken
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Repository interface for managing `RefreshToken` entities in the MongoDB database.
 * Inherits basic CRUD and query execution methods from `MongoRepository`.
 * Provides additional functionality for querying and removing specific refresh tokens
 * based on user ID and hashed token.
 */
interface RefreshTokenRepository: MongoRepository<RefreshToken, ObjectId> {
     /**
      * Finds a refresh token by the user's unique identifier and the hashed version of the token.
      *
      * This method retrieves a `RefreshToken` object associated with the specified user ID and hashed token.
      * It is typically used to validate the existence of a token during authentication or refresh token processes.
      *
      * @param userId The unique identifier of the user associated with the refresh token.
      * @param hashedToken The hashed version of the refresh token to look for.
      * @return The `RefreshToken` object if one exists matching the given criteria, or `null` if no match is found.
      */
     fun findByUserIdAndHashedToken(userId: ObjectId, hashedToken: String): RefreshToken?
     /**
      * Deletes a refresh token entry associated with the given user ID and hashed token.
      *
      * This method removes a specific refresh token from the database that matches
      * both the user ID and the hashed token, ensuring it is no longer valid for authentication.
      *
      * @param userId The unique identifier of the user associated with the refresh token.
      * @param hashedToken The hashed value of the refresh token to be deleted.
      */
     fun deleteByUserIdAndHashedToken(userId: ObjectId, hashedToken: String)
}