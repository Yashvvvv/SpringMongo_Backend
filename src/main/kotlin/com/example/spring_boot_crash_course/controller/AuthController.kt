package com.example.spring_boot_crash_course.controller

import com.example.spring_boot_crash_course.security.AuthService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller responsible for handling authentication-related API endpoints.
 * This includes registering new users, logging in, and refreshing tokens.
 *
 * All endpoints in this controller are prefixed with "/auth".
 *
 * @constructor Initializes the controller with the provided `AuthService` implementation
 * for handling authentication-related business logic.
 *
 * @param authService The service responsible for authentication operations.
 */
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    /**
     * Data class representing an authentication request.
     *
     * This class serves as a data model for user authentication requests,
     * such as registration and login. It contains the user's email and
     * password, both of which are validated for format and content.
     *
     * @property email The email address provided by the user. It must
     * be a valid email format.
     * @property password The password provided by the user. It must
     * contain at least one lowercase letter, one uppercase letter,
     * one digit, and be at least 9 characters long.
     */
    data class AuthRequest(
        @field:Email(message = "Invalid email format.")
        val email: String,
        @field:Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$",
            message = "Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character."
        )
        val password: String
    )

    /**
     * Represents a request to refresh an expired or soon-to-expire token.
     *
     * This data class is used in authentication flows to exchange a valid but expired refresh token
     * for a new access token. The `refreshToken` is required to be provided by the client in order
     * to successfully process the token refresh operation.
     *
     * @property refreshToken The token provided by the client, which is used to obtain a new access token.
     */
    data class RefreshRequest(
        val refreshToken: String,
    )

    /**
     * Handles user registration by accepting a request with email and password.
     *
     * This endpoint validates the provided credentials, checks if the email
     * is valid and unique, and creates a new user in the system with a hashed password.
     *
     * @param body The request body containing the user's email and password.
     *             The email must be in a valid email format, and the password
     *             must meet the specified strength criteria (minimum 9 characters,
     *             including at least one uppercase letter, one lowercase letter, and one digit).
     */
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody body: AuthRequest
    ) {
        authService.register(body.email, body.password)

    }

    /**
     * Handles user login by authenticating the provided credentials.
     *
     * @param body the request body containing the user's email and password for authentication
     * @return a pair of access and refresh tokens upon successful authentication
     */
    @PostMapping("/login")
    fun login(
        @RequestBody body: AuthRequest
    ): AuthService.TokenPair {
        return authService.login(body.email, body.password)
    }

    /**
     * Handles the token refresh operation.
     *
     * This method allows clients to refresh their authentication tokens
     * by providing a valid refresh token. It will return a new pair of
     * access and refresh tokens if the operation is successful.
     *
     * @param body The request body containing the refresh token required for the refresh operation.
     * @return A `TokenPair` containing a new access token and refresh token.
     */
    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: RefreshRequest
    ): AuthService.TokenPair {
        return authService.refresh(body.refreshToken)

    }
}