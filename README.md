# Spring Boot Library Application

## Overview

This Spring Boot application serves as a library management system with user authentication, role-based access control, and RESTful APIs for managing books, authors, and book reservations.

## Features

- User Management:
  - **Registration:** Users can register with the system, providing necessary details.(Email OTP verification required)
  - **Authentication:** Secured with Spring Security to ensure user authentication.
  - **Roles:** Users have roles (e.g., USER, ADMIN) to control access.
  - **Password Reset**: Users can change or reset their passwords by verifying using Email OTP.

- Book and Author Management:
  - **Get Books and Authors:** Retrieve information about available books and authors.
  - **Reserve Books:** Users can reserve books.
  - **Return Books:** Users can return reserved books.
 
- Subscription Service:
  - **Plans:** Users can subscribe to different plans (e.g., Weekly, Monthy, Yearly) with varying reservation limits.
  - **Reservation Limits:** The number of reservations allowed is determined by the user's subscription plan.

- Exception Handling:
  - **Global Exception Handling:** Comprehensive handling of exceptions to provide meaningful error responses.
  - **Custom Exceptions:** Custom exception classes for specific application scenarios.
 
- Logging and rolling:
  - **Log4j2:** Logs all operattions and errors using a custom format, stores them in daily logs.
 
- Unit Testing:
  - **JUnit and Mockito:** Basic unit tests to ensure the reliability and correctness of the application.


## Technologies Used

- **Spring Boot:** Framework for building Java-based enterprise applications.
- **Spring Security:** Provides authentication and access control features.
- **RESTful APIs:** For communication between the client and the server.
- **Spring Data JPA:** Simplifies database operations using Java annotations.
- **PostgreSQL Database:** SQL database to store all the data.
- **Redis:** In-memory data structure store used for caching. Stores frequent CRUD operations data in a hashmap.
- **RabbitMQ:** Message broker for handling asynchronous communication. Used to forword none-core features to a microservice.
- **SMTP:** Used to verify users and reset passwords.
- **SWAGGER UI:** To list all created APIs.


## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/Hsein-Shabshoul/library-system-app.git
