package com.secondproject.secondproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProRegDto {
    Long id;
    String name;
    Long college;
    String collegeName;
    Long major;
    String majorName;
}
