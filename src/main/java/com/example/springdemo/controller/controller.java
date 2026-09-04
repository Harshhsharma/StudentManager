package com.example.springdemo.controller;

import com.example.springdemo.entity.Student;
import com.example.springdemo.responseStructure.ResponseStructure;
import com.example.springdemo.service.service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class controller {

     private final service serv;

    public controller(service serv) {
        this.serv = serv;
    }

    @PostMapping
    public ResponseEntity<ResponseStructure<Student>> createStudent(
            @RequestBody Student student) {

        Student savedStudent = serv.createStudent(student);

        ResponseStructure<Student> response =
                new ResponseStructure<>(
                        201,
                        "Student created successfully",
                        savedStudent
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Student>> getStudentById(
            @PathVariable Long id) {

        Student student = serv.getStudentById(id);

        ResponseStructure<Student> response =
                new ResponseStructure<>(
                        200,
                        "Student found successfully",
                        student
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<Student>>> getAllStudents() {

        List<Student> students = serv.getAllStudents();

        ResponseStructure<List<Student>> response =
                new ResponseStructure<>(
                        200,
                        "Students fetched successfully",
                        students
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<Student>> updateStudent(
            @PathVariable Long id,
            @RequestBody Student student) {

        Student updatedStudent =
                serv.updateStudent(id, student);

        ResponseStructure<Student> response =
                new ResponseStructure<>(
                        200,
                        "Student updated successfully",
                        updatedStudent
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<String>> deleteStudent(
            @PathVariable Long id) {

        serv.deleteStudent(id);

        ResponseStructure<String> response =
                new ResponseStructure<>(
                        200,
                        "Student deleted successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top10")
    public ResponseEntity<ResponseStructure<List<Student>>> getTop10Students(){
        List<Student> stu = serv.getTop10Students();
        ResponseStructure<List<Student>> rs = new ResponseStructure<>(200 , "student fetch successfully" ,stu);
        return ResponseEntity.ok(rs);
    }

    @PostMapping("/bulk")
    public ResponseEntity<ResponseStructure<List<Student>>> createMultipleEntries(@RequestBody List<Student> students){
        List<Student> stu = serv.createMultipleEntries(students);
        ResponseStructure<List<Student>> rs = new ResponseStructure<>(201,"List added Successfully", stu);
        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    @GetMapping("/gender")
    public ResponseEntity<ResponseStructure<Map<String ,List<Student>>>> getStudentByGender(){
        Map<String ,List<Student>>  students = serv.getAllStudentsByGender();
        ResponseStructure<Map<String , List<Student>>> rs = new ResponseStructure<>(200 , "students fetc hed successfully" ,students);
        return ResponseEntity.ok(rs);
    }
}