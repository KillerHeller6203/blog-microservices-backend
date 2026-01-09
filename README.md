Blog Platform – Microservices Architecture

A full-stack blog platform built using Spring Boot microservices with JWT-based authentication, PostgreSQL, Docker, and an API Gateway. The project demonstrates real-world backend architecture, secure service communication, and frontend integration.

Tech Stack:
• Backend
• Java 21
• Spring Boot
• Spring Security (JWT Authentication)
• Spring Data JPA (Hibernate)
• PostgreSQL
• Docker (multi-stage builds)
• Maven

Frontend:
• JavaScript-based frontend (framework/library of choice)
• JWT token handling
• REST API integration
• Architecture Overview

The system is designed using a microservices approach. Each service is responsible for a single domain and communicates through HTTP APIs.

Services:-
• User Service: Handles user registration, authentication, and JWT token generation.
• Post Service: Manages blog posts and content.
• Category Service: Manages post categories.
• Tag Service: Manages post tags.
• API Gateway: Central entry point for routing and request forwarding.

The frontend interacts only with the API Gateway or exposed service endpoints.

Authentication & Security:
• Stateless authentication using JWT
• Token issued by User Service
• Token validation enforced on protected endpoints
• Role-based access control supported
• Secure inter-service communication

Database Design:
• PostgreSQL used for persistence
• JPA entities mapped using Hibernate
• Automatic schema generation during startup (development mode)
• Each service owns its own data model

Containerization:
• All backend services are containerized using Docker
• Multi-stage Dockerfiles used to reduce image size
• Services can be run locally using Docker or deployed individually

Running the Project Locally:-

Prerequisites:
• Java 21
• Docker & Docker Compose

Maven:
• Environment Configuration:
• Backend services rely on environment variables for configuration:
• Database URL
• Database username
• Database password
• JWT secret
• Service ports
This allows seamless switching between local and deployed environments.

Frontend Integration:
• The frontend application:
• Authenticates users via the User Service
• Stores JWT securely on the client
• Sends authorized requests to backend APIs
• Demonstrates complete end-to-end functionality

Deployment Status :
• User Service is deployed and running
• Other services are designed to be deployable but are currently run locally due to free-tier limitations
• Architecture and configuration are production-ready, even if not all services are hosted live
• This project prioritizes correct architecture, security, and deployment readiness over full cloud hosting.
