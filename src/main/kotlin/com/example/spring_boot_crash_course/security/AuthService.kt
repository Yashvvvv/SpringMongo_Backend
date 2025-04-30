package com.example.spring_boot_crash_course.security

import com.example.spring_boot_crash_course.database.model.RefreshToken
import com.example.spring_boot_crash_course.database.model.User
import com.example.spring_boot_crash_course.database.repository.RefreshTokenRepository
import com.example.spring_boot_crash_course.database.repository.UserRepository
import jakarta.validation.constraints.Email
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

/**
 * Service responsible for authentication-related operations, such as user registration,
 * login, token generation, and token refreshing. It interacts with dependencies like the
 * JWT service, hashing encoder, and repositories for user and refresh token management.
 */
@Service
class AuthService(
    private val jwtService: JwtService,
    private val hashEncoder: HashEncoder,
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    /**
     * Represents a pair of tokens typically used for authentication.
     *
     * A `TokenPair` object contains an access token and a refresh token,
     * which are commonly used in token-based authentication mechanisms.
     *
     * @property accessToken The short-lived token used for accessing resources.
     * @property refreshToken The long-lived token used to obtain a new access token.
     */
    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    /**
     * Registers a new user with the provided email and password.
     *
     * This method verifies if a user with the given email already exists. If the user exists,
     * it throws a conflict error. If not, the user's password is hashed, and a new user is
     * saved in the repository.
     *
     * @param email The email address of the new user. This should be a valid, non-empty string.
     * @param password The plain-text password for the new user. This should be a non-empty string.
     * @return The newly created `User` object.
     * @throws ResponseStatusException with HttpStatus.CONFLICT if a user with the given email already exists.
     */
    fun register(email: String, password: String): User {
        val user = userRepository.findByEmail(email.trim())
        if(user != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT,"User already exists.")
        }
        return userRepository.save(
            User(
                email = email,
                hashedPassword = hashEncoder.encode(password)
            )
        )
    }

    /**
     * Authenticates a user based on their email and password and generates new access and refresh tokens.
     *
     * This method validates the provided email and password, generating a new access token and refresh token
     * if the credentials are correct. If the validation fails, an exception is thrown.
     *
     * @param email The email address of the user attempting to log in.
     * @param password The plaintext password of the user attempting to log in.
     * @return A `TokenPair` containing a new access token and refresh token for the authenticated user.
     * @throws BadCredentialsException If the email is not associated with a user or the password is incorrect.
     */
    fun login(email: String, password: String): TokenPair {

        val user = userRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid Exceptions") as Throwable

        if(!hashEncoder.matches(password, user.hashedPassword))
            throw BadCredentialsException("Invalid Credentials") as Throwable

        val newAccessToken = jwtService.generateAccessToken(user.id.toString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toString())

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    /**
     * Refreshes the access and refresh tokens using a valid refresh token.
     *
     * This method verifies the provided refresh token, extracts the associated user ID,
     * ensures the token is valid and recognized, deletes the current token, and generates
     * a new pair of access and refresh tokens. The new refresh token is securely stored
     * against the user for future operations.
     *
     * @param refreshToken The existing refresh token provided by the client for renewal.
     * @return A new `TokenPair`, containing both an access token and a refresh token.
     * @throws ResponseStatusException If the refresh token is invalid, unrecognized, expired,
     *                                 or the associated user is not found.
     */
    @Transactional
    fun refresh(refreshToken: String): TokenPair{
        if(!jwtService.validateRefreshToken(refreshToken)) {
            throw ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh token.")
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            throw ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh token.")
        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id, hashed)
            ?: throw ResponseStatusException(
                HttpStatusCode.valueOf(401),"refresh token is not recognised (maybe used or expired).")

        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id, hashed)

        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)

        storeRefreshToken(user.id, newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    /**
     * Stores a refresh token for the given user in the database.
     *
     * The method hashes the provided raw refresh token, calculates its expiration time,
     * and persists it as a `RefreshToken` entity in the repository.
     *
     * @param userId The unique identifier of the user to whom the refresh token belongs.
     * @param rawRefreshToken The raw refresh token that needs to be securely stored.
     */
    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiryAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                hashedToken = hashed,
                expireAt = expiryAt
            )
        )
    }

    /**
     * Generates a SHA-256 hash of the provided token and encodes it using Base64 encoding.
     *
     * @param token the input token to be hashed
     * @return the Base64-encoded hash of the input token
     */
    private fun hashToken(token: String): String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.toByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}