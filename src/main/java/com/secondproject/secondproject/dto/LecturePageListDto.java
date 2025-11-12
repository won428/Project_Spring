package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.CompletionDiv;
import com.secondproject.secondproject.Enum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecturePageListDto {
    int pageNumber;
    int pageSize;
    CompletionDiv searchCompletionDiv;
    Long searchMajor;
    Integer searchCredit;
    String searchStartDate;
    String searchMode;
    String searchKeyword;
    DayOfWeek searchSchedule;
    String searchYear;
    Integer searchLevel;
    Long searchUser;
    Status searchStatus;
}
