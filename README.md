Student Course Management System — Microservices Project
1. Project Overview

The Student Course Management System is a microservices-based backend application developed using Java and Spring Boot.

The main purpose of the project is to manage:

Student registration and management
Course management
Student-course enrollment
Authentication and authorization
Service-to-service communication
Synchronous communication using OpenFeign
Asynchronous communication using Apache Kafka
Service discovery using Consul
API Gateway-based request routing
JWT-based authentication
Database operations using Spring Data JPA and Hibernate
Exception handling and validation

The application is divided into multiple independent services so that each service is responsible for a specific business domain.

2. Overall Architecture

The project consists of the following major components:

                         API GATEWAY :8082
                                |
              +-----------------+-----------------+
              |                 |                 |
              v                 v                 v
        AUTH SERVICE      STUDENT MANAGER    COURSE MANAGER
           :8083               :8080             :8081
              |                   |                 |
              v                   v                 v
           Auth DB            Student DB         Course DB


                         CONSUL :8500
                              |
                              v
                     Service Discovery


                         KAFKA :9092
                              |
                              v
                 Asynchronous Communication
Main services:
API Gateway
Auth Service
Student Manager
Course Manager
Consul Service Discovery
Apache Kafka
3. Technology Stack

The major technologies used in the project are:

Java
Spring Boot
Spring Data JPA
Hibernate
PostgreSQL
Spring Cloud OpenFeign
Spring Cloud Gateway
Spring Cloud Consul
Apache Kafka
Spring Kafka
JWT
Maven
Git and GitHub
REST APIs
4. Microservices
4.1 Auth Service

The Auth Service is responsible for authentication-related functionality.

Responsibilities
User registration
User login
Credential validation
JWT generation
Authentication-related operations
Authentication Flow
Client
   |
   | Login credentials
   v
Auth Service
   |
   | Validate credentials
   v
Generate JWT
   |
   v
Return JWT to Client

The client can then use the JWT token when making protected API requests.

5. API Gateway

The API Gateway acts as the single entry point for clients.

Instead of directly accessing every microservice, the client sends requests to the Gateway.

Client
   |
   v
API Gateway
   |
   +--------> Auth Service
   |
   +--------> Student Manager
   |
   +--------> Course Manager
Responsibilities of API Gateway
Request routing
Forwarding requests to appropriate services
JWT validation
Authorization/security checks
Providing a single entry point to the backend

For example:

GET /students/13
        |
        v
API Gateway
        |
        v
Student Manager :8080
6. Consul Service Discovery

The project uses Consul for service discovery.

Consul runs on:

localhost:8500

Instead of manually maintaining the locations of all services, services register themselves with Consul.

For example:

student-service
course-service
auth-service

Other services can discover these services using their service names.

This becomes particularly useful when combined with Spring Cloud OpenFeign.

7. Student Manager

The Student Manager is responsible for student-related operations and enrollment management.

Main responsibilities
Student CRUD
Student search
Student filtering
Pagination
Student-course enrollment
Enrollment validation
Maintaining the Enrollment table
Publishing enrollment events
8. Student CRUD Operations

The Student Manager provides REST APIs for common CRUD operations.

Typical operations include:

POST    /students
GET     /students/{id}
GET     /students
PUT     /students/{id}
PATCH   /students/{id}
DELETE  /students/{id}

The application follows layered architecture:

Controller
     |
     v
Service
     |
     v
Repository
     |
     v
Database
Controller

Handles HTTP requests and responses.

Service

Contains business logic.

Repository

Handles database operations using Spring Data JPA.

Entity

Represents the database table.

DTO

Represents data transferred between different layers or services.

9. Student Filtering and Pagination

Spring Data JPA derived query methods are used for filtering.

Examples include:

findByCourseAndGenderIgnoreCase(...)

This allows filtering students based on both course and gender.

Other methods include:

findByCourseIgnoreCase(...)
findByGenderIgnoreCase(...)
findTop10ByOrderByMarksDesc(...)

The project also supports pagination using Spring Data's Pageable.

10. Course Manager

The Course Manager is responsible for course-related functionality.

