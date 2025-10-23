package com.secondproject.secondproject.mapper;

import com.secondproject.secondproject.dto.CollegeCreateReq;
import com.secondproject.secondproject.dto.ColResponseDto;
import com.secondproject.secondproject.entity.College;
import org.springframework.stereotype.Component;

@Component
public class CollegeMapper {

    // 새 데이터 삽입, 생성Dto -> entity로 변환
    public College InsertToEntity(CollegeCreateReq req){
        College college = new College();
        college.setC_type(normalizeType(req.getC_type()));
        college.setC_office(normalizeOffice(req.getC_office()));
        return college;
    }

    // 수정 Dto -> entity
//    public void updateEntity()

    // 응답Dto ->  entity로 변환
    public ColResponseDto toResponse(College college){
        return new ColResponseDto(college.getId(), college.getC_type(), college.getC_office());
    }


    // --- 내부 정규화 규칙 모음 ---
    private String normalizeType(String v) {
        return v == null ? null : v.trim().replaceAll("\\s{2,}", " ");
    } // 계열 입력 시 계열이 null일 경우 null을 반환하고, null이 아닐 경우 문자열 사이 공백 제거하여 저장

    private String normalizeOffice(String v) {
        if (v == null || v.isBlank()) return null; // 빈 값은 null 저장
        return v.trim();
    } // 전화번호가 null이거나 비었을 경우 그대로 저장
}
