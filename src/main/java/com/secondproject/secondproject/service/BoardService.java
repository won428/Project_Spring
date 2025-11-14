package com.secondproject.secondproject.service;


import com.secondproject.secondproject.Enum.InquiryStatus;
import com.secondproject.secondproject.Enum.Status;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.Mapping.BoardAttach;
import com.secondproject.secondproject.entity.Mapping.InquiryAttach;
import com.secondproject.secondproject.entity.Mapping.LecRegAttach;
import com.secondproject.secondproject.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BoardService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final AttachmentService attachmentService;
    private final BoardAttachRepository boardAttachRepository;
    private final InquiryAttRepository inquiryAttRepository;
    private final InquiryRepository inquiryRepository;
    private final AttachmentRepository attachmentRepository;
    private final AnswerOnInquiryRepo answerOnInquiryRepo;


    public void createNotice(BoardDto boardDto, List<MultipartFile> files) throws IOException {

        Long userCode = Long.parseLong(boardDto.getUsername());
        User user = userRepository.findByUserCode(userCode)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다."));
        Notice notice = new Notice();
        notice.setUser(user);
        notice.setTitle(boardDto.getTitle());
        notice.setContent(boardDto.getContent());

        Notice saved = boardRepository.save(notice);

        List<MultipartFile> safeFiles = (files == null)
                ? java.util.Collections.emptyList()
                : files.stream().filter(f -> f != null && !f.isEmpty()).toList();

        if (safeFiles.isEmpty()) {
            return;
        }
        for (MultipartFile file : files) {
            Attachment attachment = attachmentService.save(file, user);
            BoardAttach boardAttach = new BoardAttach();
            boardAttach.setAttachment(attachment);
            boardAttach.setNotice(saved);
            boardAttachRepository.save(boardAttach);
        }
    }

    public Page<BoardListDto> getPagedNotices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by((Sort.Direction.DESC), "createdAt"));

        Page<Notice> result = boardRepository.findAll(pageable);
        return result.map(BoardListDto::fromEntity);


    }

    public BoardResponseDto findById(Long id) {

        List<BoardAttach> attaches = boardAttachRepository.findByNoticeId(id);

        Notice notice = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지 존재 X"));

        List<Attachment> attachments = attaches.stream()
                .map(BoardAttach::getAttachment)
                .toList();
        return BoardResponseDto.fromEntity(notice, attachments);


    }

    public void deleteNotice(Long id) {
        Notice notice = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지 없음"));
        List<BoardAttach> boardAttaches = boardAttachRepository.findByNoticeId(id);
        for (BoardAttach bb : boardAttaches) {
            attachmentService.deleteById(bb.getAttachment().getId());
            boardAttachRepository.deleteById(bb.getId());
        }
        boardRepository.deleteById(id);
    }

    public void updateNotice(Long id, BoardListDto boardListDto, List<MultipartFile> files, List<String> existingFileKeys) throws IOException {
        Notice notice = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공지 없음"));
        notice.setTitle(boardListDto.getTitle());
        notice.setContent(boardListDto.getContent());
        boardRepository.save(notice);

        List<BoardAttach> boardAttaches = boardAttachRepository.findByNoticeId(notice.getId());
        if (boardAttaches != null && !boardAttaches.isEmpty()) {
            boardAttachRepository.deleteAll(boardAttaches);
        }
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                Attachment attachment = attachmentService.save(file, notice.getUser());
                BoardAttach saved = new BoardAttach();
                saved.setNotice(notice);
                saved.setAttachment(attachment);
                boardAttachRepository.save(saved);
            }
        }
        if ((existingFileKeys != null && !existingFileKeys.isEmpty())) {
            for (String storedKey : existingFileKeys) {
                Attachment existingAttachment = attachmentService.findByStoredKey(storedKey)
                        .orElseThrow(() -> new EntityNotFoundException("기존 파일 키를 찾을 수 없습니다: " + storedKey));
                BoardAttach reSaved = new BoardAttach();
                reSaved.setNotice(notice);
                reSaved.setAttachment(existingAttachment);
                boardAttachRepository.save(reSaved);
            }

        }

    }

    @Transactional
    public void createInquiry(InquiryDto post, List<MultipartFile> files) {
        User user = this.userRepository.findById(post.getUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 유저입니다."));

        Inquiry inquiry = new Inquiry();
        inquiry.setContent(post.getContent());
        inquiry.setTitle(post.getTitle());
        inquiry.setUser(user);
        inquiry.setPrivate(post.getIsPrivate());
        inquiry.setTag(post.getTag());

        this.inquiryRepository.save(inquiry);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {

                    Attachment attachment = attachmentService.save(file, user);

                    InquiryAttach inquiryAttach = new InquiryAttach();
                    inquiryAttach.setAttachment(attachment);
                    inquiryAttach.setInquiry(inquiry);

                    this.inquiryAttRepository.save(inquiryAttach);

                } catch (IOException ex) {
                    throw new UncheckedIOException("파일 저장 실패", ex);
                }
            }
        }
    }


    @Transactional
    public void updateInquiry(InquiryDto post, List<MultipartFile> files, List<AttachmentDto> existingDtos) {
        User user = this.userRepository.findById(post.getUser())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 유저입니다."));

        List<InquiryAttach> inquiryAttachList = this.inquiryAttRepository.findAllByInquiry_Id(post.getPostNumber());

        Set<Long> keepIds = new HashSet<>();
        if (existingDtos != null) {
            for (AttachmentDto dto : existingDtos) {
                if (dto != null && dto.getId() != null) {
                    keepIds.add(dto.getId());
                }
            }
        }
        Path base = Path.of(uploadDir).toAbsolutePath().normalize();
        for (InquiryAttach InquiryAttach : inquiryAttachList) {
            Long attId = InquiryAttach.getAttachment().getId();

            if (!keepIds.contains(attId)) {

                this.inquiryAttRepository.deleteByInquiryIdAndAttachmentId(post.getPostNumber(), attId);
                Attachment attachment = this.attachmentRepository.findById(attId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다."));

                Path file = base.resolve(attachment.getStoredKey()).normalize();
                if (!file.startsWith(base)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "허용되지 않은 경로 요청");
                }

                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    throw new UncheckedIOException("파일 삭제 실패", e);
                }

                this.attachmentRepository.deleteById(attId);


            }
        }


        Inquiry inquiry = this.inquiryRepository.findById(post.getPostNumber())
                .orElseThrow((() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 게시글 입니다.")));
        inquiry.setContent(post.getContent());
        inquiry.setTitle(post.getTitle());
        inquiry.setPrivate(post.getIsPrivate());
        inquiry.setTag(post.getTag());

        this.inquiryRepository.save(inquiry);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {

                    Attachment attachment = attachmentService.save(file, user);

                    InquiryAttach inquiryAttach = new InquiryAttach();
                    inquiryAttach.setAttachment(attachment);
                    inquiryAttach.setInquiry(inquiry);

                    this.inquiryAttRepository.save(inquiryAttach);

                } catch (IOException ex) {
                    throw new UncheckedIOException("파일 저장 실패", ex);
                }
            }
        }
    }

    public List<InquiryDto> myIquiryList(Long id) {
        List<Inquiry> inquiryList = this.inquiryRepository.findAllByUser_Id(id);
        List<InquiryDto> inquiryDtoList = new ArrayList<>();
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저 없음"));
        for (Inquiry inquiry : inquiryList) {
            InquiryDto inquiryDto = new InquiryDto();

            inquiryDto.setTag(inquiry.getTag());
            inquiryDto.setIsPrivate(inquiry.isPrivate());
            inquiryDto.setContent(inquiry.getContent());
            inquiryDto.setTitle(inquiry.getTitle());
            inquiryDto.setUserName(user.getName());
            inquiryDto.setPostNumber(inquiry.getId());
            inquiryDto.setStatus(inquiry.getInquiryStatus());
            inquiryDto.setCreatedAt(inquiry.getCreatedAt());
            inquiryDto.setViewCount(inquiry.getViewCount());

            inquiryDtoList.add(inquiryDto);
        }


        return inquiryDtoList;
    }

    public InquiryDto getInquiryPage(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 게시글입니다."));
        InquiryDto inquiryDto = new InquiryDto();
        List<InquiryAttach> inquiryAttachList = this.inquiryAttRepository.findAllByInquiry_Id(inquiry.getId()); // 첨부파일 매핑 리스트 만들기 (게시판에 매칭되는 파일첨부 PK를 가져오기 위함)
        List<AttachmentDto> attachmentDtos = new ArrayList<>(); // 파일 정보를 담을 DTO 리스트(리액트로 반환)
        if(inquiryAttachList != null && !inquiryAttachList.isEmpty()){ // 파일첨부가 존재 할때만 작동
            for(InquiryAttach inquiryAttach : inquiryAttachList){
                Attachment attachment = this.attachmentRepository.findById(inquiryAttach.getAttachment().getId())
                        .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"없는 파일입니다.")); // 매핑테이블에서 뽑아온 파일 PK로 파일객체 만들기
                AttachmentDto attachmentDto = new AttachmentDto(); // 파일정보를 담을 DTO객체

                attachmentDto.setUploadAt(attachment.getUploadAt());
                attachmentDto.setName(attachment.getName());
                attachmentDto.setId(attachment.getId());
                attachmentDto.setSha256(attachment.getSha256());
                attachmentDto.setStoredKey(attachment.getStoredKey());
                attachmentDto.setSizeBytes(attachment.getSizeBytes());
                attachmentDto.setContentType(attachment.getContentType());
                // 필요한정보 DTO에 담기
                attachmentDtos.add(attachmentDto); // 만들어진 객체 리스트에 담기 (매핑테이블 리스트 끝날때까지)
            }
        }


        inquiryDto.setViewCount(inquiry.getViewCount());
        inquiryDto.setCreatedAt(inquiry.getCreatedAt());
        inquiryDto.setTag(inquiry.getTag());
        inquiryDto.setTitle(inquiry.getTitle());
        inquiryDto.setContent(inquiry.getContent());
        inquiryDto.setStatus(inquiry.getInquiryStatus());
        inquiryDto.setIsPrivate(inquiry.isPrivate());
        inquiryDto.setUpdateAt(inquiry.getUpdateAt());
        inquiryDto.setUser(inquiry.getUser().getId());
        inquiryDto.setUserName(inquiry.getUser().getName());
        inquiryDto.setPostNumber(inquiry.getId());
        inquiryDto.setAttachmentDtos(attachmentDtos); // 위에서 만든 파일첨부 정보를 DTO 파일리스트 필드변수에 집어넣기

        return inquiryDto;
    }

    public void plusViewCount(Long id) {
        Inquiry inquiry = this.inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 게시글 입니다."));
        int plusView = inquiry.getViewCount() + 1;
        inquiry.setViewCount(plusView);

        this.inquiryRepository.save(inquiry);
    }

    public void writeComment(CommentDto comment) {
        AnswerOnInquiry answerOnInquiry = new AnswerOnInquiry();
        Inquiry inquiry = this.inquiryRepository.findById(comment.getPostId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 게시글 입니다."));
        User user = this.userRepository.findById(comment.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 유저 입니다."));
        answerOnInquiry.setInquiry(inquiry);
        answerOnInquiry.setUser(user);
        answerOnInquiry.setContent(comment.getContent());

        this.answerOnInquiryRepo.save(answerOnInquiry);
    }

    public List<CommentDto> inquiryCommentList(Long id) {
        List<AnswerOnInquiry> answerOnInquiryList = this.answerOnInquiryRepo.findAllByInquiry_id(id);
        List<CommentDto> commentDtoList = new ArrayList<>();

        for (AnswerOnInquiry answer : answerOnInquiryList) {
            CommentDto comment = new CommentDto();
            comment.setContent(answer.getContent());
            comment.setUserId(answer.getUser().getId());
            comment.setPostId(answer.getId());
            comment.setUserName(answer.getUser().getName());
            comment.setUpdatedAt(answer.getUpdatedAt());
            comment.setCreatedAt(answer.getCreatedAt());

            commentDtoList.add(comment);
        }

        return commentDtoList;
    }

    public List<InquiryDto> findAll() {
        List<Inquiry> inquiryList = this.inquiryRepository.findAll();
        List<InquiryDto> inquiryDtoList = new ArrayList<>();

        for (Inquiry inquiry : inquiryList) {
            InquiryDto inquiryDto = new InquiryDto();
            User user = this.userRepository.findById(inquiry.getUser().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 유저 입니다."));

            inquiryDto.setTag(inquiry.getTag());
            inquiryDto.setIsPrivate(inquiry.isPrivate());
            inquiryDto.setContent(inquiry.getContent());
            inquiryDto.setTitle(inquiry.getTitle());
            inquiryDto.setUserName(user.getName());
            inquiryDto.setPostNumber(inquiry.getId());
            inquiryDto.setStatus(inquiry.getInquiryStatus());
            inquiryDto.setCreatedAt(inquiry.getCreatedAt());
            inquiryDto.setViewCount(inquiry.getViewCount());

            inquiryDtoList.add(inquiryDto);
        }

        return inquiryDtoList;
    }

    public void updateComment(Long id, UpdateCommentDto editingText) {
        AnswerOnInquiry answer = this.answerOnInquiryRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 댓글입니다."));
        answer.setContent(editingText.getContent());
        this.answerOnInquiryRepo.save(answer);
    }

    public void deleteComment(Long id) {
        this.answerOnInquiryRepo.deleteById(id);

    }

    @Transactional
    public void deletePost(Long id) {
        List<AnswerOnInquiry> answer = this.answerOnInquiryRepo.findAllByInquiry_id(id);
        List<InquiryAttach> inquiryAttachList = this.inquiryAttRepository.findAllByInquiry_Id(id);
        Path base = Path.of(uploadDir).toAbsolutePath().normalize();

        for (InquiryAttach InquiryAttach : inquiryAttachList) {
            Long attId = InquiryAttach.getAttachment().getId();

            if (inquiryAttachList != null && !inquiryAttachList.isEmpty()) {

                this.inquiryAttRepository.deleteByInquiryIdAndAttachmentId(id, attId);
                Attachment attachment = this.attachmentRepository.findById(attId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다."));

                Path file = base.resolve(attachment.getStoredKey()).normalize();
                if (!file.startsWith(base)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "허용되지 않은 경로 요청");
                }

                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    throw new UncheckedIOException("파일 삭제 실패", e);
                }

                this.attachmentRepository.deleteById(attId);
            }
        }
        if (answer != null && !answer.isEmpty()) {
            for (AnswerOnInquiry answerOnInquiry : answer) {
                this.answerOnInquiryRepo.deleteById(answerOnInquiry.getId());
            }
        }
        this.inquiryRepository.deleteById(id);

    }

    public void updatePostStatus(Long id, InquiryStatus postStatus) {
        Inquiry inquiry = this.inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 게시글 입니다."));
        inquiry.setInquiryStatus(postStatus);

        this.inquiryRepository.save(inquiry);
    }
}
