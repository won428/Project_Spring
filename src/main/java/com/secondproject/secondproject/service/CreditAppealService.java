package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.AppealListDto;
import com.secondproject.secondproject.dto.CreditAppealDto;
import com.secondproject.secondproject.dto.EnrollmentInfoDto;
import com.secondproject.secondproject.dto.GradeAppealDto;
import com.secondproject.secondproject.entity.Appeal;
import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.repository.AppealRepository;
import com.secondproject.secondproject.repository.EnrollmentRepository;
import com.secondproject.secondproject.repository.LectureRepository;
import com.secondproject.secondproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditAppealService {

    private final EnrollmentRepository enrollmentRepository;
    private final AppealRepository appealRepository;
    private final LectureRepository lectureRepository;




    public List<AppealListDto> getAppealsByStudentId(Long studentId) {
        List<Appeal> appeals = appealRepository.findBySendingId(studentId); // ✅ 인스턴스로 호출

        return appeals.stream()
                .map(a -> new AppealListDto(
                        a.getId(),
                        a.getEnrollment().getLecture().getName(), // 강의명
                        a.getTitle(),
                        a.getContent(),
                        a.getAppealDate(),
                        a.getStatus()
                ))
                .toList();
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

    public void createGradeAppeal(GradeAppealDto appealForm) {
        Appeal appeal = new Appeal(); // 새로 만들 이의제기 객체

        // 폼에서 보낸 강의ID로 만든 객체
        Lecture lecture = this.lectureRepository.findById(appealForm.getLectureId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"없는 강의 입니다,"));
        // 특정된 강의와 보낸사람 ID와 일치하는 수강정보 생성
        Enrollment enrollment = this.enrollmentRepository.findByUserIdAndLectureId(appealForm.getSendingId(), lecture.getId());

        appeal.setReceiverId(appealForm.getReceiverId());
        appeal.setSendingId(appealForm.getSendingId());
        appeal.setAppealDate(LocalDate.now());
        appeal.setAppealType(appealForm.getAppealType());
        appeal.setTitle(appealForm.getTitle());
        appeal.setContent(appealForm.getContent());
        appeal.setLecture(lecture);
        appeal.setEnrollment(enrollment);
        appeal.setStatus(Status.PENDING);

        this.appealRepository.save(appeal);
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
