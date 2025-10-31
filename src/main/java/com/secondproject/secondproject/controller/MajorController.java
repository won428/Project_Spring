package com.secondproject.secondproject.controller;


import com.secondproject.secondproject.Enum.MajorPaging;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/major")
@RequiredArgsConstructor
public class MajorController {
    private final MajorService majorService;
    private final CollegeService collegeService;
    private final LectureService lectureService;

    @GetMapping("/listAll")
    public List<MajorListDto> selectAll(){
        List<MajorListDto> majorListDtos = majorService.selectAll();
        return majorListDtos;
    }

    // 단과대학에 속한 학과목록 조회
    @GetMapping("/list") // 매핑주소 수정 필요
    public List<MajorInCollegeDto> majorListByCollege(@RequestParam("college_id") Long collegeId){
        List<MajorInCollegeDto> majorList = majorService.getMajorListByCollege(collegeId);
        return majorList;
    }

    // 전체 학과조회
    @GetMapping("/all/list")
    public List<MajorListDto> majorList(){

        List<MajorListDto> listDtos = this.majorService.majorList();

        return listDtos;
    }

    // 선택한 특정 학과 가져오기(업데이트용)
    @GetMapping("/selectOne/{id}")
    public ResponseEntity<MajorListDto> selectMajorId(@PathVariable("id") Long id){
        return  majorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 수정한 정보로 업데이트하기
    @PutMapping("/update/{id}")
    public ResponseEntity<MajorResponseDto> updateMajor(@PathVariable Long id,
                                                        @RequestBody @Valid MajorInsertDto majorInsertDto){
        MajorResponseDto majorResponseDto = majorService.updateMajor(id,majorInsertDto);
        return ResponseEntity.ok(majorResponseDto);
    }

    // 학과목록 조회 + 검색기능 + 페이징
    @GetMapping("/findAllMajor")
    public Page<MajorListDto> findAllMajors(Pageable pageable,
                                            @RequestParam(defaultValue = "ALL") MajorPaging searchType,
                                            @RequestParam(defaultValue = "")String searchKeyword){
        return majorService.findAllMajors(pageable, searchType, searchKeyword);
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

    // 학과 삭제
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

