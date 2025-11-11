package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.Appeal;
import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.entity.Grade;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.secondproject.secondproject.Enum.Status.APPROVED;

@Service
@RequiredArgsConstructor
public class CreditAppealService {

    private final EnrollmentRepository enrollmentRepository;
    private final AppealRepository appealRepository;
    private final LectureRepository lectureRepository;
    private final GradeRepository gradeRepository;

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

    public StudentCreditDto getGradeForStudent(Long userId, Long lectureId) {
        Grade grade = gradeRepository.findByUserIdAndLectureId(userId, lectureId)
                .orElseThrow(() -> new RuntimeException("성적 정보가 존재하지 않습니다."));

        // 엔티티 -> DTO 변환
        StudentCreditDto dto = new StudentCreditDto();
        dto.setId(grade.getId());
        dto.setUserId(grade.getUser().getId());
        dto.setLectureId(grade.getLecture().getId());
        dto.setAScore(grade.getAScore());
        dto.setAsScore(grade.getAsScore());
        dto.setTScore(grade.getTScore());
        dto.setFtScore(grade.getFtScore());
        dto.setTotalScore(grade.getTotalScore());
        dto.setLectureGrade(grade.getLectureGrade());

        dto.setLecName(grade.getLecture().getName());
        dto.setStartDate(grade.getLecture().getStartDate());

        return dto;
    }

    // 강의별 이의제기 조회
    @Transactional(readOnly = true)
    public List<AppealManageDto> getAppealsByLecture(Long lectureId, Long receiverId) {
        List<Appeal> appeals = appealRepository.findByLectureIdAndReceiverId(lectureId, receiverId);

        return appeals.stream().map(appeal -> {
            Enrollment enrollment = appeal.getEnrollment();
            if (enrollment == null || enrollment.getUser() == null || enrollment.getGrade() == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Enrollment/Grade/User 정보가 없습니다.");
            }
            Grade grade = appeal.getEnrollment().getGrade();
            String studentName = enrollment.getUser().getName();
            Long studentCode = enrollment.getUser().getUserCode();

            return new AppealManageDto(
                    appeal.getId(),
                    appeal.getSendingId(),
                    appeal.getReceiverId(),
                    appeal.getLecture().getId(),
                    appeal.getTitle(),
                    studentName,
                    studentCode,
                    appeal.getContent(),
                    appeal.getAppealDate(),
                    appeal.getStatus(),
                    appeal.getAppealType(),
                    grade.getAScore(),
                    grade.getAsScore(),
                    grade.getTScore(),
                    grade.getFtScore(),
                    grade.getTotalScore(),
                    grade.getLectureGrade()
            );
        }).collect(Collectors.toList());
    }

    // 승인
    @Transactional
    public void approveAppeal(Long appealId, AppealManageDto dto) {
        Appeal appeal = appealRepository.findById(appealId)
                .orElseThrow(() -> new RuntimeException("Appeal not found"));

        // Enrollment → Grade 조회
        Grade grade = gradeRepository.findById(appeal.getEnrollment().getGrade().getId())
                .orElseThrow(() -> new RuntimeException("Grade not found"));

        // 점수는 null 안전하게 업데이트
        if (dto.getAScore() != null) grade.setAScore(dto.getAScore());
        if (dto.getAsScore() != null) grade.setAsScore(dto.getAsScore());
        if (dto.getTScore() != null) grade.setTScore(dto.getTScore());
        if (dto.getFtScore() != null) grade.setFtScore(dto.getFtScore());
        if (dto.getTotalScore() != null) grade.setTotalScore(dto.getTotalScore());

        // 상태 변경 등 다른 로직
        appeal.setStatus(APPROVED);

        appealRepository.save(appeal);
        gradeRepository.save(grade);
    }

    // 반려
    @Transactional
    public void rejectAppeal(Long appealId) {
        Appeal appeal = appealRepository.findById(appealId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "이의제기가 존재하지 않습니다."));
        appeal.setStatus(Status.REJECTED);
        appealRepository.save(appeal);
    }

    @Transactional
    public void updateScores(Long appealId, UpdateScoresDto dto) {
        Grade grade = gradeRepository.findByUserIdAndLectureId(dto.getSendingId(), dto.getLectureId())
                .orElseThrow(() -> new IllegalArgumentException("해당 학생의 성적 정보를 찾을 수 없습니다."));

        if (dto.getAScore() != null) grade.setAScore(dto.getAScore());
        if (dto.getAsScore() != null) grade.setAsScore(dto.getAsScore());
        if (dto.getTScore() != null) grade.setTScore(dto.getTScore());
        if (dto.getFtScore() != null) grade.setFtScore(dto.getFtScore());
        if (dto.getTotalScore() != null) grade.setTotalScore(dto.getTotalScore());
        if (dto.getLectureGrade() != null) grade.setLectureGrade(dto.getLectureGrade());

        gradeRepository.save(grade);
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
