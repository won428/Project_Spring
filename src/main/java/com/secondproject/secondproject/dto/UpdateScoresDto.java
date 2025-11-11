package com.secondproject.secondproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateScoresDto {
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
    private Long sendingId;   // 학생 id
    private Long receiverId;  // 교수 id
    private Long lectureId;   // 강의 id
}
