package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.SubmitStatus;
import com.secondproject.secondproject.entity.Assignment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SubmitAsgmtDto {
    private Long id; // 과제ID(PK)

    private String username;

    private String title; // 공지 제목

    private String content; // 공지 본문

    private Long assignmentId;//과제 Id

    private SubmitStatus submitStatus = SubmitStatus.PENDING; // 제출 시작일

    private LocalDateTime submitAt; // 과제 제출일

    private LocalDateTime updateAt; // 과제 수정일


//    public static SubmitAsgmtDto fromEntity(Assignment assignment) {
//        SubmitAsgmtDto dto = new SubmitAsgmtDto();
//        dto.setId(assignment.getId());
//        dto.setTitle(assignment.getTitle());
//        dto.setContent(assignment.getContent());
//        dto.setOpenAt(assignment.getOpenAt());
//        dto.setDueAt(assignment.getDueAt());
//
//        if (assignment.getUser() != null) {
//            dto.setUsername(assignment.getUser().getName());
//        }
//        dto.setCreateAt(assignment.getCreateAt());
//        dto.setUpdateAt(assignment.getUpdateAt());
//        return dto;
//    }


//    public static AssignmentDto fromEntity(Assignment assignment) {
//
//
//    }

}
