package com.secondproject.secondproject.service;

import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.repository.UserRepository;
import com.secondproject.secondproject.repository.RecordStatusRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final RecordStatusRepository recordStatusRepository;

    public StudentService(UserRepository userRepository, RecordStatusRepository recordStatusRepository) {
        this.userRepository = userRepository;
        this.recordStatusRepository = recordStatusRepository;
    }

    // 이메일(=username)로 학생 조회
    public User getStudentByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getType() == UserType.STUDENT)
                .orElse(null);
    }

    // 사용자 ID로 학생 조회(향후 확장용, 현재 컨트롤러는 미사용)
    public User getStudentById(Long id) {
        return userRepository.findById(id)
                .filter(u -> u.getType() == UserType.STUDENT)
                .orElse(null);
    }

    // 사용자 ID로 학적 상태 조회
    public StatusRecords getStatusRecordByUserId(Long userId) {
        return (StatusRecords) recordStatusRepository.findByUserId(userId).orElse(null);
    }

}
