package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.MajorPaging;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MajorSearchDto {
    private MajorPaging majorPaging; // 카테고리 선택 콤보박스
    private String searchKeyword; // 검색할 키워드
//    private int pageSize; // 한페이지에 들어갈 데이터 갯수
//    private int pageNumber; // 현재 페이지
}
