package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.LecRegAttach;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LectureService {
    @Value("${file.upload-dir}")
    private String uploadDir;

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
    private final CollegeRepository collegeRepository;
    private final AttachmentRepository attachmentRepository;

    @Transactional
    public void insertByAdmin(LectureDto lectureDto, List<LectureScheduleDto> lectureScheduleDtos, List<MultipartFile> files, PercentDto percent) {

        if(lectureDto.getMajor() == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"소속 대학과 학과를 선택해주세요");
        }
        if(lectureDto.getStartDate() == null || lectureDto.getEndDate() == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "강의 날짜를 선택해주세요");
        }

        LocalDate start = lectureDto.getStartDate();
        LocalDate end   = lectureDto.getEndDate();
        LocalDate today = LocalDate.now();

        if(!start.isAfter(today)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"오늘 이후의 강의만 등록할 수 있습니다.");
        }

        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "종료일이 시작일보다 빠릅니다.");
        }

        LocalDate minEnd = start.plusMonths(2);
        if (end.isBefore(minEnd)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "강의 기간은 최소 2개월이어야 합니다. (가능한 최소 종료일: " + minEnd + ")"
            );
        }
        if(lectureScheduleDtos == null || lectureScheduleDtos.isEmpty()){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"수업 일정을 하루 이상 선택해주세요.");
        }

        if(lectureDto.getUser() == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"담당 교수를 선택해주세요.");
        }
        if(lectureDto.getCredit() == 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"이수 학점은 1점 이상이여야 합니다.");
        }
        if(lectureDto.getName().isBlank() || lectureDto.getName() == null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"강의명을 입력해주세요.");
        }
        if(lectureDto.getLevel() == 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"학년을 선택해주세요.");
        }
        if(lectureDto.getTotalStudent() < 10){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수강인원은 10명 이상이여야 합니다.");
        }

        BigDecimal totalPercent = percent.getAssignment()
                .add(percent.getAttendance())
                .add(percent.getMidtermExam())
                .add(percent.getFinalExam());
        BigDecimal overPercent = new BigDecimal("100.00");
        if (totalPercent.compareTo(overPercent) > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"비율은 100을 넘을 수 없습니다.");
        }

        for(LectureScheduleDto lectureScheduleDto : lectureScheduleDtos){
            if(lectureScheduleDto.getDay() == null || lectureScheduleDto.getStartTime() == null || lectureScheduleDto.getEndTime() == null){
                throw new ResponseStatusException(HttpStatus.CONFLICT,"수업 요일과 교시를 모두 선택해주세요.");
            }
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


    @Transactional
    public List<LectureDto> findAll() {
        List<Lecture> lectureList = this.lectureRepository.findAll();
        List<LectureDto> lectureDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            Long nowStudent = this.courseRegRepository.countByLecture_IdAndStatus(lecture.getId(), Status.SUBMITTED);
            List<LectureSchedule> lectureScheduleList = this.lecScheduleRepository.findAllByLecture_Id(lecture.getId());
            List<LectureScheduleDto> lectureScheduleDtos = new ArrayList<>();
            for(LectureSchedule lectureSchedule : lectureScheduleList){

                LectureScheduleDto scheduleDto = new LectureScheduleDto();
                scheduleDto.setLecture(lectureSchedule.getLecture().getId());
                scheduleDto.setDay(lectureSchedule.getDay());
                scheduleDto.setEndTime(lectureSchedule.getEndTime());
                scheduleDto.setStartTime(lectureSchedule.getStartTime());

                lectureScheduleDtos.add(scheduleDto);
            }

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
            lectureDto.setLectureSchedules(lectureScheduleDtos);

            lectureDtoList.add(lectureDto);
        }

        return lectureDtoList;
    }
    @Transactional
    public void updateStatus(Long id, Status status) {
        Optional<Lecture> lectureOpt = this.lectureRepository.findById(id);
        Lecture lecture = lectureOpt
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, id + " 해당 강의가 존재하지 않습니다."));
        if (status.equals(Status.INPROGRESS)) {
            List<CourseRegistration> courseRegistrationList = this.courseRegRepository.findAllByLecture_IdAndStatus(id, Status.SUBMITTED);
            if (courseRegistrationList == null || courseRegistrationList.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "신청 인원이 없습니다.");
            }

            lecture.setStatus(status);
            this.lectureRepository.save(lecture);
            // 테스트 위해서 임시로 비활성화
