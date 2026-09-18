package com.example.springdemo.service;

import com.example.springdemo.Dto.StudentResponseDto;
import com.example.springdemo.entity.Enrollment;
import com.example.springdemo.entity.Student;
import org.springframework.data.domain.Page;


import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface service {

    Student createStudent(Student student);

    Page<StudentResponseDto> getAllStudents(String Course , String gender , Pageable pageable);

    StudentResponseDto getStudentById(Long id);

    Student updateStudent(Long id, Student student);

    void deleteStudent(Long id);

    List<Student> getTop10Students();

    List<Student> createMultipleEntries(List<Student> students);

    Map<String ,List<Student>> getAllStudentsByGender();

    Student patchUpdate(Long id , Student student);

    void testCourseApi();

    Enrollment enrollStudent(Long studentId, Long courseId);

    List<Long> getCourseIdsByStudentId(Long studentId);

    List<Student> getStudentsByCourseId(Long courseId);

    Page<StudentResponseDto> getStudentsByCourseId(
            Long courseId,
            Pageable pageable
    );
}
