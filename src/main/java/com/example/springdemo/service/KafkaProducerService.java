package com.example.springdemo.service;

import com.example.springdemo.Dto.EnrollmentEvent;

public interface KafkaProducerService {

    void sendEnrollmentEvent(EnrollmentEvent event);
}