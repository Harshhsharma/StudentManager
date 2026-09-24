package com.example.springdemo.service;

import com.example.springdemo.Dto.CourseResponseDto;
import com.example.springdemo.Dto.EnrollmentEvent;
import com.example.springdemo.Dto.StudentResponseDto;
import com.example.springdemo.client.CourseClient;
import com.example.springdemo.entity.Enrollment;
import com.example.springdemo.entity.Student;
import com.example.springdemo.exception.DuplicateResourceException;
import com.example.springdemo.exception.EnrollmentLimitException;
import com.example.springdemo.exception.InvalidGenderException;
import com.example.springdemo.exception.ResourceNotFoundException;
import com.example.springdemo.repository.EnrollmentRepository;
import com.example.springdemo.repository.Repo;
import com.example.springdemo.responseStructure.ResponseStructure;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Pageable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class serviceimpl implements service {

    private final Repo repo;

    private final RestTemplate restTemplate;

    private final EnrollmentRepository enrollmentRepository;

    private final CourseClient courseClient;

    private final KafkaProducerService kafkaProducerService;

    @Override
    public Student createStudent(Student student) {
        Optional<Student> existing = repo.findByEmail(student.getEmail());
        if(existing.isPresent()){
            throw new DuplicateResourceException("Email already exists");
        }
        return repo.save(student);
    }

    @Override
    public Page<StudentResponseDto> getAllStudents(
            String course,
            String gender,
            Pageable pageable) {

        if (gender != null) {
            if (!gender.equalsIgnoreCase("male")
                    && !gender.equalsIgnoreCase("female")
                    && !gender.equalsIgnoreCase("others")) {

                throw new InvalidGenderException(
                        "please enter a valid gender.Allowed gender : male , female and others");
            }
        }

        Page<Student> students;

        if (course != null && gender != null) {
            students = repo.findByCourseAndGenderIgnoreCase(
                    course, gender, pageable);

        } else if (course != null) {
            students = repo.findByCourseIgnoreCase(
                    course, pageable);

        } else if (gender != null && !gender.isBlank()) {
            students = repo.findByGenderIgnoreCase(
                    gender, pageable);

        } else {
            students = repo.findAll(pageable);
        }


        // Page<Student> → Page<StudentResponseDto>
        return students.map(student -> {

            StudentResponseDto response = new StudentResponseDto();

            response.setId(student.getId());
            response.setName(student.getName());
            response.setEmail(student.getEmail());
            response.setMarks(student.getMarks());
            response.setCourse_id(student.getCourse_id());


            // Student ke enrolled course IDs nikalo
            List<Long> courseIds =
                    getCourseIdsByStudentId(student.getId());


            // Course details store karne ke liye list
            List<CourseResponseDto> courses =
                    new ArrayList<>();


            // Har course ID ka data Course Manager se lao
            for (Long courseId : courseIds) {

//                String url =   // ( with consul and feign client)
//                        "http://localhost:8081/courses/" + courseId;
//
//                ResponseEntity<ResponseStructure<CourseResponseDto>> courseResponse =
//                        restTemplate.exchange(
//                                url,
//                                HttpMethod.GET,
//                                null,
//                                new ParameterizedTypeReference<
//                                        ResponseStructure<CourseResponseDto>>() {}
//                        );
//
//                CourseResponseDto coursee =
//                        courseResponse.getBody().getData();

                ResponseEntity<ResponseStructure<CourseResponseDto>> courseResponse =
                        courseClient.getCourseById(courseId);

                CourseResponseDto coursee =
                        courseResponse.getBody().getData();

                courses.add(coursee);
            }


            // Saare courses DTO mein set karo
            response.setCourseResponseDtos(courses);

            return response;
        });
    }

    // Helper method
    private StudentResponseDto convertToStudentResponseDto(Student student) {

        StudentResponseDto response = new StudentResponseDto();

        response.setId(student.getId());
        response.setName(student.getName());
        response.setEmail(student.getEmail());
        response.setMarks(student.getMarks());
        response.setCourse_id(student.getCourse_id());

        List<Long> courseIds =
                getCourseIdsByStudentId(student.getId());

        List<CourseResponseDto> courses =
                new ArrayList<>();

        for (Long courseId : courseIds) {

            ResponseEntity<ResponseStructure<CourseResponseDto>> courseResponse =
                    courseClient.getCourseById(courseId);

            CourseResponseDto course =
                    courseResponse.getBody().getData();

            courses.add(course);
        }

        response.setCourseResponseDtos(courses);

        return response;
    }
    // for pagination and sort linked to enrollment table
    @Override
    public Page<StudentResponseDto> getStudentsByCourseId(
            Long courseId,
            Pageable pageable) {

        Page<Enrollment> enrollments =
                enrollmentRepository.findByCourseId(courseId, pageable);

        return enrollments.map(enrollment ->
                repo.findById(enrollment.getStudentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found with id: "
                                                + enrollment.getStudentId()
                                )
                        )
        ).map(this::convertToStudentResponseDto);
    }
    @Override
    public StudentResponseDto getStudentById(Long id) {

        // 1. Student ko database se find karo
        Student student = repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + id
                        )
                );


        // 2. Student ke enrolled course IDs nikalo
        List<Long> courseIds =
                getCourseIdsByStudentId(id);


        // 3. Course details store karne ke liye List
        List<CourseResponseDto> courses = new ArrayList<>();


        // 4. Har course ID ke liye Course Manager ko call karo
        for (Long courseId : courseIds) {

            ResponseEntity<ResponseStructure<CourseResponseDto>> courseResponse =
                    courseClient.getCourseById(courseId);

            CourseResponseDto course =
                    courseResponse.getBody().getData();

            courses.add(course);
        }


        // 5. Student ka response DTO banao
        StudentResponseDto response =
                new StudentResponseDto();

        response.setId(student.getId());
        response.setName(student.getName());
        response.setEmail(student.getEmail());
        response.setMarks(student.getMarks());

        response.setCourse_id(student.getCourse_id());

        // 6. Saare enrolled courses DTO mein set karo
        response.setCourseResponseDtos(courses);


        return response;
    }
    @Override
    public Student updateStudent(Long id, Student student) {
        Student existing = repo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("student not found with"+id));
         existing.setName(student.getName());
         existing.setEmail(student.getEmail());
         existing.setGender(student.getGender());
         existing.setAge(student.getAge());
         existing.setCourse(student.getCourse());
         existing.setMarks(student.getMarks());
         existing.setCourse_id(student.getCourse_id());

         return repo.save(existing);
    }

    @Override
    public void deleteStudent(Long id) {
          Student existing = repo.findById(id)
                  .orElseThrow(()-> new ResourceNotFoundException("student not found with"+id));
           repo.delete(existing);
    }

    @Override
    public List<Student> getTop10Students() {
        return repo.findTop10ByOrderByMarksDesc();
    }

    @Override
    public List<Student> createMultipleEntries(List<Student> students) {
        return repo.saveAll(students);
    }

    @Override
    public Map<String, List<Student>> getAllStudentsByGender() {
        List<Student> students = repo.findAll();
        return students.stream().collect(Collectors.groupingBy(Student::getGender));
    }

    @Override
    public Student patchUpdate(Long id, Student student) {
        Student existing = repo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Student is not present with id :" +id));
        if( student.getName() != null){
            existing.setName(student.getName());
        }

        if( student.getName() != null){
            existing.setName(student.getName());
        }
        if( student.getEmail() != null){
            existing.setEmail(student.getEmail());}

        if( student.getGender() != null){
            existing.setGender(student.getGender());
        }

        if( student.getAge() != null){
            existing.setAge(student.getAge());
        }
        if( student.getCourse() != null){
            existing.setCourse(student.getCourse());
        }

        if(student.getMarks() != null){
            existing.setMarks(student.getMarks());
        }

        if(student.getCourse_id() != null){
            existing.setCourse_id(student.getCourse_id());
        }



      return repo.save(existing);

    }

    @Override
    public void testCourseApi() {
        CourseResponseDto course =
                restTemplate.getForObject(
                        "http://localhost:8081/courses/2",
                        CourseResponseDto.class
                );

        System.out.println(course.getName());

    }
    @Override
    public Enrollment enrollStudent(Long studentId, Long courseId) {

        // 1. Check karo student exist karta hai ya nahi
        repo.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId
                        )
                );


        // 2. Check karo student already same course mein enrolled toh nahi hai
        Optional<Enrollment> existing =
                enrollmentRepository.findByStudentIdAndCourseId(
                        studentId,
                        courseId
                );

        if (existing.isPresent()) {
            throw new DuplicateResourceException(
                    "Student is already enrolled in this course"
            );
        }


        // 3. Course Manager se check karo ki course exist karta hai ya nahi

        try {

            ResponseEntity<ResponseStructure<CourseResponseDto>> courseResponse =
                    courseClient.getCourseById(courseId);

        } catch (FeignException.NotFound e) {

            throw new ResourceNotFoundException(
                    "Course not found with id: " + courseId
            );
        }


        // 4. Student ke already kitne courses hain?
        long count =
                enrollmentRepository.countByStudentId(studentId);

        if (count >= 2) {
            throw new EnrollmentLimitException(
                    "You are already enrolled in 2 courses. " +
                            "You cannot enroll in more courses"
            );
        }


        // 5. Enrollment object banao
        Enrollment enrollment = new Enrollment();

        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);


        // 6. Enrollment table mein save karo
        Enrollment savedEnrollment =
                enrollmentRepository.save(enrollment);

// 7. Kafka event publish karo
        EnrollmentEvent event =
                new EnrollmentEvent(
                        studentId,
                        courseId
                );

        kafkaProducerService.sendEnrollmentEvent(event);

        return savedEnrollment;
    }

    @Override
    public List<Long> getCourseIdsByStudentId(Long studentId) { //Student ko fetch karte time uske saare enrolled courses ka data dikhana hai.

        List<Enrollment> enrollments =
                enrollmentRepository.findByStudentId(studentId);

        return enrollments.stream()
                .map(Enrollment::getCourseId)
                .toList();
    }


    @Override
    public List<Student> getStudentsByCourseId(Long courseId) {

        List<Enrollment> enrollments =
                enrollmentRepository.findByCourseId(courseId);

        return enrollments.stream()
                .map(enrollment -> repo.findById(enrollment.getStudentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found with id: "
                                                + enrollment.getStudentId()
                                )
                        ))
                .toList();
    }
}
