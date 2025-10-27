package com.secondproject.secondproject.controller;


import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.service.CollegeService;
import com.secondproject.secondproject.service.LectureService;
import com.secondproject.secondproject.service.MajorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/major")
@RequiredArgsConstructor
public class MajorController {
    private final MajorService majorService;
    private final CollegeService collegeService;
    private final LectureService lectureService;

    // 단과대학에 속한 학과목록 조회
    @GetMapping("/list") // 매핑주소 수정 필요
    public List<MajorInCollegeDto> majorListByCollege(@RequestParam("college_id") Long collegeId){
        List<MajorInCollegeDto> majorList = majorService.getMajorListByCollege(collegeId);
        return majorList;
    }


    // 페이징 시 여기에 Pageable 넣기
    @GetMapping("/findAllMajor")
    public Page<MajorListDto> findAllMajors(@ModelAttribute MajorSearchDto majorSearchDto, Pageable pageable){
        return majorService.findAllMajors(majorSearchDto, pageable);
    }

    // 학과 등록
    @PostMapping("/insert")
    public ResponseEntity<MajorResponseDto> insert(@RequestBody @Valid MajorInsertDto majorInsertDto){
        MajorResponseDto responseDto= majorService.insertMajor(majorInsertDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/major/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(null);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        if(!majorService.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        majorService.deleteMajor(id);
        String msg = "해당 과목이 삭제되었습니다.";
        return ResponseEntity.ok(msg);
    }
}

