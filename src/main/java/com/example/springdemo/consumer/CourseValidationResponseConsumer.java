package com.example.springdemo.consumer;

import com.example.springdemo.Dto.CourseValidationResponse;
import com.example.springdemo.entity.Enrollment;
import com.example.springdemo.service.service;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CourseValidationResponseConsumer {

    private final service serv;

    public CourseValidationResponseConsumer(service serv) {
        this.serv = serv;
        System.out.println("🔥 CourseValidationResponseConsumer CREATED");
    }

    @KafkaListener(
            topics = "course-validation-to-student",
            groupId = "student-validation-group"

    )
    public void consumeCourseValidationResponse(
            CourseValidationResponse response) {

        System.out.println("🔥 COURSE VALIDATION CONSUMER CLASS LOADED");

        System.out.println(
                "Course validation response received: "
                        + response.isCourseExists()
        );




    if (response.isCourseExists()) {

        Enrollment enrollment =
                serv.saveEnrollment(
                        response.getStudentId(),
                        response.getCourseId()
                );

        serv.updateEnrollmentRequestStatus(
                response.getRequestId(),
                "COMPLETED",
                enrollment.getId(),
                "Enrollment completed successfully"
        );

        System.out.println(
                "Enrollment created: "
                        + enrollment.getId()
        );
    }
    else {

        serv.updateEnrollmentRequestStatus(
                response.getRequestId(),
                "FAILED",
                null,
                "Course not found"
        );

        System.out.println(
                "Enrollment failed: course not found"
        );
    }

    }
}