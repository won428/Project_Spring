package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.AttendanceRequestDto;
import com.secondproject.secondproject.dto.AttendanceResponseDto;
import com.secondproject.secondproject.entity.Attendance_records;
import com.secondproject.secondproject.entity.Enrollment;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceStudentService {
    private final AttendanceRecordsRepository attendanceRecordsRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final GradingWeightsRepository gradingWeightsRepository;

    // 학생 출결 일괄 등록
    public List<AttendanceResponseDto> insertAttendances(Long id, List<AttendanceRequestDto> requestDtos) {
        // requestDtos에 있는 userId들만 뽑아놓은 리스트
        List<Long> userIds = requestDtos.stream()
                .map(r -> r.getUserId())
                .distinct()
                .toList();
        log.info("POST /lecture/{}/insertAttendances userIds={}", id, userIds);

        // 해당 강의에서 수강엔터티만 뽑아온 것(userId -> Enrollment로 매핑)
        List<Enrollment> enrollmentIds = enrollmentRepository.findByLecture_IdAndUser_IdIn(id, userIds);

        Map<Long, Enrollment> byUserId = enrollmentIds.stream()
                .collect(Collectors.toMap(enrollment -> enrollment.getUser().getId(), Function.identity()));

        List<Long> missing = userIds.stream()
                .filter(uid -> !byUserId.containsKey(uid))
                .toList();
        log.info("enrollmentsFound={} missing={}", enrollmentIds.size(), missing);

        if (!missing.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "수강(enrollment) 없음: " + missing);
        }

        // requestDtos 사이즈의 List 생성
        List<AttendanceResponseDto> responseDtoList = new ArrayList<>(requestDtos.size());

        // requestDtos 객체로 나열해서 Attendance_records에 저장하기
        for (AttendanceRequestDto dts : requestDtos){
            Enrollment e = byUserId.get(dts.getUserId());
            if (e == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"해당 ID가 없습니다.");
            }
            Long enrollmentId = e.getId();

            User user = userRepository.findById(dts.getUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"해당하는 사용자가 없습니다."));

            boolean dup = attendanceRecordsRepository.findByEnrollmentIdAndAttendanceDate(enrollmentId,dts.getAttendanceDate()).isPresent();

            if (dup){
                throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 등록된 출결입니다.");
            }

            Attendance_records a = new Attendance_records();
            a.setUser(user);
            a.setEnrollment(e);
            a.setAttendanceDate(dts.getAttendanceDate());
            a.setAttendStudent(dts.getAttendStudent());

            try{
                attendanceRecordsRepository.save(a);
            } catch (DataIntegrityViolationException ex){
                throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 저장된 정보입니다.");
            }

            AttendanceResponseDto responseDto = AttendanceResponseDto.builder()
                    .userId(dts.getUserId())
                    .enrollmentId(enrollmentId)
                    .attendanceDate(dts.getAttendanceDate())
                    .attendStudent(dts.getAttendStudent()).build();

            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }

    // 학생 출결 저장 시 기존 데이터가 존재하는지 확인
    public boolean isFinalizedAny(Long lectureId, LocalDate date){
        return attendanceRecordsRepository.existsByEnrollment_Lecture_IdAndAttendanceDate(lectureId, date);
    }

    public List<AttendanceResponseDto> getAttendances(Long lectureId, LocalDate date) {
        List<Attendance_records> list =
                attendanceRecordsRepository.findByEnrollment_Lecture_IdAndAttendanceDate(lectureId, date);

        return list.stream()
                .map(a -> AttendanceResponseDto.builder()
                        .userId(a.getUser().getId())
                        .enrollmentId(a.getEnrollment().getId())
                        .attendanceDate(a.getAttendanceDate())
                        .attendStudent(a.getAttendStudent())
                        .build())
                .toList();
    }

    // 학생 출결 점수 계산
    public AttendanceSummary getAttendanceSummary(Long lectureId, Long userId) {
        AttendanceCounts c = attendanceRecordsRepository.countByLectureAndOptionalUser(lectureId, userId);
        long total = Optional.ofNullable(c.getTotal()).orElse(0L); // 총 출결수
        long lateN = Optional.ofNullable(c.getLate()).orElse(0L); // 지각 수
        long earlyN = Optional.ofNullable(c.getEarlyLeave()).orElse(0L); // 조퇴 수
        long absent = Optional.ofNullable(c.getAbsent()).orElse(0L); // 결석 수
        long excused = Optional.ofNullable(c.getExcused()).orElse(0L); // 공결 수

        long totalLateAndAbsent = lateN + earlyN; // 지각 + 조퇴 총합

        double absentEq = absent + (0.3 * totalLateAndAbsent) + (0.1 * (totalLateAndAbsent / 3)); // 3회 지각시 -1점
        double effective = Math.max(0, total - absentEq);

        BigDecimal ratio = gradingWeightsRepository.findAttendanceRatioByLectureId(lectureId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"해당하는 값이 없습니다."));
        double r = ratio.doubleValue();
        double score = (total == 0) ? 0.0 : Math.round((effective / total * r) * 100.0) / 100.0;

        return  new AttendanceSummary(total, c.getPresent(), lateN, earlyN, absent, c.getExcused(), score);
    }

    public List<AttendanceResponseDto> listAttendance(Long userId, Long enrollmentId){
        List<Attendance_records> attendanceRecords = attendanceRecordsRepository.findByUserIdAndEnrollmentId(userId, enrollmentId);
        List<AttendanceResponseDto> dtoList = new ArrayList<>();
        for (Attendance_records a : attendanceRecords){
            AttendanceResponseDto dto = AttendanceResponseDto.builder()
                    .userId(a.getUser().getId())
                    .enrollmentId(a.getEnrollment().getId())
                    .attendanceDate(a.getAttendanceDate())
                    .attendStudent(a.getAttendStudent())
                    .build();
            dtoList.add(dto);
        }

        return dtoList;
    }

}
