# Spring Boot Crash Course

## Overview
This repository contains a Spring Boot application built with Kotlin that demonstrates a RESTful API for managing notes. The application uses MongoDB as the database, Spring Security for authentication and authorization, and Spring WebFlux for reactive programming.

## Technologies Used
- **Kotlin 1.9.25**: Modern JVM language with concise syntax and null safety
- **Spring Boot 3.4.4**: Framework for building production-ready applications
- **MongoDB**: NoSQL database for storing note data
- **Spring Data MongoDB**: Data access layer for MongoDB
- **Spring Security**: Authentication and authorization framework
- **JWT (JSON Web Tokens)**: For stateless authentication
- **Gradle**: Build automation tool using Kotlin DSL
- **JUnit 5**: Testing framework
- **Java 21**: Latest LTS version of Java

## Project Structure
```
src/
├── main/
│   ├── kotlin/
│   │   └── com/
│   │       └── example/
│   │           └── spring_boot_crash_course/
│   │               ├── controller/
│   │               │   └── NoteController.kt     # REST API endpoints
│   │               ├── database/
│   │               │   ├── model/
│   │               │   │   └── Note.kt           # MongoDB document model
│   │               │   └── repository/
│   │               │       └── NoteRepository.kt # Data access interface
│   │               ├── security/
│   │               │   └── SecurityConfig.kt     # Security configuration
│   │               └── SpringBootCrashCourseApplication.kt # Main application
│   └── resources/
│       └── application.properties    # Configuration properties
└── test/
    └── kotlin/
        └── com/
            └── example/
                └── spring_boot_crash_course/
                    └── # Test classes
```

## API Endpoints
The application provides the following REST API endpoints:

- `POST /notes`: Create a new note
- `GET /notes?ownerId={ownerId}`: Get all notes for a specific owner

## Model
The `Note` model includes:
- `id`: Unique identifier (MongoDB ObjectId)
- `title`: Note title
- `content`: Note content
- `color`: Color code for the note (stored as Long)
- `createdAt`: Timestamp when the note was created
- `ownerId`: Reference to the owner of the note

## Security
The application is configured with a basic security setup that:
- Disables CSRF protection for API endpoints
- Allows all requests without authentication (for demonstration purposes)
- Uses stateless sessions

## Getting Started

### Prerequisites
- JDK 21 or later
- MongoDB running locally or accessible via network

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

## Future Enhancements
- User authentication and registration
- Role-based access control
- Note sharing functionality
- Search and filtering capabilities
- Real-time updates using WebSocket

## License
This project is open source and available under the [MIT License](LICENSE). 