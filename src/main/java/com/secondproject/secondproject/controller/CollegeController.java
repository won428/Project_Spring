package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.Enum.CollegePaging;
import com.secondproject.secondproject.dto.CollegeInsertDto;
import com.secondproject.secondproject.dto.CollegeResponseDto;
import com.secondproject.secondproject.dto.CollegeSearchDto;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.service.CollegeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/college")
@RequiredArgsConstructor
public class CollegeController {
    private final CollegeService collegeService;

    @PostMapping("/insert") // 단과대학 등록
    public ResponseEntity<CollegeResponseDto> insert(@RequestBody @Valid CollegeInsertDto collegeInsertDto){
        CollegeResponseDto collegeResponseDto = collegeService.insert(collegeInsertDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/college/{id}") // HTTP 헤더에 넣을
                .buildAndExpand(collegeResponseDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(collegeResponseDto);
    }

    @GetMapping("/list")
    public List<CollegeResponseDto> list(){
        return collegeService.getList();
    }

    @GetMapping("/list")
    public ResponseEntity<Page<College>>collageLists(
            @RequestParam(defaultValue = "") CollegePaging collegePaging,
            @RequestParam(defaultValue = "") String searchKeyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size
            ){
        CollegeSearchDto collegeSearchDto = new CollegeSearchDto(collegePaging, searchKeyword);

        Page<College> colleges = collegeService.listCollege(collegeSearchDto, page, size);
        return ResponseEntity.ok(colleges);
    }

    @DeleteMapping("/delete/{college_id}")
    public ResponseEntity<String> deleteCollege(@PathVariable("college_id") Long college_id) {
        if (!collegeService.existsById(college_id)) {
            return ResponseEntity.notFound().build();
        }
        collegeService.deleteById(college_id);

        String msg = "해당 단과대학이 삭제되었습니다.";

        return ResponseEntity.ok(msg);
    }

    // College 정보 업데이트 시 폼에 기존 정보 가져오는 메소드
    @GetMapping("/{id}")
    public ResponseEntity<CollegeResponseDto> findOne(@PathVariable Long id) {
        return collegeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 수정한 실제 정보로 UPDATE하는 메소드
    @PutMapping("/{id}")
    public ResponseEntity<CollegeResponseDto> update(@PathVariable Long id,
                                                     @RequestBody @Valid CollegeInsertDto req) {
        CollegeResponseDto updated = collegeService.update(id, req);
        return ResponseEntity.ok(updated);
    }

    }
