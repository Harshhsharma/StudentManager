Student Course Management System — Project Overview

# Project Type

-Microservices-based Student Course Management System
-Developed using Java, Spring Boot and PostgreSQL

# Main Components

-API Gateway – Single entry point, request routing and JWT validation
-Auth Service – User registration, login and JWT generation
-Student Manager – Student CRUD, filtering, pagination and enrollment management
-Course Manager – Course CRUD and course validation
-Consul – Service discovery
-Apache Kafka – Asynchronous event-based communication
-PostgreSQL – Database for persistent data

# Architecture
 
             Client
                ↓
           API Gateway
                ↓
 ┌──────────────┬─────────────────┐
   ↓                              ↓                                     ↓
Auth          Student          Course
Service       Manager  <---->  Manager
                 │                 │
             Student DB        Course DB

          Consul → Service Discovery
          Kafka  → Async Communication
          
          
# Key Features

-Student CRUD operations
-Course CRUD operations
-Student filtering and pagination
-Student-course enrollment
-Duplicate enrollment validation
-Maximum 2-course enrollment limit
-JWT-based authentication
-API Gateway routing and security
-Service discovery using Consul
-Global exception handling
-Custom exceptions and proper HTTP status codes
-Synchronous service communication using OpenFeign
-Asynchronous service communication using Kafka     

# Enrollment Communication

**Synchronous Flow:

 Student Manager
      ↓
 OpenFeign
      ↓
Course Manager
      ↓
Course Validation
      ↓
Save Enrollment

----------------------------------------------

**Asynchronous Kafka Flow:

Student Manager
      ↓
Kafka
      ↓
Notification Service
      ↓
Kafka
      ↓
Course Manager
      ↓
Course Validation
      ↓
Kafka
      ↓
Notification Service
      ↓
Kafka
      ↓
Student Manager
      ↓
Save Enrollment
      ↓
Enrollment Event

---------------------------------------------------

# Kafka Topics

-course-validation-request
-course-validation-to-course
-course-validation-response
-course-validation-to-student
-student-enrollment-events

# Kafka Implementation

-Producer: KafkaTemplate
-Consumer: @KafkaListener
-Serialization: Jackson JSON Serializer
-Deserialization: Jackson JSON Deserializer
-Consumer Groups: Separate groups for different processing responsibilities
-Correlation ID: requestId used to track a validation request through the asynchronous flow

# Main Kafka DTOs
  
-CourseValidationEvent — Student ID, Course ID and Request ID
-CourseValidationResponse — Request ID, Student ID, Course ID and validation result
-EnrollmentEvent — Student ID and Course ID after successful enrollment

# Exception Handling

-ResourceNotFoundException → 404 Not Found
-DuplicateResourceException → 409 Conflict
-EnrollmentLimitException → Enrollment limit validation
 Global Exception Handler for centralized error handling

# Key Learning / Implementation

-Microservice architecture and service-to-service communication
-OpenFeign-based synchronous communication
-Kafka-based asynchronous communication
-Kafka producers and consumers
-Serialization and deserialization
-Consumer groups and topics
-JWT authentication
-Service discovery with Consul
-Spring Data JPA/Hibernate
-REST API development
-Debugging distributed-service communication issues

# Project Highlight

Implemented both synchronous (OpenFeign) and asynchronous (Apache Kafka) communication for the enrollment workflow, including course validation, enrollment processing, event publishing, and inter-service message handling.

