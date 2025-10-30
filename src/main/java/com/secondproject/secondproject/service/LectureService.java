package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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


    public void insertByAdmin(LectureDto lectureDto) {
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

        this.lectureRepository.save(lecture);
    }

    public List<LectureDto> findAll() {
        List<Lecture> lectureList = this.lectureRepository.findAll();
        List<LectureDto> lectureDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            Long nowStudent = this.courseRegRepository.countByLecture_Id(lecture.getId());

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
            Long nowStudent = this.courseRegRepository.countByLecture_Id(lecId);
            int totalStudent = lecture.getTotalStudent();

            if (nowStudent >= totalStudent) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, lecture.getName() + " 강의는 정원 초과입니다.");

            }


            boolean alreadyApplied = courseRegRepository.existsByUser_IdAndLecture_Id(userId, lecId);
                if (alreadyApplied) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, lecture.getName() + " 강의는 이미 신청한 강의입니다.");
                }




//            boolean alreadyEnrollment = enrollmentRepository.existsByUser_IdAndLecture_Id(userId, lecture.getId());
//            if(alreadyEnrollment){
//                throw new ResponseStatusException(HttpStatus.CONFLICT, lecture.getName() + " 강의는 이미 수강한 강의입니다.");
//            }

            CourseRegistration courseRegistration = new CourseRegistration();

            // 수강 신청 테이블 생성
            courseRegistration.setUser(user);
            courseRegistration.setLecture(lecture);
            courseRegistration.setDate(LocalDateTime.now());
            courseRegistration.setStatus(Status.PENDING);
            this.courseRegRepository.save(courseRegistration);

        }
    }

    public List<LectureDto> myLectureList(Long userId) {
        List<CourseRegistration> courseRegistrations = this.courseRegRepository.findByUser_Id(userId);
        List<LectureDto> myLectureList = new ArrayList<>();

        for (CourseRegistration courseReg : courseRegistrations) {
            Lecture lecture = this.lectureRepository.findById(courseReg.getLecture().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 강의"));
            LectureDto lectureDto = new LectureDto();

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

            myLectureList.add(lectureDto);

        }


        return myLectureList;
    }

    public LectureDto findByID(Long id) {
        Lecture lecture = this.lectureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));
        LectureDto lectureDto = new LectureDto();
        Long nowStudent = this.courseRegRepository.countByLecture_Id(id);

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

        for(Lecture lecture : lectureList){
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

        for(Long lectureId : idList){
            Lecture lecture = this.lectureRepository.findById(lectureId)
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 강의입니다."));

            List<CourseRegistration> courseRegistrationList = this.courseRegRepository.findAllByLecture_Id(lectureId);


            int total = lecture.getTotalStudent();
            int minRequired = (int) Math.ceil(total * 0.3);


            if(minRequired > courseRegistrationList.size()){
                throw new ResponseStatusException(HttpStatus.CONFLICT,"신청 인원이 부족합니다");
            }

                for (CourseRegistration courseReg : courseRegistrationList){
                    User user = this.userRepository.findById(courseReg.getUser().getId())
                            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

                    lecture.setStatus(Status.INPROGRESS);

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
}
