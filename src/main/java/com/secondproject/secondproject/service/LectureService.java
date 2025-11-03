package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.LecRegAttach;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final CourseRegRepository courseRegRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final LecScheduleRepository lecScheduleRepository;
    private final AttachmentService attachmentService;
    private final LecRegAttachRepository lecRegAttachRepository;
    private final GradingWeightsRepository gradingWeightsRepository;

    public void insertByAdmin(LectureDto lectureDto, List<LectureScheduleDto> lectureScheduleDtos, List<MultipartFile> files, PercentDto percent) {


        BigDecimal totalPercent = percent.getAssignment()
                .add(percent.getAttendance())
                .add(percent.getMidtermExam())
                .add(percent.getFinalExam());
        BigDecimal overPercent = new BigDecimal("100.00");
        if (totalPercent.compareTo(overPercent) > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"비율은 100을 넘을 수 없습니다.");
        }


        Lecture lecture = new Lecture();

        Optional<User> optUser = this.userRepository.findById(lectureDto.getUser());
        User user = optUser.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음"));
        Optional<Major> optMajor = this.majorRepository.findById(lectureDto.getMajor());
        Major major = optMajor.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "학과 없음"));

        lecture.setName(lectureDto.getName());
        lecture.setUser(user);
        lecture.setCredit(lectureDto.getCredit());
        lecture.setStartDate(lectureDto.getStartDate());
        lecture.setEndDate(lectureDto.getEndDate());
        lecture.setDescription(lectureDto.getDescription());
        lecture.setMajor(major);
        lecture.setTotalStudent(lectureDto.getTotalStudent());
        lecture.setStatus(lectureDto.getStatus());
        lecture.setLevel(lectureDto.getLevel());
        lecture.setCompletionDiv(lectureDto.getCompletionDiv());

        Lecture saveLecture = this.lectureRepository.save(lecture);

        for (LectureScheduleDto dtoSchedule : lectureScheduleDtos){
            LectureSchedule schedule = new LectureSchedule();
            schedule.setDay(dtoSchedule.getDay());
            schedule.setLecture(saveLecture);
            schedule.setStartTime(dtoSchedule.getStartTime());
            schedule.setEndTime(dtoSchedule.getEndTime());

            this.lecScheduleRepository.save(schedule);
        }

        GradingWeights gradingWeights = new GradingWeights();
        gradingWeights.setLecture(saveLecture);
        gradingWeights.setAttendanceScore(percent.getAttendance());
        gradingWeights.setAssignmentScore(percent.getAssignment());
        gradingWeights.setMidtermExam(percent.getMidtermExam());
        gradingWeights.setFinalExam(percent.getFinalExam());

        this.gradingWeightsRepository.save(gradingWeights);

        if(files != null && !files.isEmpty()){
            for (MultipartFile file : files){
                try {
                    Attachment attachment = attachmentService.save(file,user);

                    LecRegAttach lecRegAttach = new LecRegAttach();
                    lecRegAttach.setAttachment(attachment);
                    lecRegAttach.setLecture(saveLecture);

                    this.lecRegAttachRepository.save(lecRegAttach);

                }catch (IOException ex){
                    throw new UncheckedIOException("파일 저장 실패", ex);
                }


            }
        }
    }



    public List<LectureDto> findAll() {
        List<Lecture> lectureList = this.lectureRepository.findAll();
        List<LectureDto> lectureDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            Long nowStudent = this.courseRegRepository.countByLecture_IdAndStatus(lecture.getId(), Status.SUBMITTED);

            LectureDto lectureDto = new LectureDto();
            lectureDto.setId(lecture.getId());
            lectureDto.setUser(lecture.getUser().getId());
            lectureDto.setName(lecture.getName());
            lectureDto.setCredit(lecture.getCredit());
            lectureDto.setStartDate(lecture.getStartDate());
            lectureDto.setEndDate(lecture.getEndDate());
            lectureDto.setMajorName(lecture.getMajor().getName());
            lectureDto.setUserName(lecture.getUser().getName());
            lectureDto.setStatus(lecture.getStatus());
            lectureDto.setTotalStudent(lecture.getTotalStudent());
            lectureDto.setNowStudent(nowStudent);
            lectureDto.setCompletionDiv(lecture.getCompletionDiv());
            lectureDto.setLevel(lecture.getLevel());

            lectureDtoList.add(lectureDto);
        }

        return lectureDtoList;
    }

    public void updateStatus(Long id, Status status) {
        Optional<Lecture> lectureOpt = this.lectureRepository.findById(id);
        Lecture lecture = lectureOpt
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, id + " 해당 강의가 존재하지 않습니다."));
        lecture.setStatus(status);
        this.lectureRepository.save(lecture);
    }

    // 일괄 수강신청
    public void applyLecture(List<Long> idList, Long userId) {

        if (idList == null || idList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "강의는 최소 1개 이상 선택해야합니다..");
        } else if (userId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음");
        }

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 사용자"));

        for (Long lecId : idList) {
            Lecture lecture = this.lectureRepository.findById(lecId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않습니다."));
            Long nowStudent = this.courseRegRepository.countByLecture_IdAndStatus(lecture.getId(), Status.SUBMITTED);
            int totalStudent = lecture.getTotalStudent();

            if (nowStudent >= totalStudent) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, lecture.getName() + " 강의는 정원 초과입니다.");
            }

            boolean alreadyApplied = courseRegRepository.existsByUser_IdAndLecture_Id(userId, lecId);
            if (alreadyApplied) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, lecture.getName() + " 강의는 이미 신청한 강의입니다.");
            }

