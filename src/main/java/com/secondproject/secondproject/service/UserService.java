package com.secondproject.secondproject.service;


import com.secondproject.secondproject.Entity.User;
import com.secondproject.secondproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User getUserById(Long id) {
//        User user = userRepository.findByUid(id).orElse(null);
        User user = userRepository.getUserById(id);
        if (user == null) {
            // throw new RuntimeException("해당 유저 없음");
            return null;
        }
        return user;
    }


//    public Object issueCertificate(Long id, String type) {
//        // 1. 유저 조회
//        User user = getUserById(id);
//        if (user == null || user.getU_type() != UserType.STUDENT) {
//            return Map.of("error", "학생 전용 서비스입니다.");
//        }
//        return null;
//        // 2. 학적 정보(RecordStatus) 조회
//        StatusRecords sr = getStatusRecordById(user.getStatus_id());
//        if (sr == null) {
//            return Map.of("error", "학적 정보가 없습니다.");
//        }
//        boolean isGraduated = "졸업".equals(sr.getStudent_status());
//
//        // 3. 졸업생 여부 체크
//        if (!isGraduated) {
//            return Map.of("error", "재학생/휴학생은 증명서 발급 메뉴에서 신청하세요.");
//        }
//
//        // 4. 증명서 종류 확인
//        if (!List.of("등록금납부내역서", "성적증명서", "장학수혜내역서").contains(type)) {
//            return Map.of("error", "졸업생만 발급 가능한 증명서 종류입니다.");
//        }
//
//        // 5. 실제 증명서 발급 로직 (여기선 단순 성공 메시지 반환)
//        return Map.of("message", type + " 발급 완료 (유저ID: " + id + ")");
//    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
