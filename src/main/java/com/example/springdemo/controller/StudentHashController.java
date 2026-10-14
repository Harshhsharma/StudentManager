
package com.example.springdemo.controller;

import com.example.springdemo.redis.StudentHashService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentHashController {

    private final StudentHashService studentHashService;

    public StudentHashController(StudentHashService studentHashService) {
        this.studentHashService = studentHashService;
    }

    @PostMapping("/{id}/hash")
    public String saveStudentHash(@PathVariable Long id) {
        studentHashService.saveStudentById(id);
        return "Student data saved to Redis Hash for ID: " + id;
    }


    @GetMapping("/{id}/hash")
    public Map<Object, Object> getStudentHash(@PathVariable Long id) {
        return studentHashService.getStudentHash(id);
    }

}

