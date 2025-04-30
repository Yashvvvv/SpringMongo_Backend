package com.example.spring_boot_crash_course.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

/**
 * A component class responsible for encoding and verifying hashed passwords.
 *
 * This class uses the `BCryptPasswordEncoder` to securely hash raw passwords and
 * validate them against hashed values, ensuring proper authentication mechanisms.
 */
@Component
class HashEncoder {
    /**
     * Instance of `BCryptPasswordEncoder` used for encoding and verifying passwords securely.
     *
     * This variable provides the ability to encode plaintext strings into securely hashed passwords
     * and to validate plaintext input against a previously hashed value. It is commonly used to ensure
     * password security by employing the BCrypt hashing algorithm.
     *
     * Encapsulation within the class ensures that the implementation details are abstracted away
     * from external usage.
     */
    private val bcrypt = BCryptPasswordEncoder()

    /**
     * Encodes the given raw string into a hashed format using the BCrypt algorithm.
     *
     * @param raw The raw string to be encoded.
     * @return A hashed string representation of the input.
     */
    fun encode(raw: String): String = bcrypt.encode(raw)

    /**
     * Compares a raw string with a hashed string to determine if they match.
     *
     * @param raw The raw string input, typically the plain text password.
     * @param hashed The hashed string to compare against, typically the stored hashed password.
     * @return true if the raw string matches the hashed string, false otherwise.
     */
    fun matches(raw: String, hashed: String): Boolean = bcrypt.matches(raw, hashed)

}