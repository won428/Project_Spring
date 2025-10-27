package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public void insertByAdmin(LectureDto lectureDto) {
        Lecture lecture = new Lecture();
        Optional<User> optUser = this.userRepository.findById(lectureDto.getUser());
        User user = optUser.orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,  "사용자 없음"));
        Optional<Major> optMajor = this.majorRepository.findById(lectureDto.getMajor());
        Major major = optMajor.orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,  "학과 없음"));

        lecture.setName(lectureDto.getName());
        lecture.setUser(user);
        lecture.setCredit(lectureDto.getCredit());
        lecture.setStartDate(lectureDto.getStartDate());
        lecture.setEndDate(lectureDto.getEndDate());
        lecture.setDescription(lectureDto.getDescription());
        lecture.setMajor(major);
        lecture.setTotalStudent(lectureDto.getTotalStudent());
        lecture.setStatus(lectureDto.getStatus());

        this.lectureRepository.save(lecture);
    }

    public List<LectureDto> findAll() {
        List<Lecture> lectureList = this.lectureRepository.findAll();
        List<LectureDto> lectureDtoList = new ArrayList<>();
        for (Lecture lecture : lectureList){
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

            lectureDtoList.add(lectureDto);

        }

        return lectureDtoList;
    }

    public void updateStatus(Long id, Status status) {
        Optional<Lecture> lectureOpt= this.lectureRepository.findById(id);
        Lecture lecture = lectureOpt
                .orElseThrow(()->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, id + " 해당 강의가 존재하지 않습니다."));
        lecture.setStatus(status);
        this.lectureRepository.save(lecture);
    }

    public void applyLecture(List<Long> idList, Long userId) {

        if(idList == null || idList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "lectureIds는 최소 1개 이상이어야 합니다.");
        }else if(userId == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음");
        }

        User user = this.userRepository.findById(userId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"없는 사용자"));

        for(Long lecId : idList){
            Lecture lecture = this.lectureRepository.findById(lecId)
                    .orElseThrow(()->  new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않습니다."));
            Long nowStudent = this.enrollmentRepository.countByLecture_Id(lecId);
            int totalStudent = lecture.getTotalStudent();

            if(nowStudent >= totalStudent){
                throw new ResponseStatusException(HttpStatus.CONFLICT, lecture.getName() + "강의는 정원 초과입니다");
            }

            boolean alreadyApplied = enrollmentRepository.existsByUser_IdAndLecture_Id(userId, lecId);
            if (alreadyApplied) {
                // 이 강의는 건너뜀 (또는 예외 던져도 됨)
                continue;
            }

            Enrollment enrollment = new Enrollment();
            CourseRegistration courseRegistration = new CourseRegistration();

            // 수강 신청 테이블 생성
            courseRegistration.setUser(user);
            courseRegistration.setLecture(lecture);
            courseRegistration.setDate(LocalDateTime.now());
            courseRegistration.setStatus(Status.PENDING);
            this.courseRegRepository.save(courseRegistration);

            // 성적 테이블 생성
            Grade grade = new Grade();
            grade.setUser(user);
            grade.setLecture(lecture);
            Grade newGrade = this.gradeRepository.save(grade);

            // 수강 테이블 생성
            enrollment.setUser(user);
            enrollment.setLecture(lecture);
            enrollment.setStatus(Status.PENDING);
            enrollment.setGrade(newGrade);
            enrollment.setCompletionDiv("전공 필수");
            this.enrollmentRepository.save(enrollment);

        }
    }
}
