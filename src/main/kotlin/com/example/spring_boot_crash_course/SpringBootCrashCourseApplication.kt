package com.example.spring_boot_crash_course

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Entry point for the Spring Boot application.
 *
 * This class is annotated with `@SpringBootApplication`, which serves as a combination of:
 * - `@Configuration`: Indicates that this class can be used as a source of bean definitions.
 * - `@EnableAutoConfiguration`: Enables Spring Boot's auto-configuration mechanism.
 * - `@ComponentScan`: Allows component scanning in the package and subpackages.
 *
 * By including this annotation, the application is automatically set up and ready to run.
 * The main function initializes the application by invoking `runApplication`.
 */
@SpringBootApplication
class SpringBootCrashCourseApplication

/**
 * Entry point for the Spring Boot application.
 *
 * @param args an array of command-line arguments passed to the application
 */
fun main(args: Array<String>) {
	runApplication<SpringBootCrashCourseApplication>(*args)
}
