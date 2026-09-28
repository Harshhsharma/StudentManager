package com.example.springdemo.Dto;

public class CourseValidationEvent {

    private String requestId;
    private Long studentId;
    private Long courseId;

    public CourseValidationEvent() {
    }

    public CourseValidationEvent(
            String requestId,
            Long studentId,
            Long courseId) {

        this.requestId = requestId;
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
