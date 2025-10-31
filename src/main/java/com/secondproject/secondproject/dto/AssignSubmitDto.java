package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.SubmitStatus;
import com.secondproject.secondproject.entity.Assignment;
import com.secondproject.secondproject.entity.SubmitAsgmt;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AssignSubmitDto {
    private Long id; // 과제ID(PK)

    private String title; // 공지 제목

    private String content; // 공지 본문

    private boolean isEnabled = true;
    // 제출창구(제출 마감시 비활성화(0), 제출 진행중일시 활성화(1))

    private String username; // 공지한사람(=교수이름)

    private LocalDateTime submitAt; // 공지 등록일

    private LocalDateTime updateAt; // 공지 수정일

    private SubmitStatus submitStatus;

//    public static AssignSubmitDto fromEntity(SubmitAsgmt submitAsgmt) {
//        AssignSubmitDto dto = new AssignSubmitDto();
//        dto.setId(submitAsgmt.getId());
//        dto.setTitle(submitAsgmt.getTitle());
//        dto.setContent(submitAsgmt.getContent());
//        dto.setSubmitStatus(SubmitStatus.COMPLETED);
//        if (submitAsgmt.getUser() != null) {
//            dto.setUsername(submitAsgmt.getUser().getName());
//        }
//        dto.setSubmitAt(submitAsgmt.getSubmitAt());
//        dto.setUpdateAt(submitAsgmt.getUpdateAt());
//
//        return dto;
//    }


//    public static AssignmentDto fromEntity(Assignment assignment) {
//
//
//    }

}
