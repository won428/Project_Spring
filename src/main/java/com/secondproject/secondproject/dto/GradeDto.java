package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradeDto {
    User user;
    Lecture lecture;


}
