package com.secondproject.secondproject.service;

import com.secondproject.secondproject.Enum.Gender;
import com.secondproject.secondproject.Enum.UserType;
import com.secondproject.secondproject.dto.*;
import com.secondproject.secondproject.entity.*;
import com.secondproject.secondproject.entity.StatusRecords;
import com.secondproject.secondproject.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import lombok.val;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.ConstraintViolation;

import jakarta.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StatusRecordsRepository statusRecordsRepository;
    private final MajorRepository majorRepository;
    private final CollegeRepository collegeRepository;
    private final CourseRegRepository courseRegRepository;
    private final DataFormatter formatter = new DataFormatter();
    private final Validator validator;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void insertUser(UserDto userinfo) {
        System.out.println(userinfo);

        if(userinfo.getEmail() == null || userinfo.getEmail().isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"이메일은 필수 입력 사항입니다..");
        }else if(userinfo.getBirthdate() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"생년월일은 필수 입력 사항입니다..");
        }else if(userinfo.getPhone() == null || userinfo.getPhone().isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"휴대폰 번호는 필수 입력 사항입니다.");
        }else if(userinfo.getMajor() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"학과는 필수 입력 사항입니다.");
        }

        User user = new User();
        Major major = this.majorRepository.findById(userinfo.getMajor())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String encodePassWord  = passwordEncoder.encode(userinfo.getPhone());

        if(this.userRepository.existsByPhone(userinfo.getPhone())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 휴대폰 번호입니다.");
        }
        if(this.userRepository.existsByEmail(userinfo.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 존재하는 이메일 입니다.");
        }

        user.setName(userinfo.getName());
        user.setGender(userinfo.getGender());
        user.setEmail(userinfo.getEmail());
        user.setBirthDate(userinfo.getBirthdate());
        user.setPassword(encodePassWord);
        user.setMajor(major);
        user.setPhone(userinfo.getPhone());
        user.setType(userinfo.getType());

        User saved = this.userRepository.save(user);
        StatusRecords userStatus = new StatusRecords();

        userStatus.setUser(saved);
        userStatus.setAdmissionDate(LocalDate.now());

        StatusRecords savedRecords = statusRecordsRepository.save(userStatus);

        int year = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).getYear();     // 1997-04-28
        Long id = saved.getId();
        Long majors = saved.getMajor().getId(); // 학과 코드

        if (id == null || majors == null){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"학번 생성 데이터 누락.");
        }

        String userCode = String.format("%04d%04d%03d",year,id,majors);

        Long studentId = Long.parseLong(userCode);

        saved.setUserCode(studentId);
    }

    // 전체 유저 조회
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


    public void update(Long id, UserUpdateDto userReactDto, User findUser, Major major) {

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

    public List<UserDto> findUserLectureDetail(Long lectureId) {
        List<CourseRegistration> courseRegistrations = this.courseRegRepository.findAllByLecture_Id(lectureId);
        List<UserDto> userDtoList = new ArrayList<>();

        for (CourseRegistration courseRegistration: courseRegistrations){
            User user = this.userRepository.findById(courseRegistration.getUser().getId())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"없는 유저"));
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setUserCode(user.getUserCode());
            userDto.setName(user.getName());
            userDto.setMajorName(user.getMajor().getName());
            userDto.setEmail(user.getEmail());
            userDto.setPhone(user.getPhone());

            userDtoList.add(userDto);
        }

        return userDtoList;
    }

    public Page<UserListDto> ListPageUser(UserListSearchDto userListSearchDto, int pageNumber, int pageSize) {

        Specification<User> spec = (root, query, cb) -> cb.conjunction();


        if(userListSearchDto.getSearchMajor() != null){
            spec = spec.and(PublicSpecification.hasMajor(userListSearchDto.getSearchMajor()));
        }

        if(userListSearchDto.getSearchUserType() != null){
            spec = spec.and(PublicSpecification.hasUserType(userListSearchDto.getSearchUserType()));
        }

        if(userListSearchDto.getSearchGender() != null || !userListSearchDto.getSearchGender().isBlank()){
            spec = spec.and(PublicSpecification.hasGender(userListSearchDto.getSearchGender()));
        }

        String searchMode = userListSearchDto.getSearchMode();
        String searchKeyword = userListSearchDto.getSearchKeyword();

        if(searchMode != null && searchKeyword != null){
            if(searchMode.equals("name")){
                spec = spec.and(PublicSpecification.hasName(searchKeyword));
            }else if(searchMode.equals("email")){
                spec = spec.and(PublicSpecification.hasEmail(searchKeyword));
            }else if(searchMode.equals("phone")){
                spec = spec.and(PublicSpecification.hasPhone(searchKeyword));
            }
        }


        Sort sort = Sort.by(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);

        Page<User> userList = this.userRepository.findAll(spec, pageable);
        Page<UserListDto> userListDtos = userList.map(user -> {
            UserListDto userdto = new UserListDto();
            userdto.setId(user.getId());
            userdto.setUser_code(user.getUserCode());
            userdto.setU_name(user.getName());
            userdto.setBirthdate(user.getBirthDate());
            userdto.setGender(user.getGender());
            userdto.setEmail(user.getEmail());
            userdto.setPhone(user.getPhone());
            userdto.setMajor(user.getMajor() != null ? user.getMajor().getName() : null);
            userdto.setU_type(user.getType());
            userdto.setCollege(user.getMajor().getCollege().getType());

            return userdto;
        });

        return userListDtos;
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

                    try {
                        Gender gender = mapGender(genderRaw);
                        dto.setGender(gender);
                    } catch (Exception e) {
                        errors.add("gender : "+e.getMessage());
                        dto.setGender(null);
                    }

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

//                dto.setErrors(errors);
                dto.setValid(errors.isEmpty());
                userRow.add(dto);
            }
            return userRow;

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일을 읽을 수 없습니다.", e);
        }
    }
    // 아래는 전부 검증용, 파싱용 메소드

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

    @Transactional
    public void importUsers(@Valid List<UserStBatchDto> users) {
        // 이메일 정규화(공백제거+소문자)
        List<String> emailNorm = users.stream()
                .map(r->normalizeEmail(r.getEmail()))
                .filter(o -> o != null)
                .toList();

        // 업로드 파일 내부 중복 탐지
        Map<String,Long> inFileCounts = emailNorm.stream() // 정규화한 이메일 리스트를 스트림으로
                .collect(Collectors.groupingBy(e -> e,Collectors.counting()));
        // groupingBy(e -> e, ...) :같은 이메일끼리 묶기 / Collectors.counting() : 각 그룹의 개수 Long으로 카운팅

        Set<String> dupInFile = inFileCounts.entrySet().stream() // 위에서 Map(키, 값) 페어를 담은 스트림
                .filter(e -> e.getValue() > 1) // 등장횟수가 2회 이상인 항목 = 중복된 이메일만 남김
                .map(Map.Entry::getKey) // 중복이메일 Map(이메일, 횟수)에서 이메일만 추출
                .collect(Collectors.toSet()); // 중복 이메일을 Set집합으로 수집

        // 중복 이메일이 존재하면 에러 반영
        if (!dupInFile.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST
                    ,"업로드 파일 내부에 중복된 이메일이 있습니다.\n중복된 이메일 : "+String.join(", ",dupInFile));
        }

        // DB에 존재하는 데이터인지 중복 확인
        List<User> existed = userRepository.findAllByEmailIn(emailNorm); // 정규화된 이메일 집합의 존재여부 확인
        Set<String> existedSet = existed.stream()
                .map(u -> normalizeEmail(u.getEmail()))
                .filter(e -> e != null)
                .collect(Collectors.toSet());
        if (!existedSet.isEmpty()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 이메일 : "+String.join(", ", existedSet));
        }

        // users리스트에서 중복 제거한 MajorId 컬렉션으로 묶기
        var majorIds = users.stream()// 매핑, 필터링을 한줄씩 작성하게 해주는 API
                .map(UserStBatchDto::getMajorId) // UserStBatchDto에서 getMajorId만 뽑아냄
                .filter(id -> id != null) // null인 id 필터링해서 제거
                .collect(Collectors.toSet()); // 중복제거된 Set으로 묶음

        // set컬렉션으로 묶은 majorId를 Map<키, 값>형태로 저장
        var majorById = majorRepository.findAllById(majorIds)
                .stream()
                .collect(Collectors.toMap(Major::getId, m -> m));

        // user 리스트 생성
        List<User> userList = new ArrayList<>(users.size());
        UserType type = UserType.STUDENT;

        // user 생성(학번은 nullable)
        for (UserStBatchDto u : users){
            Major major = Optional.ofNullable(
                    majorById.get(u.getMajorId()))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            User user = new User();

            user.setName(u.getName());
            user.setBirthDate(u.getBirthDate());
            user.setGender(u.getGender());
            user.setEmail(u.getEmail());
            user.setType(type);
            user.setPassword(passwordEncoder.encode(u.getPhoneNumber()));
            user.setPhone(u.getPhoneNumber());
            user.setMajor(major);
            userList.add(user);
        }
        userRepository.saveAll(userList); // 유저 저장 => id 생성됨

        // 연도 가져오기(학번생성 1단계)
        int year = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).getYear();

        // 학적정보도 함께 생성
        List<StatusRecords> statusRecords = new ArrayList<>(userList.size());

        for (User u : userList){
            // 유저id 불러오기(학번생성 2단계)
            Long userId = u.getId();
            Long majorId = u.getMajor().getId();

            // 유저Id, 학과Id 없을 경우 예외처리
            if (userId == null || majorId == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"학번 생성 데이터가 누락되었습니다.");
            }

            // 연도4자리+유저번호 0포함 4자리+학과코드
            String userNum = String.format("%04d%04d%03d",year,userId,majorId);

            // Long으로 타입변환
            Long userCode = Long.parseLong(userNum);

            // 유저 학번 업데이트
            u.setUserCode(userCode);

            StatusRecords statusRecordList = new StatusRecords();

            statusRecordList.setUser(u);
            statusRecordList.setAdmissionDate(LocalDate.now());
            statusRecords.add(statusRecordList);

        }
        userRepository.saveAll(userList);
        statusRecordsRepository.saveAll(statusRecords);
    }

    // email 소문자로 변환
    private String normalizeEmail(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    // 교수 이름 가져오기
    public UserNameDto getUserNameById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserNameDto(user.getId(), user.getName()))
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }
}
