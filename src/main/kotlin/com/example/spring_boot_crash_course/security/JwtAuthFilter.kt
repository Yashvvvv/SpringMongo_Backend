package com.example.spring_boot_crash_course.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * A custom authentication filter for processing JSON Web Token (JWT)-based authentication.
 * This filter intercepts HTTP requests, extracts any Bearer token from the Authorization
 * header, validates it, and, if successful, sets the appropriate authentication context
 * in the `SecurityContextHolder`.
 *
 * This filter extends `OncePerRequestFilter`, ensuring it executes once for each incoming request.
 *
 * @constructor Initializes the filter with the provided `JwtService`.
 * @param jwtService The service responsible for JWT generation, validation, and processing.
 */
@Component
class JwtAuthFilter(
    private val jwtService: JwtService
): OncePerRequestFilter() {

    /**
     * Filters incoming HTTP requests by checking for a valid JWT authorization header.
     *
     * This method intercepts requests, extracts the "Authorization" header, verifies its validity as a JWT
     * access token, and sets the security context with the authenticated user's information if the token is valid.
     * If the "Authorization" header is absent, invalid, or improperly formatted, the request proceeds without setting the authentication context.
     *
     * @param request The incoming HTTP request containing the "Authorization" header.
     * @param response The HTTP response associated with the request, used for sending a response back to the client.
     * @param filterChain The chain of filters to which the request should proceed after this filter is executed.
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val autoHeader = request.getHeader("Authorization")
        if(autoHeader != null && autoHeader.startsWith("Bearer ")){
            if(jwtService.validateAccessToken(autoHeader)) {
                val userId = jwtService.getUserIdFromToken(autoHeader)
                val auth = UsernamePasswordAuthenticationToken(userId, null, null)
                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }
}