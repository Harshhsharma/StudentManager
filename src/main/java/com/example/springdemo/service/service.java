package com.example.springdemo.service;

import com.example.springdemo.entity.Student;

import java.util.List;
import java.util.Map;

public interface service {

    Student createStudent(Student student);

    List<Student> getAllStudents();

    Student getStudentById(Long id);

    Student updateStudent(Long id, Student student);

    void deleteStudent(Long id);

    List<Student> getTop10Students();

    List<Student> createMultipleEntries(List<Student> students);

    Map<String ,List<Student>> getAllStudentsByGender();
}
