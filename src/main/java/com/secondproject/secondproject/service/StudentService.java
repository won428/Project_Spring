package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.StudentInfoDto;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.entity.StatusRecords;
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
    /**
     * 학생 이미지 업로드
     *
     * @param userId 학생 ID
     * @param file   업로드할 MultipartFile (이미지)
     * @return 업데이트된 StatusRecords
     * @throws IOException 파일 저장 중 에러
     */
    // 이미지 저장 경로 예시 (프로젝트 내 "uploads" 폴더)
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    public String saveStudentImage(Long userId, MultipartFile file) throws IOException {

        // 1. 유저 조회 및 학생 타입 체크
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("학생 정보 없음"));

        if (user.getType() != STUDENT) {
            throw new IllegalArgumentException("학생이 아닌 사용자는 업로드 불가");
        }

        // 2. StatusRecords 조회, 없으면 새로 생성
        StatusRecords record = recordStatusRepository.findByUserId(userId)
                .orElseGet(() -> {
                    StatusRecords r = new StatusRecords();
                    r.setUser(user);
                    r.setAdmissionDate(LocalDate.now());
                    return r;
                });

        // 3. 파일 저장
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String savedFileName = UUID.randomUUID() + ext;
        File saveFile = new File(imageUploadDir + savedFileName);
        saveFile.getParentFile().mkdirs();
        file.transferTo(saveFile);

        // 4. StatusRecords에 이미지 URL 저장 (React에서 접근할 URL)
        record.setStudentImage("/images/" + savedFileName);
        recordStatusRepository.save(record);

        return record.getStudentImage();
    }

}

