package com.example.springdemo.Dto;

public class CourseValidationResponse {

    private String requestId;
    private Long studentId;
    private Long courseId;
    private boolean courseExists;

    public CourseValidationResponse() {
    }

    public CourseValidationResponse(String requestId, Long studentId, Long courseId, boolean courseExists) {
        this.requestId = requestId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseExists = courseExists;
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

    public boolean isCourseExists() {
        return courseExists;
    }

    public void setCourseExists(boolean courseExists) {
        this.courseExists = courseExists;
    }
}
