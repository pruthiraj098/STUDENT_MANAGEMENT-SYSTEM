package com.sms.service;

import com.sms.dto.StudentActivityDto;
import com.sms.entity.StudentActivity;

import java.util.List;

public interface StudentActivityService {
    List<StudentActivity> getActivitiesByStudentId(Long studentId);
    StudentActivity addActivity(Long studentId, StudentActivityDto dto);
    void deleteActivity(Long activityId);
}