//          boolean alreadyEnrollment = enrollmentRepository.existsByUser_IdAndLecture_Id(userId, lecture.getId());
//          if(alreadyEnrollment){
//              throw new ResponseStatusException(HttpStatus.CONFLICT, lecture.getName() + " 강의는 이미 수강한 강의입니다.");
//          }

            CourseRegistration courseRegistration = new CourseRegistration();
            // 수강 신청 테이블 생성
            courseRegistration.setUser(user);
            courseRegistration.setLecture(lecture);
            courseRegistration.setDate(LocalDateTime.now());
            courseRegistration.setStatus(Status.PENDING);
            this.courseRegRepository.save(courseRegistration);
        }
    }

    // 단일 수강신청
    public void applyLectureOne(Long lecId, Long userId) {

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음");
        }

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 사용자"));

        Lecture lecture = this.lectureRepository.findById(lecId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않습니다."));

        Long nowStudent = this.courseRegRepository.countByLecture_IdAndStatus(lecture.getId(), Status.SUBMITTED);
        int totalStudent = lecture.getTotalStudent();

        if (nowStudent >= totalStudent) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, lecture.getName() + " 강의는 정원 초과입니다.");
        }

        boolean alreadyApplied = courseRegRepository.existsByUser_IdAndLecture_Id(userId, lecId);
        if (alreadyApplied) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, lecture.getName() + " 강의는 이미 신청한 강의입니다.");
        }

        CourseRegistration courseRegistration = new CourseRegistration();

        // 수강 신청 테이블 생성
        courseRegistration.setUser(user);
        courseRegistration.setLecture(lecture);
        courseRegistration.setDate(LocalDateTime.now());
        courseRegistration.setStatus(Status.PENDING);
        this.courseRegRepository.save(courseRegistration);
    }

    public List<LectureDto> myLectureList(Long userId) {
        List<CourseRegistration> courseRegistrations = this.courseRegRepository.findByUser_Id(userId);
        List<LectureDto> myLectureList = new ArrayList<>();

        for (CourseRegistration courseReg : courseRegistrations) {
            Lecture lecture = this.lectureRepository.findById(courseReg.getLecture().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 강의"));
            LectureDto lectureDto = new LectureDto();

            lectureDto.setId(lecture.getId());
            lectureDto.setName(lecture.getName());
            lectureDto.setMajorName(lecture.getMajor().getName());
            lectureDto.setUserName(lecture.getUser().getName());
            lectureDto.setStartDate(lecture.getStartDate());
            lectureDto.setEndDate(lecture.getEndDate());
            lectureDto.setTotalStudent(lecture.getTotalStudent());
            lectureDto.setCredit(lecture.getCredit());
            lectureDto.setStatus(lecture.getStatus());
            lectureDto.setLevel(lecture.getLevel());
            lectureDto.setCompletionDiv(lecture.getCompletionDiv());
            lectureDto.setStatus(courseReg.getStatus());
            lectureDto.setLecStatus(lecture.getStatus());

            myLectureList.add(lectureDto);
        }

        return myLectureList;
    }

    public LectureDto findByID(Long id) {
        Lecture lecture = this.lectureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));
        LectureDto lectureDto = new LectureDto();
        Long nowStudent = this.courseRegRepository.countByLecture_IdAndStatus(lecture.getId(), Status.SUBMITTED);

        lectureDto.setName(lecture.getName());
        lectureDto.setMajorName(lecture.getMajor().getName());
        lectureDto.setTotalStudent(lecture.getTotalStudent());
        lectureDto.setUserName(lecture.getUser().getName());
        lectureDto.setNowStudent(nowStudent);

        return lectureDto;
    }

    public List<LectureDto> findByUser(User user) {
        List<Lecture> lectures = lectureRepository.findByUser(user);
        List<LectureDto> lectureListDto = new ArrayList<>();
        for (Lecture lecture : lectures) {
            lectureListDto.add(LectureDto.fromEntity(lecture));
        }
        return lectureListDto;
    }

    public LectureDto LectureSpec(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("강의가 존재하지 않습니다."));
        return LectureDto.fromEntity(lecture);
    }

    public List<LectureDto> applyLecturList(Long id) {
        List<Lecture> lectureList = this.lectureRepository.findAllNotRegisteredByUser(id);
        List<LectureDto> lectureDtoList = new ArrayList<>();

        for (Lecture lecture : lectureList) {
            LectureDto lectureDto = new LectureDto();

            lectureDto.setName(lecture.getName());
            lectureDto.setStatus(lecture.getStatus());
            lectureDto.setCredit(lecture.getCredit());
            lectureDto.setUser(lecture.getUser().getId());
            lectureDto.setTotalStudent(lecture.getTotalStudent());
            lectureDto.setLevel(lecture.getLevel());
            lectureDto.setId(lecture.getId());
            lectureDto.setEndDate(lecture.getEndDate());
            lectureDto.setDescription(lecture.getDescription());
            lectureDto.setStartDate(lecture.getStartDate());
            lectureDto.setCompletionDiv(lecture.getCompletionDiv());
            lectureDto.setMajorName(lecture.getMajor().getName());
            lectureDto.setUserName(lecture.getUser().getName());
            lectureDto.setMajor(lecture.getMajor().getId());

            lectureDtoList.add(lectureDto);
        }

        return lectureDtoList;
    }

    public void lectureInprogress(List<Long> idList, Status status) {

        if (idList == null || idList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "강의는 최소 1개 이상 선택해야합니다.");
        }

        for (Long lectureId : idList) {
            Lecture lecture = this.lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));

            List<CourseRegistration> courseRegistrationList = this.courseRegRepository.findAllByLecture_IdAndStatus(lectureId, Status.SUBMITTED);

            int total = lecture.getTotalStudent();
            int minRequired = (int) Math.ceil(total * 0.3); // 신청인원 일정 비율 이상일때 개강가능

            if (minRequired > courseRegistrationList.size()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "신청 인원이 부족합니다");
            }

            for (CourseRegistration courseReg : courseRegistrationList) {
                User user = this.userRepository.findById(courseReg.getUser().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

                lecture.setStatus(status);

                Grade grade = new Grade();
                grade.setLecture(lecture);
                grade.setUser(user);

                Grade newGrade = this.gradeRepository.save(grade);

                Enrollment enrollment = new Enrollment();
                enrollment.setUser(user);
                enrollment.setGrade(newGrade);
                enrollment.setLecture(lecture);
                enrollment.setStatus(Status.INPROGRESS);

                this.enrollmentRepository.save(enrollment);
            }
        }
    }

    public List<LectureDto> findByStudent(User user) {
        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);

        List<Long> lectureId = enrollments.stream()
                .map(Enrollment::getLecture)
                .map(Lecture::getId)
                .toList();

        List<Lecture> lectures = lectureRepository.findAllById(lectureId);

