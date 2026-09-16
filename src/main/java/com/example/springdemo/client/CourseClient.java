package com.example.springdemo.client;

import com.example.springdemo.Dto.CourseResponseDto;
import com.example.springdemo.responseStructure.ResponseStructure;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service")
public interface CourseClient {

    @GetMapping("/courses/{courseId}")
    ResponseEntity<ResponseStructure<CourseResponseDto>> getCourseById(
            @PathVariable Long courseId
    );

}