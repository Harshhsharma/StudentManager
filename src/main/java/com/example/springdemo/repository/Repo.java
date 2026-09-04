package com.example.springdemo.repository;

import com.example.springdemo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Repo extends JpaRepository<Student , Long> {

    Optional<Student> findByEmail(String email);

    List<Student> findTop10ByOrderByMarksDesc();
}
