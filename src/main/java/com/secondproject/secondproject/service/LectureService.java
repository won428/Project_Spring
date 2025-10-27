package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.LectureRepository;
import com.secondproject.secondproject.repository.MajorRepository;
import com.secondproject.secondproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;

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
            lectureDto.setName(lecture.getName());
            lectureDto.setCredit(lecture.getCredit());
            lectureDto.setStartDate(lecture.getStartDate());
            lectureDto.setEndDate(lecture.getEndDate());
            lectureDto.setMajorName(lecture.getMajor().getName());
            lectureDto.setUserName(lecture.getUser().getName());
            lectureDto.setStatus(lecture.getStatus());

            lectureDtoList.add(lectureDto);

        }

        return lectureDtoList;
    }
}
