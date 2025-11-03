package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.AppealType;
import com.secondproject.secondproject.Enum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppealDto {
    private Long id;                // 이의제기 코드
    private String sendingId;       // 보내는 유저 ID
    private String receiverId;      // 받는 유저 ID
    private Long enrollmentId;      // Enrollment FK (ID만 담음)
    private String title;           // 이의제기 제목
    private String content;         // 이의제기 내용
    private LocalDate appealDate;   // 신청 날짜
    private Status status;          // 처리 상태
    private AppealType appealType;  // 이의제기 타입
}
