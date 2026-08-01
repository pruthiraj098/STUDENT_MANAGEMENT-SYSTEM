package com.sms.service.impl;

import com.sms.dto.StudentActivityDto;
import com.sms.entity.Student;
import com.sms.entity.StudentActivity;
import com.sms.exception.ResourceNotFoundException;
import com.sms.repository.StudentActivityRepository;
import com.sms.repository.StudentRepository;
import com.sms.service.StudentActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentActivityServiceImpl implements StudentActivityService {

    private final StudentActivityRepository activityRepository;
    private final StudentRepository studentRepository;

    public StudentActivityServiceImpl(StudentActivityRepository activityRepository, StudentRepository studentRepository) {
        this.activityRepository = activityRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<StudentActivity> getActivitiesByStudentId(Long studentId) {
        return activityRepository.findByStudentIdOrderByActivityDateDesc(studentId);
    }

    @Override
    @Transactional
    public StudentActivity addActivity(Long studentId, StudentActivityDto dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        StudentActivity activity = new StudentActivity();
        activity.setTitle(dto.getTitle());
        activity.setCategory(dto.getCategory() != null ? dto.getCategory() : "Academic");
        activity.setDescription(dto.getDescription());
        activity.setActivityDate(dto.getActivityDate() != null ? dto.getActivityDate() : LocalDate.now());
        activity.setStudent(student);

        return activityRepository.save(activity);
    }

    @Override
    @Transactional
    public void deleteActivity(Long activityId) {
        StudentActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentActivity", "id", activityId));
        activityRepository.delete(activity);
    }
}
