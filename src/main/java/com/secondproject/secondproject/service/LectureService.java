package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.LectureBasicInfoDto;
import com.secondproject.secondproject.dto.AttachmentDto;
import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.dto.LectureScheduleDto;
import com.secondproject.secondproject.dto.PercentDto;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.LecRegAttach;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if(status.equals(Status.INPROGRESS)){
            List<CourseRegistration> courseRegistrationList = this.courseRegRepository.findAllByLecture_IdAndStatus(id, Status.SUBMITTED);
            if(courseRegistrationList == null || courseRegistrationList.isEmpty()){
                throw new ResponseStatusException(HttpStatus.CONFLICT,"신청 인원이 없습니다.");
            }

            lecture.setStatus(status);
            this.lectureRepository.save(lecture);
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

                this.enrollmentRepository.save(enrollment);
            }
        }else if(status.equals(Status.COMPLETED)){
            List<Grade> gradeList = this.gradeRepository.findAllByLecture_Id(id);
            if(gradeList.isEmpty() || gradeList == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"점수 정보가 없는 강의는 종강 할 수 없습니다.");
            }
            for(Grade grade : gradeList){
                if(grade.getLectureGrade() == null || grade.getAScore() == null || grade.getAsScore() == null || grade.getFtScore() == null || grade.getTScore() == null){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"아직 점수가 전부 입력되지 않은 강의는 종강 할 수 없습니다.");
                }
            }
            lecture.setStatus(status);
            this.lectureRepository.save(lecture);
        }else {
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
        Lecture lecture = this.lectureRepository.findById(id) // 해당 강의 객체
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));

        LectureDto lectureDto = new LectureDto(); // 스프링으로 보낼 강의 dto객체
        Long nowStudent = this.courseRegRepository.countByLecture_IdAndStatus(lecture.getId(), Status.SUBMITTED); // 해당 강의 현재 수강신청인원

        List<LectureSchedule> lectureSchedules = this.lecScheduleRepository.findAllByLecture_Id(lecture.getId()); // 해당 강의 시간표
        List<LectureScheduleDto> lectureScheduleDtoList = new ArrayList<>(); // 해당 강의 시간표 dto 리스트

        List<LecRegAttach> lecRegAttachList = this.lecRegAttachRepository.findByLecture_id(lecture.getId()); // 해당 강의가 가지고 있는 첨부파일 리스트
        List<AttachmentDto> attachmentDtoList = new ArrayList<>(); // 해당강의 첨부파일 dto 리스트

        // 첨부파일이 없을 수도 있으므로 if문으로 작성
        if(lecRegAttachList != null || !lecRegAttachList.isEmpty()){
            for(LecRegAttach lecRegAttach : lecRegAttachList){
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
        lectureDto.setDescription(lecture.getDescription());
        lectureDto.setAttachmentDtos(attachmentDtoList);

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

    public LectureBasicInfoDto getLectureBasicInfo(Long lectureId) {
        return lectureRepository.findById(lectureId)
                .map(lecture -> new LectureBasicInfoDto(
                        lecture.getName(),
                        lecture.getUser().getId(),
                        lecture.getUser().getName()
                ))
                .orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));
    }
}
