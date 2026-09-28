package com.example.springdemo.service;

import com.example.springdemo.Dto.CourseValidationEvent;
import com.example.springdemo.Dto.EnrollmentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerServiceImpl(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEnrollmentEvent(EnrollmentEvent event) {

        kafkaTemplate.send(
                "student-enrollment-events",
                event
        );
    }

    @Override
    public void sendCourseValidationRequest(CourseValidationEvent event) {

        kafkaTemplate.send(
                "course-validation-request",
                event
        );
    }
}