//            int total = lecture.getTotalStudent();
//            int minRequired = (int) Math.ceil(total * 0.8); // 신청인원 일정 비율 이상일때 개강가능
//
//            if (minRequired > courseRegistrationList.size()) {
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "신청 인원이 부족합니다");
//            }

            for (CourseRegistration courseReg : courseRegistrationList) {
                User user = this.userRepository.findById(courseReg.getUser().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));


                Grade grade = new Grade();
                grade.setLecture(lecture);
                grade.setUser(user);

                Grade newGrade = this.gradeRepository.save(grade);

                Enrollment enrollment = new Enrollment();
                enrollment.setUser(user);
                enrollment.setGrade(newGrade);
                enrollment.setLecture(lecture);
                enrollment.setStatus(Status.INPROGRESS);
                enrollment.setCompletionDiv(lecture.getCompletionDiv());

                this.enrollmentRepository.save(enrollment);
            }
        } else if (status.equals(Status.COMPLETED)) {
            List<Grade> gradeList = this.gradeRepository.findAllByLecture_Id(id);
            if (gradeList.isEmpty() || gradeList == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "점수 정보가 없는 강의는 종강 할 수 없습니다.");
            }
            for (Grade grade : gradeList) {
                if (grade.getLectureGrade() == null || grade.getAScore() == null || grade.getAsScore() == null || grade.getFtScore() == null || grade.getTScore() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아직 점수가 전부 입력되지 않은 강의는 종강 할 수 없습니다.");
                }
            }
            lecture.setStatus(status);
            this.lectureRepository.save(lecture);
        } else {
            lecture.setStatus(status);
            this.lectureRepository.save(lecture);
        }
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

            Long nowStudent = this.courseRegRepository.countByLecture_IdAndStatus(lecture.getId(), Status.SUBMITTED);

            List<LectureSchedule> lectureScheduleList = this.lecScheduleRepository.findAllByLecture_Id(lecture.getId());
            List<LectureScheduleDto> lectureScheduleDtos = new ArrayList<>();
            for(LectureSchedule schedule : lectureScheduleList){
                LectureScheduleDto scheduleDto = new LectureScheduleDto();
                scheduleDto.setId(schedule.getId());
                scheduleDto.setLecture(schedule.getLecture().getId());
                scheduleDto.setDay(schedule.getDay());
                scheduleDto.setStartTime(schedule.getStartTime());
                scheduleDto.setEndTime(schedule.getEndTime());

                lectureScheduleDtos.add(scheduleDto);
            }

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
            lectureDto.setStatus(courseReg.getStatus()); // 내 수강신청 상태(신청 확정, 장바구니 등)
            lectureDto.setLecStatus(lecture.getStatus()); // 강의 상태(개강,종강,대기 등)
            lectureDto.setLectureSchedules(lectureScheduleDtos);
            lectureDto.setNowStudent(nowStudent);

            myLectureList.add(lectureDto);
        }

        return myLectureList;
    }

    public LectureDto findByID(Long id) {
        Lecture lecture = this.lectureRepository.findById(id) // 해당 강의 객체
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));
        Major major = this.majorRepository.findById(lecture.getMajor().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 학과입니다.")); // 소속 단과대학 & 학과 얻기위한 객체

        LectureDto lectureDto = new LectureDto(); // 스프링으로 보낼 강의 dto객체
        Long nowStudent = this.courseRegRepository.countByLecture_IdAndStatus(lecture.getId(), Status.SUBMITTED); // 해당 강의 현재 수강신청인원

        List<LectureSchedule> lectureSchedules = this.lecScheduleRepository.findAllByLecture_Id(lecture.getId()); // 해당 강의 시간표
        List<LectureScheduleDto> lectureScheduleDtoList = new ArrayList<>(); // 해당 강의 시간표 dto 리스트

        List<LecRegAttach> lecRegAttachList = this.lecRegAttachRepository.findByLecture_id(lecture.getId()); // 해당 강의가 가지고 있는 첨부파일 리스트
        List<AttachmentDto> attachmentDtoList = new ArrayList<>(); // 해당강의 첨부파일 dto 리스트

        // 첨부파일이 없을 수도 있으므로 if문으로 작성
        if (lecRegAttachList != null || !lecRegAttachList.isEmpty()) {
            for (LecRegAttach lecRegAttach : lecRegAttachList) {
                Attachment attachment = this.attachmentRepository.findById(lecRegAttach.getAttachment().getId())
                        .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"첨부파일이 존재하지 않습니다."));
                AttachmentDto attachmentDto = new AttachmentDto();

                attachmentDto.setContentType(attachment.getContentType());
                attachmentDto.setName(attachment.getName());
                attachmentDto.setId(attachment.getId());
                attachmentDto.setSha256(attachment.getSha256());
                attachmentDto.setStoredKey(attachment.getStoredKey());
                attachmentDto.setSizeBytes(attachment.getSizeBytes());
                attachmentDto.setUploadAt(attachment.getUploadAt());

                attachmentDtoList.add(attachmentDto);

            }
        }

        GradingWeights gradingWeights = this.gradingWeightsRepository.findByLecture_Id(lecture.getId());
        GradingWeightsDto weightsDto = new GradingWeightsDto(gradingWeights.getAttendanceScore(),
                gradingWeights.getAssignmentScore(),
                gradingWeights.getMidtermExam(),
                gradingWeights.getFinalExam());
