package com.example.springdemo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDto {

     private Long id ;

     private String name;

     private String email;

    private Double marks;

    private Long course_id;

    private List<CourseResponseDto> courseResponseDtos;




}