Responsibilities
Course CRUD
Maintaining course information
Course validation
Retrieving students enrolled in a course

The Course Manager owns the course-related data.

11. Enrollment Design

Enrollment was implemented inside the Student Manager instead of creating a separate Enrollment microservice.

The Enrollment table contains:

enrollments
----------------
id
student_id
course_id

The important design decision is that the Student Manager does not create a JPA relationship directly with the Course entity.

The reason is that Student and Course are separate microservices.

Student Manager
       |
       v
Student DB

Course Manager
       |
       v
Course DB

Therefore, the Enrollment table stores only:

studentId
courseId

The Course entity itself is owned by the Course Manager.

12. Enrollment Business Rules

Several validations are performed during enrollment.

12.1 Student Existence

The system first verifies that the student exists.

If the student does not exist:

ResourceNotFoundException

is thrown.

The API returns:

404 Not Found
12.2 Duplicate Enrollment

The system checks whether the student is already enrolled in the requested course.

The repository method used is:

findByStudentIdAndCourseId(
    studentId,
    courseId
);

If an enrollment already exists:

DuplicateResourceException

is thrown.

The API returns:

409 Conflict
12.3 Maximum Enrollment Limit

A student can enroll in a maximum of two courses.

The repository method:

countByStudentId(studentId)

is used to count the student's existing enrollments.

If the count is already two:

EnrollmentLimitException

is thrown.

13. Synchronous Enrollment Using OpenFeign

Initially, enrollment was implemented using synchronous communication through OpenFeign.

The flow is:

Student Manager
      |
      | Feign Request
      v
Course Manager
      |
      | Check Course
      v
Student Manager
      |
      v
Save Enrollment

The Student Manager uses a Feign client similar to:

@FeignClient(name = "course-service")
public interface CourseClient {

    @GetMapping("/courses/{courseId}")
    ResponseEntity<ResponseStructure<CourseResponseDto>>
    getCourseById(@PathVariable Long courseId);
}

The Student Manager directly calls the Course Manager to verify that the course exists.

14. Why Kafka Was Introduced

Along with the synchronous Feign implementation, an asynchronous Kafka-based enrollment flow was implemented for learning and demonstrating event-driven communication.

Synchronous communication
Student
   |
   | Request
   v
Course
   |
   | Response
   v
Student

The Student Manager waits for the Course Manager's response.

Asynchronous communication
Student
   |
   | Event
   v
Kafka
   |
   v
Notification
   |
   | Event
   v
Kafka
   |
   v
Course

The response follows another Kafka-based path.

Course
   |
   | Response Event
   v
Kafka
   |
   v
Notification
   |
   | Response Event
   v
Kafka
   |
   v
Student

This demonstrates asynchronous, event-driven communication between microservices.

15. Kafka-Based Enrollment Flow

The Kafka enrollment flow starts with:

POST /students/{studentId}/enroll-kafka/{courseId}

For example:

POST /students/13/enroll-kafka/2

The complete flow is:

Student Manager
      |
      | CourseValidationEvent
      v
course-validation-request
      |
      v
Notification Service
      |
      | Forward Event
      v
course-validation-to-course
      |
      v
Course Manager
      |
      | Validate Course
      v
course-validation-response
      |
      v
Notification Service
      |
      | Forward Response
      v
course-validation-to-student
      |
      v
Student Manager
      |
      | Save Enrollment
      v
Student Database
      |
      | EnrollmentEvent
      v
student-enrollment-events
      |
      v
Notification Service
16. CourseValidationEvent

The first DTO created for the Kafka flow is:

public class CourseValidationEvent {

    private String requestId;
    private Long studentId;
    private Long courseId;

}

It represents a request to validate a course.

Fields
requestId

A unique identifier generated using:

UUID.randomUUID().toString();

It acts as a correlation ID so that the response can be associated with the original request.

studentId

Identifies the student requesting enrollment.

courseId

Identifies the course that needs to be validated.

17. Why Request ID Is Used

The request travels through multiple services:

Student
   |
   v
Notification
   |
   v
Course
   |
   v
Notification
   |
   v
Student

The same requestId is carried throughout the flow.