//        weightsDto.setAssignment(gradingWeights.getAssignmentScore());
//        weightsDto.setFinalExam(gradingWeights.getFinalExam());
//        weightsDto.setMidtermExam(gradingWeights.getMidtermExam());
//        weightsDto.setAttendance(gradingWeights.getAttendanceScore());


        for(LectureSchedule schedule : lectureSchedules){
            LectureScheduleDto scheduleDto = new LectureScheduleDto();

            scheduleDto.setId(schedule.getId());
            scheduleDto.setLecture(schedule.getLecture().getId());
            scheduleDto.setDay(schedule.getDay());
            scheduleDto.setStartTime(schedule.getStartTime());
            scheduleDto.setEndTime(schedule.getEndTime());

            lectureScheduleDtoList.add(scheduleDto);
        }

        lectureDto.setName(lecture.getName());
        lectureDto.setMajorName(lecture.getMajor().getName());
        lectureDto.setTotalStudent(lecture.getTotalStudent());
        lectureDto.setUserName(lecture.getUser().getName());
        lectureDto.setNowStudent(nowStudent);
        lectureDto.setLectureSchedules(lectureScheduleDtoList);
        lectureDto.setDescription(lecture.getDescription());
        lectureDto.setAttachmentDtos(attachmentDtoList);
        lectureDto.setCredit(lecture.getCredit());
        lectureDto.setCollege(major.getCollege().getId());
        lectureDto.setCompletionDiv(lecture.getCompletionDiv());
        lectureDto.setId(lecture.getId());
        lectureDto.setStartDate(lecture.getStartDate());
        lectureDto.setEndDate(lecture.getEndDate());
        lectureDto.setMajor(major.getId());
        lectureDto.setLevel(lecture.getLevel());
        lectureDto.setStatus(lecture.getStatus());
        lectureDto.setUser(lecture.getUser().getId());
        lectureDto.setWeightsDto(weightsDto);

        return lectureDto;
    }

    public List<LectureDto> findByUser(User user, String sortKey) {
        List<Lecture> lectures = lectureRepository.findByUser(user);
        String[] sort = sortKey.split("-");
        int targetYear = Integer.parseInt(sort[0]);
        int termKey = Integer.parseInt(sort[1]);
        int targetMonth = termKey * 3;

        List<Lecture> filteredLecture = lectures.stream()
                .filter(
                        lecture -> {
                            LocalDate startDate = lecture.getStartDate();
                            boolean yearMatches = startDate.getYear() == targetYear;

                            boolean monthMatches = startDate.getMonthValue() == targetMonth;

                            return yearMatches && monthMatches;
                        }
                )
                .toList();
        return filteredLecture.stream()
                .map(LectureDto::fromEntity)
                .toList();
    }

    public List<LectureDto> findByStudent(User user, String sortKey) {
        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);
        String[] sort = sortKey.split("-");
        int targetYear = Integer.parseInt(sort[0]);
        int termKey = Integer.parseInt(sort[1]);
        int targetMonth = termKey * 3;

        List<Lecture> filteredLecture = enrollments.stream()
                .map(Enrollment::getLecture)
                .filter(
                        lecture -> {
                            LocalDate startDate = lecture.getStartDate();
                            boolean yearMatches = startDate.getYear() == targetYear;

                            boolean monthMatches = startDate.getMonthValue() == targetMonth;

                            return yearMatches && monthMatches;
                        }
                )
                .toList();

        return filteredLecture.stream()
                .map(LectureDto::fromEntity)
                .toList();
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

            List<LectureSchedule> lectureScheduleList = this.lecScheduleRepository.findAllByLecture_Id(lecture.getId());
            List<LectureScheduleDto> lectureScheduleDtos = new ArrayList<>();
            for(LectureSchedule schedule : lectureScheduleList){
                LectureScheduleDto scheduleDto = new LectureScheduleDto();
                scheduleDto.setId(schedule.getId());
                scheduleDto.setLecture(schedule.getLecture().getId());
                scheduleDto.setDay(schedule.getDay());
                scheduleDto.setStartTime(schedule.getStartTime());
                scheduleDto.setEndTime(schedule.getEndTime());

                lectureScheduleDtos.add(scheduleDto);
            }

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
            lectureDto.setLectureSchedules(lectureScheduleDtos);

            lectureDtoList.add(lectureDto);
        }

        return lectureDtoList;
    }

    @Transactional
    public void lectureInprogress(List<Long> idList, Status status) {

        if (idList == null || idList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "강의는 최소 1개 이상 선택해야합니다.");
        }

        for (Long lectureId : idList) {
            Lecture lecture = this.lectureRepository.findById(lectureId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));
            lecture.setStatus(status);
            this.lectureRepository.save(lecture);

            List<CourseRegistration> courseRegistrationList = this.courseRegRepository.findAllByLecture_IdAndStatus(lectureId, Status.SUBMITTED);

            if(courseRegistrationList == null || courseRegistrationList.isEmpty()){
                throw new ResponseStatusException(HttpStatus.CONFLICT,"신청 인원이 없습니다.");
            }

