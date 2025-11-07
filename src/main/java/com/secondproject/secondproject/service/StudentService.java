package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.StudentInfoDto;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.repository.UserRepository;
import com.secondproject.secondproject.repository.RecordStatusRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.secondproject.secondproject.Enum.UserType.STUDENT;

@Service
public class StudentService {
    private final UserRepository userRepository;
    private final RecordStatusRepository recordStatusRepository;

    @Value("${image.upload-dir}")
    private String imageUploadDir;

    public StudentService(UserRepository userRepository, RecordStatusRepository recordStatusRepository) {
        this.userRepository = userRepository;
        this.recordStatusRepository = recordStatusRepository;
    }

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
    /**
     * 학생 이미지 업로드
     * @param userId 학생 ID
     * @param file 업로드할 MultipartFile (이미지)
     * @return 업데이트된 StatusRecords
     * @throws IOException 파일 저장 중 에러
     */
    public StatusRecords updateStudentImage(Long userId, MultipartFile file) throws IOException {
        StatusRecords statusRecord = recordStatusRepository.findByUserId(userId).orElse(null);
        if (statusRecord == null) return null;

        // 프로퍼티에서 경로 가져오기
        File dir = new File(imageUploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = userId + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(dir, fileName);

        file.transferTo(dest);

        // DB에는 상대 경로 저장 (웹에서 접근 가능한 경로)
        statusRecord.setStudentImage("/uploads/Image/" + fileName); // 필요에 따라 경로 맞춤
        return recordStatusRepository.save(statusRecord);
    }

}

