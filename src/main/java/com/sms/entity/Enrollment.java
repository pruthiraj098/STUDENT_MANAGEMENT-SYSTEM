package com.sms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private LocalDate enrollmentDate = LocalDate.now();

    private Double numericGrade; // 0.0 to 100.0

    @Column(length = 5)
    private String letterGrade; // A+, A, B, C, D, F

    @Column(length = 20)
    private String status = "ENROLLED"; // ENROLLED, COMPLETED, DROPPED

    @Column(length = 30)
    private String semester = "Semester 1";

    private Double attendancePercentage = 100.0;

    @Column(length = 30)
    private String labStatus = "SUBMITTED"; // SUBMITTED, PENDING, EVALUATED, NEEDS_REVISION

    @Column(length = 255)
    private String labSubmissionDetails = "Lab Manual & Experiments Verified";

    public Enrollment() {}

    public Enrollment(Student student, Course course, LocalDate enrollmentDate) {
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
    }

    public void updateGrade(Double numericGrade) {
        this.numericGrade = numericGrade;
        if (numericGrade == null) {
            this.letterGrade = null;
            return;
        }
        if (numericGrade >= 90) this.letterGrade = "A+";
        else if (numericGrade >= 85) this.letterGrade = "A";
        else if (numericGrade >= 80) this.letterGrade = "A-";
        else if (numericGrade >= 75) this.letterGrade = "B+";
        else if (numericGrade >= 70) this.letterGrade = "B";
        else if (numericGrade >= 65) this.letterGrade = "B-";
        else if (numericGrade >= 60) this.letterGrade = "C+";
        else if (numericGrade >= 55) this.letterGrade = "C";
        else if (numericGrade >= 50) this.letterGrade = "D";
        else this.letterGrade = "F";
    }

    public double convertToGpaPoint() {
        if (numericGrade == null) return 0.0;
        if (numericGrade >= 90) return 4.0;
        if (numericGrade >= 85) return 3.9;
        if (numericGrade >= 80) return 3.7;
        if (numericGrade >= 75) return 3.3;
        if (numericGrade >= 70) return 3.0;
        if (numericGrade >= 65) return 2.7;
        if (numericGrade >= 60) return 2.3;
        if (numericGrade >= 55) return 2.0;
        if (numericGrade >= 50) return 1.0;
        return 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Double getNumericGrade() {
        return numericGrade;
    }

    public void setNumericGrade(Double numericGrade) {
        this.numericGrade = numericGrade;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
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
