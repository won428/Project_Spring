package com.secondproject.secondproject.dto;

import com.secondproject.secondproject.entity.Assignment;
import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.SubmitAsgmt;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AssignmentResDto {
    private Long id; // 과제ID(PK)

    private String title; // 공지 제목

    private String content; // 공지 본문

    private boolean isEnabled = true;
    // 제출창구(제출 마감시 비활성화(0), 제출 진행중일시 활성화(1))

    private LocalDate openAt; // 제출 시작일

    private LocalDate dueAt; // 제출 마감일

//    private int maxFiles; // 업로드 가능한 파일 최대 갯수
//    이미 Application 파일에 되어 있는데 굳이 사용해야하나 싶습니다.
//    private int maxSizeMb = 50; // 파일 최대 용량(mb), 기본값 50

    private Long userId;

    private String username; // 공지한사람(=교수이름)

    private LocalDateTime createAt; // 공지 등록일

    private LocalDateTime updateAt; // 공지 수정일

    private List<AttachmentDto> attachmentDto;

    private List<AttachmentDto> attachmentSubmittedDto;

    private List<SubmitAsgmtDto> submitAsgmtDto;

    private SubmitAsgmtDto SubmittedOne;

    public static AssignmentResDto fromProfessorEntity(
            Assignment assignment,
            List<Attachment> attachments,     // 1. 과제 공지 파일
            List<SubmitAsgmtDto> submitDtos  // 2. 서비스에서 조립이 끝난 '제출물 DTO 리스트'
    ) {
        AssignmentResDto dto = new AssignmentResDto();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setContent(assignment.getContent());
        dto.setOpenAt(assignment.getOpenAt());
        dto.setDueAt(assignment.getDueAt());
        if (assignment.getUser() != null) {
            dto.setUsername(assignment.getUser().getName());
            dto.setUserId(assignment.getUser().getId());
        }
        dto.setCreateAt(assignment.getCreateAt());
        dto.setUpdateAt(assignment.getUpdateAt());

        // 1. 과제 공지 파일 (AttachmentDto)
        if (attachments != null && !attachments.isEmpty()) {
            List<AttachmentDto> listAttach = attachments.stream()
                    .map(AttachmentDto::fromEntity) // (가정) AttachmentDto에 fromEntity가 있다고 가정
                    .toList();
            dto.setAttachmentDto(listAttach);
        } else {
            dto.setAttachmentDto(new ArrayList<>());
        }

        // 2. 학생 제출물 DTO 리스트 (SubmitAsgmtDto)
        // [★핵심★] 서비스에서 이미 완성된 리스트를 그대로 할당합니다.
        dto.setSubmitAsgmtDto(submitDtos);

        // [참고] 교수용 뷰에서는 최상위의 attachmentSubmittedDto와 SubmittedOne은 사용하지 않습니다.
        dto.setAttachmentSubmittedDto(new ArrayList<>());
        dto.setSubmittedOne(null);

        return dto;
    }


    // ... (기존의 잘못된 교수용 fromEntity는 삭제) ...

    public static AssignmentResDto fromEntity(
            Assignment assignment,
            List<Attachment> attachments,
            SubmitAsgmt submitAsgmts,
            List<Attachment> attachmentSubmitted
    ) {
        AssignmentResDto dto = new AssignmentResDto();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setContent(assignment.getContent());
        dto.setOpenAt(assignment.getOpenAt());
        dto.setDueAt(assignment.getDueAt());
        if (assignment.getUser() != null) {
            dto.setUsername(assignment.getUser().getName());
            dto.setUserId(assignment.getUser().getId());
        }
        dto.setCreateAt(assignment.getCreateAt());
        dto.setUpdateAt(assignment.getUpdateAt());


        List<AttachmentDto> ListAttach = new ArrayList<>();
        if (attachments != null && !attachments.isEmpty()) {
            for (Attachment attachment : attachments) {
                AttachmentDto Ado = new AttachmentDto();
                Ado.setId(attachment.getId());
                Ado.setName(attachment.getName());
                Ado.setId(attachment.getUser().getId());
                Ado.setContentType(attachment.getContentType());
                Ado.setSha256(attachment.getSha256());
                Ado.setSizeBytes(attachment.getSizeBytes());
                Ado.setStoredKey(attachment.getStoredKey());
                Ado.setUploadAt(attachment.getUploadAt());

                ListAttach.add(Ado);
            }
            dto.setAttachmentDto(ListAttach);
        } else {
            dto.setAttachmentDto(new ArrayList<>());//공지에 첨부파일 없음
        }


        if (submitAsgmts != null) {
            SubmitAsgmtDto dtoS = new SubmitAsgmtDto();
            dtoS.setId(submitAsgmts.getId());
            dtoS.setUsername(submitAsgmts.getUser().getName());
            dtoS.setTitle(submitAsgmts.getTitle());
            dtoS.setContent(submitAsgmts.getContent());
            dtoS.setSubmitStatus(submitAsgmts.getSubmitStatus());
            dtoS.setSubmitAt(submitAsgmts.getSubmitAt());
            dtoS.setUpdateAt(submitAsgmts.getUpdateAt());
            dto.setSubmittedOne(dtoS);
        } else {
            dto.setSubmittedOne(null); // 과제 미제출 상태
        }

        List<AttachmentDto> ListSubmittedAttach = new ArrayList<>();
        if (attachmentSubmitted != null && !attachmentSubmitted.isEmpty()) {
            for (Attachment attachment : attachmentSubmitted) {
                AttachmentDto Ado = new AttachmentDto();
                Ado.setId(attachment.getId());
                Ado.setName(attachment.getName());
                Ado.setId(attachment.getUser().getId());
                Ado.setContentType(attachment.getContentType());
                Ado.setSha256(attachment.getSha256());
                Ado.setSizeBytes(attachment.getSizeBytes());
                Ado.setStoredKey(attachment.getStoredKey());
                Ado.setUploadAt(attachment.getUploadAt());

                ListSubmittedAttach.add(Ado);
            }
            dto.setAttachmentSubmittedDto(ListSubmittedAttach);
        } else {
            dto.setAttachmentSubmittedDto(new ArrayList<>()); //
        }

        return dto;
    }

}