Example:

Request ID = ABC123

The same ID appears in:

Student Request
      ↓
Notification
      ↓
Course
      ↓
Course Response
      ↓
Notification
      ↓
Student

This allows the system to identify which response belongs to which original request.

18. Kafka Producer in Student Manager

Spring Kafka provides:

KafkaTemplate

which is used to publish messages.

The producer method is:

public void sendCourseValidationRequest(
        CourseValidationEvent event) {

    kafkaTemplate.send(
            "course-validation-request",
            event
    );
}

The important line is:

kafkaTemplate.send(
    "course-validation-request",
    event
);

Here:

course-validation-request

is the Kafka topic.

And:

event

is the message being sent.

19. Topic vs DTO

These two concepts are different.

Kafka Topic
course-validation-response

This tells us where the message is published.

Java DTO
CourseValidationResponse

This defines what data the message contains.

Therefore:

kafkaTemplate.send(
    "course-validation-response",
    response
);

means:

WHERE → course-validation-response
WHAT  → response object
20. Student Kafka Enrollment Method

The Student Manager contains:

@Override
public Enrollment enrollStudentUsingKafka(
        Long studentId,
        Long courseId) {

    String requestId =
            UUID.randomUUID().toString();

    CourseValidationEvent event =
            new CourseValidationEvent(
                    requestId,
                    studentId,
                    courseId
            );

    kafkaProducerService
            .sendCourseValidationRequest(event);

    return null;
}
Step 1

Generate a unique request ID.

String requestId =
        UUID.randomUUID().toString();
Step 2

Create the validation event.

CourseValidationEvent event =
        new CourseValidationEvent(
                requestId,
                studentId,
                courseId
        );
Step 3

Publish the event.

kafkaProducerService
        .sendCourseValidationRequest(event);

At this point, the request has been sent asynchronously.

The actual enrollment has not yet happened.

21. Why the Kafka Method Initially Returned null

The Kafka flow is asynchronous.

When the HTTP request is received:

HTTP Request
     |
     v
Publish Kafka Event
     |
     v
Method finishes

The Course Manager's response will arrive later.

Therefore, the method cannot immediately return the final Enrollment object.

The actual enrollment happens later inside the Kafka consumer.

For a production implementation, an asynchronous API would generally acknowledge the request with something such as:

202 Accepted

rather than returning 201 Created with a null body.

The current implementation was kept simple for learning the Kafka flow.

22. Notification Service

The Notification Service acts as a message relay/forwarder in this implementation.

It consumes messages from Kafka and forwards them to the next service.

It does not perform course validation itself.

23. Notification Producer Responsibilities

The Notification Service has two producer responsibilities.

Producer 1 — Notification to Course
sendCourseValidationRequest(
        CourseValidationEvent event
)

It publishes to:

course-validation-to-course

Flow:

Student
   ↓
course-validation-request
   ↓
Notification
   ↓
course-validation-to-course
   ↓
Course
Producer 2 — Notification to Student
sendCourseValidationResponse(
        CourseValidationResponse response
)

It publishes to:

course-validation-to-student

Flow:

Course
   ↓
course-validation-response
   ↓
Notification
   ↓
course-validation-to-student
   ↓
Student

There are therefore two producer methods, but both use the same KafkaTemplate.

24. Notification Request Consumer

The Notification Service listens to:

course-validation-request

using:

@KafkaListener(
        topics = "course-validation-request",
        groupId = "notification-validation-group"
)
public void consumeCourseValidationRequest(
        CourseValidationEvent event) {

    producer.sendCourseValidationRequest(event);
}

Its responsibility is:

Receive
   ↓
Forward

It does not validate the course itself.

25. Course Manager Kafka Consumer

The Course Manager listens to:

course-validation-to-course

using:

@KafkaListener(
        topics = "course-validation-to-course",
        groupId = "course-validation-group"
)

When it receives the event:

Course course =
        courseRepository
                .findById(event.getCourseId())
                .orElse(null);

It checks the Course database.

26. Course Validation

The result is calculated using:

boolean courseExists =
        course != null;

If the course exists:

courseExists = true

