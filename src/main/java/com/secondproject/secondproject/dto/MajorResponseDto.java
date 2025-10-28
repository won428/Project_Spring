package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.College;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MajorResponseDto {
    private Long id;
    private String name;
    private String office;
    private College college;
    private Long collegeId;

    public MajorResponseDto(Long id, String name, String office, College college) {
        this.id = id;
        this.name = name;
        this.office = office;
        this.college = college;
    }

    public MajorResponseDto(Long id, String name, String office, Long collegeId) {
        this.id = id;
        this.name = name;
        this.office = office;
        this.collegeId = collegeId;
    }
}
