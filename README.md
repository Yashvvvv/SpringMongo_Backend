# Spring Boot MongoDB Backend Auth

## Overview
This repository contains a Spring Boot application built with Kotlin that demonstrates a secure RESTful API for managing notes with robust authentication. The application implements JWT-based authentication, MongoDB for data persistence, and follows modern security practices. It features both traditional and reactive programming approaches, with comprehensive validation and error handling.

## Technologies Used
- **Kotlin 1.9.25**: Modern JVM language with concise syntax and null safety
- **Java 21**: Latest LTS version of Java
- **Spring Boot 3.4.4**: Framework for building production-ready applications
- **Spring Security**: Authentication and authorization with JWT implementation
- **Spring Data MongoDB**: Data access layer for MongoDB with both traditional and reactive support
- **MongoDB**: NoSQL database for storing application data
- **JWT (JSON Web Tokens)**: For secure, stateless authentication
- **Project Reactor**: For reactive programming with Kotlin extensions
- **Jakarta Validation**: For input validation
- **Gradle**: Build automation tool using Kotlin DSL
- **JUnit 5**: Testing framework with Kotlin support

## Project Structure
```
src/
├── main/
│   ├── kotlin/
│   │   └── com/
│   │       └── example/
│   │           └── spring_boot_crash_course/
│   │               ├── controller/
│   │               │   ├── AuthController.kt       # Authentication endpoints
│   │               │   └── NoteController.kt       # Note management endpoints
│   │               ├── database/
│   │               │   ├── model/
│   │               │   │   ├── Note.kt             # Note document model
│   │               │   │   ├── RefreshToken.kt     # Refresh token model
│   │               │   │   └── User.kt             # User document model
│   │               │   └── repository/
│   │               │       ├── NoteRepository.kt   # Note data access
│   │               │       ├── RefreshTokenRepository.kt # Token repository
│   │               │       └── UserRepository.kt   # User data access
│   │               ├── security/
│   │               │   ├── AuthService.kt          # Authentication business logic
│   │               │   ├── HashEncoder.kt          # Password encoding
│   │               │   ├── JwtAuthFilter.kt        # JWT authentication filter
│   │               │   ├── JwtService.kt           # JWT generation and validation
│   │               │   └── SecurityConfig.kt       # Security configuration
│   │               ├── GlobalValidationHandler.kt  # Global validation error handler
│   │               └── SpringBootCrashCourseApplication.kt # Main application
│   └── resources/
│       └── application.properties    # Configuration properties
└── test/
    └── kotlin/
        └── com/
            └── example/
                └── spring_boot_crash_course/
                    └── SpringBootCrashCourseApplicationTests.kt # Application tests
```

## API Endpoints

### Authentication
- `POST /auth/register`: Register a new user
  - Request body: `{ "email": "user@example.com", "password": "SecurePass123" }`
  - Response: `200 OK` on success
  
- `POST /auth/login`: Authenticate and get access tokens
  - Request body: `{ "email": "user@example.com", "password": "SecurePass123" }`
  - Response: `{ "accessToken": "jwt_token", "refreshToken": "refresh_token" }`
  
- `POST /auth/refresh`: Refresh authentication tokens
  - Request body: `{ "refreshToken": "refresh_token" }`
  - Response: `{ "accessToken": "new_jwt_token", "refreshToken": "new_refresh_token" }`

### Notes Management
- `POST /notes`: Create or update a note (authenticated)
  - Request body: `{ "id": "optional_id", "title": "Note Title", "content": "Note Content", "color": 4294198070 }`
  - Response: Note details including id, creation timestamp
  
- `GET /notes`: Get all notes for authenticated user
  - Response: Array of notes owned by the current user
  
- `DELETE /notes/{id}`: Delete a note by ID (if owned by authenticated user)
  - Response: `200 OK` on success

## Data Models

### User
- `id`: Unique identifier (MongoDB ObjectId)
- `email`: User's email address (unique)
- `password`: Hashed password

### Note
- `id`: Unique identifier (MongoDB ObjectId)
- `title`: Note title (required)
- `content`: Note content
- `color`: Color code for the note (stored as Long)
- `createdAt`: Timestamp when the note was created
- `ownerId`: Reference to the owner of the note (User ObjectId)

### RefreshToken
- `id`: Unique identifier
- `token`: The refresh token string
- `userId`: Reference to the associated user
- `expiresAt`: Timestamp when the token expires

## Authentication and Security Flow