Otherwise:

courseExists = false

The Course Manager does not directly call the Student Manager.

Instead, it creates a response event.

27. CourseValidationResponse

The response DTO is:

public class CourseValidationResponse {

    private String requestId;
    private Long studentId;
    private Long courseId;
    private boolean courseExists;

}

It contains:

Original request ID
Student ID
Course ID
Course validation result

Example:

requestId    = ABC123
studentId    = 13
courseId     = 2
courseExists = true
28. Course Manager Response Producer

The Course Manager publishes:

kafkaTemplate.send(
        "course-validation-response",
        response
);

Flow:

Course Manager
      |
      | CourseValidationResponse
      v
course-validation-response
29. Notification Response Consumer

The Notification Service consumes:

course-validation-response

using:

@KafkaListener(
        topics = "course-validation-response",
        groupId = "notification-response-group",
        containerFactory =
            "responseKafkaListenerContainerFactory"
)

It receives the CourseValidationResponse and forwards it using:

producer.sendCourseValidationResponse(response);

The response is published to:

course-validation-to-student
30. Student Response Consumer

The Student Manager consumes:

course-validation-to-student

using:

@KafkaListener(
        topics = "course-validation-to-student",
        groupId = "student-validation-group"
)

It receives:

CourseValidationResponse

Then checks:

