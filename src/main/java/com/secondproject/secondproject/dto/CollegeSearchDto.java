package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.CollegePaging;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollegeSearchDto {
    private CollegePaging collegePaging;
    private String searchKeyword;
    private Integer page = 0;         // 선택
    private Integer size = 20;
}
