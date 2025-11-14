package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.secondproject.secondproject.entity.Assignment;
import com.secondproject.secondproject.entity.Lecture;
import com.secondproject.secondproject.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentDto {
    private Long id; // 과제ID(PK)

    private String title; // 공지 제목

    private String content; // 공지 본문

    private Long LectureId;

    private boolean isEnabled = true;
    // 제출창구(제출 마감시 비활성화(0), 제출 진행중일시 활성화(1))

    private LocalDate openAt; // 제출 시작일

    private LocalDate dueAt; // 제출 마감일

//    private int maxFiles; // 업로드 가능한 파일 최대 갯수
//    이미 Application 파일에 되어 있는데 굳이 사용해야하나 싶습니다.
//    private int maxSizeMb = 50; // 파일 최대 용량(mb), 기본값 50

    private String username; // 공지한사람(=교수이름)

    private LocalDateTime createAt; // 공지 등록일

    private LocalDateTime updateAt; // 공지 수정일


    public static AssignmentDto fromEntity(Assignment assignment) {
        AssignmentDto dto = new AssignmentDto();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setContent(assignment.getContent());
        dto.setOpenAt(assignment.getOpenAt());
        dto.setDueAt(assignment.getDueAt());
        dto.setLectureId(assignment.getLecture().getId());
        if (assignment.getUser() != null) {
            dto.setUsername(assignment.getUser().getName());
        }
        dto.setCreateAt(assignment.getCreateAt());
        dto.setUpdateAt(assignment.getUpdateAt());
        return dto;
    }


//    public static AssignmentDto fromEntity(Assignment assignment) {
//
//
//    }

}
