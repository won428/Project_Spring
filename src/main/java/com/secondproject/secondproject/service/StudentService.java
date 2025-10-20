package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.Entity.StatusRecords;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.repository.UserRepository;
import com.secondproject.secondproject.repository.RecordStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {

    private final UserRepository userRepository;
    private final RecordStatusRepository recordStatusRepository;

    @Autowired
    public StudentService(UserRepository userRepository, RecordStatusRepository recordStatusRepository) {
        this.userRepository = userRepository;
        this.recordStatusRepository = recordStatusRepository;
    }

    // 학생 정보 조회
    public User getStudentById(Long id) {
        Optional<User> userOpt = userRepository.getUserById(id);
        if (userOpt.isEmpty() || userOpt.get().getU_type() != UserType.STUDENT) {
            return null; // 학생이 아니거나 유저 없음
        }
        return userOpt.get();
    }

    // 학적 정보 조회
    public StatusRecords getStatusRecordById(Long statusId) {
        return recordStatusRepository.findByStatusId(statusId).orElse(null);
    }

    // 증명서 발급(학생, 졸업생 검증 포함)
    public Object issueCertificate(Long id, String type) {
        User user = getStudentById(id);
        if (user == null) {
            return Map.of("error", "학생만 접근 가능한 서비스입니다.");
        }

        StatusRecords sr = getStatusRecordById(user.getStatusRecords().getId());
        if (sr == null) {
            return Map.of("error", "학적 정보가 없습니다.");
        }

        boolean isGraduated = "졸업".equals(sr.getStudent_status());
        if (!isGraduated) {
            return Map.of("error", "재학생/휴학생은 증명서 발급 메뉴에서 신청하세요.");
        }

        List<String> allowedTypes = List.of("등록금납부내역서", "성적증명서", "장학수혜내역서");
        if (!allowedTypes.contains(type)) {
            return Map.of("error", "졸업생만 발급 가능한 증명서 종류입니다.");
        }

        // 증명서 발급 성공 처리 로직 (예시)
        return Map.of("message", type + " 발급 완료 (유저ID: " + id + ")");
    }
}
