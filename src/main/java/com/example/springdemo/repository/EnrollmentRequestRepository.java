package com.example.springdemo.repository;

import com.example.springdemo.entity.EnrollmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRequestRepository
        extends JpaRepository<EnrollmentRequest, Long> {

    Optional<EnrollmentRequest> findByRequestId(String requestId);
}
