package com.example.springdemo.service;

import com.example.springdemo.entity.Student;
import com.example.springdemo.repository.Repo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class serviceimpl implements service {

    private final Repo repo;

    public serviceimpl(Repo repo) {
        this.repo = repo;
    }

    @Override
    public Student createStudent(Student student) {
        return repo.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    @Override
    public Student getStudentById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    @Override
    public Student updateStudent(Long id, Student student) {

        Student existingStudent = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        existingStudent.setName(student.getName());
        existingStudent.setRollno(student.getRollno());

        return repo.save(existingStudent);
    }

    @Override
    public void deleteStudent(Long id) {

        if (!repo.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }

        repo.deleteById(id);
    }
}
