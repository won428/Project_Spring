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
}
