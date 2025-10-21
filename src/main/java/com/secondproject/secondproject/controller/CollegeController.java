package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.CollegeCreateReq;
import com.secondproject.secondproject.dto.ColResponseDto;
import com.secondproject.secondproject.dto.CollegesList;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.service.CollegeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/college")
public class CollegeController {
    private  final CollegeService collegeService;

    @PostMapping("/insert") // 단과대학 등록
    public ResponseEntity<ColResponseDto> insert(@RequestBody @Valid CollegeCreateReq collegeCreateReq){
        ColResponseDto colResponseDto = collegeService.insert(collegeCreateReq);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/college/{id}") // HTTP 헤더에 넣을
                .buildAndExpand(colResponseDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(colResponseDto);
    }

    @GetMapping("/list")
    public List<CollegesList> list(){
        return collegeService.getList();
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

    @GetMapping("/{id}")
    public ResponseEntity<ColResponseDto> findOne(@PathVariable Long id) {
        return collegeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColResponseDto> update(@PathVariable Long id,
                                                 @RequestBody @Valid CollegeCreateReq req) {
        ColResponseDto updated = collegeService.update(id, req);
        return ResponseEntity.ok(updated);
    }

    }
