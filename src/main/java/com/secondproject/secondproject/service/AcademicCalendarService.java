package com.secondproject.secondproject.service;

import com.secondproject.secondproject.dto.AcademicCalendarDto;
import com.secondproject.secondproject.entity.AcademicCalendar;
import com.secondproject.secondproject.repository.AcademicCalendarRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicCalendarService {

    private final AcademicCalendarRepository academicCalendarRepository;


    public List<AcademicCalendar> getList() {
        List<AcademicCalendar> list = academicCalendarRepository.findAll();
        return list;

    }

    public Page<AcademicCalendar> getListByMonth(int year, int month, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by((Sort.Direction.DESC), "calStartDate"));
        LocalDate calStartDate = LocalDate.of(year, month, 1);
        LocalDate calEndDate = calStartDate.plusMonths(1).minusDays(1);

        return academicCalendarRepository.findByCalStartDateBetween(calStartDate, calEndDate, pageable);

    }

    public void deleteById(Long id) {
        academicCalendarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("없는 일정"));
        academicCalendarRepository.deleteById(id);
    }

    public void installCal(AcademicCalendarDto dto, MultipartFile file) throws Exception {
        if (!dto.getTitle().isEmpty()) {
            AcademicCalendar academicCalendar = new AcademicCalendar();
            academicCalendar.setTitle(dto.getTitle());
            academicCalendar.setDescription(dto.getDescription());
            academicCalendar.setCalStartDate(dto.getCalStartDate());
            academicCalendar.setEneDate(dto.getEneDate());

            academicCalendarRepository.save(academicCalendar);
        }


        if (file != null && !file.isEmpty()) {
            processExcel(file);
        }
    }

    public void processExcel(MultipartFile file) throws Exception {

        List<AcademicCalendar> dataList = new ArrayList<>();

        try (InputStream is = file.getInputStream()) {
            // 엑셀 워크북(파일) 열기
            Workbook workbook = new XSSFWorkbook(is); // .xlsx 포맷

            // 첫 번째 시트(Sheet) 가져오기
            Sheet sheet = workbook.getSheetAt(0);

            // 첫 번째 행(헤더)은 건너뛰고 두 번째 행부터 읽기 (i=1)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                AcademicCalendar dto = new AcademicCalendar();

                // 엑셀에서 값 읽기 (A열: 이름, B열: 이메일 이라고 가정)
                // getCell(0) -> A열, getCell(1) -> B열
                String title = getSafeStringCellValue(row.getCell(0));
                String des = getSafeStringCellValue(row.getCell(1));
                LocalDate calStartDate = getSafeDateCellValue(row.getCell(2));
                if (calStartDate == null) {
                    continue; // 이 행을 dataList에 추가하지 않고 다음 행으로
                }

                LocalDate endDate = getSafeDateCellValue(row.getCell(3));
                // 주의: 숫자, 날짜 등 셀 타입에 따라 getStringCellValue() 대신
                // getNumericCellValue(), getDateCellValue() 등을 사용해야 합니다.
                // 혹은 CellType을 확인하는 로직이 필요합니다.

                dto.setTitle(title);
                dto.setDescription(des);
                dto.setCalStartDate(calStartDate);
                dto.setEneDate(endDate);

                dataList.add(dto);
            }
        }

        academicCalendarRepository.saveAll(dataList); // JPA 사용 시
    }

    private String getSafeStringCellValue(Cell cell) {
        if (cell == null) {
            return null; // 또는 ""
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // 숫자를 문자열로 변환
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return null; // 또는 ""
        }
    }

    private LocalDate getSafeDateCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getLocalDateTimeCellValue().toLocalDate();

            } else if (cell.getCellType() == CellType.STRING) {

                // ▼▼▼ [해결 2] 날짜 문자열 정규화 ( . -> - ) ▼▼▼
                String dateString = cell.getStringCellValue()
                        .replace(" ", "")  // "2024. 4. 5" -> "2024.4.5"
                        .replace(".", "-"); // "2024.4.5" -> "2024-4-5"

                // LocalDate.parse()는 "2024-4-5" (yyyy-M-d) 형식을 파싱할 수 있습니다.
                return LocalDate.parse(dateString);
                // ▲▲▲ [수정 완료] ▲▲▲

            } else {
                return null;
            }
        } catch (Exception e) {
            // 파싱 실패 시 (예: "TBD" 같은 알 수 없는 문자열)
            System.out.println("WARN: 날짜 파싱 실패: " + cell.toString() + " | Error: " + e.getMessage());
            return null; // 실패 시 null 반환
        }
    }

    public AcademicCalendar findById(Long id) {
        AcademicCalendar academicCalendar = academicCalendarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("발견 안됨"));

        AcademicCalendar calendar = new AcademicCalendar();
        calendar.setId(academicCalendar.getId());
        calendar.setTitle(academicCalendar.getTitle());
        calendar.setDescription(academicCalendar.getDescription());
        calendar.setCalStartDate(academicCalendar.getCalStartDate());
        calendar.setEneDate(academicCalendar.getEneDate());
        return calendar;
    }


    public void updateById(Long id, AcademicCalendarDto dto) {
        academicCalendarRepository.findById(id);
        AcademicCalendar academicCalendar = new AcademicCalendar();

        academicCalendar.setId(id);
        academicCalendar.setTitle(dto.getTitle());
        academicCalendar.setDescription(dto.getDescription());
        academicCalendar.setCalStartDate(dto.getCalStartDate());
        academicCalendar.setEneDate(dto.getEneDate());
        academicCalendarRepository.save(academicCalendar);

    }
}
