# Java gRPC Real-Time Chat Application

This project implements a real-time chat application featuring a Java-based gRPC
server and a JavaFX client. This repository serves as an educational resource,
demonstrating the application of SOLID principles, Object-Oriented Programming
patterns, idiomatic Java coding patterns, and robust system design in a
practical setting.

# Overview

This application provides a platform for users to register, log in, and exchange
messages in a global chat room in real-time. It's built with a clean separation
of concerns between the server backend and the client frontend, communicating
via gRPC.

# Server Component

-   Handles user authentication via JWTs.
-   Persists user and message data using an H2 in-memory database via the
    Repository pattern.
-   Manages real-time message dissemination to connected clients using an
    in-memory event bus implemented via the Publish-Subscribe pattern.
-   Exposes its functionalities via gRPC services.

# Client Component

-   Provides a GUI built with JavaFX.
-   Implements an MVVM-like architecture.
-   Uses the Command pattern in the ViewModel layer.
-   Communicates with the server via gRPC to send and receive messages, and
    handle authentication.
-   Subscribes to real-time message updates from the server.

# Getting Started

## Prerequisites

- Java Development Kit (JDK)
- Apache Maven (or a compatible build tool if `pom.xml` is adapted)

## Installation & Setup

1. Clone the repository.

    ```bash
    git clone https://github.com/matee8/chat.git
    cd chat
    ```

2. Build the server.

    The project is configured to be built with Maven.

    ```bash
    cd server
    mvn clean compile assembly:single
    ```

    This will produce a JAR file in the `target/` directory, typically named
    `server-1.0-SNAPSHOT.jar`.

3. Configuration.

    The application uses configuration files located in `src/main/resources`
    under both the client and the server:

    -   **`reference.conf`**: Contains reference settings for port, database
        credentials, JWT secret, and server host/port.
    -   **`log4j2.xml`**: Configures logging levels and appenders for Log4j2.

    To rewrite these settings, create an `application.conf`.

# Project Structure

The project is organized itno `client` and `server` modules.

```
.
├── client/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── hu/progtech/chat/              # Client-specific base package
│   │       │       ├── App.java                   # Client main JavaFX application class
│   │       │       ├── communication/             # Client: gRPC client implementation
│   │       │       ├── config/                    # Client: Configuration loading
│   │       │       ├── controller/                # Client: JavaFX controllers
│   │       │       ├── model/                     # Client: Data model records
│   │       │       ├── service/                   # Client: Business logic services
│   │       │       ├── util/                      # Client: Utility classes
│   │       │       └── viewmodel/                 # Client: ViewModels and Commands
│   │       ├── resources/
│   │       │   ├── fxml/                          # FXML files for client UI
│   │       │   │   ├── LoginView.fxml
│   │       │   │   └── ChatView.fxml
│   │       │   ├── reference.conf                 # Client-specific configuration (reference)
│   │       │   └── log4j2.xml                     # Client-specific logging configuration
│   │       └── proto/
│   │           └── chat.proto                     # Protocol Buffer definitions for gRPC services and messages
│   └── pom.xml                                    # Maven POM for the client module
├── server/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── hu/progtech/chat/              # Server-specific base package
│   │   │   │       ├── App.java                   # Server main application class
│   │   │   │       ├── communication/             # Server: gRPC request handlers
│   │   │   │       ├── config/                    # Server: Configuration loading
│   │   │   │       ├── event/                     # Server: Event bus implementation
│   │   │   │       ├── model/                     # Server: Data model records
│   │   │   │       ├── repository/                # Server: Data access interfaces and H2 implementation
│   │   │   │       ├── service/                   # Server: Business logic services
│   │   │   │       └── util/                      # Server: Utility classes
│   │   │   ├── resources/
│   │   │   │   ├── reference.conf                 # Server-specific configuration (reference)
│   │   │   │   └── log4j2.xml                     # Server-specific logging configuration
│   │   │   └── proto/
│   │   │       └── chat.proto                     # Protocol Buffer definitions for gRPC services and messages
│   │   └── test/
│   │       ├── java/
│   │       │   └── hu/progtech/chat/              # Server test base package
│   │       │       ├── config/                    # Tests for config classes
│   │       │       ├── repository/                # Tests for repository classes
│   │       │       └── util/                      # Tests for utility classes
│   │       └── resources/
│   │           └── application.conf               # Server test-specific configuration
│   └── pom.xml                                    # Maven POM for the server module
├── docs/
│   └── system_design.md                           # System design documentation
├── .gitignore
├── pom.xml                                        # Parent Maven POM
├── LICENSE                                        # Project licensing information
└── README.md
```

# Usage

## Running the server

The server needs to be started first.

1.  Ensure the server has been built using `mvn clean install` from the `server`
    directory.
2.  Navigate to the `server` directory.
3.  Run the server application.

    ```bash
    java -jar target/client-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```

    The server will start, initialize the database schema (if it doesn't exist),
    and begin listening for gRPC connections on the configured port (default:
    8080 in its `reference.conf`). Log messages will indicate its status.


## Running the client

Once the server is running, you can start one or more client instances.

1.  Open a new terminal window.
2.  Navigate to the `client` directory.
3.  Run the client application.

    ```bash
    mvn clean javafx:run
    ```

    The JavaFX client window will appear.

## Interacting with the Chat

1.  **Register**: New users must first register with a unique username and a 
    password (at least 8 characters long).
2.  **Login**: Existing users can log in using their credentials.
3.  **Chat**: After logging in, users are taken to the main chat view.
    *   Type messages into the input field and click "Send" or press Enter.
    *   Messages from all users will appear in the chat history in real-time.
4.  **Logout**: Users can log out, which clears their session and returns them
    to the login screen.

# Code Structure

The project's codebase is modularized into the `client` and `server` components.

**Server (`server/`)**:

-   **`communication/`**: Implements gRPC services and links the event bus to
    the client streams.
-   **`config/`**: Manages server-specific configuration (database, JWT, server
    port).
-   **`event/`**: Implements the Publish-Subscribe pattern for real-time message
    broadcasting.
-   **`model/`**: Server-side domain entities.
-   **`repository/`**: Data access layer.
-   **`service/`**: Core business logic for authentication and chat operations.
-   **`util/`**: Server-side utilities for database connection, password
    hashing, JWT authentication.

**Client (`client/`)**:

-   **`communication/`**: Provides implementations for server interaction using
    gRPC stubs generated from `chat.proto`.
-   **`config/`**: Manages client-specific configuration (server host/port).
-   **`controller/`**: JavaFX controllers for UI scene management.
-   **`model/`**: Client-side data models.
-   **`service/`**: Client-side services, facades interactions with gRPC client,
    and manages user sessions.
-   **`util/`**: Client-side utilities for converting timestamps.
-   **`viewmodel/`**: Implements MVVM with commands for UI logic.

# License

This project is licensed under the [MIT License](LICENSE).

# Acknowledgements

- Created for an university course focusing on software engineering principles.
