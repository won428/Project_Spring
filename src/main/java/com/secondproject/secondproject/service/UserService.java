package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.Gender;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.dto.UserListDto;
import com.secondproject.secondproject.dto.UserStBatchDto;
import com.secondproject.secondproject.dto.UserUpdateDto;
import com.secondproject.secondproject.entity.College;
import com.secondproject.secondproject.entity.Major;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.entity.User;
import com.secondproject.secondproject.repository.CollegeRepository;
import com.secondproject.secondproject.repository.MajorRepository;
import com.secondproject.secondproject.repository.StatusRecordsRepository;
import com.secondproject.secondproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.ConstraintViolation;

import jakarta.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StatusRecordsRepository statusRecordsRepository;
    private final MajorRepository majorRepository;
    private final CollegeRepository collegeRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataFormatter formatter = new DataFormatter();
    private final Validator validator;

    @Transactional
    public void insertUser(User newUser) {
        User saved = this.userRepository.save(newUser);
        StatusRecords userStatus = new StatusRecords();

        userStatus.setUser(saved);
        userStatus.setAdmissionDate(LocalDate.now());

        StatusRecords savedRecords = statusRecordsRepository.save(userStatus);

        LocalDate b = savedRecords.getAdmissionDate();     // 1997-04-28
        Long id = saved.getId();
        int year = b.getYear();                // 입학년도
        Long major = saved.getMajor().getId(); // 학과 코드

        String stringYear = Integer.toString(year); // 입학년도 문자열로 변환
        String stringMajor = Long.toString(major); // 학과 코드 문자열로 변환
        String stringId = Long.toString(id); // id 문자열로 변환

        String stringStudentId = stringYear + stringId + stringMajor;

        Long studentId = Long.parseLong(stringStudentId);

        saved.setUserCode(studentId);
    }

    public List<UserListDto> findUserList() {
        List<User> userList = this.userRepository.findAll();
        List<UserListDto> userListDto = new ArrayList<>();


        for (User user : userList) {
            UserListDto userDto = new UserListDto();
            Major major = this.majorRepository.findMajorById(user.getMajor().getId());
            College college = this.collegeRepository.findCollegeById(major.getCollege().getId());

            String majorName = major.getName();
            String collegeName = college.getType();

            userDto.setU_name(user.getName());
            userDto.setBirthdate(user.getBirthDate());
            userDto.setGender(user.getGender());
            userDto.setUser_code(user.getUserCode());
            userDto.setPhone(user.getPhone());
            userDto.setEmail(user.getEmail());
            userDto.setMajor(majorName);
            userDto.setCollege(collegeName);
            userDto.setU_type(user.getType());

            userListDto.add(userDto);
        }

        return userListDto;
    }

    public List<UserListDto> findProfessorList(Long majorId) {
        Major findmajor = this.majorRepository.findMajorById(majorId);
        List<User> userList = this.userRepository.findAllByMajor(findmajor);
        List<UserListDto> userListDto = new ArrayList<>();


        for (User user : userList) {
            UserListDto userDto = new UserListDto();
            Major major = this.majorRepository.findMajorById(user.getMajor().getId());
            College college = this.collegeRepository.findCollegeById(major.getCollege().getId());
            if (user.getType() != UserType.PROFESSOR) continue;
            String majorName = major.getName();
            String collegeName = college.getType();

            userDto.setId(user.getId());
            userDto.setU_name(user.getName());
            userDto.setBirthdate(user.getBirthDate());
            userDto.setGender(user.getGender());
            userDto.setUser_code(user.getUserCode());
            userDto.setPhone(user.getPhone());
            userDto.setEmail(user.getEmail());
            userDto.setPassword(user.getPassword());
            userDto.setMajor(majorName);
            userDto.setCollege(collegeName);
            userDto.setU_type(user.getType());

            userListDto.add(userDto);

        }

        return userListDto;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public Optional<User> getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    public void setPassword(User user) {
        userRepository.save(user);
    }

    public Optional<User> findByUsercode(Long userCode) {
        Optional<User> user = this.userRepository.findByUserCode(userCode);

        return user;
    }


    public void save(Long id, UserUpdateDto userReactDto, User findUser, Major major) {

        User user = findUser;
        String password = passwordEncoder.encode(userReactDto.getPassword());

        if (userReactDto.getPassword() != null && !userReactDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userReactDto.getPassword()));
        }

        if (userReactDto.getGender() != null) user.setGender(userReactDto.getGender());
        if (userReactDto.getPassword() != null) user.setPassword(password);
        if (userReactDto.getMajor() != null) user.setMajor(major);
        if (userReactDto.getEmail() != null) user.setEmail(userReactDto.getEmail());
        if (userReactDto.getPhone() != null) user.setPhone(userReactDto.getPhone());
        if (userReactDto.getBirthdate() != null) user.setBirthDate(userReactDto.getBirthdate());
        if (userReactDto.getU_type() != null) user.setType(userReactDto.getU_type());

        this.userRepository.save(user);
    }

    // 학생 일괄 등록용 파일 읽어오기(DB 저장 X)
    //MultipartFile : HTTP요청에서 온 파일파트를 추상화한 인터페이스, html에서 보낸 파일이 이 타입으로 매핑
    public List<UserStBatchDto> parse(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook wb = WorkbookFactory.create(is)){
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

            // Workbook의 첫번째 시트를 가져오라는 뜻
            Sheet sheet = wb.getSheetAt(0);

            // 존재하는 학과인지 검증용, Set으로 중복제거&순서 없음
            Set<Long> validMajorId = new HashSet<>(majorRepository.findAll().stream().map(Major::getId).toList());

            List<UserStBatchDto> userRow = new ArrayList<>();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                UserStBatchDto dto = new UserStBatchDto();
                List<String> errors = new ArrayList<>();

                try {
                    // 열 인덱스는 헤더 순서에 맞춤
                    String name = getString(row.getCell(0), evaluator);
                    LocalDate birth = parseExcelLikeDate(row.getCell(1), evaluator);
                    String genderRaw = getString(row.getCell(2), evaluator);
                    String email = getString(row.getCell(3), evaluator);

                    // 전화번호: 숫자만 + 선행0 보존
                    String phoneShown = getShownText(row.getCell(4), evaluator); // 표시 문자열
                    String phone = normalizePhoneAndEnsureLeadingZero(phoneShown);

                    Long majorId = getLongSafe(row.getCell(5), evaluator);

                    dto.setName(name);
                    dto.setBirthDate(birth);
                    dto.setGender(genderRaw);
                    dto.setEmail(email);
                    dto.setPhoneNumber(phone);
                    dto.setMajorId(majorId);

                    // Bean Validation
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        violations.forEach(v -> errors.add(v.getPropertyPath() + " : " + v.getMessage()));
                    }

                    // 성별 매핑 검증 (MALE/FEMALE/남/여/M/F)
                    try {
                        mapGender(genderRaw);
                    } catch (Exception e) {
                        errors.add("gender : " + e.getMessage());
                    }

                    // 학과 존재 여부
                    if (dto.getMajorId() == null || !validMajorId.contains(dto.getMajorId())) {
                        errors.add("존재하지 않는 학과코드 : " + dto.getMajorId());
                    }

                } catch (Exception ex) {
                    errors.add("row " + (r + 1) + " 파싱 오류: " + ex.getMessage());
                }

                dto.setErrors(errors);
                dto.setValid(errors.isEmpty());
                userRow.add(dto);
            }
            return userRow;

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일을 읽을 수 없습니다.", e);
        }
    }
    // 아래는 전부 검증용, 파싱용 메소드

    // 헤더 검증: 기대하는 헤더명과 동일한지(대소문자 무시)
    private void assertHeader(Row header, FormulaEvaluator ev, String... expected) {
        if (header == null) {
            throw new IllegalArgumentException("엑셀 첫 행(헤더)이 없습니다.");
        }
        for (int i = 0; i < expected.length; i++) {
            String got = getString(header.getCell(i), ev);
            String want = expected[i];
            if (got == null || !want.equalsIgnoreCase(got)) {
                throw new IllegalArgumentException("헤더 불일치: col " + (i + 1) +
                        " expected='" + want + "' but was='" + got + "'");
            }
        }
    }

    // 표시 문자열(보이는 값) 가져오기 - 선행0를 포함해 사용하고 싶을 때
    private String getShownText(Cell c, FormulaEvaluator ev) {
        if (c == null) return null;
        String s = formatter.formatCellValue(c, ev);
        s = (s != null) ? s.trim() : null;
        return (s == null || s.isEmpty()) ? null : s;
    }

    // 문자열(일반 용) 가져오기
    private String getString(Cell c, FormulaEvaluator ev) {
        return getShownText(c, ev); // 통일: formatter 기반
    }

    // 선행 0 보존 필요 없고 **정수**로 안전 파싱
    private Long getLongSafe(Cell cell, FormulaEvaluator ev) {
        if (cell == null) return null;

        CellType t = cell.getCellType();
        if (t == CellType.FORMULA) t = ev.evaluateFormulaCell(cell);

        if (t == CellType.NUMERIC) {
            // 숫자는 텍스트로 변환 후 파싱 (과학표기/소수 방지)
            String txt = NumberToTextConverter.toText(cell.getNumericCellValue());
            txt = txt.replace(",", "").trim();
            if (txt.isEmpty()) return null;
            // ".0" 같은 꼬리 제거
            if (txt.endsWith(".0")) txt = txt.substring(0, txt.length() - 2);
            return new BigDecimal(txt).longValueExact();
        }

        String s = getShownText(cell, ev);
        if (s == null) return null;
        s = s.replace(",", "").trim();
        if (s.isEmpty()) return null;
        if (s.endsWith(".0")) s = s.substring(0, s.length() - 2);

        return new BigDecimal(s).longValueExact();
    }

    // 날짜 안전 파싱: 날짜서식 → 직렬값 → 문자열 패턴
    private static final DateTimeFormatter[] DATE_PATTERNS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_LOCAL_DATE,          // 2025-10-29
            DateTimeFormatter.ofPattern("yyyy.M.d"),
            DateTimeFormatter.ofPattern("yyyy/M/d"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    };

    // 전화번호 정규화(숫자만) + 선행 0 보장
    private String normalizePhoneAndEnsureLeadingZero(String s) {
        if (s == null) return null;
        s = s.replaceAll("\\D", "");   // 숫자만
        if (s.isEmpty()) return s;
        return s.startsWith("0") ? s : "0" + s;
    }

    // 성별 매핑(검증용) - MALE/FEMALE, 남/여, M/F 지원
    private Gender mapGender(String raw) {
        if (raw == null || raw.isBlank()) throw new IllegalArgumentException("성별 누락");
        String v = raw.trim().toUpperCase();
        if (v.equals("남") || v.equals("M") || v.equals("MALE")) return Gender.MALE;
        if (v.equals("여") || v.equals("F") || v.equals("FEMALE")) return Gender.FEMALE;
        throw new IllegalArgumentException("성별 값 오류: " + raw);
    }

    // 표시 문자열(보이는 값)
    private String shown(Cell c, FormulaEvaluator ev) {
        if (c == null) return null;
        String s = formatter.formatCellValue(c, ev);
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    // "+56634-06-05" 같은 **직렬값 변종**까지 잡아내는 LocalDate 파서
    private LocalDate parseExcelLikeDate(Cell c, FormulaEvaluator ev) {
        if (c == null) return null;

        // 1) 날짜 서식/직렬값이면 즉시 처리
        CellType t = c.getCellType();
        if (t == CellType.FORMULA) t = ev.evaluateFormulaCell(c);
        if (DateUtil.isCellDateFormatted(c)) {
            return c.getLocalDateTimeCellValue().toLocalDate();
        }
        if (t == CellType.NUMERIC && DateUtil.isValidExcelDate(c.getNumericCellValue())) {
            return DateUtil.getLocalDateTime(c.getNumericCellValue()).toLocalDate();
        }

        // 2) 문자열 처리
        String s = shown(c, ev);
        if (s == null) return null;

        // "+56634-06-05" → "56634-06-05"
        if (s.startsWith("+")) s = s.substring(1).trim();

        // 2-1) "56634-06-05" / "56634/06/05" / "56634.06.05"
        //      맨 앞 숫자 블록을 '엑셀 직렬값'으로 간주
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("^(\\d{4,6})\\s*[-\\./]\\s*\\d{1,2}\\s*[-\\./]\\s*\\d{1,2}$")
                .matcher(s);
        if (m.matches()) {
            double serial = Double.parseDouble(m.group(1));
            if (DateUtil.isValidExcelDate(serial)) {
                return DateUtil.getLocalDateTime(serial).toLocalDate();
            }
        }

        // 2-2) 전부 숫자면 직렬값으로 간주
        if (s.matches("^\\d{4,6}$")) {
            double v = Double.parseDouble(s);
            if (DateUtil.isValidExcelDate(v)) {
                return DateUtil.getLocalDateTime(v).toLocalDate();
            }
        }

        // 2-3) 일반 문자열 날짜 패턴 (필요시 추가)
        DateTimeFormatter[] patterns = new DateTimeFormatter[]{
                DateTimeFormatter.ISO_LOCAL_DATE,           // 2025-10-29
                DateTimeFormatter.ofPattern("yyyy.M.d"),
                DateTimeFormatter.ofPattern("yyyy/M/d"),
                DateTimeFormatter.ofPattern("yyyyMMdd")
        };
        for (DateTimeFormatter f : patterns) {
            try {
                LocalDate d = LocalDate.parse(s.replaceAll("\\s*\\.\\s*", ".")
                        .replaceAll("\\s*/\\s*", "/")
                        .replaceAll("\\s*-\\s*", "-"), f);
                if (d.getYear() <= 3000) return d;
            } catch (Exception ignore) {}
        }
        return null; // 못 읽으면 null
    }
}