//        for(Long lec : lectureId){
//            Lecture lectures = lectureRepository.findAllById(lec)
//                    .orElseThrow(()->new EntityNotFoundException("sdsd"));
//            lectureDtoList.add(lectures);
//        }

        return lectures.stream()
                .map(LectureDto::fromEntity)
                .toList();
    }

    public void lectureChangeStatus(List<Long> idList, Status status) {

        if (idList == null || idList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "강의는 최소 1개 이상 선택해야합니다.");
        }

        for (Long lectureId : idList) {
            Lecture lecture = this.lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));
            lecture.setStatus(status);

            this.lectureRepository.save(lecture);
        }

    }

    public List<LecSessionResponseDto> selectSessions(Long id, LecSessionRequestDto requestDto) {
        List<LocalDate> base = datesByDays(requestDto.getStart(),requestDto.getEnd(),requestDto.getDays());

        Set<LocalDate> set = new LinkedHashSet<>(base);

        List<LecSessionListDto> sessions = set.stream()
                .sorted()
                .map(d -> new LecSessionListDto(

                ))
                .toList();
        return null;
    }

    public static List<LocalDate> datesByDays(LocalDate start, LocalDate end, Set<DayOfWeek> days){
        List<LocalDate> out = new ArrayList<>(); // 결과 날짜들을 담을 리스트 생성

        for(DayOfWeek dow : days){ // 요청한 각 요일에 대해 반복
            LocalDate first = start.with( // 시작일 이상에서
                    TemporalAdjusters.nextOrSame(dow)); // 해당 요일의 첫번째 날짜를 계산
            for (LocalDate d = first; // 첫번째 날짜가 d면서,
                 !d.isAfter(end); // 종료일을 초과하지 않으면서,
                 d = d.plusWeeks(1)){ // 1주(7일)씩 증가하여 매주 같은 요일로 증가
                out.add(d); // 해당 날짜를 결과 리스트에 담기
            }
        }
        out.sort(Comparator.naturalOrder()); // 요일별로 모은 리스트를 전체 오름차순으로 정렬
        return out; // 최종리스트 반환
    }
}
