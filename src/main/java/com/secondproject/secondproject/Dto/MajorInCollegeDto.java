package com.secondproject.secondproject.Dto;

import com.secondproject.secondproject.Entity.College;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MajorInCollegeDto {


    private Long id; // 학과 코드
    private String m_name; // 학과 이름
    private Long college_id; // 소속 단과대학 코드

    public MajorInCollegeDto(Long id, String m_name, Long college_id) {
        this.id = id;
        this.m_name = m_name;
        this.college_id = college_id;
    }

    public MajorInCollegeDto(Long id) {

    }
}
