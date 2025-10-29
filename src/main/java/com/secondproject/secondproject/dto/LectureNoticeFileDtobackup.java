//package com.secondproject.secondproject.dto;
//
//import com.secondproject.secondproject.entity.Attachment;
//import com.secondproject.secondproject.entity.LectureNotice;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//@Setter
//@NoArgsConstructor
//public class LectureNoticeFileDtobackup {
//    LectureNotice lectureNotice;
//    List<Attachment> files;
//
//
//    public static LectureNoticeFileDtobackup fromEntity(
//            LectureNotice lectureNotice,
//            List<Attachment> attachments
//    ) {
//        LectureNoticeFileDtobackup dto = new LectureNoticeFileDtobackup();
//        dto.setLectureNotice(lectureNotice);
//        List<Attachment> ListAttach = new ArrayList<>();
//        if (!attachments.isEmpty()) {
//            for (Attachment attachment : attachments) {
//                ListAttach.add(attachment);
//            }
//            dto.setFiles(ListAttach);
//        }
//        return dto;
//    }
//}
