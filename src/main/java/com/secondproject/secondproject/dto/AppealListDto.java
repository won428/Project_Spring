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
public class AppealListDto {
    private Long appealId;
    private String lectureName;
    private String title;
    private String content;
    private LocalDate appealDate;
    private Status status;
    private AppealType appealType;
}
