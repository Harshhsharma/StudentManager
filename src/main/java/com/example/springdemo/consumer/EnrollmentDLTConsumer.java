package com.example.springdemo.consumer;

import com.example.springdemo.Dto.CourseValidationResponse;
import com.example.springdemo.service.service;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentDLTConsumer {

    private final service serv;

    public EnrollmentDLTConsumer(service serv) {
        this.serv = serv;
    }

    @KafkaListener(
            topics = "course-validation-to-student-dlt",
            groupId = "student-validation-dlt-group"
    )
    public void consumeDLT(CourseValidationResponse response) {

        System.out.println(
                "DLT MESSAGE RECEIVED: "
                        + response.getRequestId()
        );

        serv.updateEnrollmentRequestStatus(
                response.getRequestId(),
                "FAILED",
                null,
                "Enrollment processing failed after retries"
        );
    }
}