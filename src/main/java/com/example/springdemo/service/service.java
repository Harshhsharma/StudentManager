package com.example.springdemo.service;

import com.example.springdemo.entity.Student;

import java.util.List;

public interface service {

    Student createStudent(Student student);

    List<Student> getAllStudents();

    Student getStudentById(Long id);

    Student updateStudent(Long id, Student student);

    void deleteStudent(Long id);
}
