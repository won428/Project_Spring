package com.secondproject.secondproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MajorSearchDto {
    private Long majorId;
    private String majorName;
    private Long collegeId;
    private String collegeName;
}
