package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.AppealType;
import com.secondproject.secondproject.Enum.AttendStudent;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.AppealAttach;
import com.secondproject.secondproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final AttendanceRecordsRepository attendanceRecordsRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final AppealAttachRepository appealAttachRepository;
    private final GradeService gradeService;
    private final AttendanceStudentService attendanceStudentService;
    private final GradingCalculator gradingCalculator;
    private final GradingWeightsRepository gradingWeightsRepository;

    public List<AppealListDto> getAppealsByStudentId(Long studentId) {
        List<Appeal> appeals = appealRepository.findBySendingId(studentId); // ✅ 인스턴스로 호출

        return appeals.stream()
                .map(a -> new AppealListDto(
                        a.getId(),
                        a.getEnrollment().getLecture().getName(), // 강의명
                        a.getTitle(),
                        a.getContent(),
                        a.getAppealDate(),
                        a.getStatus(),
                        a.getAppealType()
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

    @Transactional
    public Long createGradeAppeal(GradeAppealDto appealForm) {
        // 1. 새 이의제기 객체 생성
        Appeal appeal = new Appeal();

        // 강의와 수강정보 조회
        Lecture lecture = this.lectureRepository.findById(appealForm.getLectureId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 강의입니다"));
        Enrollment enrollment = this.enrollmentRepository.findByUserIdAndLectureId(
                appealForm.getSendingId(), lecture.getId());

        // 이의제기 정보 설정
        appeal.setReceiverId(appealForm.getReceiverId());
        appeal.setSendingId(appealForm.getSendingId());
        appeal.setAppealDate(LocalDate.now());
        appeal.setAppealType(appealForm.getAppealType());
        appeal.setTitle(appealForm.getTitle());
        appeal.setContent(appealForm.getContent());
        appeal.setLecture(lecture);
        appeal.setEnrollment(enrollment);
        appeal.setStatus(Status.PENDING);

        // 2. 새 이의제기 저장
        this.appealRepository.save(appeal);

        // 3. ID 반환 (파일 매핑 시 사용)
        return appeal.getId();
    }

    @Transactional
    public void mapAttachmentsToAppeal(Long appealId, List<Attachment> attachments) {
        Appeal appeal = appealRepository.findById(appealId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appeal not found"));

        for (Attachment file : attachments) {
            AppealAttach attachMapping = new AppealAttach();
            attachMapping.setAppeal(appeal);
            attachMapping.setAttachment(file);
            appealAttachRepository.save(attachMapping);
        }
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

        List<Appeal> appeals = appealRepository.findByLectureAndReceiverWithDetails(lectureId, receiverId);

        return appeals.stream().map(appeal -> {

            Enrollment enrollment = appeal.getEnrollment();
            if (enrollment == null || enrollment.getUser() == null || enrollment.getGrade() == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Enrollment/Grade/User 정보가 없습니다.");
            }

            User sender = userRepository.findById(appeal.getSendingId())
                    .orElse(null);

            String studentName = "";
            String studentCode = "";

            if (sender != null && sender.getType() == UserType.STUDENT) {
                studentName = sender.getName();
                studentCode = String.valueOf(sender.getUserCode());
            }

            Grade grade = enrollment.getGrade();

            return new AppealManageDto(
                    appeal.getId(),                         // appealId
                    appeal.getSendingId(),                  // sendingId
                    appeal.getReceiverId(),                 // receiverId
                    appeal.getLecture().getId(),            // lectureId
                    appeal.getTitle(),                      // title
                    studentName,                            // studentName
                    studentCode,                            // studentCode
                    appeal.getContent(),                    // content
                    appeal.getAppealDate(),                 // appealDate
                    appeal.getStatus(),                     // status
                    appeal.getAppealType(),                 // appealType
                    grade.getAScore(),                      // aScore
                    grade.getAsScore(),                     // asScore
                    grade.getTScore(),                      // tScore
                    grade.getFtScore(),                     // ftScore
                    grade.getTotalScore(),                  // totalScore
                    grade.getLectureGrade()                 // lectureGrade
            );

        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AttendanceCheckDto getAttendanceByAppeal(Long appealId) {
        // 1. Appeal 조회
        Appeal appeal = appealRepository.findById(appealId)
                .orElseThrow(() -> new RuntimeException("Appeal not found"));

        // 2. Enrollment ID 가져오기
        Long enrollmentId = appeal.getEnrollment().getId();

        // 3. Attendance_records 조회 (학생 신청일 기준으로 필터)
        List<Attendance_records> records = attendanceRecordsRepository.findByEnrollmentId(enrollmentId);

        // 4. 학생이 신청한 record 찾기
        // 예: Appeal.content 안에 신청한 출결 상태가 포함되어 있다고 가정
        Attendance_records appliedRecord = records.stream()
                .filter(r -> r.getAttendStudent().toString().equals(extractRequestedStatus(appeal.getContent())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Attendance record not found for this appeal"));

        // 5. DTO 반환
        return new AttendanceCheckDto(
                appliedRecord.getId(),
                appliedRecord.getAttendanceDate(),
                appliedRecord.getAttendStudent()
        );
    }

    private String extractRequestedStatus(String content) {
        // content 예시: "[MEDICAL_PROBLEM] [ABSENT] ... "
        if (content.contains("ABSENT")) return "ABSENT";
        if (content.contains("PRESENT")) return "PRESENT";
        if (content.contains("LATE")) return "LATE";
        if (content.contains("EARLY_LEAVE")) return "EARLY_LEAVE";
        if (content.contains("EXCUSED")) return "EXCUSED";
        return "";
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

    @Transactional
    public void updateAttendanceAppeal(Long appealId, AttendanceAppealDto dto) {
        // 1. Appeal 조회
        Appeal appeal = appealRepository.findById(appealId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이의제기가 존재하지 않습니다."));

        if (appeal.getAppealType() != AppealType.ATTENDANCE) {
            throw new IllegalArgumentException("출결 이의제기가 아닙니다.");
        }

        // 2. Enrollment 조회
        Enrollment enrollment = appeal.getEnrollment();
        if (enrollment == null) {
            throw new IllegalStateException("Enrollment 정보가 존재하지 않습니다.");
        }

        if (dto.getAttendanceDate() == null) {
            throw new IllegalArgumentException("출결 날짜가 지정되지 않았습니다.");
        }

        // 3. Attendance_records 조회 (Enrollment + attendanceDate 기준)
        Attendance_records attendanceRecord = attendanceRecordsRepository
                .findByEnrollment_IdAndAttendanceDate(enrollment.getId(), dto.getAttendanceDate())
                .orElseThrow(() -> new IllegalStateException("출결 기록이 존재하지 않습니다."));

        // 4. Attendance_records 상태 업데이트
        AttendStudent newStatus = dto.getAttendStudent();
        if (newStatus != null) {
            attendanceRecord.setAttendStudent(newStatus);
            attendanceRecordsRepository.save(attendanceRecord);
        }

        // 5. Appeal 상태 업데이트 (선택)
        if (dto.getStatus() != null) {
            appeal.setStatus(dto.getStatus());
            appealRepository.save(appeal);
        }
// 여기까지 완료
        AttendanceSummary newatt = this.attendanceStudentService.getAttendanceSummary(enrollment.getLecture().getId(), enrollment.getUser().getId());
        Grade grade = this.gradeRepository.findByUser_IdAndLecture_Id(enrollment.getUser().getId(), enrollment.getLecture().getId());
        GradingWeights gradingWeights = this.gradingWeightsRepository.findByLecture_Id(enrollment.getLecture().getId());

        BigDecimal attWeight = gradingWeights.getAttendanceScore();
        BigDecimal asWeight = gradingWeights.getAssignmentScore();
        BigDecimal tWeight = gradingWeights.getMidtermExam();
        BigDecimal fWeight = gradingWeights.getFinalExam();

        BigDecimal attScore  = BigDecimal.valueOf(newatt.score());
        BigDecimal asScore = grade.getAsScore();
        BigDecimal tScore = grade.getTScore();
        BigDecimal ftScore = grade.getFtScore();

        GradingInputs gradingInputs = new GradingInputs(attScore,asScore,tScore,ftScore);
        GradingWeightsDto gradingWeightsDto = new GradingWeightsDto(attWeight, asWeight,tWeight,fWeight);
        GradingResult gradingResult = gradingCalculator.gradeCalculating(gradingInputs, gradingWeightsDto);

        grade.setAScore(attScore);
        grade.setTotalScore(gradingResult.totalScore());
        grade.setLectureGrade(gradingResult.gpa());

        this.gradeRepository.save(grade);



    }
    @Transactional
    public List<AttachmentDto> getAttachmentsByAppealId(Long appealId) {
        List<AppealAttach> mappings = attachmentRepository.findAttachmentsByAppealId(appealId);

        List<AttachmentDto> dtoList = new ArrayList<>();
        for (AppealAttach mapping : mappings) {
            Attachment att = attachmentRepository.findById(mapping.getAttachment().getId())
                    .orElseThrow(() -> new RuntimeException("Attachment not found"));

            AttachmentDto dto = new AttachmentDto();
            dto.setId(att.getId());
            dto.setName(att.getName());
            dtoList.add(dto);
        }

        return dtoList;
    }
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