if (response.isCourseExists()) {

If the course exists, it calls:

saveEnrollment(
        response.getStudentId(),
        response.getCourseId()
);
31. saveEnrollment() Method

The actual enrollment logic is kept inside:

saveEnrollment()

This method performs all enrollment validations.

Step 1 — Validate Student
repo.findById(studentId)
Step 2 — Check Duplicate Enrollment
findByStudentIdAndCourseId(
        studentId,
        courseId
)
Step 3 — Check Enrollment Limit
countByStudentId(studentId)

Maximum allowed:

2 courses
Step 4 — Save Enrollment
enrollmentRepository.save(enrollment);
32. Enrollment Event

After successful enrollment, an event is created:

EnrollmentEvent event =
        new EnrollmentEvent(
                studentId,
                courseId
        );

It is published to:

student-enrollment-events

This represents:

The student has successfully enrolled in the course.

33. Final Notification Event

The Notification Service consumes:

student-enrollment-events

The current implementation logs the successful enrollment event.

Example:

Received enrollment event:
student=13, course=2

This proves that the complete asynchronous flow successfully completed.

34. Complete Kafka Topic Flow

The complete Kafka architecture is:

                 STUDENT MANAGER
                       |
                       |
                       v
          course-validation-request
                       |
                       v
                NOTIFICATION
                       |
                       |
                       v
          course-validation-to-course
                       |
                       v
                 COURSE MANAGER
                       |
                       |
                       v
          course-validation-response
                       |
                       v
                NOTIFICATION
                       |
                       |
                       v
          course-validation-to-student
                       |
                       v
                 STUDENT MANAGER
                       |
                       |
                       v
                Save Enrollment
                       |
                       |
                       v
          student-enrollment-events
                       |
                       v
                NOTIFICATION
35. Kafka Topics Summary
Topic	Producer	Consumer	Purpose
course-validation-request	Student Manager	Notification Service	Sends course validation request
course-validation-to-course	Notification Service	Course Manager	Forwards validation request
course-validation-response	Course Manager	Notification Service	Sends validation result
course-validation-to-student	Notification Service	Student Manager	Forwards validation result
student-enrollment-events	Student Manager	Notification Service	Announces successful enrollment
36. Kafka Producer

A Kafka Producer is responsible for publishing messages to Kafka topics.

Spring Kafka provides:

KafkaTemplate<String, Object>

Example:

kafkaTemplate.send(
    "course-validation-request",
    event
);
37. Kafka Consumer

A Kafka Consumer reads messages from Kafka topics.

Spring Kafka uses:

@KafkaListener

Example:

@KafkaListener(
    topics = "course-validation-request",
    groupId = "notification-validation-group"
)

This tells Spring:

Listen to this Kafka topic and invoke this method when a message is available.

38. Kafka Serializer

Kafka works with serialized message data.

The project uses:

JacksonJsonSerializer

The process is:

Java Object
     |
     v
JacksonJsonSerializer
     |
     v
JSON Message
     |
     v
Kafka

Example:

CourseValidationEvent
        ↓
{
  "requestId": "ABC123",
  "studentId": 13,
  "courseId": 2
}
39. Kafka Deserializer

The consumer uses:

JacksonJsonDeserializer

The process is:

Kafka Message
      |
      v
JacksonJsonDeserializer
      |
      v
Java Object

For example:

JSON
  ↓
CourseValidationEvent
40. ConsumerFactory

ConsumerFactory contains the configuration required to create Kafka consumers.

It specifies things such as:

Kafka broker address
Key deserializer
Value deserializer
Default Java object type

Example:

config.put(
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
    "localhost:9092"
);

And:

config.put(
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    JacksonJsonDeserializer.class
);
41. Kafka Listener Container Factory

KafkaListenerContainerFactory provides the infrastructure required to run @KafkaListener methods.

Conceptually:

ConsumerFactory
       |
       v
Listener Container Factory
       |
       v
Kafka Listener
       |
       v
Business Method

For the response message, a separate listener container factory was created because the response uses a different DTO type.

42. Why Separate Response Consumer Configuration Was Required

The Notification Service receives two different message types.

Request
course-validation-request
        ↓
CourseValidationEvent
Response
course-validation-response
        ↓
CourseValidationResponse

Initially, the default consumer was configured to deserialize messages as:

CourseValidationEvent

When a CourseValidationResponse arrived, Spring attempted to convert it using the wrong expected Java type.

This caused a deserialization/conversion mismatch.

The solution was:

Request Consumer
        ↓
CourseValidationEvent

Response Consumer
        ↓
CourseValidationResponse

A separate:

responseConsumerFactory()

and:

responseKafkaListenerContainerFactory()

were created.

The response listener explicitly uses:

containerFactory =
    "responseKafkaListenerContainerFactory"
43. @EnableKafka

The Student Manager required:

@EnableKafka

to enable the Kafka listener infrastructure.

After adding it and restarting the service, the Student Manager successfully subscribed to:

course-validation-to-student

and received partition assignments.

44. Error 1 — Kafka Broker Was Not Running

During development, Kafka itself was not running correctly because of a Kafka storage/log directory issue.

The Kafka storage was formatted again and the Kafka broker was restarted.

Once Kafka showed that it was accepting connections on:

0.0.0.0:9092

the Spring Boot services could connect to it.

Lesson

Before debugging producer/consumer code, always verify:

Is Kafka broker running?
45. Error 2 — Controller Endpoint Was Not Recognized

After adding:

/enroll-kafka/{courseId}

the endpoint initially returned:

NoResourceFoundException

The reason was that the Student Manager had not been restarted after the controller mapping was added.

The running Spring application was still using the previous controller configuration.

Solution

Restart the Student Manager.

After restart, Spring loaded the new endpoint mapping.

46. Error 3 — Response Deserialization Mismatch

The Notification Service initially tried to deserialize the response message as:

CourseValidationEvent

while the listener expected:

CourseValidationResponse

This caused a conversion/deserialization error.

Solution

Create a separate response consumer configuration:

responseConsumerFactory()

with:

CourseValidationResponse

as the default type.

Then assign:

responseKafkaListenerContainerFactory

to the response listener.

47. Error 4 — Student Response Consumer Was Missing

The Course Manager and Notification Service were successfully processing the validation response, but the Student Manager initially did not have a consumer for:

course-validation-to-student

Therefore, the Student Manager could not process the response.

Solution

Added:

CourseValidationResponse DTO
Student Kafka Consumer
Student Consumer Configuration
@KafkaListener
@EnableKafka
48. Error 5 — Listener Was Not Active

The Student consumer bean existed, but the Kafka listener was not actively consuming messages.

The Kafka listener infrastructure was enabled using:

@EnableKafka

After restarting the service, the logs showed:

Subscribed to topic(s):
course-validation-to-student

and the consumer received a partition assignment.

49. Error 6 — String vs CourseValidationEvent

The Notification Service had an enrollment event consumer that initially expected:

String message

However, the configured Kafka deserializer was converting the JSON message into:

CourseValidationEvent

Therefore, the listener expected a String while Spring had a CourseValidationEvent.

Solution

Change:

public void consume(String message)

to:

public void consume(CourseValidationEvent event)

The final successful log was:

Received enrollment event:
student=13, course=2

50. Exception Handling

The project uses custom exceptions for business errors.

ResourceNotFoundException

Used when a requested resource does not exist.

Example:

Student not found
Course not found

Response:

404 Not Found
DuplicateResourceException

Used when duplicate data is detected.

Example:

Student is already enrolled in this course

Response:

409 Conflict
EnrollmentLimitException

Used when the student has already reached the maximum enrollment limit.

Maximum:

2 courses
51. Global Exception Handling

A global exception handler is used to handle exceptions centrally rather than writing separate try-catch logic in every controller.

Conceptually:

Controller
    |
    v
Service
    |
    v
Exception
    |
    v
Global Exception Handler
    |
    v
Standard HTTP Response

This keeps controller code cleaner and provides consistent error responses.

52. Important Architectural Decisions
52.1 Enrollment is not a separate microservice

Enrollment was kept inside Student Manager because it is closely related to the student's enrollment operations.

52.2 No direct JPA relationship between Student and Course

Since Student and Course are managed by separate services, the Enrollment table stores:

studentId
courseId

instead of creating a cross-microservice JPA relationship.

52.3 Feign and Kafka both exist

The project contains both:

Synchronous communication
OpenFeign

and:

Asynchronous communication
Kafka

The Feign flow demonstrates direct service-to-service communication, while Kafka demonstrates event-driven asynchronous communication.

53. Complete Project Flow
Authentication Flow
Client
   |
   v
API Gateway
   |
   v
Auth Service
   |
   v
Validate Credentials
   |
   v
Generate JWT
   |
   v
Client
Student CRUD Flow
Client
   |
   v
API Gateway
   |
   v
Student Manager
   |
   v
Service Layer
   |
   v
Repository
   |
   v
Student Database
Course CRUD Flow
Client
   |
   v
API Gateway
   |
   v
Course Manager
   |
   v
Service Layer
   |
   v
Repository
   |
   v
Course Database
Synchronous Enrollment Flow
Client
   |
   v
API Gateway
   |
   v
Student Manager
   |
   | OpenFeign
   v
Course Manager
   |
   | Validate Course
   v
Student Manager
   |
   | Validate Enrollment
   v
Student Database
Asynchronous Kafka Enrollment Flow
Client
   |
   v
API Gateway
   |
   v
Student Manager
   |
   | CourseValidationEvent
   v
Kafka
   |
   | course-validation-request
   v
Notification
   |
   | course-validation-to-course
   v
Course Manager
   |
   | Check Course DB
   v
CourseValidationResponse
   |
   | course-validation-response
   v
Notification
   |
   | course-validation-to-student
   v
Student Manager
   |
   | courseExists == true
   v
saveEnrollment()
   |
   v
Student Database
   |
   | EnrollmentEvent
   v
Kafka
   |
   | student-enrollment-events
   v
Notification
54. Final Project Summary

This project demonstrates a microservices-based Student Course Management System using Spring Boot.

The system contains:

Auth Service for authentication and JWT
API Gateway for routing and security
Consul for service discovery
Student Manager for student and enrollment management
Course Manager for course management
PostgreSQL for persistent data
OpenFeign for synchronous service communication
Apache Kafka for asynchronous event-driven communication
Spring Data JPA/Hibernate for database interaction
Global exception handling for consistent API error responses

The Kafka implementation demonstrates an asynchronous course-validation and enrollment workflow where the Student Manager publishes a validation request, the Notification Service forwards it, the Course Manager validates the course, and the result is sent back through Kafka. After successful validation, the Student Manager saves the enrollment and publishes a final enrollment event.

The project also involved practical debugging of Kafka broker availability, controller mappings, consumer configuration, deserialization mismatches, listener activation, and message-type mismatches.


