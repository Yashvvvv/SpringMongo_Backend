package com.example.spring_boot_crash_course.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.Base64
import java.util.Date


/**
 * The JwtService class is responsible for handling JSON Web Token (JWT) operations
 * such as generating, validating, and parsing tokens. It supports both access and refresh tokens
 * and uses symmetric-key cryptography for signing and verification.
 */
@Service
class JwtService(
    @Value("\${jwt.secret}") private val jwtSecret: String
) {

    /**
     * Represents the cryptographic key used for signing and verifying JSON Web Tokens (JWTs).
     * It is initialized with a secret key encoded as a Base64 string, which is decoded and
     * transformed into a secure HMAC-SHA key.
     *
     * This key is critical for token integrity and security, ensuring that tokens cannot
     * be tampered with or generated without the proper credentials.
     */
    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    /**
     * Represents the validity duration of an access token in milliseconds.
     *
     * This duration is used to define the expiration period for generated access tokens,
     * ensuring that they remain valid for a limited, predefined time. The default value is
     * set to 15 minutes (15 * 60 * 1,000 milliseconds).
     *
     * Access tokens are short-lived credentials designed to secure interactions by limiting
     * their usability within this timeframe, after which users are required to reauthenticate
     * or obtain a new token.
     */
    private val accessTokenValidityMs  = 15 * 60 * 1_000L      // 15 minutes
    /**
     * Represents the validity duration for a refresh token in milliseconds.
     *
     * This value dictates the expiration time for the refresh token, which is set to 30 days.
     * It is used within the JWT-based authentication process to manage token renewal lifecycle.
     */
    val refreshTokenValidityMs = 30L * 24 * 60 * 60 * 1_000  // 30 days

    /**
     * Generates a JSON Web Token (JWT) with the specified user ID, type, and expiration duration.
     *
     * @param userId The unique identifier of the user for whom the token is being generated.
     * @param type The type of token being generated (e.g., "access" or "refresh").
     * @param expiry The duration (in milliseconds) until the token expires.
     * @return A signed JWT string representing the generated token.
     */
    private fun generateToken(
        userId: String,
        type: String,
        expiry: Long
    ): String {

        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    /**
     * Generates an access token for the specified user.
     *
     * @param userId The unique identifier of the user for whom the access token is generated.
     * @return A String representing the generated access token.
     */
    fun generateAccessToken(userId: String): String {
        return generateToken(userId, "access", accessTokenValidityMs)
    }

    /**
     * Generates a refresh token for the specified user ID.
     *
     * @param userId The unique identifier of the user for whom the refresh token is generated.
     * @return A string representation of the generated refresh token.
     */
    fun generateRefreshToken(userId: String): String {
        return generateToken(userId, "refresh", refreshTokenValidityMs)
    }

    /**
     * Validates if the given JWT token is a valid access token.
     *
     * @param token the JWT token to validate
     * @return `true` if the token is a valid access token, `false` otherwise
     */
    fun validateAccessToken(token: String): Boolean {
        val claims = parseALLClaims(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "access"
    }

    /**
     * Validates whether the provided JWT token is a refresh token by analyzing its claims.
     *
     * @param token the JWT token to validate.
     * @return true if the token is a valid refresh token, false otherwise.
     */
    fun validateRefreshToken(token: String): Boolean {
        val claims = parseALLClaims(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "refresh"
    }

    /**
     * Extracts the user ID from a given JWT token.
     *
     * @param token The JWT token string. This can include the "Bearer " prefix,
     *              which will be removed internally to extract the raw token.
     * @return The user ID extracted from the JWT token's subject field.
     * @throws ResponseStatusException If the token is invalid or cannot be parsed.
     */
    //Authorization: Bearer <token> Cut the bearer to retrieve raw token
    fun getUserIdFromToken(token: String): String {
        val claims = parseALLClaims(token) ?: throw ResponseStatusException(
            HttpStatusCode.valueOf(401),"Invalid token")
        return claims.subject
    }

    /**
     * Parses the provided JWT token and extracts the claims if the token is valid.
     * The method removes the "Bearer " prefix if present, and attempts to parse the token using the configured secret key.
     *
     * @param token the JWT token to be parsed, possibly prefixed with "Bearer ".
     * @return the claims contained within the token, or null if the token is invalid or parsing fails.
     */
    fun parseALLClaims(token: String): Claims? {
        val rawToken = if(token.startsWith("Bearer ")){
            token.removePrefix("Bearer ")
        }
        else token
        return try {
            Jwts.parser() //parse to understand claims
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(rawToken)
                .payload
        }
        catch(e: Exception) {
            null
        }
    }
}