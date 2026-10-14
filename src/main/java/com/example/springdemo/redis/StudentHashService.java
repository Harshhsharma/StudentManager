
package com.example.springdemo.redis;

import com.example.springdemo.entity.Student;
import com.example.springdemo.repository.Repo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StudentHashService {

    private final StringRedisTemplate redisTemplate;
    private final Repo repo;

    public StudentHashService(StringRedisTemplate redisTemplate, Repo repo) {
        this.redisTemplate = redisTemplate;
        this.repo = repo;
    }


    public void saveStudentById(Long id) {

        Student student = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found: " + id));

        String key = "student:" + student.getId();

        redisTemplate.opsForHash().put(key, "name", student.getName());
        redisTemplate.opsForHash().put(key, "email", student.getEmail());
        redisTemplate.opsForHash().put(key, "gender", String.valueOf(student.getGender()));
        redisTemplate.opsForHash().put(key, "age", String.valueOf(student.getAge()));
        redisTemplate.opsForHash().put(key, "course", String.valueOf(student.getCourse()));
        redisTemplate.opsForHash().put(key, "marks", String.valueOf(student.getMarks()));
        redisTemplate.opsForHash().put(key, "course_id", String.valueOf(student.getCourse_id()));
    }

    public Map<Object, Object> getStudentHash(Long id) {
        String key = "student:" + id;
        return redisTemplate.opsForHash().entries(key);
    }

    public boolean studentHashExists(Long id) {
        String key = "student:" + id;
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }


    public Student getStudentFromDatabase(Long id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student not found: " + id));
    }


    public Student convertHashToStudent(Long id) {
        Map<Object, Object> data = getStudentHash(id);

        if (data.isEmpty()) {
            return null;
        }

        Student student = new Student();

        student.setId(id);
        student.setName((String) data.get("name"));
        student.setEmail((String) data.get("email"));
        student.setGender((String) data.get("gender"));
        student.setCourse((String) data.get("course"));

        String age = (String) data.get("age");
        student.setAge(age == null || age.equals("null")
                ? null : Integer.valueOf(age));

        String marks = (String) data.get("marks");
        student.setMarks(marks == null || marks.equals("null")
                ? null : Double.valueOf(marks));

        String courseId = (String) data.get("course_id");
        student.setCourse_id(courseId == null || courseId.equals("null")
                ? null : Long.valueOf(courseId));

        return student;
    }

}


