package com.example.springdemo.service;

import com.example.springdemo.entity.Student;
import com.example.springdemo.exception.DuplicateResourceException;
import com.example.springdemo.exception.ResourceNotFoundException;
import com.example.springdemo.repository.Repo;
import org.springframework.stereotype.Service;

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
    public List<Student> getAllStudents() {

        return repo.findAll();

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


}
