package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.StudentInfoDto;
import com.secondproject.secondproject.entity.Attachment;
import com.secondproject.secondproject.entity.Mapping.UserAttach;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.repository.AttachmentRepository;
import com.secondproject.secondproject.repository.UserAttachRepository;
import com.secondproject.secondproject.repository.UserRepository;
import com.secondproject.secondproject.repository.RecordStatusRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.secondproject.secondproject.Enum.UserType.STUDENT;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final UserRepository userRepository;
    private final RecordStatusRepository recordStatusRepository;
    private final AttachmentRepository attachmentRepository;
    private final UserAttachRepository userAttachRepository;

    @Value("${image.upload-dir}")
    private String imageUploadDir;

    // userId 기반 StudentInfoDto 조회
    public StudentInfoDto getStudentInfoById(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getType() != STUDENT) {
            return null;
        }

        StatusRecords status = recordStatusRepository.findByUserId(userId).orElse(null);

        return new StudentInfoDto(user, status);
    }

    // DTO를 프론트에서 요구하는 Response 형태로 매핑
    public Map<String, Object> mapToResponse(StudentInfoDto dto) {
        if (dto == null) return null;

        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("statusid", dto.getStatusId());
        statusMap.put("studentStatus", dto.getStudent_status());
        statusMap.put("admissionDate", dto.getAdmissionDate());
        statusMap.put("leaveDate", dto.getLeaveDate());
        statusMap.put("returnDate", dto.getReturnDate());
        statusMap.put("graduationDate", dto.getGraduationDate());
        statusMap.put("retentionDate", dto.getRetentionDate());
        statusMap.put("expelledDate", dto.getExpelledDate());
        statusMap.put("majorCredit", dto.getMajorCredit());
        statusMap.put("generalCredit", dto.getGeneralCredit());
        statusMap.put("totalCredit", dto.getTotalCredit());
        statusMap.put("currentCredit", dto.getCurrentCredit());
        statusMap.put("studentImage", dto.getStudentImage());

        Map<String, Object> studentMap = new HashMap<>();
        studentMap.put("userid", dto.getId());
        studentMap.put("userCode", dto.getUserCode());
        studentMap.put("name", dto.getName());
        studentMap.put("password", dto.getPassword());
        studentMap.put("birthDate", dto.getBirthDate());
        studentMap.put("email", dto.getEmail());
        studentMap.put("phone", dto.getPhone());
        studentMap.put("gender", dto.getGender());
        studentMap.put("major", dto.getMajor()); // null 안전 처리 완료
        studentMap.put("type", dto.getType());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("type", dto.getType());
        responseBody.put("studentInfo", studentMap);
        responseBody.put("statusRecords", statusMap);

        return responseBody;
    }

    // ====================== 학생 이미지 업로드 ======================
    public String saveStudentImage(Long userId, MultipartFile file) throws IOException {
        // 1. 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 파일 형식 검증 (이미지 파일만 허용)
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        // 3. 파일 크기 제한 (예: 5MB)
        long maxSize = 5 * 1024 * 1024;  // 5MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
        }

        // 4. 파일명 처리
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String savedFilename = "student_" + userId + "_" + System.currentTimeMillis() + extension;

        // 5. 저장 경로 생성 (서버 내 파일 저장 경로 지정)
        File destFile = new File(imageUploadDir, savedFilename);
        destFile.getParentFile().mkdirs(); // 디렉토리가 없으면 생성
        file.transferTo(destFile); // 파일을 실제 디렉토리에 저장

        // 6. Attachment 엔티티 저장
        Attachment attachment = new Attachment();
        attachment.setName(originalFilename); // 원본 파일 이름
        attachment.setStoredKey(savedFilename); // 저장된 파일 이름
        attachment.setSizeBytes(file.getSize());
        attachment.setContentType(file.getContentType()); // 파일의 MIME 타입
        attachmentRepository.save(attachment); // 데이터베이스에 저장

        // 7. UserAttach 매핑 테이블에 연결
        UserAttach mapping = new UserAttach();
        mapping.setUser(user);
        mapping.setAttachment(attachment);
        userAttachRepository.save(mapping);

        // 8. 클라이언트에서 접근할 수 있는 URL 반환
        // 실제 서버에서 제공할 수 있는 파일 경로
        return "/files/" + savedFilename; // 웹에서 파일 접근할 수 있는 경로
    }

}

