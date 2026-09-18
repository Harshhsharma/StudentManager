package com.example.springdemo.controller;

import com.example.springdemo.Dto.StudentResponseDto;
import com.example.springdemo.entity.Enrollment;
import com.example.springdemo.entity.Student;
import com.example.springdemo.responseStructure.ResponseStructure;
import com.example.springdemo.service.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/test-course")
    public String testCourse() {

        serv.testCourseApi();

        return "Course API called successfully";
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<StudentResponseDto>> getStudentById(
            @PathVariable Long id) {

        StudentResponseDto student = serv.getStudentById(id);

        ResponseStructure<StudentResponseDto> response =
                new ResponseStructure<>(
                        200,
                        "Student found successfully",
                        student
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<Page<StudentResponseDto>>> getAllStudents(
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {

        String[] sortParams = sort.split(",");

        Sort.Direction direction =
                Sort.Direction.fromString(sortParams[1]);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(direction, sortParams[0])
                );

        Page<StudentResponseDto> students =
                serv.getAllStudents(course, gender, pageable);

        ResponseStructure<Page<StudentResponseDto>> response =
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

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseStructure<Student>> patchupdate(@PathVariable  Long id, @RequestBody Student student){

        Student updateStudent = serv.patchUpdate( id, student);
        ResponseStructure<Student> rs = new ResponseStructure<>(200, "Student partially update successfully" ,updateStudent);

        return ResponseEntity.ok(rs);

    }
    @PostMapping("/{studentId}/enroll/{courseId}")
    public ResponseEntity<ResponseStructure<Enrollment>> enrollStudent(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        Enrollment enrollment =
                serv.enrollStudent(studentId, courseId);

        ResponseStructure<Enrollment> response =
                new ResponseStructure<>(
                        201,
                        "Student enrolled successfully",
                        enrollment
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ResponseStructure<List<Student>>> getStudentsByCourseId(
            @PathVariable Long courseId) {

        List<Student> students =
                serv.getStudentsByCourseId(courseId);

        ResponseStructure<List<Student>> response =
                new ResponseStructure<>(
                        200,
                        "Students enrolled in course fetched successfully",
                        students
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );


    }
  // for pagination and sort linked to enrollment table
    @GetMapping("/course/{courseId}/students")
    public ResponseEntity<ResponseStructure<Page<StudentResponseDto>>> getStudentsByCourseId(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<StudentResponseDto> students =
                serv.getStudentsByCourseId(courseId, pageable);

        ResponseStructure<Page<StudentResponseDto>> response =
                new ResponseStructure<>(
                        200,
                        "Students enrolled in course fetched successfully",
                        students
                );

        return ResponseEntity.ok(response);
    }
}