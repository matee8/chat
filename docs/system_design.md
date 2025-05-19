# System Purpose

The purpose of the software is a real-time chat application with user
authentication, which uses the gRPC protocol for communication between the
client and server. Users can register, log in, send text messages to a common
room, and see messages sent by other users in real-time. The system
implementation will be a platform running in a desktop environment, built on
modern technologies. Authentication will be token-based (JWT), and passwords
will be stored securely, hashed.

# Project Plan

**Project Team and Responsibilities**:

The system can be divided into two main components, the development of which requires
separate tasks.

- Server development:

    - Responsible: matee8, **GBWYKF**
    - Tasks:

        - Implementation of gRPC services.
        - Implementation of business logic.
        - Handling database interactions.
        - Development of authentication mechanisms.

- Client development:

    - Responsible: Balent100, **GIBJIJ**
    - Tasks:

        - Design and implementation of the user interface using JavaFX.
        - Communication with the server via gRPC.
        - Management of user state and session.
        - Display of real-time messages.

# Business Process Model

The system supports the following main processes:

- **Registration**: creating a new user account with a username and password.
- **Login**: logging into an existing account with a username and password, resulting in the client receiving an authentication token.
- **Sending a message**: as a logged-in user, sending a text message to the common room.
- **Receiving messages**: as a logged-in user, subscribing to the common room's message stream to receive messages in real-time.
- **Logout**: closing the client-side session, invalidating the token.

# Requirements

## Functional Requirements

- The system shall allow new users to register with a unique username and password.
- The system shall provide a login option for registered users.
- For login, the system shall use token-based (JWT) authentication.
- Logged-in users shall be able to send text messages.
- The system shall store user data (username, password hash, creation date) in a database.
- The system shall store sent messages (sender ID, message content, timestamp) in a database.
- The system shall broadcast new messages in real-time to all subscribed clients using gRPC server-side streaming.
- The client application shall be a JavaFX-based desktop application.
- The server application shall run as a gRPC service.

## Non-functional Requirements

- User passwords shall be stored securely, hashed (BCrypt). Data communication is protected via tokens.
- The code shall be well-structured, following a layered architecture.
- Detailed logging shall aid in debugging and monitoring.
- The system shall handle errors appropriately.
- The use of gRPC shall ensure efficient and low-latency communication between the client and server.
- The server port, database connection details, and JWT token parameters shall be externally configurable.

# Functional Design

## System Actors

- User:

    - Can register in the system.
    - Can log into the system with their account.
    - After logging in, can send messages to the common room.
    - After logging in, can receive messages from others in the common room in real-time.
    - Can log out of the system.

# Physical Environment

- Server:

    - Java Runtime Environment.
    - Platform-independent.
    - Network connection to serve clients.
    - Database: JDBC-compatible database, H2 by default.

- Client:

    - Java Runtime Environment.
    - Platform-independent.
    - Network connection to access the server.

# Architectural Design

- Server:

    - **Communication Layer**: communication with the client.
    - **Service Layer**: business logic.
    - **Data Storage Layer**: persistence.
    - **Model Layer**: data structures.
    - **Event Handling**: publishing and subscribing.

- Client:

    - **View Layer**: user interface elements.
    - **ViewModel Layer**: management of user interface logic and state.
    - **Model Layer**: data structures.
    - **Service Layer**: summarized server business logic.
    - **Communication Layer**: communication with the server.

# Database Design

- `users` table:

    - `user_id`,
    - `username`,
    - `password`: hashed password,
    - `created_at`

- `messages` table:

    - `message_id`,
    - `sender_id`: ID of the user who sent the message,
    - `content`,
    - `timestamp`

# Implementation Plan

- Server:

    - **Language**: Java
    - **Build tool**: Maven
    - **Main libraries**:

        - `io.grpc`,
        - `org.mindrot.jbcrypt`,
        - `com.auth0.jwt`,
        - `com.h2database.h2`,

- Client:

    - **Language**: Java
    - **Build tool**: Maven
    - **Main libraries**:

        - `org.openjfx`,
        - `io.grpc`

# Test Plan

## Unit Tests

- Server:

    - Testing service classes with mocked data storage.
    - Testing data storage classes with an embedded database.
    - Testing helper classes.
    - Testing event handling.

- Client:

    - Testing ViewModel classes with mocked services.
    - Testing client-side services with a mocked communication layer.
    - Testing helper classes.


## Integration Tests

- Server:

    - Testing the interaction of service and repository layers.
    - Testing gRPC endpoints.

- Client:

    - Testing ViewModel and client-side services.
    - Testing the communication layer against a gRPC server.
