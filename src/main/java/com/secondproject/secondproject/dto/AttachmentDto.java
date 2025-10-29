package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.LectureNotice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentDto {
    private Long id;
    private String name;
    private String storedKey;
    private String contentType;
    private Long sizeBytes;
    private LocalDate uploadAt;
    private String sha256;


//    public static AttachmentDto fromEntity(
//            LectureNotice lectureNotice,
//            List<Attachment> attachments
//    ) {
//        AttachmentDto dto = new AttachmentDto();
//        dto.setLectureNotice(lectureNotice);
//        List<Attachment> ListAttach = new ArrayList<>();
//        if (!attachments.isEmpty()) {
//            for (Attachment attachment : attachments) {
//
//
//                ListAttach.add(attachment);
//            }
//            dto.setFiles(ListAttach);
//        }
//        return dto;
//    }
}
