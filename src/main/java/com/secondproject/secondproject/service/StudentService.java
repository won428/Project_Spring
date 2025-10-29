package com.secondproject.secondproject.service;

import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.repository.UserRepository;
import com.secondproject.secondproject.repository.RecordStatusRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
    private final UserRepository userRepository;
    private final RecordStatusRepository recordStatusRepository;

    public StudentService(UserRepository userRepository, RecordStatusRepository recordStatusRepository) {
        this.userRepository = userRepository;
        this.recordStatusRepository = recordStatusRepository;
    }

    public User getStudentByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getType() == UserType.STUDENT)
                .orElse(null);
    }

    public User getStudentById(Long id) {
        return userRepository.findById(id)
                .filter(u -> u.getType() == UserType.STUDENT)
                .orElse(null);
    }

    public StatusRecords getStatusRecordByUserId(Long userId) {
        return recordStatusRepository.findByUserId(userId).orElse(null);
    }


}

