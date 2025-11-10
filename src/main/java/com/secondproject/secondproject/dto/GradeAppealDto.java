package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.Enum.AppealType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GradeAppealDto {
    Long sendingId;
    Long receiverId;
    AppealType appealType;
    String content;
    String title;
    Long lectureId;
    List<AttachmentDto> attachmentDtos;
}
