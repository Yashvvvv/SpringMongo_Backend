package com.example.spring_boot_crash_course.security

import jakarta.servlet.DispatcherType
import org.apache.tomcat.util.net.DispatchType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain

/**
 * Security configuration class that sets up the Spring Security filters and authentication mechanisms
 * for the application. It defines security policies, adds custom filters, and configures endpoints with
 * their respective access constraints.
 *
 * This class utilizes a stateless session management strategy, ensuring that no session is created or
 * stored on the server side. The configuration safeguards routes and applies JWT-based authentication.
 *
 * @constructor Initializes the configuration class with the required `JwtAuthFilter`.
 * @param jwtAuthFilter Custom filter for handling JWT authentication.
 */
@Configuration
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {
    /**
     * Configures a `SecurityFilterChain` for the application by setting up security features such as
     * disabling CSRF, defining session management policies, authorizing HTTP requests, handling
     * authentication exceptions, and integrating a JWT authentication filter.
     *
     * @param httpSecurity An `HttpSecurity` instance that allows configuring web-based security
     *                      for specific HTTP requests. It provides methods to customize security
     *                      behavior for the application.
     * @return A `SecurityFilterChain` instance that captures the security configuration for the
     *         application.
     */
    @Bean
    fun FilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .csrf { csrf -> csrf.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth->
                auth
                    .requestMatchers("/auth/**").permitAll()
                    .dispatcherTypeMatchers(
                        DispatcherType.ERROR,
                        DispatcherType.FORWARD
                    )
                    .permitAll()
                    .anyRequest()
                    .authenticated()
//                auth.requestMatchers("/api/users/**").hasRole("USER")
//                auth.requestMatchers("/api/admin/**").hasRole("ADMIN")
            }
            .exceptionHandling { configurer ->
                configurer
                    .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}