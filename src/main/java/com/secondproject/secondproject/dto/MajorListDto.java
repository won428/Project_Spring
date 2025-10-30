package com.secondproject.secondproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MajorListDto {
    private Long id;
    private String name;
    private String office;
    private Long collegeId;
    private String collegeName;


}
