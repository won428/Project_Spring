package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secondproject.secondproject.Enum.AppealType;
import com.secondproject.secondproject.Enum.AttendStudent;
import com.secondproject.secondproject.Enum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppealManageDto {
    private Long appealId;
    private Long sendingId;       // 학생 ID
    private Long receiverId;      // 교수 ID
    private Long lectureId;
    private String title;         // 이의제기 제목
    private String studentName;
    private Long studentCode;
    private String content;       // 상세 내용
    private LocalDate appealDate;
    private Status status;        // PENDING, APPROVED, REJECTED
    private AppealType appealType; // 이의제기 유형

    @JsonProperty("aScore")
    private BigDecimal aScore;
    @JsonProperty("asScore")
    private BigDecimal asScore;
    @JsonProperty("tScore")
    private BigDecimal tScore;
    @JsonProperty("ftScore")
    private BigDecimal ftScore;
    @JsonProperty("totalScore")
    private BigDecimal totalScore;
    @JsonProperty("lectureGrade")
    private BigDecimal lectureGrade;

    // 현재 출결 상태
    private LocalDate attendanceDate;   // 실제 강의일 기준 출결일
    private AttendStudent attendStudent; // 현재 출결 상태
}

