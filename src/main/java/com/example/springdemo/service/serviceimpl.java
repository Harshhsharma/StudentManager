package com.example.springdemo.service;

import com.example.springdemo.entity.Student;
import com.example.springdemo.exception.DuplicateResourceException;
import com.example.springdemo.exception.ResourceNotFoundException;
import com.example.springdemo.repository.Repo;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class serviceimpl implements service {

    private final Repo repo;

    public serviceimpl(Repo repo) {
        this.repo = repo;
    }


    @Override
    public Student createStudent(Student student) {
        Optional<Student> existing = repo.findByEmail(student.getEmail());
        if(existing.isPresent()){
            throw new DuplicateResourceException("Email already exists");
        }
        return repo.save(student);
    }

    @Override
    public Page<Student> getAllStudents(String course, String gender, Pageable pageable) {
         if(course != null && gender != null){
             return repo.findByCourseAndGender(course,gender,pageable);
         } else if (course != null) {
             return repo.findByCourse(course, pageable);

         } else if (gender != null) {
             return repo.findByGender(gender, pageable);

         } else{
             return repo.findAll(pageable);
         }
    }


    @Override
    public Student getStudentById(Long id) {
        return repo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("student not found with id"+ id));
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


      return repo.save(existing);

    }


}
