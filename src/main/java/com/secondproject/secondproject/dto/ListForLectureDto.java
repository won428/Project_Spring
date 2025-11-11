package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListForLectureDto {
    private Long id;
    private String name;
    private String office;
    private Long collegeId;
    private String collegeName;
    private List<UserDto> userList;


}
