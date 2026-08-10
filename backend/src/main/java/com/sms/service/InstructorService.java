package com.sms.service;

import com.sms.dto.InstructorDto;
import com.sms.dto.InstructorRequestDto;
import com.sms.entity.Instructor;
import com.sms.entity.InstructorRequest;

import java.util.List;

public interface InstructorService {
    List<Instructor> getAllInstructors();
    Instructor getInstructorById(Long id);
    Instructor saveInstructor(InstructorDto dto);
    Instructor updateInstructor(Long id, InstructorDto dto);
    void deleteInstructor(Long id);
    void toggleInstructorStatus(Long id);
    void resetInstructorPassword(Long id, String newPassword);
    long getTotalInstructorsCount();

    // Request System methods
    InstructorRequest submitInstructorRequest(InstructorRequestDto requestDto);
    List<InstructorRequest> getPendingRequests();
    List<InstructorRequest> getAllRequests();
    Instructor approveInstructorRequest(Long requestId);
    void rejectInstructorRequest(Long requestId, String reason);
}