```
┌─────────┐                              ┌─────────────┐                              ┌────────────┐
│  Client │                              │   Backend   │                              │  MongoDB   │
└────┬────┘                              └──────┬──────┘                              └─────┬──────┘
     │                                          │                                           │
     │ POST /auth/register                      │                                           │
     │ {email, password}                        │                                           │
     │─────────────────────────────────────────>│                                           │
     │                                          │ Check if email exists                     │
     │                                          │──────────────────────────────────────────>│
     │                                          │<──────────────────────────────────────────│
     │                                          │ Hash password                             │
     │                                          │ Store new user                            │
     │                                          │──────────────────────────────────────────>│
     │                      200 OK              │                                           │
     │<─────────────────────────────────────────│                                           │
     │                                          │                                           │
     │ POST /auth/login                         │                                           │
     │ {email, password}                        │                                           │
     │─────────────────────────────────────────>│                                           │
     │                                          │ Verify credentials                        │
     │                                          │──────────────────────────────────────────>│
     │                                          │<──────────────────────────────────────────│
     │                                          │ Generate JWT tokens                       │
     │                                          │ Store refresh token                       │
     │                                          │──────────────────────────────────────────>│
     │        {accessToken, refreshToken}       │                                           │
     │<─────────────────────────────────────────│                                           │
     │                                          │                                           │
     │ Request with Bearer Token                │                                           │
     │─────────────────────────────────────────>│                                           │
     │                                          │ Validate JWT                              │
     │                     Response             │                                           │
     │<─────────────────────────────────────────│                                           │
```

## Data Flow for Note Operations

```
┌───────────┐                            ┌──────────────┐                            ┌────────────┐
│ Auth User │                            │  Controllers │                            │  MongoDB   │
└─────┬─────┘                            └──────┬───────┘                            └─────┬──────┘
      │                                         │                                          │
      │ POST /notes with JWT                    │                                          │
      │ {title, content, color}                 │                                          │
      │────────────────────────────────────────>│                                          │
      │                                         │ Extract user ID from JWT                 │
      │                                         │ Validate input                           │
      │                                         │ Create note with owner ID                │
      │                                         │─────────────────────────────────────────>│
      │                                         │<─────────────────────────────────────────│
      │           Note data response            │                                          │
      │<────────────────────────────────────────│                                          │
      │                                         │                                          │
      │ GET /notes with JWT                     │                                          │
      │────────────────────────────────────────>│                                          │
      │                                         │ Extract user ID from JWT                 │
      │                                         │ Query notes by owner ID                  │
      │                                         │─────────────────────────────────────────>│
      │                                         │<─────────────────────────────────────────│
      │           Array of user notes           │                                          │
      │<────────────────────────────────────────│                                          │
      │                                         │                                          │
      │ DELETE /notes/{id} with JWT             │                                          │
      │────────────────────────────────────────>│                                          │
      │                                         │ Extract user ID from JWT                 │
      │                                         │ Verify note ownership                    │
      │                                         │─────────────────────────────────────────>│
      │                                         │<─────────────────────────────────────────│
      │                                         │ Delete if owner matches                  │
      │                                         │─────────────────────────────────────────>│
      │                200 OK                   │                                          │
      │<────────────────────────────────────────│                                          │
```

## Security Features
The application is configured with comprehensive security features:

- **Stateless JWT Authentication**: No server-side session state is maintained
- **Password Hashing**: Secure password storage with encoding
- **Token Refresh Mechanism**: Short-lived access tokens with refresh capability
- **Path-based Security Rules**: Different authorization rules for different endpoints
- **CSRF Protection**: Disabled for API endpoints as tokens are used
- **Password Validation**: Strong password policy enforced with regex pattern
- **Input Validation**: Global validation handler for enforcing data constraints

## Getting Started

### Prerequisites
- JDK 21 or later
- MongoDB running locally or accessible via network

### Configuration
Configure MongoDB connection in `application.properties`:
```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=notesdb
```

### Running the Application
1. Clone the repository
2. Start MongoDB
3. Run the application:
```bash
./gradlew bootRun
```

### Building the Application
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

## Dependencies
The project uses the following key dependencies:
- Spring Boot Starter Web
- Spring Boot Starter Data MongoDB
- Spring Boot Starter Data MongoDB Reactive
- Spring Boot Starter Security
- Spring Security Crypto
- Spring Boot Starter Validation
- Reactor Kotlin Extensions
- Kotlinx Coroutines Reactor
- JJWT (JSON Web Token) libraries
- Jakarta Servlet API

## License
This project is open source and available under the [MIT License](LICENSE).
