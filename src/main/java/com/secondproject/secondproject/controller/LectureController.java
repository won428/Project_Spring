package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.LectureDto;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.LectureRepository;
import com.secondproject.secondproject.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/Lecture")
@RequiredArgsConstructor
public class LectureController {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;


    @GetMapping("/List")
    @Transactional
    public ResponseEntity<List<LectureDto>> GiveLectureList(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> (new UsernameNotFoundException("존재하지 않는 사용자 입니다.")));
        List<LectureDto> lectureDto = new ArrayList<>();
        List<Lecture> lectures = lectureRepository.findByUser(user);
        for (Lecture lecture : lectures) {
            lectureDto.add(LectureDto.fromEntity(lecture));
        }
        return ResponseEntity.ok(lectureDto);
    }

}
