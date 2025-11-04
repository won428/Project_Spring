package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.AppealListDto;
import com.secondproject.secondproject.dto.CreditAppealDto;
import com.secondproject.secondproject.dto.EnrollmentInfoDto;
import com.secondproject.secondproject.entity.Appeal;
import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.repository.AppealRepository;
import com.secondproject.secondproject.repository.EnrollmentRepository;
import com.secondproject.secondproject.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditAppealService {

    private final EnrollmentRepository enrollmentRepository;
    private final AppealRepository appealRepository;


    public CreditAppealService(EnrollmentRepository enrollmentRepository,
                               AppealRepository appealRepository,
                               UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.appealRepository = appealRepository;

    }

    @Transactional(readOnly = true)
    public List<EnrollmentInfoDto> findEnrollmentsByUserId(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUser_Id(userId);
        return enrollments.stream().map(en -> {
            Lecture lecture = en.getLecture();
            EnrollmentInfoDto dto = new EnrollmentInfoDto();
            dto.setStudentId(dto.getStudentId());
            dto.setStudentName(dto.getStudentName());
            dto.setLectureId(lecture.getId());
            dto.setLectureName(lecture.getName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void createAppeal(CreditAppealDto saved) {
        Appeal gradeAppeal = new Appeal();
        Long userId = saved.getSendingId();
        Long lectureId = saved.getLectureId();
        Enrollment enrollment = this.enrollmentRepository.findByUserIdAndLectureId(userId, lectureId);

        gradeAppeal.setSendingId(saved.getSendingId());
        gradeAppeal.setEnrollment(enrollment);
        gradeAppeal.setAppealType(saved.getAppealType());
        gradeAppeal.setAppealDate(saved.getAppealDate());
        gradeAppeal.setStatus(saved.getStatus());
        gradeAppeal.setContent(saved.getContent());
        gradeAppeal.setTitle(saved.getTitle());
        gradeAppeal.setReceiverId(saved.getReceiverId());

        appealRepository.save(gradeAppeal);


    }

//    public List<AppealListDto> getMyAppeals(String sendingId) {
//        List<Appeal> appeals = appealRepository.findBySendingId(sendingId);
//
//        return appeals.stream()
//                .map(a -> {
//                    String lectureName = null;
//                    if (a.getEnrollment() != null && a.getEnrollment().getLecture() != null) {
//                        // 엔티티의 필드명이 lecName 인 경우(getLecName)
//                        // 만약 너의 Lecture 엔티티가 getLectureName() 또는 getName()이면 변경 필요
//                        try {
//                            lectureName = a.getEnrollment().getLecture().getName();
//                        } catch (NoSuchMethodError | NoSuchFieldError e) {
//                            // 안전망: 엔티티 메서드명이 다를 수 있으니 필요시 변경
//                            // lectureName = a.getEnrollment().getLecture().getLectureName();
//                        }
//                    }
//
//                    return new AppealListDto(
//                            a.getId(),
//                            lectureName,
//                            a.getTitle(),
//                            a.getContent(),
//                            a.getAppealDate(),
//                            a.getStatus()
//                    );
//                })
//                .collect(Collectors.toList());
//    }

    // 교수로 로그인 시 성적 이의제기 처리 기능 구현필요
}
