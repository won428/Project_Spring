package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.Notice;
import com.secondproject.secondproject.entity.OnlineLecture;
import com.secondproject.secondproject.entity.UserLectureProgress;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OnlineLectureResDto {
    private Long id; // 온라인 강의 코드

    private Long userId;

    private String username;

    private String title; // 강의 이름

    private boolean disable = false; // 강의 활성화 유무, default false로 잡아놨는데 수정해야하면 수정해주세요

    private LocalDate startDate; // 강의 등록일

    private LocalDate endDate;

    private String path;

    private int lastViewedSec;

    private int totalWatchedSec;

    private int vidLength;

    public static OnlineLectureResDto fromEntity(
            OnlineLecture onlineLecture,
            Attachment attachment,
            UserLectureProgress progress
    ) {
        OnlineLectureResDto resDto = new OnlineLectureResDto();
        resDto.setId(onlineLecture.getId());
        resDto.setUserId(onlineLecture.getUser().getId());
        resDto.setUsername(onlineLecture.getUname());
        resDto.setTitle(onlineLecture.getTitle());
        resDto.setDisable(onlineLecture.isDisable());
        resDto.setStartDate(onlineLecture.getCreatedAt());
        resDto.setEndDate(onlineLecture.getEndDate().toLocalDate());
        resDto.setLastViewedSec(progress.getLastViewedSec());
        resDto.setTotalWatchedSec(progress.getTotalWatchedSec());
        resDto.setPath(attachment.getStoredKey());
        resDto.setVidLength(onlineLecture.getVidLength());
        return resDto;
    }
}
