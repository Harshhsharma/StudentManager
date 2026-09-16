
package com.example.springdemo.repository;

import com.example.springdemo.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    long countByStudentId(Long studentId);  // "Kitne hain?" → 2 ,, Kitne courses hain?

    Optional<Enrollment> findByStudentIdAndCourseId(
            Long studentId,    //"Kya Course 2 wala hai?" → Yes
            Long courseId       // Kya particular course mein enrolled hai?
    );
    List<Enrollment> findByStudentId(Long studentId);   // Student 7 ke saare records dikhao.

    List<Enrollment> findByCourseId(Long courseId);
}

