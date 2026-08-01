package com.sms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EnrollmentDto {

    private Long id;

    @NotNull(message = "Student is required")
    private Long studentId;

    @NotNull(message = "Course is required")
    private Long courseId;

    @Min(value = 0, message = "Grade cannot be negative")
    @Max(value = 100, message = "Grade cannot exceed 100")
    private Double numericGrade;

    private String status = "ENROLLED";

    private String semester = "Semester 1";

    @Min(value = 0, message = "Attendance percentage cannot be negative")
    @Max(value = 100, message = "Attendance percentage cannot exceed 100")
    private Double attendancePercentage = 100.0;

    private String labStatus = "SUBMITTED"; // SUBMITTED, PENDING, EVALUATED, NEEDS_REVISION

    private String labSubmissionDetails = "Lab Manual & Experiments Verified";

    public EnrollmentDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getNumericGrade() {
        return numericGrade;
    }

    public void setNumericGrade(Double numericGrade) {
        this.numericGrade = numericGrade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(Double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public String getLabStatus() {
        return labStatus;
    }

    public void setLabStatus(String labStatus) {
        this.labStatus = labStatus;
    }

    public String getLabSubmissionDetails() {
        return labSubmissionDetails;
    }

    public void setLabSubmissionDetails(String labSubmissionDetails) {
        this.labSubmissionDetails = labSubmissionDetails;
    }
}
