package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.SubmitStatus;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SubmitAsgmtnsertDto {
    private String email;

    private String username;

    private String title;

    private String content;

    private Long assignmentId;

    private SubmitStatus submitStatus; // 제출 상태

    private LocalDateTime submitAt; // 과제 제출일

    private LocalDateTime updateAt; // 과제 수정일


//    public static AssignmentDto fromEntity(Assignment assignment) {
//
//
//    }

}
