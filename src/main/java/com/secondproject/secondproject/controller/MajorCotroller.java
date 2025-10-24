package com.secondproject.secondproject.controller;

import com.secondproject.secondproject.dto.MajorInsertDto;
import com.secondproject.secondproject.dto.MajorResponseDto;
import com.secondproject.secondproject.service.CollegeService;
import com.secondproject.secondproject.service.MajorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/major")
@RequiredArgsConstructor
public class MajorCotroller {
    private final CollegeService collegeService;
    private final MajorService majorService;

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



}