//            int total = lecture.getTotalStudent();
//            int minRequired = (int) Math.ceil(total * 0.3); // 신청인원 일정 비율 이상일때 개강가능
//
//            if (minRequired > courseRegistrationList.size()) {
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "신청 인원이 부족합니다");
//            }

            for (CourseRegistration courseReg : courseRegistrationList) {
                User user = this.userRepository.findById(courseReg.getUser().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));



                Grade grade = new Grade();
                grade.setLecture(lecture);
                grade.setUser(user);

                Grade newGrade = this.gradeRepository.save(grade);

                Enrollment enrollment = new Enrollment();
                enrollment.setUser(user);
                enrollment.setGrade(newGrade);
                enrollment.setLecture(lecture);
                enrollment.setStatus(Status.INPROGRESS);
                enrollment.setCompletionDiv(lecture.getCompletionDiv());

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

    /// ///////////////
    public LectureDto findBylectureID(Long id) {
        Lecture lecture = this.lectureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));

        LectureDto lectureDto = new LectureDto();

        Long nowStudent = this.courseRegRepository.countByLecture_IdAndStatus(lecture.getId(), Status.SUBMITTED);
        List<LectureSchedule> lectureSchedules = this.lecScheduleRepository.findAllByLecture_Id(lecture.getId());
        List<LectureScheduleDto> lectureScheduleDtoList = new ArrayList<>();

        for(LectureSchedule schedule : lectureSchedules){
            LectureScheduleDto scheduleDto = new LectureScheduleDto();

            scheduleDto.setLecture(schedule.getLecture().getId());
            scheduleDto.setDay(schedule.getDay());
            scheduleDto.setStartTime(schedule.getStartTime());
            scheduleDto.setEndTime(schedule.getEndTime());

            lectureScheduleDtoList.add(scheduleDto);
        }

        lectureDto.setName(lecture.getName());
        lectureDto.setMajorName(lecture.getMajor().getName());
        lectureDto.setTotalStudent(lecture.getTotalStudent());
        lectureDto.setUserName(lecture.getUser().getName());
        lectureDto.setNowStudent(nowStudent);
        lectureDto.setLectureSchedules(lectureScheduleDtoList);
        lectureDto.setStartDate(lecture.getStartDate());
        lectureDto.setEndDate(lecture.getEndDate());

        gradingWeightsRepository.findByLectureId(id).ifPresent(gw ->
        {lectureDto.setGradingWeightsDto(new GradingWeightsDto(
                gw.getAttendanceScore(),
                gw.getAssignmentScore(),
                gw.getMidtermExam(),
                gw.getFinalExam()
        ));
        });

        return lectureDto;
    }

    public List<LectureScheduleDto> getSchedule(Long lectureId){
        return lecScheduleRepository.findByLecture_Id(lectureId).stream() .map(e -> LectureScheduleDto.fromEntity(e)).toList();
    }

    public LecSessionResponseDto selectSessions(Long id, LecSessionRequestDto requestDto) {
        if (requestDto.getDays() == null || requestDto.getDays().isEmpty()) {
            // 필요하면 여기서 400 던지거나 빈 리스트 반환
            return new LecSessionResponseDto(id, requestDto.getStart(), requestDto.getEnd(), requestDto.getDays(),
                    requestDto.getPeriodStart(), requestDto.getPeriodEnd(), 0, Collections.emptyList());
        }

        List<LocalDate> base = datesByDays(requestDto.getStart(),requestDto.getEnd(),requestDto.getDays());

        Set<LocalDate> set = new LinkedHashSet<>(base);

        List<LecSessionListDto> sessions = set.stream()
                .sorted()
                .map(d -> new LecSessionListDto(
                d,
                        d.getDayOfWeek(),
                        computeWeekNo(requestDto.getStart(),d,DayOfWeek.MONDAY),
                        requestDto.getPeriodStart(),
                        requestDto.getPeriodEnd(),
                        BellTimeRules.bellStart(requestDto.getPeriodStart()),        // 선택
                        BellTimeRules.bellEnd(requestDto.getPeriodEnd())
                ))
                .toList();

        LecSessionResponseDto responseDto = new LecSessionResponseDto();

        responseDto.setLectureId(id);
        responseDto.setStart(requestDto.getStart());
        responseDto.setEnd(requestDto.getEnd());
        responseDto.setDays(requestDto.getDays());
        responseDto.setPeriodStart(requestDto.getPeriodStart());
        responseDto.setPeriodEnd(requestDto.getPeriodEnd());
        responseDto.setSessionLists(sessions);
        return responseDto;
    }

    public static int computeWeekNo(LocalDate termStart, LocalDate date, DayOfWeek weekStartsOn){
        // 학기 시작일을 해당 주의 시작요일로 맞춰줌
        LocalDate startAncor = termStart.with(java.time.temporal.TemporalAdjusters.previousOrSame(weekStartsOn));

        // date도 본인이 속한 해당 주의 시작요일로 맞춤
        // -> 각각 해당 주의 첫날로 정렬되어 주 단위 차이를 셀 수 있음.
        LocalDate dateAnchor = date.with(java.time.temporal.TemporalAdjusters.previousOrSame(weekStartsOn));

        // 두 날짜 사이의 주 간격을 구함
        int weeks = (int) java.time.temporal.ChronoUnit.WEEKS.between(startAncor,dateAnchor);

        // 0base를 +1 함으로써 1base(1주차)로 시작함
        return Math.max(1,weeks + 1);
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

    public LectureBasicInfoDto getLectureBasicInfo(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .map(lecture -> new LectureBasicInfoDto(
                        lecture.getName(),
                        lecture.getUser().getId(),
                        lecture.getUser().getName()
                ))
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
    }

        @Transactional
        public void updateLecture (LectureDto
        lectureDto, List < LectureScheduleDto > lectureScheduleDtos, List < MultipartFile > files, PercentDto percent, List<AttachmentDto> existingDtos){

            if (lectureDto.getMajor() == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "소속 대학과 학과를 선택해주세요");
            }
            if (lectureDto.getStartDate() == null || lectureDto.getEndDate() == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "강의 날짜를 선택해주세요");
            }

            LocalDate start = lectureDto.getStartDate();
            LocalDate end = lectureDto.getEndDate();
            LocalDate today = LocalDate.now();

            if (!start.isAfter(today)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "오늘 이후의 강의만 등록할 수 있습니다.");
            }

            if (end.isBefore(start)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "종료일이 시작일보다 빠릅니다.");
            }

            LocalDate minEnd = start.plusMonths(2);
            if (end.isBefore(minEnd)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "강의 기간은 최소 2개월이어야 합니다. (가능한 최소 종료일: " + minEnd + ")"
                );
            }
            if (lectureScheduleDtos == null || lectureScheduleDtos.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "수업 일정을 하루 이상 선택해주세요.");
            }

            if (lectureDto.getUser() == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "담당 교수를 선택해주세요.");
            }
            if (lectureDto.getCredit() == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이수 학점은 1점 이상이여야 합니다.");
            }
            if (lectureDto.getName().isBlank() || lectureDto.getName() == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "강의명을 입력해주세요.");
            }
            if (lectureDto.getLevel() == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "학년을 선택해주세요.");
            }
            if (lectureDto.getTotalStudent() < 10) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수강인원은 10명 이상이여야 합니다.");
            }

            BigDecimal totalPercent = percent.getAssignment()
                    .add(percent.getAttendance())
                    .add(percent.getMidtermExam())
                    .add(percent.getFinalExam());
            BigDecimal overPercent = new BigDecimal("100.00");
            if (totalPercent.compareTo(overPercent) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비율은 100을 넘을 수 없습니다.");
            }

            for (LectureScheduleDto lectureScheduleDto : lectureScheduleDtos) {
                if (lectureScheduleDto.getDay() == null || lectureScheduleDto.getStartTime() == null || lectureScheduleDto.getEndTime() == null) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "수업 요일과 교시를 모두 선택해주세요.");
                }
            }

            Lecture lecture = this.lectureRepository.findById(lectureDto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 강의 입니다."));


            List<LecRegAttach> lecRegAttachList = this.lecRegAttachRepository.findByLecture_id(lecture.getId());


            Set<Long> keepIds = new HashSet<>();
            if (existingDtos != null) {
                for (AttachmentDto dto : existingDtos) {
                    if (dto != null && dto.getId() != null) {
                        keepIds.add(dto.getId());
                    }
                }
            }
            Path base = Path.of(uploadDir).toAbsolutePath().normalize();
            for(LecRegAttach lecRegAttach : lecRegAttachList){
                Long attId = lecRegAttach.getAttachment().getId();

                if(!keepIds.contains(attId)){

                    this.lecRegAttachRepository.deleteByLectureIdAndAttachmentId(lecture.getId(), attId);
                    Attachment attachment = this.attachmentRepository.findById(attId)
                            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"존재하지 않는 파일입니다."));

                    Path file = base.resolve(attachment.getStoredKey()).normalize();
                    if (!file.startsWith(base)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "허용되지 않은 경로 요청");
                    }

                    try {
                        Files.deleteIfExists(file);
                    } catch (IOException e) {
                        throw new UncheckedIOException("파일 삭제 실패", e);
                    }

                    this.attachmentRepository.deleteById(attId);


                }
            }


            this.lecScheduleRepository.deleteAllByLecture_Id(lecture.getId());

            for (LectureScheduleDto scheduleDto : lectureScheduleDtos) {
                LectureSchedule lectureSchedule = new LectureSchedule();
                lectureSchedule.setLecture(lecture);
                lectureSchedule.setDay(scheduleDto.getDay());
                lectureSchedule.setStartTime(scheduleDto.getStartTime());
                lectureSchedule.setEndTime(scheduleDto.getEndTime());

                this.lecScheduleRepository.save(lectureSchedule);
            }

            Major major = this.majorRepository.findById(lectureDto.getMajor())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 학과 입니다."));
            User user = this.userRepository.findById(lectureDto.getUser())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 교수 입니다."));

            lecture.setDescription((lectureDto.getDescription()));
            lecture.setMajor(major);
            lecture.setStatus(lectureDto.getStatus());
            lecture.setUser(user);
            lecture.setLevel(lectureDto.getLevel());
            lecture.setCompletionDiv(lectureDto.getCompletionDiv());
            lecture.setTotalStudent(lectureDto.getTotalStudent());
            lecture.setStartDate(lectureDto.getStartDate());
            lecture.setEndDate(lectureDto.getEndDate());
            lecture.setName(lectureDto.getName());
            lecture.setCredit(lectureDto.getCredit());

            Lecture saveLecture = this.lectureRepository.save(lecture);

        GradingWeights gradingWeights = this.gradingWeightsRepository.findByLecture_Id(saveLecture.getId());

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

    public List<LectureDto> applyLecturListEnd(Long id) {
        List<CourseRegistration> courseRegistrationList = this.courseRegRepository.findAllByUser_Id(id);
        List<LectureDto> lectureDtoList = new ArrayList<>();
        for(CourseRegistration courseReg : courseRegistrationList){
            Lecture lecture = this.lectureRepository.findById(courseReg.getLecture().getId())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"존재 하지 않는 강의입니다."));

            Long nowStudent = this.courseRegRepository.countByLecture_IdAndStatus(lecture.getId(), Status.SUBMITTED);

            List<LectureSchedule> lectureScheduleList = this.lecScheduleRepository.findAllByLecture_Id(lecture.getId());
            List<LectureScheduleDto> lectureScheduleDtos = new ArrayList<>();
            for(LectureSchedule schedule : lectureScheduleList){
                LectureScheduleDto scheduleDto = new LectureScheduleDto();
                scheduleDto.setId(schedule.getId());
                scheduleDto.setLecture(schedule.getLecture().getId());
                scheduleDto.setDay(schedule.getDay());
                scheduleDto.setStartTime(schedule.getStartTime());
                scheduleDto.setEndTime(schedule.getEndTime());

                lectureScheduleDtos.add(scheduleDto);
            }

            LectureDto lectureDto = new LectureDto();

            lectureDto.setId(lecture.getId());
            lectureDto.setLectureSchedules(lectureScheduleDtos);
            lectureDto.setUser(lecture.getUser().getId());
            lectureDto.setLevel(lecture.getLevel());
            lectureDto.setStatus(courseReg.getStatus());
            lectureDto.setLecStatus(lecture.getStatus());
            lectureDto.setEndDate(lecture.getEndDate());
            lectureDto.setStartDate(lecture.getStartDate());
            lectureDto.setNowStudent(nowStudent);
            lectureDto.setTotalStudent(lecture.getTotalStudent());
            lectureDto.setMajor(lecture.getMajor().getId());
            lectureDto.setCredit(lecture.getCredit());
            lectureDto.setCompletionDiv(lecture.getCompletionDiv());
            lectureDto.setName(lecture.getName());
            lectureDto.setMajorName(lecture.getMajor().getName());
            lectureDto.setUserName(lecture.getUser().getName());

            lectureDtoList.add(lectureDto);
        }

        return lectureDtoList;
    }
}






